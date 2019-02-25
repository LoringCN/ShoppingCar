package cn.huace.ad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.huace.ad.entity.AdRelation;
import cn.huace.ad.repository.AdRelationRepository;
import cn.huace.common.service.BaseService;

@Service
public class AdRelationService extends BaseService<AdRelation, Integer> {
	 @Autowired
	 private AdRelationRepository adRelationRepository; 
}
