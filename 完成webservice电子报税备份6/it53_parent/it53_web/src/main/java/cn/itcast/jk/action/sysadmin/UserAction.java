package cn.itcast.jk.action.sysadmin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Dept;
import cn.itcast.jk.domain.Role;
import cn.itcast.jk.domain.User;
import cn.itcast.jk.exception.SysException;
import cn.itcast.jk.service.DeptService;
import cn.itcast.jk.service.RoleService;
import cn.itcast.jk.service.UserService;
import cn.itcast.jk.util.Page;

public class UserAction extends BaseAction implements ModelDriven<User>{
	
	private User model = new User();
	@Override
	public User getModel() {
		return model;
	}
	//从Spring引用获取配置的bean类需要
	private UserService userService;
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	private DeptService deptService;
	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}
	
	private RoleService roleService;
	
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}
	//分页组件的预先安置
	Page<User> page = new Page<User>();
	public Page<User> getPage() {
		return page;
	}
	public void setPage(Page<User> page) {
		this.page = page;
	}

	/**
	 * @return
	 * @throws Exception
	 * 分页显示
	 */
	public String list() throws Exception {
		//hql语句针对对象进行查询
		String hql = "from User where state=1";
		userService.findPage(hql, page, User.class, null);
		//设置url
		page.setUrl("userAction_list");
		
		//将page存入到栈顶
		System.out.println(page);
			ActionContext.getContext().getValueStack().push(page);;
		return "list";
	}
	
	public String toview() throws Exception{
		User user = userService.get(User.class, model.getId());
		ActionContext.getContext().getValueStack().push(user);
		return "toview";
	}
	/**需要部门列表  用户列表来选择上级
	 * @return
	 * @throws Exception
	 */
	public String tocreate ()throws Exception{
		List<User> userList = userService.find("from User where state=1", User.class, null);
		List<Dept> deptList = deptService.find("from Dept where state=1", Dept.class, null);
		
		ActionContext.getContext().put("userList", userList);
		ActionContext.getContext().put("deptList", deptList);
//		ActionContext.getContext().getValueStack().set("userList", userList);
		return "create";
	}
	/**
	 * @return 因为要更新所以回显用户名 所在部门  
	 * @throws Exception
	 */
	
	public String toupdate() throws Exception {
//		List<User> userList = userService.find("from User where state=1", User.class, null);
		List<Dept> deptList = deptService.find("from Dept where state=1", Dept.class, null);
		ActionContext.getContext().getValueStack().set("deptList", deptList);
		/*通过jUserList.jsp提交过来id的数据回显到update.jsp页面*/
		String id = model.getId();
		User user = userService.get(User.class, id);
		ActionContext.getContext().getValueStack().push(user);
		return "update";
	}
	
	/**
	 * @return 页面获取id 父部门对象 部门name 还缺少state   
	 * @throws Exception
	 */
	public String update() throws Exception {
		//根据id获取user对象，再修改父部门 和部门名称
		User user = userService.get(User.class, model.getId());
		user.setUserName(model.getUserName());
		user.setState(model.getState());
		//外键是通过回传主键获取对象的
		user.setDept(model.getDept());
		userService.saveOrUpdate(user);
		return "finish";
	}
	
	/**页面获取大量信息已经直接被封装到model中了（struts中将数据封装到domain，hibernate中将domain保存或者从数据库取出）
	 * @return
	 * @throws Exception
	 */
	public String insert ()throws Exception{
		userService.saveOrUpdate(model);
//		查询显示
		return "finish";
		
		/*
		 * List arr = new ArrayList();
		arr.add(model.getUserName());
		List<User> list = userService.find("from User where userName = ? and state =1",User.class, arr.toArray() );
		if(StringUtils.isBlank(list.get(0).getId())){
			model.setId("asdf");
		}else{
			System.out.println("异常");
		}
		 * 
		 * */
	}
	/**删除部门  
	 * 	有子部门要连子部门一起删除，不然子部门查询的时候会显示已经被删除的父部门
	 * 通过父部门的id找到关联到该id的所有子部门进行删除	
	 * @return  删除完成后回到部门清单页面
	 * @throws Exception
	 * @param 页面传递过来的是部门的id struts2框架如果传递的是多个name=id的数据，可以是数组（通过属性驱动），也可能是“， ”字符串(模型驱动)
	 */
	
	public String delete() throws Exception {
		String str = model.getId();
		String[] ids = str.split(", ");
		userService.delete(User.class, ids);
		return "finish";
	}
	
	/**
	 * @return 页面需要用户的信息  角色列表  
	 * @throws Exception
	 */
	public String torole() throws Exception {
		
		//将当前用户信息交给页面
		User user =null;
		try {
			user = userService.get(User.class, model.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SysException("用户未选中，请选择用户");
		}
		super.pushToValueStack(user);
		
		//将当前用户的所有角色拼接成字符串交给页面
		Set<Role> roles = user.getRoles();
		StringBuilder sb = new StringBuilder();
		for (Role role : roles) {
			sb.append(role.getName());
		}
		String roleStr = sb.toString();
		System.out.println(roleStr);
		putToContextStack("roleStr", roleStr);
		
		//将所有角色列表交给页面
		List<Role> roleList = roleService.find("from Role", Role.class, null);
		putToContextStack("roleList", roleList);
		
		return "torole";
	}
	
	
	/**
	 * @return 需要将用户的角色进行保存 
	 * @throws Exception
	 */
	public String role() throws Exception {
		//获取角色id
		User user = userService.get(User.class, model.getId());
		Set<Role> roles = new HashSet<>();

		String[] roleIds = (String[]) ServletActionContext.getRequest().getParameterValues("roleIds");
		//Object object = request.get("roleIds");
		System.out.println(roleIds);
		for (String id : roleIds) {
			Role role = roleService.get(Role.class, id);
			roles.add(role);
		}
		user.setRoles(roles);
		//获取Role对象存入到set中
		//用户id
		userService.saveOrUpdate(user);
		return "finish";
	}
}
