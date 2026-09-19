package site.bleem.wechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final WxAccountAccessInterceptor wxAccountAccessInterceptor;

    public WebMvcConfig(WxAccountAccessInterceptor wxAccountAccessInterceptor) {
        this.wxAccountAccessInterceptor = wxAccountAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(wxAccountAccessInterceptor)
            .addPathPatterns("/manage/**")
            .excludePathPatterns(
                "/manage/wxAccount/**",
                "/manage/console/accounts",
                "/manage/console/account-config/**"
            );
    }
}
