package cn.huace.shop.app.service;

import cn.huace.common.service.BaseService;
import cn.huace.common.utils.Reflections;
import cn.huace.shop.app.entity.App;
import cn.huace.shop.app.entity.AppList;
import cn.huace.shop.app.repository.AppListRepository;
import cn.huace.shop.app.vo.AppListVo;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Loring on 2017/7/24.
 */
@Service
public class AppListService extends BaseService<AppList,Integer>{

    @Autowired
    private AppListRepository appListRepository;

    /**
     * 列表查询
     * @param vo
     * @param page
     * @param rows
     * @return
     */
    public Page<AppListVo> list(AppListVo vo,Integer page,Integer rows){
        Map<String,Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(vo.getName())){
            searchMap.put("LIKE_name",vo.getName());
        }
        if(vo.getShopId() != null){
            searchMap.put("EQ_shopId",vo.getShopId());
        }
        if(StringUtils.isNotBlank(vo.getPackageName())){
            searchMap.put("LIKE_packageName",vo.getPackageName());
        }
        searchMap.put("EQ_isEnabled",true);

        Page<AppList> poPage = findAll(searchMap,page,rows,Sort.Direction.DESC,"id");
        List<AppListVo> retData = new ArrayList<AppListVo>();
        retData =  poPage.getContent().stream().map( appList -> PoToVo(appList,new AppListVo())).collect(Collectors.toList());
        Page<AppListVo> ret = new PageImpl<>(retData,null,poPage.getTotalElements());
        return ret;
    }

    /**
     * 主键查询
     * @param id
     * @return
     */
    public AppListVo preEdit(Integer id){
        AppList appList = findOne(id);
        return PoToVo(appList,new AppListVo());
    }

    /**
     * 保存
     * @param vo
     * @return
     */
    public Boolean saveVo(AppListVo vo){
        AppList appList = VoToPo(vo,new AppList());
        return save(appList)!=null?true:false;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean deleteById(Integer id){
        return appListRepository.deleteById(id)>0?true:false;
    }

    /**
     * 查询商店下所有有效的app大类
     * @param shopId
     * @return
     */
    public List<AppListVo> listByShopId(Integer shopId){
        List<Object> list = appListRepository.listByShopId(shopId);
        if(list.isEmpty()){
            return null;
        }
        return list.stream().map(object -> (AppListVo) new AliasToBeanResultTransformer(AppListVo.class).transformTuple((Object[]) object,Reflections.getFiledName(new AppListVo()))).collect(Collectors.toList());
    }

    /**
     * PoToVo
     * @param source
     * @param target
     * @return
     */
    public AppListVo PoToVo(AppList source,AppListVo target){
        BeanUtils.copyProperties(source,target);
        target.setId(source.getId());
        return target;
    }

    /**
     * VoToPo
     * @param source
     * @param target
     * @return
     */
    public AppList VoToPo(AppListVo source,AppList target){
        BeanUtils.copyProperties(source,target);
        if(source.getId() != null){
        target.setId(source.getId());
        }
        return target;
    }

}
