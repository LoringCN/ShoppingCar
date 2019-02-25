package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.ThemeType;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 主题分类
 * Created by yld on 2017/7/28.
 */
public interface ThemeTypeRepository extends BaseRepository<ThemeType,Integer>{

    @Query("select tt from ThemeType tt where tt.flag = '1'")
    List<ThemeType> findAllThemeTypeAvailable();

    @Query("select tt from ThemeType tt where tt.name = ?1 and tt.flag='1'")
    ThemeType findThemeTypeByName(String name);
}
