package cn.huace.common.vo;

import cn.huace.common.Vo.BaseVo;
import lombok.Data;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * dictionnary config value object
 * created by Loring on 2018-07-16
 */
@Data
public class BizCodeTypeVo{
    private String codeType;
    private String typeName;
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
        return JSONObject.fromObject(this).toString();
    }
}
