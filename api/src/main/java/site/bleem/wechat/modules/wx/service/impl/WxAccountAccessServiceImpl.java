package site.bleem.wechat.modules.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import site.bleem.wechat.common.exception.RRException;
import site.bleem.wechat.common.utils.Constant;
import site.bleem.wechat.modules.wx.dao.SysUserWxAccountMapper;
import site.bleem.wechat.modules.wx.entity.SysUserWxAccount;
import site.bleem.wechat.modules.wx.entity.WxAccount;
import site.bleem.wechat.modules.wx.service.WxAccountAccessService;
import site.bleem.wechat.modules.wx.service.WxAccountService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WxAccountAccessServiceImpl implements WxAccountAccessService {
    private final WxAccountService wxAccountService;
    private final SysUserWxAccountMapper accessMapper;

    public WxAccountAccessServiceImpl(WxAccountService wxAccountService, SysUserWxAccountMapper accessMapper) {
        this.wxAccountService = wxAccountService;
        this.accessMapper = accessMapper;
    }

    @Override
    public List<WxAccount> getAccessibleAccounts(Long userId) {
        if (userId != null && userId == Constant.SUPER_ADMIN) {
            return wxAccountService.list();
        }

        List<SysUserWxAccount> accessList = accessMapper.selectList(
            new QueryWrapper<SysUserWxAccount>().eq("user_id", userId));
        if (accessList.isEmpty()) {
            return Collections.emptyList();
        }
        return wxAccountService.listByIds(accessList.stream()
            .map(SysUserWxAccount::getAppid)
            .collect(Collectors.toList()));
    }

    @Override
    public void assertAccessible(Long userId, String appid) {
        if (appid == null || appid.trim().isEmpty()) {
            throw new RRException("请先选择公众号");
        }
        if (userId != null && userId == Constant.SUPER_ADMIN) {
            return;
        }
        Long count = accessMapper.selectCount(new QueryWrapper<SysUserWxAccount>()
            .eq("user_id", userId)
            .eq("appid", appid));
        if (count == null || count == 0) {
            throw new RRException("没有该公众号的操作权限");
        }
    }
}
