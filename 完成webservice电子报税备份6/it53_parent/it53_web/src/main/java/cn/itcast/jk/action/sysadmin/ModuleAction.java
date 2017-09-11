package cn.itcast.jk.action.sysadmin;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Module;
import cn.itcast.jk.service.ModuleService;
import cn.itcast.jk.util.Page;

public class ModuleAction extends BaseAction implements ModelDriven<Module>{
	
	private Module model = new Module();
	@Override
	public Module getModel() {
		return model;
	}
	//从Spring引用获取配置的bean类需要
	private ModuleService moduleService;
	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}
	
	//分页组件的预先安置
	Page<Module> page = new Page<Module>();
	public Page<Module> getPage() {
		return page;
	}
	public void setPage(Page<Module> page) {
		this.page = page;
	}

	/**
	 * @return
	 * @throws Exception
	 * 分页显示
	 */
	public String list() throws Exception {
		//hql语句针对对象进行查询   
		String hql = "from Module ";
		moduleService.findPage(hql, page, Module.class, null);
		//设置url
		page.setUrl("moduleAction_list");
		
		//将page存入到栈顶
		System.out.println(page);
			ActionContext.getContext().getValueStack().push(page);;
		return "list";
	}
	
	public String toview() throws Exception{
		Module module = moduleService.get(Module.class, model.getId());
		ActionContext.getContext().getValueStack().push(module);
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
		Module module = moduleService.get(Module.class, id);
		ActionContext.getContext().getValueStack().push(module);
		return "update";
	}
	
	/**
	 * @return 页面获取id 父部门对象 部门name 还缺少state   
	 * @throws Exception
	 */
	public String update() throws Exception {
		
		Module module = moduleService.get(Module.class, model.getId());
		
		module.setName(model.getName());
		module.setLayerNum(model.getLayerNum());
		module.setCpermission(model.getCpermission());
		module.setCurl(model.getCurl());
		module.setCtype(model.getCtype());
		module.setState(model.getState());
		module.setBelong(model.getBelong());
		module.setCwhich(model.getCwhich());
		module.setRemark(model.getRemark());
		module.setOrderNo(model.getOrderNo());
		
		moduleService.saveOrUpdate(module);
		return "finish";
	}
	
	/** 
	 * @return
	 * @throws Exception
	 */
	public String insert ()throws Exception{
		moduleService.saveOrUpdate(model);
//		查询显示
		return "finish";
		
		/*
		 * List arr = new ArrayList();
		arr.add(model.getModuleName());
		List<Module> list = moduleService.find("from Module where moduleName = ? and state =1",Module.class, arr.toArray() );
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
		moduleService.delete(Module.class, ids);
		return "finish";
	}
}
