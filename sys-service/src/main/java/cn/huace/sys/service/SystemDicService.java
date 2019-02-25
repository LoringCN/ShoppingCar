package cn.huace.sys.service;



import cn.huace.common.service.BaseService;
import cn.huace.sys.entity.SystemDic;
import cn.huace.sys.repository.SystemDicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统字典表.
 * 
 * @author
 */
@Service
public class SystemDicService extends BaseService<SystemDic,Integer>
{
    @Autowired
    private SystemDicRepository systemDicRepository;
    public  List<SystemDic> findDicByType(Integer type){
        return systemDicRepository.findDicByType(type);
    }
}
