package cn.itcast.jk.shiro;

import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import cn.itcast.jk.domain.Module;
import cn.itcast.jk.domain.Role;
import cn.itcast.jk.domain.User;
import cn.itcast.jk.service.UserService;
import cn.itcast.jk.util.UtilFuns;

public class AuthRealm extends AuthorizingRealm {
	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private CustomCredentialsMatcher credentialsMatcher;

	public void setCredentialsMatcher(CustomCredentialsMatcher credentialsMatcher) {
		this.credentialsMatcher = credentialsMatcher;
	}

	/*
	 * 业务逻辑是判断用户是否有访问相应模块（菜单）的权限，有：显示；没有：不显示
	 * 
	 * 当页面加载到shiro标签的时候会自动请求进行是否授权判断授权 根据当前登录用户获取到用户的
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("调用了授权方法");
		// 获取认证的时候封装到SimpleAuthenticationInfo中的用户信息
		User user = (User) principals.getPrimaryPrincipal();
		// 获取用户的角色---》获取角色对应
		// 的模块获取模块的名字最终获取到Module中的cpermission属性和和shiro标签的name进行比对
		Set<Role> roles = user.getRoles();
		// 声明一个需要返回的对象
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		
		for (Role role : roles) {   //多个role可能有重复的模块，但是不影响判断
			Set<Module> modules = role.getModules();
			for (Module module : modules) {
				//将模块名存入到shiro
				info.addStringPermission(module.getCpermission());
			}
		}
		return info;
	}

	/*
	 * 就是获取到页面的信息，从数据库查询有没有对应的对象返回给
	 * 
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("调用了认证方法");
		// 获取令牌里面的数据来调用matcher进行比较
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		List<User> list = userService.find("from User where userName=?", User.class,
				new Object[] { usernamePasswordToken.getUsername() });
		// list有对象代表该用户名对应了对象
		if (UtilFuns.isNotEmpty(list)) {
			User user = list.get(0);

			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
			System.out.println("(UsernamePasswordToken)token方法中的this.getName()：" + this.getName());
			return info;
		}
		return null;
	}

}
