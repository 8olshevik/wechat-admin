package site.bleem.wechat.modules.sys.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import site.bleem.wechat.modules.sys.entity.SysLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志
 * @author Mark 8olshevik@gmail.com
 */
@Mapper
public interface SysLogDao extends BaseMapper<SysLogEntity> {

}
