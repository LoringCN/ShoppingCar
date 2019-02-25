package cn.huace.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Loring on 20180523
 */
@MappedSuperclass
public class StrAbstractEntity<ID extends Serializable> implements Entity<ID>, Serializable {

    @Id
    @Column(name = "id")
    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
    @JsonIgnore
    public boolean isNew() {
        if (this.id == null || "".equals(this.id)) {
            this.setId(null);
            return true;
        }

        return false;
    }

}