package site.bleem.wechat.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bleem.wechat.common.utils.R;
import site.bleem.wechat.modules.sys.entity.SysUserTokenEntity;

/**
 * 用户Token
 * @author Mark 8olshevik@gmail.com
 */
public interface SysUserTokenService extends IService<SysUserTokenEntity> {

    /**
     * 生成token
     * @param userId  用户ID
     */
    R createToken(long userId);

    /**
     * 退出，修改token值
     * @param userId  用户ID
     */
    void logout(long userId);

}
