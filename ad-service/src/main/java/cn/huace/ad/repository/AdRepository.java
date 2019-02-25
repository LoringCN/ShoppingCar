package cn.huace.ad.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import cn.huace.ad.entity.Ad;
import cn.huace.common.repository.BaseRepository;

public interface AdRepository extends BaseRepository<Ad,Integer>{
	/**
     * 根据id和有效标示去查询唯一数据
     * @param id 主键
     * @param validFlag 是否有效
     * @return
     */
    @Query("select t from Ad t where id = ?1 and valid_Flag = ?2 " )
    Ad findAdById(Integer id,String validFlag);
    
    /**
     * 删除广告
     * @param id
     */
    @Query("update Ad set valid_Flag = '0' where id = ?1 ")
    void deleteAdById(Integer id);
    
    /**
     * 查询超市下所有的广告
     * @param shopId
     * @param validFlag
     * @return
     */
    @Query("select t from Ad t where shopId = ?1 and valid_Flag = ?2 order by id asc" )
    List<Ad> findAdListByShopId(Integer shopId,String validFlag);

    @Query("select o from Ad o where shopId=?1 and valid_Flag=1 and o.type=4")
    List<Ad> findReccAdList(Integer shopId) ;
}
