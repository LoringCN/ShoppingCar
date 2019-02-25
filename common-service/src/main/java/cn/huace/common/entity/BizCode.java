package cn.huace.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

/**
 * 字典实体类  create on 2018-05-23
 * @author Loring
 */
@Data
@Entity
@Table(name="biz_code")
public class BizCode implements Serializable {
    private static final long serialVersionUID = 3875576920801558430L;
    /**
     * 主键
     */
    @Id
    @Column(name = "code_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codeCode;
    /**
     * 外键指向主表biz_code_type
     */
    @ManyToOne(optional = false)//表之间inner join
    @JoinColumn(name = "code_type")
    private BizCodeType bizCodeType;
    /**
     * 字典名称
     */
    @Column(name = "code_name")
    private String codeName;
    /**
     * 字典编码
     */
    @Column(name = "item_code")
    private String itemCode;

    private String creator;

    private String modifier;

    @Column(updatable = false, name = "created_time")
    private Date createdTime;

    @Column(name = "modified_time")
    private Date modifiedTime;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    private String remark;

    @PrePersist
    public void beforeAdd(){
        modifiedTime=createdTime=new Date();
    }
    @PreUpdate
    public void beforeModified(){
        modifiedTime=new Date();
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreatedTime() {
        return createdTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }


}
