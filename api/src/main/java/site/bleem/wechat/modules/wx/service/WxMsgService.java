package site.bleem.wechat.modules.wx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bleem.wechat.common.utils.PageUtils;
import site.bleem.wechat.modules.wx.entity.WxMsg;

import java.util.Map;

/**
 * 微信消息
 *
 * @author niefy
 * @date 2020-05-14 17:28:34
 */
public interface WxMsgService extends IService<WxMsg> {
    /**
     * 分页查询用户数据
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 记录msg，异步入库
     * @param msg
     */
    void addWxMsg(WxMsg msg);
}

