package cn.huace.shop.shop.entity;


import cn.huace.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * <门店>
 *
 * @author huangdan
 */
@Data
@Entity
@Table(name = "shop")
public class Shop extends BaseEntity
{

    @Column(name = "name")
    private String name;

    @Column(name = "use_flag")
    private Boolean useFlag;

    //负责人
    @Column(name = "person")
    private String person;

    //负责人电话
    @Column(name = "phone")
    private String phone;

    //备注
    @Column(name = "remark")
    private String remark;

    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "area_id")
    private Integer areaId;

    @Column(name = "area")
    private String area;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    //营业时间
    @Column(name = "businessTime")
    private String businessTime;

    //开业日期
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "open_date")
    private Date openDate;

    //地址
    @Column(name = "address")
    private String address;
    //门店总体评价星星数

    @Column(name = "shop_phone")
    private String shopPhone;

    //告警楼层，供寻车计算购物车状态使用
    @Column(name = "alarm_floor")
    private String alarmFloor;
    //超市所在楼层
    @Column(name = "shop_floor")
    private String shopFloor;

//    //经度
//    @Column(name = "longitude")
//    private Double longitude;
//    //纬度
//    @Column(name = "latitude")
//    private Double latitude;
//    //定位距离
//    @Transient
//    private Double distance;

    //超市wifi登录名
    @Column(name = "ssid")
    private String ssid;

    //超市wifi密码
//    @Column(name = "pwd")
//    private String pwd;

    /**
     * 蜂鸟地图
     */
    @Column(name = "fmap_id")
    private String fmapId;

    @Transient
    private boolean closeBusiness;

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    //是否关店，true已经打烊，false正在营业
    public boolean getCloseBusiness(){
        try {
            if (StringUtils.isNotBlank(businessTime)){
                String[] business = businessTime.split("_");
                Long start = getMiliSeconds(business[0]);
                Long end = getMiliSeconds(business[1]);
                Long now = getMiliSecondsNow();
                if ((now>start&&now<end)||(now<start&&now>end)){
                    return false;
                }else {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public boolean getOpenFlag(){
        if (openDate!=null){
            Long time = openDate.getTime();
            Long now = System.currentTimeMillis();
            if (time>now){
                return false;
            }else {
                return true;
            }
        }
        return false;
    }

    //获取毫秒 01:00:00相当于1*60*60*1000
    private Long getMiliSeconds(String time) throws NumberFormatException{
        String hms[] = time.split(":");
        Long timeMili = 0L;
        timeMili += (Long.parseLong(hms[0])*3600+Long.parseLong(hms[1])*60+Long.parseLong(hms[2]))*1000L;
        return timeMili;
    }

    private Long getMiliSecondsNow(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return (hour*3600+minute*60+second)*1000L;
    }

}