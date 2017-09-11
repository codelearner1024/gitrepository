package cn.itcast.jk.action.cargo;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.ExportProduct;
import cn.itcast.jk.domain.Dept;
import cn.itcast.jk.domain.User;
import cn.itcast.jk.service.ExportProductService;
import cn.itcast.jk.service.DeptService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.SysConstant;
import cn.itcast.jk.util.UtilFuns;

public class ExportProductAction extends BaseAction implements ModelDriven<ExportProduct> {

	private ExportProduct model = new ExportProduct();

	@Override
	public ExportProduct getModel() {
		return model;
	}

	private ExportProductService ExportProductService;

	public void setExportProductService(ExportProductService ExportProductService) {
		this.ExportProductService = ExportProductService;
	}

	private DeptService deptService;

	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}

	Page<ExportProduct> page = new Page<ExportProduct>();

	public Page<ExportProduct> getPage() {
		return page;
	}

	public void setPage(Page<ExportProduct> page) {
		this.page = page;
	}

	// 创建空集合存储所有子id
	List<String> IDlist = new ArrayList();

	// 通过部门id获取所有下属部门id
	public void getSonID(String id) {
		// 子部门列表
		List<Dept> Deptlist = deptService.find("from Dept where parent.id=?", Dept.class, new Object[] { id });

		if (UtilFuns.isNotEmpty(Deptlist)) {
			// 遍历子部门
			for (Dept dept : Deptlist) {
				// 添加部门id
				IDlist.add(dept.getId());
				// 递归查询该子部门有没有子部门
				getSonID(dept.getId());
			}
			// 完成部门
		}

	}

	public String list() throws Exception {
		// hql语句针对对象进行查询商品没有状态
		String hql = "from ExportProduct where 1=1 ";
		// 添加细粒度权限控制，通过对不同的用户的degree进行区分
		// 获取session中的用户
		User user = (User) session.get(SysConstant.CURRENT_USER_INFO);
		Integer degree = user.getUserinfo().getDegree();
		if (degree == 4) {
			// 普通员工创建人id
			hql += "and createBy=" +"'"+ user.getId()+"'";
		}
		if (degree == 3) {
			// 经理:管理所属部门
			hql += "and createDept=" + "'"+user.getDept().getId()+"'";
		}
		if (degree == 2) {
			// 总经理：管理所属部门及下属部门当前部门以及所有下属部门
			// 用户的所在部门以及子部门的id
			// 用户所在部门id
			String id = user.getDept().getId();
			hql += "and createDept=" +"'"+ id +"'";
			getSonID(id);

			// 获取到 id数组遍历添加sql语句
			if (UtilFuns.isNotEmpty(IDlist)) {
				for (String string : IDlist) {
					hql += " or createDept=" +"'"+ string +"'";
				}
			}

		}
		if (degree == 1) {
			//可以跨部门跨小妹
		}
		if (degree == 0) {

		} 
		if (degree>4||degree<0) {
			return "list";
		}
		ExportProductService.findPage(hql, page, ExportProduct.class, null);
		// 设置url
		page.setUrl("ExportProductAction_list");

		// 将page存入到栈顶
		System.out.println(page);
		ActionContext.getContext().getValueStack().push(page);
		;
		return "list";
	}

	public String toview() throws Exception {
		ExportProduct ExportProduct = ExportProductService.get(ExportProduct.class, model.getId());
		ActionContext.getContext().getValueStack().push(ExportProduct);
		return "toview";
	}

	public String tocreate() throws Exception {
		List<ExportProduct> ExportProductList = ExportProductService.find("from ExportProduct", ExportProduct.class, null);
		ActionContext.getContext().put("ExportProductList", ExportProductList);
		// ActionContext.getContext().getValueStack().set("ExportProductList",
		// ExportProductList);
		return "create";
	}


	public String toupdate() throws Exception {
		/* 通过jExportProductList.jsp提交过来id的数据回显到update.jsp页面 */
		String id = model.getId();
		ExportProduct ExportProduct = ExportProductService.get(ExportProduct.class, id);
		ActionContext.getContext().getValueStack().push(ExportProduct);
		return "update";
	}

	/**
	 * @return 页面获取id 父部门对象 部门name 还缺少state
	 * @throws Exception
	 */
	public String update() throws Exception {
		// 根据id获取ExportProduct对象，再修改父部门 和部门名称
		ExportProduct ExportProduct = ExportProductService.get(ExportProduct.class, model.getId());

		// 2.只对jsp页面中修改的值进行保存

		ExportProductService.saveOrUpdate(ExportProduct);
		return "finish";
	}

	/**
	 * @return 新增购销合同
	 * @throws Exception
	 */
	public String insert() throws Exception {
		// 对新增合同添加createbY和createDept
		// 获取session中的用户
		User user = (User) session.get(SysConstant.CURRENT_USER_INFO);
		//获取用户的id
		String createByID = user.getId();
		//获取部门的id
		String createDeptID = user.getDept().getId();
		model.setCreateBy(createByID);
		model.setCreateDept(createDeptID);
		ExportProductService.saveOrUpdate(model);
		// 查询显示
		return "finish";
	}

	public String delete() throws Exception {
		String str = model.getId();
		String[] ids = str.split(", ");
		ExportProductService.delete(ExportProduct.class, ids);
		return "finish";
	}

	public String submit() throws Exception {
		ExportProduct exportProduct = ExportProductService.get(ExportProduct.class, model.getId());
		ExportProductService.saveOrUpdate(exportProduct);
		// 更改保存好状态后可以直接回到list
		return "finish";
	}

	public String cancel() throws Exception {
		ExportProduct exportProduct = ExportProductService.get(ExportProduct.class, model.getId());
		ExportProductService.saveOrUpdate(exportProduct);
		// 更改保存好状态后可以直接回到list
		return "finish";
	}
}
