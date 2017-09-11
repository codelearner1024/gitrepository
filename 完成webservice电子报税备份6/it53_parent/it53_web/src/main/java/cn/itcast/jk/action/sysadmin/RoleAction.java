package cn.itcast.jk.action.sysadmin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Module;
import cn.itcast.jk.domain.Role;
import cn.itcast.jk.service.ModuleService;
import cn.itcast.jk.service.RoleService;
import cn.itcast.jk.util.Page;

public class RoleAction extends BaseAction implements ModelDriven<Role>{
	
	private Role model = new Role();
	@Override
	public Role getModel() {
		return model;
	}
	//从Spring引用获取配置的bean类需要
	private RoleService roleService;
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}
	
	private ModuleService moduleService;
	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}
	//分页组件的预先安置
	Page<Role> page = new Page<Role>();
	public Page<Role> getPage() {
		return page;
	}
	public void setPage(Page<Role> page) {
		this.page = page;
	}

	/**
	 * @return
	 * @throws Exception
	 * 分页显示
	 */
	public String list() throws Exception {
		//hql语句针对对象进行查询
		String hql = "from Role ";
		roleService.findPage(hql, page, Role.class, null);
		//设置url
		page.setUrl("roleAction_list");
		
		//将page存入到栈顶
		System.out.println(page);
			ActionContext.getContext().getValueStack().push(page);;
		return "list";
	}
	
	public String toview() throws Exception{
		Role role = roleService.get(Role.class, model.getId());
		ActionContext.getContext().getValueStack().push(role);
		return "toview";
	}
	/**需要部门列表  用户列表来选择上级
	 * @return
	 * @throws Exception
	 */
	public String tocreate ()throws Exception{
		return "create";
	}
	/**
	 * @return 因为要更新所以回显用户名 所在部门  
	 * @throws Exception
	 */
	
	public String toupdate() throws Exception {
		String id = model.getId();
		Role role = roleService.get(Role.class, id);
		ActionContext.getContext().getValueStack().push(role);
		return "update";
	}
	
	/**
	 * @return 页面获取id 父部门对象 部门name 还缺少state   
	 * @throws Exception
	 */
	public String update() throws Exception {
		Role role = roleService.get(Role.class, model.getId());
		role.setName(model.getName());
		role.setRemark(model.getRemark());
		roleService.saveOrUpdate(role);
		return "finish";
	}
	
	/** 
	 * @return
	 * @throws Exception
	 */
	public String insert ()throws Exception{
		roleService.saveOrUpdate(model);
//		查询显示
		return "finish";
		
		/*
		 * List arr = new ArrayList();
		arr.add(model.getRoleName());
		List<Role> list = roleService.find("from Role where roleName = ? and state =1",Role.class, arr.toArray() );
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
		roleService.delete(Role.class, ids);
		return "finish";
	}
	

	/**
	 * @return 对角色进行访问模块权限的选择  获取用户信息   加载所有的模块 
	 * @throws Exception
	 */
	public String tomodule() throws Exception {
		//用户对象放到栈顶
		Role role = roleService.get(Role.class, model.getId());
		pushToValueStack(role);
		//用户模块ids拼成字符串交给上下文栈
		Set<Module> modules = role.getModules();
		StringBuilder roleModuleStr = new StringBuilder();
		for (Module module : modules) {
			roleModuleStr.append(module.getId()+",");
		}
		putToContextStack("roleModuleStr", roleModuleStr.toString());
		//模块列表放到上下文栈
		List<Module> moduleList = moduleService.find("from Module", Module.class, null);
		putToContextStack("moduleList", moduleList);
		return "tomodule";
	}
	
	/**
	 * @return 获取到页面传递过来的选中的模块  然后将模块存入到role对象中，
	 * 保存角色对象即可
	 * @throws Exception
	 */
	public String module() throws Exception {
		Role role = roleService.get(Role.class, model.getId());
		String moduleIds = ServletActionContext.getRequest().getParameter("moduleIds");
		String[] ids = moduleIds.split(",");
		System.out.println(ids);
		HashSet<Module> set = new HashSet<>();
		for (String id : ids) {
			set.add(moduleService.get(Module.class, id));
		}
		role.setModules(set);
		roleService.saveOrUpdate(role);
		return "finish";
	}
	
	/**向页面发送ajax请求
	 * @throws Exception
	 * 			
	 */
	public void jsonTreeNodes() throws Exception {
				//用户对象放到栈顶
				Role role = roleService.get(Role.class, model.getId());
				//用户模块ids拼成字符串交给上下文栈
				Set<Module> modules = role.getModules();
				/*StringBuilder roleModuleStr = new StringBuilder();
				for (Module module : modules) {
					roleModuleStr.append(module.getCpermission()+",");
				}
				String roleModules = roleModuleStr.toString();
				*/
				List<Module> moduleList = moduleService.find("from Module", Module.class, null);
				//拼接字符串：遍历模块列表 添加所有的模块，中间判断该用户已添加的模块进行check赋值
		/*					[
				  				{ "id":"23", "pId":"2", "name":"系统首页"},
								{ "id":"1", "pId":"0", "name":"随意勾选 1", "checked":"true", "open":"true"}
							]
		*/
				StringBuilder sb = new StringBuilder();
				//区分最后是否要加，
				int size = moduleList.size();
				int i = 0;
				sb.append("[");
				for (Module module : moduleList) {
					++i;
					sb.append("{");
					sb.append("\"id\":\"");
					sb.append(module.getId());
					sb.append("\", \"pId\":\"");
					sb.append(module.getParentId());
					sb.append("\", \"name\":\"");
					sb.append(module.getCpermission());
					//判断是否需要勾选
					for (Module m : modules) {
						if (m.getCpermission().equals(module.getCpermission())) {
							//需要添加勾选
							sb.append("\", \"checked\":\"true");
						}
					}
					sb.append("\"}");
					if (i!=size) {
					sb.append(",");	
					}
				}
				sb.append("]");
				//通过response响应将字符串数据返回给ajax请求
				HttpServletResponse response = ServletActionContext.getResponse();
				response.setContentType("application/json;charset=UTF-8");
				System.out.println(sb.toString());
				response.getWriter().print(sb.toString());
	}
}
