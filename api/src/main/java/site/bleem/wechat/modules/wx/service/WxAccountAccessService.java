package site.bleem.wechat.modules.wx.service;

import site.bleem.wechat.modules.wx.entity.WxAccount;

import java.util.List;

public interface WxAccountAccessService {
    List<WxAccount> getAccessibleAccounts(Long userId);

    void assertAccessible(Long userId, String appid);
}
