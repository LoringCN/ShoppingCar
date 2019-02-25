package cn.huace.statis.track.service;

import cn.huace.common.service.BaseService;
import cn.huace.statis.track.entity.StatisTrack;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * Created by huangdan on 2016/12/27.
 */
@Service
public class StatisTrackService extends BaseService<StatisTrack, Integer> {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<StatisTrack> listTrack(Map<String, Object> searchMap, Integer page, Integer rows) {
        Page<StatisTrack> pageResult = findAll(searchMap, page, rows, Sort.Direction.DESC, "atTime");
        return pageResult;
    }

    public long countDeviceNum(Integer shopId, String devId, Date startDate, Date endDate) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from statis_track st where 1=1 ");
        if (shopId != null) {
            sb.append(" and st.shop_id =:shopId ");
        }
        if (!StringUtils.isEmpty(devId)) {
            sb.append(" and st.dev_id like:devId ");
        }
        if (startDate != null) {
            sb.append(" and st.at_time >=:startDate");
        }
        if (endDate != null) {
            sb.append(" and st.at_time <=:endDate");
        }
        String sql = " select  count(DISTINCT st.dev_id)  " + sb.toString();
        javax.persistence.Query query = entityManager.createNativeQuery(sql);
        if (shopId != null) {
            query.setParameter("shopId", shopId);
        }
        if (!StringUtils.isEmpty(devId)) {
            query.setParameter("devId", "%" + devId + "%");
        }
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }
        BigInteger count = (BigInteger) query.getSingleResult();
        return count.longValue();
    }


}
