package site.bleem.wechat.modules.oss.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import site.bleem.wechat.modules.oss.dao.SysOssDao;
import site.bleem.wechat.modules.oss.entity.SysOssEntity;
import site.bleem.wechat.modules.oss.service.SysOssService;
import site.bleem.wechat.common.utils.PageUtils;
import site.bleem.wechat.common.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysOssEntity> page = this.page(
            new Query<SysOssEntity>().getPage(params)
        );

        return new PageUtils(page);
    }

}
