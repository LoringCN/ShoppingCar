package cn.huace.common.Vo;

import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * 公共Vo created on 2018-05-29
 * @author Loring
 */
@Getter
@Setter
public class BaseVo {
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改人
     */
    private String modifier;
    /**
     * 修改时间
     */
    private Date modifiedTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 有效状态
     */
    private Boolean isEnabled;
    /**
     * 数据校验码
     */
    private String checkCode;

    @Override
    public String toString()
    {
//        return ToStringBuilder.reflectionToString(this);
        return JSONObject.fromObject(this).toString();
    }

}
