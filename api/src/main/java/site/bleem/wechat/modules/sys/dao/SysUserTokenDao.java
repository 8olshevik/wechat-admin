package site.bleem.wechat.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import site.bleem.wechat.modules.sys.entity.SysUserTokenEntity;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Token
 * @author Mark 8olshevik@gmail.com
 */
@Mapper
@CacheNamespace(flushInterval = 300000L)//缓存五分钟过期
public interface SysUserTokenDao extends BaseMapper<SysUserTokenEntity> {

    SysUserTokenEntity queryByToken(String token);

}
