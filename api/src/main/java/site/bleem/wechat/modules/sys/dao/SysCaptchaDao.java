package site.bleem.wechat.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import site.bleem.wechat.modules.sys.entity.SysCaptchaEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验证码
 * @author Mark 8olshevik@gmail.com
 */
@Mapper
public interface SysCaptchaDao extends BaseMapper<SysCaptchaEntity> {

}
