package cn.itcast.jk.action.cargo;

import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.domain.ContractProduct;
import cn.itcast.jk.domain.ExtCproduct;
import cn.itcast.jk.domain.Factory;
import cn.itcast.jk.service.ContractProductService;
import cn.itcast.jk.service.ContractService;
import cn.itcast.jk.service.ExtCproductService;
import cn.itcast.jk.service.FactoryService;
import cn.itcast.jk.util.Page;

public class ExtCproductAction extends BaseAction implements ModelDriven<ExtCproduct>{
	
	private ExtCproduct model = new ExtCproduct();
	@Override
	public ExtCproduct getModel() {
		return model;
	}
	
	private ExtCproductService extCproductService;
			
	public void setExtCproductService(ExtCproductService extCproductService) {
		this.extCproductService = extCproductService;
	}
	
	private ContractProductService contractProductService;
	
	private ContractService contractService;
	
	public void setContractProductService(ContractProductService contractProductService) {
		this.contractProductService = contractProductService;
	}
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}
	
	private FactoryService factoryService;

	public void setFactoryService(FactoryService factoryService) {
		this.factoryService = factoryService;
	}

	Page<ExtCproduct> page = new Page<ExtCproduct>();
	public Page<ExtCproduct> getPage() {
		return page;
	}
	public void setPage(Page<ExtCproduct> page) {
		this.page = page;
	}
	
	/** 对对应商品的附件进行操作   增删改  
	 * @return
	 * @throws Exception
	 */
	public String tocreate ()throws Exception{
		//需要将附件超链接的参数获取到并且传递到action传递到下一个页面
				String pid = model.getContractProduct().getId();
				String cid = model.getContractProduct().getContract().getId();
				
				String hql ="from ExtCproduct where contractProduct.id =? and contractProduct.contract.id=? ";
				//加载分页
				extCproductService.findPage(hql , page, ExtCproduct.class,new Object[]{pid,cid});
				page.setUrl("extCproductAction_tocreate");
				pushToValueStack(page);
				//记载工厂列表
				List<Factory> list = factoryService.find("from Factory where ctype='附件' and state=1", Factory.class, null);
				putToContextStack("factoryList", list);
				//去到操作指定商品对应的附件的页面
				return "create";
	}
	/**插入数据  跳转到list页面
	 * @return
	 * @throws Exception
	 */
	
	public String toupdate() throws Exception {
		/*通过jExtCproductList.jsp提交过来id的数据回显到update.jsp页面*/
		String id = model.getId();
		ExtCproduct ExtCproduct = extCproductService.get(ExtCproduct.class, id);
		ActionContext.getContext().getValueStack().push(ExtCproduct);
		
		//记载工厂列表
		List<Factory> list = factoryService.find("from Factory where ctype='附件' and state=1", Factory.class, null);
		putToContextStack("factoryList", list);
		
		return "update";
	}
	/**
	 * @return 
	 * @throws Exception
	 */
	public String update() throws Exception {
		ExtCproduct extCproduct = extCproductService.get(ExtCproduct.class, model.getId());		

		extCproduct.setFactory(model.getFactory());
		extCproduct.setProductNo(model.getProductNo());
        extCproduct.setProductImage(model.getProductImage());		
        extCproduct.setCnumber(model.getCnumber());
        extCproduct.setPackingUnit(model.getPackingUnit());
        extCproduct.setPrice(model.getPrice());
        extCproduct.setOrderNo(model.getOrderNo());
        extCproduct.setProductDesc(model.getProductDesc());
        extCproduct.setProductRequest(model.getProductRequest());
		
		extCproductService.saveOrUpdate(extCproduct);
		return tocreate();
	}
	
	public String insert ()throws Exception{
		//激活部门
//		model.setId(null);
		extCproductService.saveOrUpdate(model);
//		查询显示
		return tocreate();
		
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
		extCproductService.delete(ExtCproduct.class, ids);
		return tocreate();
	}
}
