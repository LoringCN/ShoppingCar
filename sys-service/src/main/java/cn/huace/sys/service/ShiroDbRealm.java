package cn.huace.sys.service;


import cn.huace.common.utils.Encodes;
import cn.huace.sys.bean.MySimpleByteSource;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemMenu;
import cn.huace.sys.entity.SystemUser;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class ShiroDbRealm extends AuthorizingRealm
{
    @Autowired
    protected AccountService accountService;

    @Autowired
    protected SystemUserService systemUserService;

    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
        throws AuthenticationException
    {
        UsernamePasswordToken token = (UsernamePasswordToken)authcToken;
        SystemUser user = accountService.findUserByAccount(token.getUsername());
        if (user != null)
        {
//            byte[] salt = Encodes.decodeHex(user.getSalt());
//            return new SimpleAuthenticationInfo(new ShiroUser(user.getId(), user.getAccount(), user.getName()),
//                user.getPassword(), ByteSource.Util.bytes(salt), user.getAccount());
            byte[] salt = Encodes.decodeHex(user.getSalt());
            ShiroUser shiroUser=new ShiroUser(user.getId(), user.getAccount(), user.getName());
            //设置用户session
//            Session session = SecurityUtils.getSubject().getSession();
//            session.setAttribute("user", user);
            return new SimpleAuthenticationInfo(shiroUser,user.getPassword(), new MySimpleByteSource(salt), getName());


//            AuthenticationInfo authcInfo =
//
//                    new SimpleAuthenticationInfo(user.getName(), user.getPassword(),user.getAccount());
//            return authcInfo;
        }
        else
        {
            return null;
        }
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    {
        ShiroUser shiroUser = (ShiroUser)principals.getPrimaryPrincipal();
        SystemUser user = accountService.findUserByAccount(shiroUser.getAccount());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        List<String> list=new ArrayList<String>();
        if(user.isSuperUser()){
            list.add("*");
        }else{
          List<SystemMenu> systemMenuList=  systemUserService.getMenuByUserId(user.getId());
          for(SystemMenu systemMenu:systemMenuList){
              list.add(systemMenu.getLink());
          }
        }
        info.addStringPermissions(list);
        return info;
    }

    /**
     * 设定Password校验的Hash算法与迭代次数.
     */
    @SuppressWarnings("static-access")
    @PostConstruct
    public void initCredentialsMatcher()
    {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(AccountService.HASH_ALGORITHM);
        matcher.setHashIterations(AccountService.HASH_INTERATIONS);
        setCredentialsMatcher(matcher);
//        setCredentialsMatcher(new HashedCredentialsMatcher("md5"));
    }

}

