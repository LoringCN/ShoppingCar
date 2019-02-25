package cn.huace.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

/**
 * 字典定义实体类 create on 2018-05-23
 * @author Loring
 */
@Data
@Entity
@Table(name="biz_code_type")
public class BizCodeType implements Serializable {
        /**
     * 主键
     */
    @Id
    @Column(name = "code_type")
    private String codeType;

    @Column(name = "type_name")
    private String typeName;

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
