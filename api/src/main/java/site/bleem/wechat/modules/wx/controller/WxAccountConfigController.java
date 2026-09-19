package site.bleem.wechat.modules.wx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bleem.wechat.common.exception.RRException;
import site.bleem.wechat.common.utils.Constant;
import site.bleem.wechat.common.utils.R;
import site.bleem.wechat.modules.sys.controller.AbstractController;
import site.bleem.wechat.modules.wx.config.EventMessageListenerContainerConfig;
import site.bleem.wechat.modules.wx.dao.SysUserWxAccountMapper;
import site.bleem.wechat.modules.wx.dto.WxAccountConfigView;
import site.bleem.wechat.modules.wx.entity.SysUserWxAccount;
import site.bleem.wechat.modules.wx.entity.WxAccount;
import site.bleem.wechat.modules.wx.form.WxAccountConfigForm;
import site.bleem.wechat.modules.wx.service.WxAccountAccessService;
import site.bleem.wechat.modules.wx.service.WxAccountService;

import java.util.Date;

/**
 * 运营台公众号接入配置。密钥仅用于保存，不会被接口返回。
 * @author Mark 8olshevik@gmail.com
 */
@RestController
@RequestMapping("/manage/console/account-config")
public class WxAccountConfigController extends AbstractController {
    private final WxAccountService wxAccountService;
    private final WxAccountAccessService wxAccountAccessService;
    private final SysUserWxAccountMapper accessMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public WxAccountConfigController(WxAccountService wxAccountService,
                                     WxAccountAccessService wxAccountAccessService,
                                     SysUserWxAccountMapper accessMapper,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.wxAccountService = wxAccountService;
        this.wxAccountAccessService = wxAccountAccessService;
        this.accessMapper = accessMapper;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/{appid}")
    @RequiresPermissions("wx:wxaccount:info")
    public R info(@PathVariable String appid) {
        wxAccountAccessService.assertAccessible(getUserId(), appid);
        WxAccount account = wxAccountService.getById(appid);
        if (account == null) {
            throw new RRException("公众号不存在", 404);
        }
        return R.ok().put("account", WxAccountConfigView.from(account));
    }

    @PostMapping
    @RequiresPermissions("wx:wxaccount:save")
    public R save(@RequestBody WxAccountConfigForm form) {
        validateForm(form);

        String appid = form.getAppid().trim();
        WxAccount account = wxAccountService.getById(appid);
        boolean creating = account == null;
        if (creating) {
            account = new WxAccount();
            account.setAppid(appid);
        } else {
            wxAccountAccessService.assertAccessible(getUserId(), appid);
        }

        account.setName(form.getName().trim());
        account.setType(form.getType() == null ? 1 : form.getType());
        account.setVerified(form.getVerified() == null || form.getVerified());
        if (StringUtils.hasText(form.getSecret())) {
            account.setSecret(form.getSecret().trim());
        }
        if (StringUtils.hasText(form.getToken())) {
            account.setToken(form.getToken().trim());
        }
        if (Boolean.TRUE.equals(form.getClearAesKey())) {
            account.setAesKey(null);
        } else if (StringUtils.hasText(form.getAesKey())) {
            account.setAesKey(form.getAesKey().trim());
        }

        if (creating && !StringUtils.hasText(account.getSecret())) {
            throw new RRException("首次接入必须填写 AppSecret");
        }
        if (!StringUtils.hasText(account.getToken())) {
            throw new RRException("必须填写消息推送 Token");
        }
        if (creating && !Boolean.TRUE.equals(form.getClearAesKey())
            && !StringUtils.hasText(account.getAesKey())) {
            throw new RRException("安全模式必须填写 EncodingAESKey");
        }
        if (!Boolean.TRUE.equals(form.getClearAesKey())
            && StringUtils.hasText(form.getAesKey())
            && account.getAesKey().length() != 43) {
            throw new RRException("EncodingAESKey 必须为 43 位");
        }

        wxAccountService.saveOrUpdateWxAccount(account);
        grantCreatorAccess(appid);
        redisTemplate.convertAndSend(EventMessageListenerContainerConfig.WX_ACCOUNT_UPDATE, appid);
        return R.ok().put("account", WxAccountConfigView.from(account));
    }

    private void validateForm(WxAccountConfigForm form) {
        if (form == null || !StringUtils.hasText(form.getAppid())) {
            throw new RRException("请填写 AppID");
        }
        if (!StringUtils.hasText(form.getName())) {
            throw new RRException("请填写公众号名称");
        }
        if (form.getAppid().trim().length() > 20) {
            throw new RRException("AppID 格式不正确");
        }
    }

    private void grantCreatorAccess(String appid) {
        if (getUserId() == Constant.SUPER_ADMIN) {
            return;
        }
        Long count = accessMapper.selectCount(new QueryWrapper<SysUserWxAccount>()
            .eq("user_id", getUserId())
            .eq("appid", appid));
        if (count != null && count > 0) {
            return;
        }
        SysUserWxAccount access = new SysUserWxAccount();
        access.setUserId(getUserId());
        access.setAppid(appid);
        access.setCreateTime(new Date());
        accessMapper.insert(access);
    }
}
