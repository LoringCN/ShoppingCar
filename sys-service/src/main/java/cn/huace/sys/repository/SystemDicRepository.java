package cn.huace.sys.repository;



import cn.huace.common.repository.BaseRepository;
import cn.huace.sys.entity.SystemDic;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SystemDicRepository extends BaseRepository<SystemDic, Integer>
{
    @Query("select s from SystemDic s where s.type =(?1) order by s.sort")
    List<SystemDic> findDicByType(Integer type);
}
