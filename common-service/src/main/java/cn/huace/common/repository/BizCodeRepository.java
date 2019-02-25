package cn.huace.common.repository;


import cn.huace.common.entity.BizCode;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 基础字典 持久层created on 2018-06-12
 * @author Loring
 */
public interface BizCodeRepository extends BaseRepository<BizCode, Integer>
{
    @Query("select t from BizCode t where t.bizCodeType.codeType = ?1 ORDER BY t.itemCode asc")
    List<BizCode> findByTypeCode(String typeCode);

    @Query("select t from BizCode t where t.bizCodeType.codeType = ?1 and t.itemCode = ?2 ")
    BizCode findByTypeAndCode(String typeCode,String itemCode);
}
