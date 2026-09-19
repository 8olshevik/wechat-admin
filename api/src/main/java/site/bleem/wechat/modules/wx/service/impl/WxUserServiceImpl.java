package site.bleem.wechat.modules.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import site.bleem.wechat.common.utils.Query;
import site.bleem.wechat.config.TaskExcutor;
import site.bleem.wechat.modules.wx.dao.WxUserMapper;
import site.bleem.wechat.modules.wx.entity.WxUser;
import site.bleem.wechat.modules.wx.service.WxUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nifury
 * @date 2017-9-27
 */
@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String SYNC_LOCK_KEY = "wx:user:sync:lock";

    @Autowired
    private WxUserMapper userMapper;
	@Autowired
	private WxMpService wxMpService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<WxUser> queryPage(Map<String, Object> params) {
        String openid = (String) params.get("openid");
        String nickname = (String) params.get("nickname");
		String appid = (String) params.get("appid");
		String city = (String) params.get("city");
		String tagId = (String) params.get("tagid");
		String qrSceneStr = (String) params.get("qrSceneStr");
        return this.page(
            new Query<WxUser>().getPage(params),
            new QueryWrapper<WxUser>()
				.eq(StringUtils.hasText(appid), "appid", appid)
                .eq(StringUtils.hasText(openid), "openid", openid)
                .like(StringUtils.hasText(nickname), "nickname", nickname)
				.eq(StringUtils.hasText(city), "city", city)
				.eq(StringUtils.hasText(qrSceneStr), "qrSceneStr", qrSceneStr)
				.apply(StringUtils.hasText(tagId),"JSON_CONTAINS(tagid_list,{0})",tagId)
        );
    }

    /**
     * 根据openid更新用户信息
     *
     * @param openid
     * @return
     */
    @Override
    public WxUser refreshUserInfo(String openid,String appid) {
        try {
			// 获取微信用户基本信息
			logger.info("更新用户信息，openid={}",openid);
			wxMpService.switchover(appid);
			WxMpUser userWxInfo = wxMpService.getUserService().userInfo(openid, null);
			if (userWxInfo == null) {
				logger.error("获取不到用户信息，无法更新,openid:{}",openid);
				return null;
			}
			WxUser user = new WxUser(userWxInfo,appid);
			this.saveOrUpdate(user);
			return user;
		} catch (Exception e) {
			logger.error("更新用户信息失败,openid:{}",openid);
		}
		return null;
    }

    /**
	 * 异步批量同步用户信息
	 * @param openidList
	 */
	@Override
	@Async
	public void refreshUserInfoAsync(String[] openidList,String appid) {
		logger.info("批量更新用户信息：任务开始");
		for(String openid:openidList){
			wxMpService.switchover(appid);
			TaskExcutor.submit(()->this.refreshUserInfo(openid,appid));
		}
		logger.info("批量更新用户信息：任务全部添加到线程池");
	}

    /**
     * 数据存在时更新，否则新增
     *
     * @param user
     */
    @Override
    public void updateOrInsert(WxUser user) {
        boolean exists = this.count(new QueryWrapper<WxUser>().eq("openid", user.getOpenid())) > 0;
        if (!exists) {
            userMapper.insert(user);
            return;
        }
        UpdateWrapper<WxUser> update = new UpdateWrapper<WxUser>().eq("openid", user.getOpenid());
        if (StringUtils.hasText(user.getAppid())) update.set("appid", user.getAppid());
        if (StringUtils.hasText(user.getNickname())) update.set("nickname", user.getNickname());
        if (user.getSex() != null) update.set("sex", user.getSex());
        if (StringUtils.hasText(user.getCity())) update.set("city", user.getCity());
        if (StringUtils.hasText(user.getProvince())) update.set("province", user.getProvince());
        if (StringUtils.hasText(user.getCountry())) update.set("country", user.getCountry());
        if (StringUtils.hasText(user.getHeadimgurl())) update.set("headimgurl", user.getHeadimgurl());
        if (StringUtils.hasText(user.getUnionid())) update.set("unionid", user.getUnionid());
        if (StringUtils.hasText(user.getRemark())) update.set("remark", user.getRemark());
        if (StringUtils.hasText(user.getSubscribeScene())) update.set("subscribe_scene", user.getSubscribeScene());
        if (StringUtils.hasText(user.getQrSceneStr())) update.set("qr_scene_str", user.getQrSceneStr());
        if (user.getSubscribeTime() != null) update.set("subscribe_time", user.getSubscribeTime());
        if (user.getTagidList() != null) update.set("tagid_list", user.getTagidList());
        update.set("subscribe", user.isSubscribe());
        if (StringUtils.hasText(user.getPhone())) update.set("phone", user.getPhone());
        this.update(update);
    }

    @Override
    public void unsubscribe(String openid) {
        userMapper.unsubscribe(openid);
    }
    
    /**
	 * 同步用户列表,公众号一次拉取调用最多拉取10000个关注者的OpenID，可以通过传入nextOpenid参数多次拉取
	 */
    @Override
	@Async
    public void syncWxUsers(String appid) {
		// 同步较慢，用 Redis SET NX 防止多实例/多线程重复执行
		Boolean acquired = redisTemplate.opsForValue().setIfAbsent(SYNC_LOCK_KEY, appid, Duration.ofHours(1));
		Assert.isTrue(Boolean.TRUE.equals(acquired), "后台有同步任务正在进行中，请稍后重试");
		try {
			wxMpService.switchoverTo(appid);
			logger.info("同步公众号粉丝列表：任务开始");
			boolean hasMore=true;
			String nextOpenid=null;
			WxMpUserService wxMpUserService = wxMpService.getUserService();
			int page=1;
			while (hasMore){
				WxMpUserList wxMpUserList = wxMpUserService.userList(nextOpenid);//拉取openid列表，每次最多1万个
				logger.info("拉取openid列表：第{}页，数量：{}",page++,wxMpUserList.getCount());
				List<String> openids = wxMpUserList.getOpenids();
				this.syncWxUsers(openids,appid);
				nextOpenid=wxMpUserList.getNextOpenid();
				hasMore=StringUtils.hasText(nextOpenid) && wxMpUserList.getCount()>=10000;
			}
			logger.info("同步公众号粉丝列表：完成");
		} catch (WxErrorException e) {
			logger.error("同步公众号粉丝出错:",e);
		} finally {
			redisTemplate.delete(SYNC_LOCK_KEY);
		}
	}

	/**
	 * 通过传入的openid列表，同步用户列表
	 * @param openids
	 */
	@Override
	public void syncWxUsers(List<String> openids,String appid) {
		if(openids.size()<1) {
            return;
        }
		final String batch=openids.get(0).substring(20);//截取首个openid的一部分做批次号（打印日志时使用，无实际意义）
		WxMpUserService wxMpUserService = wxMpService.getUserService();
		int start=0,batchSize=openids.size(),end=Math.min(100,batchSize);
		logger.info("开始处理批次：{}，批次数量：{}",batch,batchSize);
		while (start<end && end<=batchSize){//分批处理,每次最多拉取100个用户信息
			final int finalStart = start,finalEnd = end;
			final List<String> subOpenids=openids.subList(finalStart,finalEnd);
			TaskExcutor.submit(()->{//使用线程池同步数据，否则大量粉丝数据需同步时会很慢
				logger.info("同步批次:【{}--{}-{}】，数量：{}",batch, finalStart, finalEnd,subOpenids.size());
				wxMpService.switchover(appid);
				List<WxMpUser> wxMpUsers = null;//批量获取用户信息，每次最多100个
				try {
					wxMpUsers = wxMpUserService.userInfoList(subOpenids);
				} catch (WxErrorException e) {
					logger.error("同步出错，批次：【{}--{}-{}】，错误信息：{}",batch, finalStart, finalEnd,e);
				}
				if(wxMpUsers!=null && !wxMpUsers.isEmpty()){
					List<WxUser> wxUsers=wxMpUsers.parallelStream().map(item->new WxUser(item,appid)).collect(Collectors.toList());
					this.saveOrUpdateBatch(wxUsers);
				}
			});
			start=end;
			end=Math.min(end+100,openids.size());
		}
		logger.info("批次：{}处理完成",batch);
	}

}
