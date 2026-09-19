package site.bleem.wechat.modules.wx.controller;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bleem.wechat.common.utils.R;
import site.bleem.wechat.modules.sys.controller.AbstractController;
import site.bleem.wechat.modules.wx.entity.WxAccount;
import site.bleem.wechat.modules.wx.service.WxAccountAccessService;
import site.bleem.wechat.modules.wx.dto.WxAccountConfigView;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manage/console")
public class WxConsoleController extends AbstractController {
    private final WxAccountAccessService wxAccountAccessService;

    public WxConsoleController(WxAccountAccessService wxAccountAccessService) {
        this.wxAccountAccessService = wxAccountAccessService;
    }

    @GetMapping("/accounts")
    public R accounts() {
        List<WxAccount> accounts = wxAccountAccessService.getAccessibleAccounts(getUserId());
        return R.ok().put("list", accounts.stream()
            .map(WxAccountConfigView::from)
            .collect(Collectors.toList()));
    }

    @GetMapping("/context")
    public R context(@CookieValue(value = "appid", required = false) String appid) {
        wxAccountAccessService.assertAccessible(getUserId(), appid);
        return R.ok().put("appid", appid);
    }
}
