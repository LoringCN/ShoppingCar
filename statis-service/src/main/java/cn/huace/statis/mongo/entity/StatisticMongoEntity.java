package cn.huace.statis.mongo.entity;

import cn.huace.common.utils.DateUtils;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
/**
 * 
 * @author Lin Huan
 * @date  2018年9月11日
 * @desc   描述 mongo存储统计结果实体类
 * @version 1.0.0
 */
@Document(collection="statistic")
@Data
public class StatisticMongoEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	
	private Integer shopId;
	
	private String date;
	
	private String type;
	
	private Object data;
	
	private String creatTime = DateUtils.getToday(DateUtils.DATE_FORMAT_DATE_TIME);


}
