package site.bleem.wechat.modules.wx.form;

import site.bleem.wechat.common.utils.Json;
import lombok.Data;

@Data
public class MaterialFileDeleteForm {
    String mediaId;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }
}
