package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.GoodsPictrue;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Loring on 2018/12/6.
 */
public interface GoodsPictrueRepository extends BaseRepository<GoodsPictrue,Integer> {

    @Query("select g from GoodsPictrue g where g.barcode = ?1")
    List<GoodsPictrue> findByBarcode(String barcode);

}
