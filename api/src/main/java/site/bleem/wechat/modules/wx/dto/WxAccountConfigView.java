package site.bleem.wechat.modules.wx.dto;

import lombok.Data;
import site.bleem.wechat.modules.wx.entity.WxAccount;

@Data
public class WxAccountConfigView {
    private String appid;
    private String name;
    private int type;
    private boolean verified;
    private boolean secretConfigured;
    private boolean tokenConfigured;
    private boolean aesKeyConfigured;

    public static WxAccountConfigView from(WxAccount account) {
        WxAccountConfigView view = new WxAccountConfigView();
        view.setAppid(account.getAppid());
        view.setName(account.getName());
        view.setType(account.getType());
        view.setVerified(account.isVerified());
        view.setSecretConfigured(account.getSecret() != null && !account.getSecret().trim().isEmpty());
        view.setTokenConfigured(account.getToken() != null && !account.getToken().trim().isEmpty());
        view.setAesKeyConfigured(account.getAesKey() != null && !account.getAesKey().trim().isEmpty());
        return view;
    }
}
