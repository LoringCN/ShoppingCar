package cn.huace.sys.entity;

import cn.huace.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * <系统菜单实体类>
 * 
 * @author 陆小凤
 * @version [1.0, 2015年7月17日]
 */
@Data
@Entity
@Table(name = "system_menu")
public class SystemMenu extends BaseEntity
{
    private static final long serialVersionUID = 12131231231233L;

    private String name;

    private String ename;

    private String tname;

    private String link;

    private Integer sort;

    private String router;

    @JsonProperty("parentId")
    @Column(name = "parent_id")
    private Integer parentId;

    @Transient
    private List<SystemMenu> childMenus;

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
    

    
}