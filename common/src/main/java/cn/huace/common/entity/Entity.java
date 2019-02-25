package cn.huace.common.entity;

import java.io.Serializable;

/**
 * 
 * @author Loy Fu qq群 540553957
 * @since 1.7
 * @version 1.0.0
 * 
 */
public interface Entity<ID extends Serializable> {

    public ID getId();

    public void setId(final ID id);

    public boolean isNew();

}