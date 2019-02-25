package cn.huace.shop.app.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.app.entity.AppList;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Loring on 2017/7/24.
 */
public interface AppListRepository extends BaseRepository<AppList,Integer> {

    @Modifying
    @Query("update AppList t set t.isEnabled = false where t.id = ?1 and t.isEnabled =true ")
    Integer deleteById(Integer id);

    @Query(value = "select * from app_list t where t.shop_id = ?1 and t.is_enabled =1",nativeQuery = true)
    List<Object> listByShopId(Integer shopId);

}
