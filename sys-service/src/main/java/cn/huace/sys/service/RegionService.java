package cn.huace.sys.service;


import cn.huace.sys.entity.RegionArea;
import cn.huace.sys.entity.RegionCity;
import cn.huace.sys.entity.RegionProvince;
import cn.huace.sys.repository.RegionAreaRepository;
import cn.huace.sys.repository.RegionCityRepository;
import cn.huace.sys.repository.RegionProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 区域管理
 * 
 * @author 陆小凤
 */
@Service
public class RegionService
{
    @Autowired
    private RegionAreaRepository regionAreaRepository;

    @Autowired
    private RegionCityRepository regionCityRepository;

    @Autowired
    private RegionProvinceRepository regionProvinceRepository;

    public List<RegionProvince>listProvince(){
       return  (List<RegionProvince>)regionProvinceRepository.findAll();
    }

    public List<RegionCity>listCity(Integer proviceId){
        return  (List<RegionCity>)regionCityRepository.findCitys(proviceId);
    }

    public List<RegionArea>listArea(Integer cityId){
        return  (List<RegionArea>)regionAreaRepository.findAreas(cityId);
    }
    public RegionProvince findProvince(Integer proviceId){
        return regionProvinceRepository.find(proviceId );
    }
    public RegionCity findCity(Integer cityId){
        return regionCityRepository.find(cityId );
    }
    public RegionArea findArea(Integer areaId){
        return regionAreaRepository.find(areaId );
    }
}
