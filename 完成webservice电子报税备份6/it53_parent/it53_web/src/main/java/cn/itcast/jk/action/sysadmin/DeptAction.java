package cn.itcast.jk.action.sysadmin;

import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Dept;
import cn.itcast.jk.service.DeptService;
import cn.itcast.jk.util.Page;

public class DeptAction extends BaseAction implements ModelDriven<Dept>{
	
	private Dept model = new Dept();
	@Override
	public Dept getModel() {
		return model;
	}
	
	private DeptService deptService;
			
	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}
	
	Page<Dept> page = new Page<Dept>();
	public Page<Dept> getPage() {
		return page;
	}
	public void setPage(Page<Dept> page) {
		this.page = page;
	}

	/**
	 * @return
	 * @throws Exception
	 * 分页显示
	 */
	public String list() throws Exception {
		//hql语句针对对象进行查询
		String hql = "from Dept where state=1";
		deptService.findPage(hql, page, Dept.class, null);
		//设置url
		page.setUrl("deptAction_list");
		
		//将page存入到栈顶
		System.out.println(page);
			ActionContext.getContext().getValueStack().push(page);;
		return "list";
	}
	
	public String toview() throws Exception{
		Dept dept = deptService.get(Dept.class, model.getId());
		ActionContext.getContext().getValueStack().push(dept);
		return "toview";
	}
	/**需要查询出上级部门供新增部门选择
	 * @return
	 * @throws Exception
	 */
	public String tocreate ()throws Exception{
		List<Dept> deptList = deptService.find("from Dept where state=1", Dept.class, null);
		ActionContext.getContext().put("deptList", deptList);
//		ActionContext.getContext().getValueStack().set("deptList", deptList);
		return "create";
	}
	/**插入数据  跳转到list页面
	 * @return
	 * @throws Exception
	 */
	
	public String toupdate() throws Exception {
		/*通过jDeptList.jsp提交过来id的数据回显到update.jsp页面*/
		String id = model.getId();
		Dept dept = deptService.get(Dept.class, id);
		ActionContext.getContext().getValueStack().push(dept);
		
		List<Dept> deptList = deptService.find("from Dept where state=1", Dept.class, null);
		//删除dept对象删除成功为true 删除失败为false，不会报错
		deptList.remove(dept);
		ActionContext.getContext().put("deptList", deptList);
		return "update";
	}
	
	/**
	 * @return 页面获取id 父部门对象 部门name 还缺少state   
	 * @throws Exception
	 */
	public String update() throws Exception {
		//根据id获取dept对象，再修改父部门 和部门名称
		Dept dept = deptService.get(Dept.class, model.getId());
		dept.setParent(model.getParent());
		dept.setDeptName(model.getDeptName());
		deptService.saveOrUpdate(dept);
		return "finish";
	}
	
	public String insert ()throws Exception{
		//激活部门
//		model.setId(null);
		deptService.saveOrUpdate(model);
//		查询显示
		return "finish";
		
		/*
		 * List arr = new ArrayList();
		arr.add(model.getDeptName());
		List<Dept> list = deptService.find("from Dept where deptName = ? and state =1",Dept.class, arr.toArray() );
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
		deptService.delete(Dept.class, ids);
		return "finish";
	}
	

}
