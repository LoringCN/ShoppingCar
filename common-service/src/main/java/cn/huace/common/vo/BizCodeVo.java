package cn.huace.common.vo;

import cn.huace.common.Vo.BaseVo;
import lombok.Data;
import net.sf.json.JSONObject;

import java.util.Date;

/**
 * dictionnary value object
 * created by Loring on 2018-07-16
 */
@Data
public class BizCodeVo{
    private Integer codeCode;
    private BizCodeTypeVo bizCodeTypeVo;
    private String codeName;
    private String itemCode;
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

    /**
     * 字典类编码
     */
    private String codeType;

    @Override
    public String toString()
    {
        return JSONObject.fromObject(this).toString();
    }

}
