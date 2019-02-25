package cn.huace.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 
 * @author Loy Fu qq群 540553957
 * @since 1.7
 * @version 1.0.0
 * 
 */
@MappedSuperclass
public class AbstractEntity<ID extends Serializable> implements Entity<ID>, Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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