package cn.huace.sys.service;



import cn.huace.common.utils.Digests;
import cn.huace.common.utils.Encodes;
import cn.huace.common.utils.jpa.DynamicSpecifications;
import cn.huace.common.utils.jpa.SearchFilter;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.repository.SystemUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理类.
 * 
 * @author 陆小凤
 */
@Service
public class AccountService
{
    
    public static final String HASH_ALGORITHM = "SHA-1";
    
    public static final int HASH_INTERATIONS = 1024;
    
    private static final int SALT_SIZE = 8;

    @Autowired
    private SystemUserRepository systemUserRepository;
    
    /**
     * <获得用户信息列表>
     * 
     * @param searchParams
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<SystemUser> getUserList(Map<String, Object> searchParams, int pageNumber, int pageSize)
    {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        Specification<SystemUser> spec = buildSpecification(searchParams);
        return systemUserRepository.findAll(spec, pageRequest);
    }
    
    public List<SystemUser> getAllUser()
    {
        return (List<SystemUser>)systemUserRepository.findAll();
    }
    
    public SystemUser getUser(Integer id)
    {
        return systemUserRepository.findOne(id);
    }

    public SystemUser findUserByAccount(String account)
    {
        
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("EQ_account", account);
        Specification<SystemUser> spec = buildSpecification(searchParams);
        List<SystemUser> userList = systemUserRepository.findAll(spec);
        if (!userList.isEmpty())
        {
            return userList.get(0);
        }
        else
        {
            return null;
        }
        
    }
    
    public void registerUser(SystemUser SystemUser)
    {
        entryptPassword(SystemUser);
        systemUserRepository.save(SystemUser);
    }
    
    public void addSystemUser(SystemUser SystemUser)
    {
        systemUserRepository.save(SystemUser);
    }
    
    public void updateUser(SystemUser SystemUser)
    {
        if (StringUtils.isNotBlank(SystemUser.getPlainPassword()))
        {
            entryptPassword(SystemUser);
        }
        systemUserRepository.save(SystemUser);
    }
    
    public void deleteUser(Integer id)
    {
        if (isSupervisor(id))
        {
//            log.info("操作员{}尝试删除超级管理员用户", getCurrentUserName());

            throw new ServiceException("不能删除超级管理员用户");
        }
        systemUserRepository.delete(id);
        
    }
    
    /**
     * 判断是否超级管理员.
     */
    private boolean isSupervisor(Integer id)
    {
        return id == 1;
    }
    
    /**
     * 取出Shiro中的当前用户LoginName.
     */
    private String getCurrentUserName()
    {
        ShiroUser SystemUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
        return SystemUser.getAccount();
    }
    
    /**
     * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
     */
    public void entryptPassword(SystemUser SystemUser)
    {
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        SystemUser.setSalt(Encodes.encodeHex(salt));
        byte[] hashPassword = Digests.sha1(SystemUser.getPlainPassword().getBytes(), salt, HASH_INTERATIONS);
        SystemUser.setPassword(Encodes.encodeHex(hashPassword));
//        SystemUser.setPassword(new Md5Hash(SystemUser.getPlainPassword()).toString());
    }

    
    /**
     * 创建分页请求.
     */
    private PageRequest buildPageRequest(int pageNumber, int pagzSize)
    {
        Sort sort = null;
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }
    
    /**
     * 创建动态查询条件组合.
     */
    private Specification<SystemUser> buildSpecification(Map<String, Object> searchParams)
    {
        Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        // filters.put("firm.firmId", new SearchFilter("firm.firmId",
        // Operator.EQ, userId));
        Specification<SystemUser> spec = DynamicSpecifications.bySearchFilter(filters.values(), SystemUser.class);
        return spec;
    }
}
