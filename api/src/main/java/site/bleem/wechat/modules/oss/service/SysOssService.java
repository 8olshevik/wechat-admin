package site.bleem.wechat.modules.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bleem.wechat.common.utils.PageUtils;
import site.bleem.wechat.modules.oss.entity.SysOssEntity;

import java.util.Map;

/**
 * 文件上传
 * @author Mark 8olshevik@gmail.com
 */
public interface SysOssService extends IService<SysOssEntity> {
    /**
     * 分页查询用户数据
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);
}
