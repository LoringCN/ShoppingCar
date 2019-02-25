package cn.huace.sys.service;

import cn.huace.common.service.BaseService;
import cn.huace.common.utils.jpa.PageUtil;
import cn.huace.sys.entity.SystemMenu;
import cn.huace.sys.entity.SystemRole;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.repository.SystemMenuRepository;
import cn.huace.sys.repository.SystemUserRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <用户信息接口>
 * 
 * @author 陆小凤
 * @version [版本号, 2014年6月15日]
 * 
 */
@Service
public class SystemUserService extends BaseService<SystemUser,Integer>
{
    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private SystemMenuRepository systemMenuRepository;
    
    /**
     * <查询分页返回的用户列表信息>
     * 
     * @param searchParams 查询参数
     * @param pageNumber 查询起始页
     * @param pageSize 查询页大小
     * @return
     * 
     */
    public Page<SystemUser> getUsers(Map<String, Object> searchParams, int pageNumber, int pageSize)
    {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNumber, pageSize);
        Specification<SystemUser> spec = PageUtil.buildSpecification(searchParams, SystemUser.class);
        return systemUserRepository.findAll(spec, pageRequest);
    }
    
    public List<SystemUser> findUser(Map<String, Object> searchParams, Sort sort)
    {
        Specification<SystemUser> spec = PageUtil.buildSpecification(searchParams, SystemUser.class);
        return systemUserRepository.findAll(spec, sort);
    }

    public List<SystemMenu> getMenuByUserId(Integer userId) {
        SystemUser user = systemUserRepository.findOne(userId);
        List<SystemMenu> menus = Lists.newArrayList();
            if (user.isSuperUser()) {
                menus = (List<SystemMenu>) systemMenuRepository.findAll();
            } else {
                Set<SystemRole> roles = user.getRoleSet();
                if (roles.isEmpty())
                    return Lists.newArrayList();
                for (SystemRole role : roles) {
                    for (SystemMenu menu : role.getMenuSet()) {
                        if (!menus.contains(menu)) {
                            menus.add(menu);
                        }
                    }
                }
            }
        return menus;
    }

    /**
     * 根据userId获取菜单列表 当前菜单较少，采取一次查询后封装 后续菜单增加后，可多次查询DB再封装
     *
     * @param userId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<SystemMenu> getMenuTreeByUserId(Integer userId)
    {
        List<SystemMenu> retMenus = Lists.newArrayList();
        List<SystemMenu> menus= getMenuByUserId(userId);
        List<SystemMenu>allMenus=(List<SystemMenu>) systemMenuRepository.findSortAll();
        if (!CollectionUtils.isEmpty(menus)){
            for(SystemMenu menu:allMenus){
                for (SystemMenu menut : menus){
                    if(menut.getParentId()!=null&&menut.getParentId().equals(menu.getId())){
                        if(!retMenus.contains(menu))
                        retMenus.add(menu);
                        if(menu.getChildMenus()==null){
                            menu.setChildMenus(Lists.<SystemMenu>newArrayList());
                        }
                        menu.getChildMenus().add(menut);
                    }
                }
            }
        }
        return retMenus;
    }
    public List<SystemMenu> getMenuTree()
    {
        List<SystemMenu> retMenus = Lists.newArrayList();
        List<SystemMenu> menus=(List<SystemMenu>) systemMenuRepository.findAll();
        List<SystemMenu>allMenus=(List<SystemMenu>) systemMenuRepository.findSortAll();
        if (!CollectionUtils.isEmpty(menus)){
            for(SystemMenu menu:allMenus){
                for (SystemMenu menut : menus){
                    if(menut.getParentId()!=null&&menut.getParentId().equals(menu.getId())){
                        if(!retMenus.contains(menu))
                            retMenus.add(menu);
                        if(menu.getChildMenus()==null){
                            menu.setChildMenus(Lists.<SystemMenu>newArrayList());
                        }
                        menu.getChildMenus().add(menut);
                    }
                }
            }
        }
        return retMenus;
    }
}
