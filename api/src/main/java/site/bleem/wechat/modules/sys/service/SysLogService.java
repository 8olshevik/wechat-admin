package site.bleem.wechat.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import site.bleem.wechat.common.utils.PageUtils;
import site.bleem.wechat.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 * @author Mark 8olshevik@gmail.com
 */
public interface SysLogService extends IService<SysLogEntity> {
    /**
     * 分页查询用户数据
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);


}
