package cn.huace.common.service;



import cn.huace.common.repository.BaseRepository;
import cn.huace.common.utils.jpa.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 
 * <通用Service类>
 *
 * @author 陆小凤
 * @version [版本号1.0, 2015年11月2日]
 */
public abstract class BaseService<T, ID extends Serializable>
{
    @Autowired
    protected BaseRepository<T, ID> baseRepository;
    
    private Class<T> entityClass;

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public BaseService()
    {
        if (null == entityClass)
        {
            Type type = getClass().getGenericSuperclass();
            if (!(type instanceof ParameterizedType))
            {
                type = getClass().getSuperclass().getGenericSuperclass();
            }
            entityClass = (Class<T>)((ParameterizedType)type).getActualTypeArguments()[0];
        }
    }
    
    @Transactional(readOnly = false)
    public T save(T entity)
    {
        return baseRepository.save(entity);
    }
    
    @Transactional(readOnly = false)
    public void delete(ID id) {baseRepository.delete(id);}

    @Transactional(readOnly = false)
    public void delete(T entity)
    {
        baseRepository.delete(entity);
    }
    
    @Transactional(readOnly = false)
    public void delete(List<T> entities)
    {
        baseRepository.delete(entities);
    }
    
    @Transactional(readOnly = false)
    public void deleteAll()
    {
        baseRepository.deleteAll();
    }
    
    @Transactional(readOnly = true)
    public T findOne(ID id)
    {
        return baseRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public T findOne(Map<String, Object> searchParams) {
        Specification<T> spec = PageUtil.buildSpecification(searchParams, entityClass);
        return baseRepository.findOne(spec);
    }

    @Transactional(readOnly = true)
    public boolean exists(ID id)
    {
        return baseRepository.exists(id);
    }
    
    @Transactional(readOnly = true)
    public Long count(Map<String, Object> searchParams)
    {
        Specification<T> spec = PageUtil.buildSpecification(searchParams, entityClass);
        return baseRepository.count(spec);
    }
    
    @Transactional(readOnly = true)
    public List<T> findAll()
    {
        return (List<T>)baseRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<T> findAll(List<ID> ids)
    {
        return (List<T>)baseRepository.findAll(ids);
    }
    
    @Transactional(readOnly = true)
    public Page<T> findAll(Map<String, Object> searchParams, int pageNumber, int pageSize)
    {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNumber, pageSize);
        return findAll(searchParams, pageRequest);
    }
    
    @Transactional(readOnly = true)
    public Page<T> findAll(Map<String, Object> searchParams, int pageNumber, int pageSize, Direction direction,
                           String orderBy)
    {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNumber, pageSize, direction, orderBy);
        return findAll(searchParams, pageRequest);
    }
    
    @Transactional(readOnly = true)
    public Page<T> findAll(Map<String, Object> searchParams, int pageNumber, int pageSize, Direction direction,
                           String... orderBys)
    {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNumber, pageSize, direction, orderBys);
        return findAll(searchParams, pageRequest);
    }
    
    @Transactional(readOnly = true)
    public Page<T> findAll(Map<String, Object> searchParams, int pageNumber, int pageSize, Direction direction,
                           List<String> orderBys)
    {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNumber, pageSize, direction, orderBys);
        return findAll(searchParams, pageRequest);
    }
    
    @Transactional(readOnly = true)
    public Page<T> findAll(Map<String, Object> searchParams, int pageNumber, int pageSize, List<Direction> directions,
                           List<String> orderBys)
    {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNumber, pageSize, directions, orderBys);
        return findAll(searchParams, pageRequest);
    }
    
    @Transactional(readOnly = true)
    public Page<T> findAll(Map<String, Object> searchParams, PageRequest pageRequest)
    {
        Specification<T> spec = PageUtil.buildSpecification(searchParams, entityClass);
        return baseRepository.findAll(spec, pageRequest);
    }
    
    @Transactional(readOnly = true)
    public List<T> findAll(Map<String, Object> searchParams)
    {
        Specification<T> spec = PageUtil.buildSpecification(searchParams, entityClass);
        return baseRepository.findAll(spec);
    }
    @Transactional(readOnly = true)
    public List<T> findAll(Map<String, Object> searchParams,Sort sort)
    {
        Specification<T> spec = PageUtil.buildSpecification(searchParams, entityClass);
        return baseRepository.findAll(spec,sort);
    }

    /**
     * 批量插入数据
     * @param entityList
     * @return
     */
    @Transactional(readOnly = false)
    public int batchInsert(List<T> entityList){
        if(!CollectionUtils.isEmpty(entityList)){
            for (int i = 0; i < entityList.size(); i++) {
                entityManager.persist(entityList.get(i));
                if(i % 20 == 0){
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            return entityList.size();
        }
        return 0;
    }

    /**
     * 批量更新
     * @param entityList
     * @return
     */
    @Transactional(readOnly = false)
    public int batchUpdate(List<T> entityList){
        if(!CollectionUtils.isEmpty(entityList)){
            for (int i = 0; i < entityList.size(); i++) {
                entityManager.merge(entityList.get(i));
                if(i % 20 == 0){
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            return entityList.size();
        }
        return 0;
    }
}
