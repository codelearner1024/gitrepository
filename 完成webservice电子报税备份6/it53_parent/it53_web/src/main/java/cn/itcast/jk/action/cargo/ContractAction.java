package cn.itcast.jk.action.cargo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.domain.ContractProduct;
import cn.itcast.jk.domain.Dept;
import cn.itcast.jk.domain.User;
import cn.itcast.jk.service.ContractProductService;
import cn.itcast.jk.service.ContractService;
import cn.itcast.jk.service.DeptService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.SysConstant;
import cn.itcast.jk.util.UtilFuns;

public class ContractAction extends BaseAction implements ModelDriven<Contract> {

	private Contract model = new Contract();
	private ContractProductService contractProductService;

	public void setContractProductService(ContractProductService contractProductService) {
		this.contractProductService = contractProductService;
	}

	@Override
	public Contract getModel() {
		return model;
	}

	private ContractService contractService;

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	private DeptService deptService;

	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}

	Page<Contract> page = new Page<Contract>();

	public Page<Contract> getPage() {
		return page;
	}

	public void setPage(Page<Contract> page) {
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

	/**
	 * @return
	 * @throws Exception
	 *             分页显示
	 */
	public String list() throws Exception {
		// hql语句针对对象进行查询商品没有状态
		String hql = "from Contract where 1=1 ";
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
		contractService.findPage(hql, page, Contract.class, null);
		// 设置url
		page.setUrl("contractAction_list");

		// 将page存入到栈顶
		System.out.println(page);
		ActionContext.getContext().getValueStack().push(page);
		;
		return "list";
	}

	public String toview() throws Exception {
		Contract Contract = contractService.get(Contract.class, model.getId());
		ActionContext.getContext().getValueStack().push(Contract);
		return "toview";
	}

	/**
	 * 去新增购销合同页面，需要准备
	 * 
	 * @return
	 * @throws Exception
	 */
	public String tocreate() throws Exception {
		List<Contract> ContractList = contractService.find("from Contract", Contract.class, null);
		ActionContext.getContext().put("ContractList", ContractList);
		// ActionContext.getContext().getValueStack().set("ContractList",
		// ContractList);
		return "create";
	}

	/**
	 * 更新订单将整个合同对象返回给数据就行
	 * 
	 * @return
	 * @throws Exception
	 */

	public String toupdate() throws Exception {
		/* 通过jContractList.jsp提交过来id的数据回显到update.jsp页面 */
		String id = model.getId();
		Contract contract = contractService.get(Contract.class, id);
		ActionContext.getContext().getValueStack().push(contract);
		return "update";
	}

	/**
	 * @return 页面获取id 父部门对象 部门name 还缺少state
	 * @throws Exception
	 */
	public String update() throws Exception {
		// 根据id获取Contract对象，再修改父部门 和部门名称
		Contract contract = contractService.get(Contract.class, model.getId());

		// 2.只对jsp页面中修改的值进行保存
		contract.setCustomName(model.getCustomName());
		contract.setPrintStyle(model.getPrintStyle());
		contract.setContractNo(model.getContractNo());
		contract.setOfferor(model.getOfferor());
		contract.setInputBy(model.getInputBy());
		contract.setCheckBy(model.getCheckBy());
		contract.setInspector(model.getInspector());
		contract.setSigningDate(model.getSigningDate());
		contract.setImportNum(model.getImportNum());
		contract.setShipTime(model.getShipTime());
		contract.setTradeTerms(model.getTradeTerms());
		contract.setDeliveryPeriod(model.getDeliveryPeriod());
		contract.setCrequest(model.getCrequest());
		contract.setRemark(model.getRemark());

		contractService.saveOrUpdate(contract);
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
		contractService.saveOrUpdate(model);
		// 查询显示
		return "finish";

		/*
		 * List arr = new ArrayList(); arr.add(model.getContractName());
		 * List<Contract> list = ContractService.find(
		 * "from Contract where ContractName = ? and state =1",Contract.class,
		 * arr.toArray() ); if(StringUtils.isBlank(list.get(0).getId())){
		 * model.setId("asdf"); }else{ System.out.println("异常"); }
		 * 
		 */
	}

	/**
	 * 删除合同 需要删除商品表，以及辅料表
	 * 
	 * @return 删除完成后回到清单页面
	 * @throws Exception
	 * @param 页面传递过来的是部门的id
	 *            struts2框架如果传递的是多个name=id的数据，可以是数组（通过属性驱动），也可能是“， ”字符串(模型驱动)
	 */

	public String delete() throws Exception {
		String str = model.getId();
		String[] ids = str.split(", ");
		contractService.delete(Contract.class, ids);
		return "finish";
	}

	/*
	 * 提交的时候，将合同状态改为已上报
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public String submit() throws Exception {
		Contract contract = contractService.get(Contract.class, model.getId());
		contract.setState(1);
		contractService.saveOrUpdate(contract);
		// 更改保存好状态后可以直接回到list
		return "finish";
	}

	public String cancel() throws Exception {
		Contract contract = contractService.get(Contract.class, model.getId());
		contract.setState(0);
		contractService.saveOrUpdate(contract);
		// 更改保存好状态后可以直接回到list
		return "finish";
	}
	
	/**
	 * @return 打印：自动下载
	 * @throws Exception
	 */
	public void print() throws Exception {
		
		HttpServletResponse response = ServletActionContext.getResponse();
		Contract contract = contractService.get(Contract.class, model.getId());
		String path = ServletActionContext.getServletContext().getRealPath(File.separator);
		ContractPrint contractPrint = new ContractPrint();
		//获取ContractProduct集合，并且按照工厂顺序排在一起
		List<ContractProduct> oList = contractProductService.find("from ContractProduct where contract.id=? order by factoryName",null, new Object[]{model.getId()});
		contractPrint.print(contract, oList , path, response);
	}
}
