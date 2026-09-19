package site.bleem.wechat.config;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.bleem.wechat.modules.sys.entity.SysUserEntity;
import site.bleem.wechat.modules.wx.service.WxAccountAccessService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class WxAccountAccessInterceptor implements HandlerInterceptor {
    private final WxAccountAccessService wxAccountAccessService;

    public WxAccountAccessInterceptor(WxAccountAccessService wxAccountAccessService) {
        this.wxAccountAccessService = wxAccountAccessService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        if (user != null) {
            wxAccountAccessService.assertAccessible(user.getUserId(), findAppid(request));
        }
        return true;
    }

    private String findAppid(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("appid".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
