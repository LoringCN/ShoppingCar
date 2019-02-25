package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.document.SearchGoods;
import cn.huace.goods.entity.Theme;
import cn.huace.goods.repository.jpa.ThemeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 *
 * Created by yld on 2017/7/24.
 */
@Slf4j
@Service
public class ThemeService extends BaseService<Theme,Integer>{
    @Autowired
    private ThemeRepository themeRepository;

    /**
     * 查询主题列表
     * @param searchMap
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<Theme> findAllThemes(Map<String,Object> searchMap, Integer pageNo, Integer pageSize){
        Page<Theme> themes = findAll(searchMap,pageNo,pageSize, Sort.Direction.DESC,"modifiedTime");
        return themes;
    }
    /**
     * 删除主题
     */
    @Transactional
    public Boolean delTheme(List<Integer> ids){
        List<Theme> themes = findAll(ids);
        List<Theme> delThemes = new ArrayList<>();
        if(!CollectionUtils.isEmpty(themes)){
            for(Theme theme:themes){
                theme.setFlag("-1");
                delThemes.add(theme);
            }
        }
        int delNum = batchUpdate(delThemes);
        log.info("***** 批量删除主题数目，delNum= "+delNum);
        return delNum == themes.size();
    }
    /**
     * 根据shopId和主题类型检查主题是否存在,避免重复添加
     * @return true -- 存在
     *          false -- 不存在
     */
    public Boolean checkThemeByShopIdAndThemeType(Integer shopId,Integer themeTypeId,String themeName){
        Map<String,Object> searchMap = new HashMap<>();
        if(!StringUtils.isEmpty(shopId)){
            searchMap.put("EQ_shop.id",shopId);
        }

        if(!StringUtils.isEmpty(themeTypeId)){
            searchMap.put("EQ_type.id",themeTypeId);
        }
        if(!StringUtils.isEmpty(themeName)){
            searchMap.put("EQ_name",themeName);
        }

        searchMap.put("EQ_flag","1");

        List<Theme> theme = findAll(searchMap);
        if(!CollectionUtils.isEmpty(theme)){
            return true;
        }
        return false;
    }
    /**
     * 根据商店Id数组查询所有主题
     */
    public List<Theme> findAllThemeByShopIds(Integer[] shopIds){
        return themeRepository.findAllThemeByShopIds(shopIds);
    }
    /**
     * 删除主题tag与已删除商品关联关系
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean removeThemeTagRelationGoods(List<Integer> goodsIdList,Integer[] shopIdArr){
        //删除已删除商品与主题关联
        List<Theme> oldThemes = findAllThemeByShopIds(shopIdArr);
        if(CollectionUtils.isEmpty(oldThemes)){
            log.info("***** 给定商店下不存在主题信息，《删除商品》无需进行删除主题tag与已删除商品关联关系操作");
            return false;
        }
        List<Theme> themes = new ArrayList<>();
        for(Theme oldTheme:oldThemes){
            String tags = oldTheme.getTag();
            if(!StringUtils.isEmpty(tags)){
                String[] tagsArr = tags.split(",");
                StringBuilder sb = new StringBuilder();
                for(String goodsId:tagsArr){
                    if(!StringUtils.isEmpty(goodsId) && !goodsIdList.contains(Integer.parseInt(goodsId))){
                        sb.append(goodsId+",");
                    }
                }
                String newTag = sb.toString();
                oldTheme.setTag(newTag.substring(0,newTag.length()));
            }else{
                log.info("******** 主题：{}，没有关联商品~~~",oldTheme.getId());
            }
            themes.add(oldTheme);
        }
        //批量更新数据
        int result = batchUpdate(themes);
        if(result == oldThemes.size()){
            log.info("******* 成功删除主题tag与已删除商品关联关系");
            return true;
        }
        return false;
    }
    /**
     * 检查是否已有同主题类型主题处于上架状态
     */
    public Theme checkIsExistOnlineTheme(Integer shopId,Integer themeTypeId){
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_type.id",themeTypeId);
        searchMap.put("EQ_active","1");
        searchMap.put("EQ_flag","1");
        List<Theme> themes = findAll(searchMap);
        if(!CollectionUtils.isEmpty(themes)){
            //由于每个超市只能有一个同类型主题上线，因此此处只会有一个结果或者没有
            return themes.get(0);
        }
        return null;
    }
    /**
     * 根据超市、主题类型查询已上架主题
     */
    public Theme findOnlineThemeByShopIdAndThemeTypeId(Integer shopId,Integer themeTypeId){
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_type.id",themeTypeId);
        searchMap.put("EQ_active","1");
        searchMap.put("EQ_flag","1");
        List<Theme> themes = findAll(searchMap);
        if(!CollectionUtils.isEmpty(themes)){
            return themes.get(0);
        }
        return null;
    }

    /**
     * 查询指定商店下所有特价主题
     */
    public List<Theme> findPromotionThemes(Integer shopId, Integer themeTypeId) {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_type.id",themeTypeId);
        searchMap.put("EQ_flag","1");
        return findAll(searchMap);
    }

    /**
     * 商品类型从《特价》修改为《normal》时，移除特价主题tag关联商品ID
     * @param shopId
     * @param themeTypeId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean removePromotionThemeTagRelationGoods(Integer shopId,Integer themeTypeId,Integer id){
        List<Theme> oldThemes = findPromotionThemes(shopId,themeTypeId);
        if(CollectionUtils.isEmpty(oldThemes)){
            log.info("******* 指定商店下没有《特价主题》信息，《修改商品》时无需进行移除特价主题tag关联商品操作");
            return false;
        }
        List<Theme> themes = new ArrayList<>();
        for(Theme oldTheme:oldThemes){
            String tags = oldTheme.getTag();
            if(!StringUtils.isEmpty(tags)){
                String[] tagsArr = tags.split(",");
                StringBuilder sb = new StringBuilder();
                for(String goodsId:tagsArr){
                    if(!StringUtils.isEmpty(goodsId) && !goodsId.equals(String.valueOf(id))){
                        sb.append(goodsId+",");
                    }
                }
                String newTag = sb.toString();
                oldTheme.setTag(newTag.substring(0,newTag.length()));
            }
            themes.add(oldTheme);
        }
        //批量更新数据
        int result = batchUpdate(themes);
        if(result == oldThemes.size()){
            log.info("******* 成功删除《特价主题》tag与已修改商品关联关系，size= "+ result);
            return true;
        }
        return false;
    }
}
