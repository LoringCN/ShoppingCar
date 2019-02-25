package cn.huace.common.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Persistent;


import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/7.
 */

@MappedSuperclass
public abstract class BaseEntity extends AbstractEntity<Integer> {

    @Column(updatable = false, name = "created_time")
    private Date createdTime;

    @Column( name = "modified_time")
    private Date modifiedTime;

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

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getModifiedTime() {
        return modifiedTime;
    }


    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
