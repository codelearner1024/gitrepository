package cn.itcast.jk.action.cargo;

import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.util.CreateIfNull;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.domain.ContractProduct;
import cn.itcast.jk.domain.Factory;
import cn.itcast.jk.service.ContractProductService;
import cn.itcast.jk.service.ContractService;
import cn.itcast.jk.service.FactoryService;
import cn.itcast.jk.util.Page;

public class ContractProductAction extends BaseAction implements ModelDriven<ContractProduct>{
	
	private ContractProduct model = new ContractProduct();
	@Override
	public ContractProduct getModel() {
		return model;
	}
	
	private ContractProductService contractProductService;
			
	public void setContractProductService(ContractProductService contractProductService) {
		this.contractProductService = contractProductService;
	}
	private FactoryService factoryService;
	

	public void setFactoryService(FactoryService factoryService) {
		this.factoryService = factoryService;
	}

	Page<ContractProduct> page = new Page<ContractProduct>();
	public Page<ContractProduct> getPage() {
		return page;
	}
	public void setPage(Page<ContractProduct> page) {
		this.page = page;
	}

	
	/**指定的合同添加或者修改货物      页面传递过来合同的id 通过合同的id加载相应页面（需要传递的数据）
	 * @return
	 * @throws Exception
	 */
	public String tocreate ()throws Exception{
		//需要将货物超链接的参数获取到并且传递到经过action传递到下一个页面
		String id = model.getContract().getId();
		String hql ="from ContractProduct where contract.id =?";
		//加载分页
		contractProductService.findPage(hql , page, ContractProduct.class,new Object[]{id});
		page.setUrl("contractProductAction_tocreate");
		pushToValueStack(page);
		//加载货物工厂列表  
		List<Factory> list = factoryService.find("from Factory where ctype='货物'and state=1", Factory.class, null);
		putToContextStack("factoryList", list);
		//去到新增页面
		return "create";
	}
	/**进入到货物修改页面
	 * 加载工厂列表，
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public String toupdate() throws Exception {
		String id = model.getId();
		ContractProduct contractProduct = contractProductService.get(ContractProduct.class, id);
		pushToValueStack(contractProduct);
		//记载工厂列表
		List<Factory> list = factoryService.find("from Factory where ctype='货物'and state=1", Factory.class, null);
		putToContextStack("factoryList", list);
		;
		return "update";
	}
	
	/**从修改订单已有商品页面返回到订单商品查询页面
	 * 将修改的商品进行保存  其中从页面获取的amount为旧的数据
	 * @return 
	 * @throws Exception
	 */
	public String update() throws Exception {
		ContractProduct contractProduct = contractProductService.get(ContractProduct.class, model.getId());
		// 设置
		contractProduct.setFactory(model.getFactory());
		contractProduct.setFactoryName(model.getFactoryName());
		contractProduct.setProductNo(model.getProductNo());
		contractProduct.setProductImage(model.getProductImage());
		contractProduct.setCnumber(model.getCnumber());
		contractProduct.setAmount(model.getAmount());
		contractProduct.setPackingUnit(model.getPackingUnit());
		contractProduct.setLoadingRate(model.getLoadingRate());
		contractProduct.setBoxNum(model.getBoxNum());
		contractProduct.setPrice(model.getPrice());
		contractProduct.setOrderNo(model.getOrderNo());
		contractProduct.setProductDesc(model.getProductDesc());
		contractProduct.setProductRequest(model.getProductRequest());
		// 更新
		contractProductService.saveOrUpdate(contractProduct);
		return tocreate();
	}
	
	/**新增商品
	 * @return
	 * @throws Exception
	 */
	public String insert ()throws Exception{
		//获取页面的商品对象，进行保存，保存的时候要分散计算合同总金额
		contractProductService.saveOrUpdate(model);
		//重新回到货物管理页面（create.jsp）
		return tocreate();
	}
	/**
	 * @return  
	 * @throws Exception
	 * @param 
	 */
	public String delete() throws Exception {
		String id = model.getId();
		contractProductService.deleteById(ContractProduct.class, id);
		return tocreate();
	}
}
