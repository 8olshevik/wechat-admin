package site.bleem.wechat.modules.wx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import site.bleem.wechat.modules.wx.entity.TemplateMsgLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TemplateMsgLogMapper extends BaseMapper<TemplateMsgLog> {
}
