package cn.huace.common.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * Integer
 * Created by Loring on 20180523
 */
@Data
@MappedSuperclass
public abstract class IntBaseEntity extends IntAbstractEntity<Integer> {

    private String creator;

    private String modifier;

    @Column(updatable = false, name = "created_time")
    private Date createdTime;

    @Column(name = "modified_time")
    private Date modifiedTime;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

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
}
