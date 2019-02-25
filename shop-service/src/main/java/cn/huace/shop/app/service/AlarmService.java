package cn.huace.shop.app.service;

import cn.huace.common.service.BaseService;
import cn.huace.shop.app.entity.Alarm;
import cn.huace.shop.app.entity.App;
import cn.huace.shop.app.repository.AlarmRepository;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by herry on 2017/5/22.
 */

@Service
public class AlarmService extends BaseService<Alarm,Integer> {

    public void deleteAllByShopId(Integer shopId) {

        ((AlarmRepository) baseRepository).deleteAllByShopId(shopId);
    }

    public String getByShopId(Integer shopId) {
        return ((AlarmRepository) baseRepository).getByShopId(shopId);
}

}
