package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.Theme;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 * Created by yld on 2017/7/24.
 */
public interface ThemeRepository extends BaseRepository<Theme,Integer>{

    @Query("select t from Theme t where t.shop.id in ?1 and t.flag = '1'")
    List<Theme> findAllThemeByShopIds(Integer[] shopIds);

}
