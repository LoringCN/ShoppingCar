package cn.huace.sys.bean;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/14.
 */
public class ShiroUser implements Serializable
{
    private static final long serialVersionUID = -1373760761780840081L;

    private Integer id;

    private String account;

    private String name;

    public ShiroUser(Integer id, String account, String name)
    {
        super();
        this.id = id;
        this.account = account;
        this.name = name;
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getAccount() {
        return account;
    }

    /**
     * 本函数输出将作为默认的<shiro:principal/>输出.
     */
    @Override
    public String toString()
    {
        return account;
    }

    /**
     * 重载hashCode,只计算loginName;
     */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(account);
    }

    /**
     * 重载equals,只计算loginName;
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
       ShiroUser other = (ShiroUser)obj;
        if (account == null)
        {
            if (other.account != null)
            {
                return false;
            }
        }
        else if (!account.equals(other.account))
        {
            return false;
        }
        return true;
    }

}