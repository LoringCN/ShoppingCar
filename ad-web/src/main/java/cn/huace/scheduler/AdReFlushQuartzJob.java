package cn.huace.scheduler;

import cn.huace.ad.entity.Ad;
import cn.huace.ad.entity.AdOnlineV2;
import cn.huace.ad.entity.AdV2;
import cn.huace.ad.service.AdOnlineV2Service;
import cn.huace.ad.service.AdService;
import cn.huace.ad.service.AdV2Service;
import cn.huace.ad.util.AdCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/** 
 * @author zhouyanbin 
 * @date 2017年6月13日 上午11:14:20 
 * @version 1.0 
*/
@Slf4j
@Configuration
@EnableScheduling
public class AdReFlushQuartzJob {
	@Autowired
	private AdService adService;

	@Autowired
	private AdV2Service adV2Service;

	@Autowired
	private AdOnlineV2Service adOnlineV2Service;

	@Scheduled(cron = "0 0 0 * * ?")
	public void doJob(){
		List<Ad> adList = adService.findAll();
		if(adList == null || adList.isEmpty()){
			return;
		}
		
		for (Ad ad : adList) {
			if(AdCodeConstants.VALID.equals(ad.getValidFlag()) && ad.getOfflineTime().before(new Date())){
				ad.setValidFlag(AdCodeConstants.INVALID);
				adService.save(ad);
				log.info("广告ID为:" +ad.getId() + "的广告下架成功,下架时间" + new Date());
			}
		}
	}

	/**
	 * 每一个小时定时检测过期广告并下线过期广告
	 */
	@Scheduled(cron = "0 0 * * * ?")
	public void offlineExpireAd(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 1.查询所有在线广告
		Map<String,Object> searchMap = new HashMap<>();
		// 外部广告
		searchMap.put("EQ_flag",AdCodeConstants.OUTER_AD);
		searchMap.put("EQ_voted",AdCodeConstants.ONLINE_AD);
		searchMap.put("NE_status",AdCodeConstants.AdStatus.DELETED);

		List<AdV2> adList = adV2Service.findAll(searchMap);

		// 存放过期广告
		List<AdV2> expireAds = new ArrayList<>();
		List<Integer> adIds = new ArrayList<>();
		// 2.取出所有已过期广告
		if(!CollectionUtils.isEmpty(adList)){
			for(AdV2 ad : adList){
				if(ad.getOverdueTime().before(new Date())){
					ad.setDeliverMethod(null);
					ad.setVoted(AdCodeConstants.OFFLINE_AD);
					expireAds.add(ad);
					adIds.add(ad.getId());
				}
			}
			// 3.批量更新过期广告voted状态为：下线
			boolean result = adV2Service.batchUpdateVotedForExpireAd(expireAds);
			if(result){
				log.info("******** 批量更新过期广告【voted】状态为下线成功！");
			}

			if (!CollectionUtils.isEmpty(adIds)) {
				// 4.批量删除上线记录
				List<AdOnlineV2> onlineAdV2s = adOnlineV2Service.findAllOnlineAdsInAdIds(adIds);
				adOnlineV2Service.batchDelete(onlineAdV2s);
				log.info("******** 批量下线广告成功！adIds = {}，time = {}",adIds,sdf.format(new Date()));
			}
		}else {
			log.info("****** 没有过期广告需要下线！，time = {}",sdf.format(new Date()));
		}
	}
}
