package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.Theme;
import cn.huace.goods.entity.ThemeType;
import cn.huace.goods.repository.jpa.ThemeTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yld on 2017/7/28.
 */
@Slf4j
@Service
public class ThemeTypeService extends BaseService<ThemeType,Integer>{
    @Autowired
    private ThemeTypeRepository themeTypeRepository;

    /**
     * 查询所有可用主题分类
     * @return
     */
    public List<ThemeType> findAllThemeTypeAvailable(){
        return themeTypeRepository.findAllThemeTypeAvailable();
    }
    /**
     * 根据主题类型名唯一查询
     */
    public ThemeType findOneThemeTypeByName(String name){
        return themeTypeRepository.findThemeTypeByName(name);
    }
}
