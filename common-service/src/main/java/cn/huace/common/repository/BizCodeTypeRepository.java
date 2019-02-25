package cn.huace.common.repository;


import cn.huace.common.entity.BizCodeType;
import org.springframework.data.jpa.repository.Query;

/**
 * 基础字典定义 持久层created on 2018-06-12
 * @author Loring
 */
public interface BizCodeTypeRepository extends BaseRepository<BizCodeType, String>
{

    @Query("select t from BizCodeType t where t.codeType = ?1 ")
    BizCodeType findByTypeCode(String codeType);
}
