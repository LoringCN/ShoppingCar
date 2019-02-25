package cn.huace.goods.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 主题分类
 * Created by yld on 2017/7/28.
 */
@Data
@Entity
@Table(name = "theme_type")
public class ThemeType extends BaseEntity{
    private static final long serialVersionUID = -7479148920441831482L;

    /**
     * 主题分类名
     */
    @Column(name = "name",length = 50)
    private String name;

    /**
     * 删除标志： 1 - 正常， -1 - 删除
     */
    @Column(name = "flag",length = 2)
    private String flag;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
