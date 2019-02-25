package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.GoodsCategory;
import cn.huace.goods.repository.jpa.GoodsCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 *
 * Created by yld on 2017/7/20.
 */
@Slf4j
@Service
public class GoodsCategoryService extends BaseService<GoodsCategory,Integer>{

    @Autowired
    private GoodsCategoryRepository categoryRepository;

    /**
     * 查询所有商品分类(管理后台用)
     * @return
     */
    public List<GoodsCategory> findGoodsCategoryByShopId(Integer shopId){
        return categoryRepository.findByShopId(shopId);
    }
    /**
     * 查询所有商品分类
     * @return
     */
    public List<GoodsCategory> findGoodsCategoryByShopIdForApp(Integer shopId){
        return categoryRepository.findByShopIdForApp(shopId);
    }
    public Page<GoodsCategory> findAllGoodsCategoryPageable(Map<String,Object> searchMap,Integer pageNo,Integer pageSize){
        Page<GoodsCategory> categories = findAll(searchMap,pageNo,pageSize,Sort.Direction.DESC,"modifiedTime");
        return categories;
    }
    /**
     * 删除商品分类
     */
    @Transactional
    public Boolean batchDeleteGoodsCategory(List<Integer> catIds){
        List<GoodsCategory> categories = findAll(catIds);
        List<GoodsCategory> delCategories = new ArrayList<>();
        if(!CollectionUtils.isEmpty(categories)){
            for(GoodsCategory category:categories){
                category.setFlag("-1");
                delCategories.add(category);
            }
        }
        int delNum = batchUpdate(delCategories);
        log.info("**** 批量删除商品分类数量，delNum = "+delNum);
        return delNum == categories.size();
    }
    /**
     * 查询所有有效商品分类信息,排除特殊分类
     */
    public List<GoodsCategory> findAllCategoryAvailable(){
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("EQ_specialMark",false);
        paramMap.put("EQ_flag","1");
        List<GoodsCategory> categories = findAll(paramMap);
        return categories;
    }

    public GoodsCategory findByCatName(String catName,Integer shopId){
        return categoryRepository.findByCatNameAndShopId(catName,shopId);
    }

    /**
     * 查询新增和修改的商品分类信息
     */
    public List<GoodsCategory> findModifyAndNewCategories(Date lastCategoryCacheTime) {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("GTE_modifiedTime",lastCategoryCacheTime);
        searchMap.put("EQ_flag","1");
        return findAll(searchMap);
    }

    /**
     * 删除商店下分类
     *
     */
    @Transactional
    public void deleteAllByShopId(Integer shopId) {
        categoryRepository.deleteAllByShopId(shopId);
    }
}
