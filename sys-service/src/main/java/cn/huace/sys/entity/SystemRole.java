package cn.huace.sys.entity;


import cn.huace.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * <角色实体类>
 * 
 * @author 陆小凤
 * @version [1.0, 2015年7月17日]
 */
@Data
@Entity
@Table(name = "system_role")
public class SystemRole extends BaseEntity
{
    /**
     * 注释
     */
    private static final long serialVersionUID = 3260110878096913809L;

    private String name;

    private String remark;

    @Column(name = "use_flag")
    private Boolean useFlag=true;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "system_role_menu_rel", joinColumns = {
            @JoinColumn(name = "role_id") }, inverseJoinColumns = {
            @JoinColumn(name = "menu_id") })
    //@Cache(region = "all", usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<SystemMenu> menuSet = new HashSet<SystemMenu>();

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}