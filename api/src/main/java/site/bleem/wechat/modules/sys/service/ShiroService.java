package site.bleem.wechat.modules.sys.service;

import site.bleem.wechat.modules.sys.entity.SysUserEntity;
import site.bleem.wechat.modules.sys.entity.SysUserTokenEntity;

import java.util.Set;

/**
 * shiro相关接口
 * @author Mark 8olshevik@gmail.com
 */
public interface ShiroService {
    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(long userId);

    SysUserTokenEntity queryByToken(String token);

    /**
     * 根据用户ID，查询用户
     * @param userId 用户ID
     * @return SysUserEntity 管理用户
     */
    SysUserEntity queryUser(Long userId);
}
