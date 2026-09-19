package site.bleem.wechat.modules.wx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import site.bleem.wechat.modules.wx.entity.SysUserWxAccount;

@Mapper
public interface SysUserWxAccountMapper extends BaseMapper<SysUserWxAccount> {
}
