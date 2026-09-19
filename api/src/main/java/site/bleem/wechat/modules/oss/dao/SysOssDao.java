package site.bleem.wechat.modules.oss.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import site.bleem.wechat.modules.oss.entity.SysOssEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件上传
 * @author Mark 8olshevik@gmail.com
 */
@Mapper
public interface SysOssDao extends BaseMapper<SysOssEntity> {

}
