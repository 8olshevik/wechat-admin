package site.bleem.wechat.modules.wx.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_user_wx_account")
public class SysUserWxAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String appid;
    private Date createTime;
}
