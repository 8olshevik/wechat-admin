package site.bleem.wechat.modules.wx.form;

import lombok.Data;

@Data
public class WxAccountConfigForm {
    private String appid;
    private String name;
    private Integer type;
    private Boolean verified;
    private String secret;
    private String token;
    private String aesKey;
    private Boolean clearAesKey;
}
