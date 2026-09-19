package site.bleem.wechat.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bleem.wechat.modules.sys.entity.SysCaptchaEntity;

import java.awt.image.BufferedImage;

/**
 * 验证码
 * @author Mark 8olshevik@gmail.com
 */
public interface SysCaptchaService extends IService<SysCaptchaEntity> {

    /**
     * 获取图片验证码
     */
    BufferedImage getCaptcha(String uuid);

    /**
     * 验证码效验
     * @param uuid  uuid
     * @param code  验证码
     * @return true：成功  false：失败
     */
    boolean validate(String uuid, String code);
}
