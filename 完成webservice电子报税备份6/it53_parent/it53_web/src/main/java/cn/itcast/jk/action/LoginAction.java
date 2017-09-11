package cn.itcast.jk.action;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.jk.domain.User;
import cn.itcast.jk.util.SysConstant;
import cn.itcast.jk.util.UtilFuns;


public class LoginAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;



	//SSH传统登录方式
	public String login() throws Exception {
		
//		if(true){
//			String msg = "登录错误，请重新填写用户名密码!";
//			this.addActionError(msg);
//			throw new Exception(msg);
//		}
//		User user = new User(username, password);
//		User login = userService.login(user);
//		if (login != null) {
//			ActionContext.getContext().getValueStack().push(user);
//			session.put(SysConstant.CURRENT_USER_INFO, login);	//记录session
//			return SUCCESS;
//		}
//		return "login";
		
		//对于该shiro首先要保证有一个用户名用来查询密码
		if (UtilFuns.isEmpty(username)) {
			return "login";
		}
		
		Subject subject = SecurityUtils.getSubject();
		AuthenticationToken token =new UsernamePasswordToken(username, password);
		try {
			//提交令牌进行验证
			subject.login(token);
			//获取结果  底层会识别结果是否为空或者null
			User user = (User) subject.getPrincipal();
			//将用户信息放到回话session域中
			session.put(SysConstant.CURRENT_USER_INFO,user);
		} catch (Exception e) {
			System.out.println("用户名或者密码不正确，为查询到结果");
			super.putToContextStack("errorInfo","用户名或者密码不正确");
			return "login";
		}
			System.out.println("认证通过...");
			return "success";
	}
	
	//退出
	public String logout(){
		session.remove(SysConstant.CURRENT_USER_INFO);		//删除session
		
		return "logout";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

