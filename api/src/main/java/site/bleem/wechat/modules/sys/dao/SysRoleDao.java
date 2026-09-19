package site.bleem.wechat.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import site.bleem.wechat.modules.sys.entity.SysRoleEntity;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色管理
 * @author Mark 8olshevik@gmail.com
 */
@Mapper
@CacheNamespace(flushInterval = 300000L)//缓存五分钟过期
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {

    /**
     * 查询用户创建的角色ID列表
     */
    List<Long> queryRoleIdList(Long createUserId);
}
