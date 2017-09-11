package cn.itcast.jk.action.cargo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.export.webservice.IEpService;
import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.domain.Dept;
import cn.itcast.jk.domain.Export;
import cn.itcast.jk.domain.ExportProduct;
import cn.itcast.jk.domain.User;
import cn.itcast.jk.service.ContractService;
import cn.itcast.jk.service.DeptService;
import cn.itcast.jk.service.ExportProductService;
import cn.itcast.jk.service.ExportService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.SysConstant;
import cn.itcast.jk.util.UtilFuns;

public class ExportAction extends BaseAction implements ModelDriven<Export> {
	// 属性驱动获取所有的报运商品的信息
	private String[] mr_id;
	private String[] mr_changed;
	private Integer[] mr_orderNo;

	public String[] getMr_id() {
		return mr_id;
	}

	public void setMr_id(String[] mr_id) {
		this.mr_id = mr_id;
	}

	public String[] getMr_changed() {
		return mr_changed;
	}

	public void setMr_changed(String[] mr_changed) {
		this.mr_changed = mr_changed;
	}

	public Integer[] getMr_orderNo() {
		return mr_orderNo;
	}

	public void setMr_orderNo(Integer[] mr_orderNo) {
		this.mr_orderNo = mr_orderNo;
	}

	public Integer[] getMr_cnumber() {
		return mr_cnumber;
	}

	public void setMr_cnumber(Integer[] mr_cnumber) {
		this.mr_cnumber = mr_cnumber;
	}

	public Double[] getMr_grossWeight() {
		return mr_grossWeight;
	}

	public void setMr_grossWeight(Double[] mr_grossWeight) {
		this.mr_grossWeight = mr_grossWeight;
	}

	public Double[] getMr_netWeight() {
		return mr_netWeight;
	}

	public void setMr_netWeight(Double[] mr_netWeight) {
		this.mr_netWeight = mr_netWeight;
	}

	public Double[] getMr_sizeLength() {
		return mr_sizeLength;
	}

	public void setMr_sizeLength(Double[] mr_sizeLength) {
		this.mr_sizeLength = mr_sizeLength;
	}

	public Double[] getMr_sizeWidth() {
		return mr_sizeWidth;
	}

	public void setMr_sizeWidth(Double[] mr_sizeWidth) {
		this.mr_sizeWidth = mr_sizeWidth;
	}

	public Double[] getMr_sizeHeight() {
		return mr_sizeHeight;
	}

	public void setMr_sizeHeight(Double[] mr_sizeHeight) {
		this.mr_sizeHeight = mr_sizeHeight;
	}

	public Double[] getMr_exPrice() {
		return mr_exPrice;
	}

	public void setMr_exPrice(Double[] mr_exPrice) {
		this.mr_exPrice = mr_exPrice;
	}

	public Double[] getMr_tax() {
		return mr_tax;
	}

	public void setMr_tax(Double[] mr_tax) {
		this.mr_tax = mr_tax;
	}

	private Integer[] mr_cnumber;
	private Double[] mr_grossWeight;
	private Double[] mr_netWeight;
	private Double[] mr_sizeLength;
	private Double[] mr_sizeWidth;
	private Double[] mr_sizeHeight;
	private Double[] mr_exPrice;
	private Double[] mr_tax;

	private Export model = new Export();

	@Override
	public Export getModel() {
		return model;
	}

	private ContractService contractService;
	private ExportService exportService;
	private DeptService deptService;
	private ExportProductService exportProductService;
	private IEpService epService;

	public void setEpService(IEpService epService) {
		this.epService = epService;
	}

	public void setExportProductService(ExportProductService exportProductService) {
		this.exportProductService = exportProductService;
	}

	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	Page page = new Page();

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
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
	 * 进入到出口报运界面，首先进入list页面，显示的是购销合同的列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public String contractList() throws Exception {
		// 查询状态为1的订单
		String hql = "from Contract where state=1 ";
		// 添加细粒度权限控制，通过对不同的用户的degree进行区分
		// 获取session中的用户
		User user = (User) session.get(SysConstant.CURRENT_USER_INFO);
		Integer degree = user.getUserinfo().getDegree();
		if (degree == 4) {
			// 普通员工创建人id
			hql += "and createBy=" + "'" + user.getId() + "'";
		}
		if (degree == 3) {
			// 经理:管理所属部门
			hql += "and createDept=" + "'" + user.getDept().getId() + "'";
		}
		if (degree == 2) {
			// 总经理：管理所属部门及下属部门当前部门以及所有下属部门
			// 用户的所在部门以及子部门的id
			// 用户所在部门id
			String id = user.getDept().getId();
			hql += "and createDept=" + "'" + id + "'";
			getSonID(id);

			// 获取到 id数组遍历添加sql语句
			if (UtilFuns.isNotEmpty(IDlist)) {
				for (String string : IDlist) {
					hql += " or createDept=" + "'" + string + "'";
				}
			}

		}
		if (degree == 1) {
			// 可以跨部门跨小妹
		}
		if (degree == 0) {

		}
		if (degree > 4 || degree < 0) {
			return "list";
		}
		contractService.findPage(hql, page, Contract.class, null);
		// 设置url
		page.setUrl("ExportAction_list");

		// 将page存入到栈顶
		System.out.println(page);
		ActionContext.getContext().getValueStack().push(page);
		;
		return "contractList";
	}

	/**
	 * 进入到JExportView.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	public String toview() throws Exception {
		Export export = exportService.get(Export.class, model.getId());
		ActionContext.getContext().getValueStack().push(export);
		return "toview";
	}

	/**
	 * 通过购销合同生成报运单
	 * 
	 * @return
	 * @throws Exception
	 */
	// 将购销合同id获取到传递到create.jsp中

	public String tocreate() throws Exception {
		// id自动从购销合同列表中传递到报运单列表中
		return "create";
	}

	/**
	 * @return 报运单的更新分成两块，运单的内容更新，以及报运商品的更新
	 * @throws Exception
	 */
	public String toupdate() throws Exception {
		/* 通过jExportList.jsp提交过来id的数据回显到update.jsp页面 */
		String id = model.getId();
		Export export = exportService.get(Export.class, id);
		ActionContext.getContext().getValueStack().push(export);

		return "update";
	}

	/**
	 * @return 返回ajax请求数据
	 * @throws Exception
	 */
	public void mRecordData() throws Exception {
		// 获取ajax请求传过来的参数id
		String eid = ServletActionContext.getRequest().getParameter("id");

		Export export = exportService.get(Export.class, eid);
		;
		// 报运商品传递到页面
		// "id", "productNo", "cnumber", "grossWeight", "netWeight",
		// "sizeLength", "sizeWidth", "sizeHeight", "exPrice", "tax"
		Set<ExportProduct> exportProducts = export.getExportProducts();

		ArrayList<Map<String, Object>> aList = new ArrayList<>();
		for (ExportProduct ep : exportProducts) {
			Map map = new HashMap();
			map.put("id", ep.getId());
			map.put("productNo", UtilFuns.convertNull(ep.getProductNo()));

			map.put("cnumber", UtilFuns.convertNull(ep.getCnumber()));
			map.put("grossWeight", UtilFuns.convertNull(ep.getGrossWeight()));
			map.put("netWeight", UtilFuns.convertNull(ep.getNetWeight()));
			map.put("sizeLength", UtilFuns.convertNull(ep.getSizeLength()));
			map.put("sizeWidth", UtilFuns.convertNull(ep.getSizeWidth()));
			map.put("sizeHeight", UtilFuns.convertNull(ep.getSizeHeight()));
			map.put("exPrice", UtilFuns.convertNull(ep.getExPrice()));
			map.put("tax", UtilFuns.convertNull(ep.getTax()));

			aList.add(map);
		}

		// 响应
		String jsonString = JSON.toJSONString(aList);
		System.out.println(jsonString);
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码等
		// 设置返回的json格式
		response.setContentType("application/json;charset=UTF-8");// json串的mime类型：application/json
		response.setHeader("Cache-Control", "no-cache");// 设置响应结束时，不使用缓存
		response.getWriter().print(jsonString);

	}

	/**
	 * @return 页面获取id
	 * @throws Exception
	 */
	public String update() throws Exception {
		// 根据id获取Export对象，再修改父部门 和部门名称
		Export export = exportService.get(Export.class, model.getId());

		export.setLcno(model.getLcno());
		export.setConsignee(model.getConsignee());
		export.setShipmentPort(model.getShipmentPort());
		export.setDestinationPort(model.getDestinationPort());
		export.setTransportMode(model.getTransportMode());
		export.setPriceCondition(model.getPriceCondition());
		export.setMarks(model.getMarks());
		export.setRemark(model.getRemark());
		// 2.只对jsp页面中修改的值进行保存

		HashSet<ExportProduct> exportProducts = new HashSet<ExportProduct>();
		// 对报运商品进行修改
		for (int i = 0; i < mr_id.length; i++) {
			ExportProduct exportProduct = exportProductService.get(ExportProduct.class, mr_id[i]);
			if ("1".equals(mr_changed[i])) {
				exportProduct.setOrderNo(mr_orderNo[i]);
				exportProduct.setCnumber(mr_cnumber[i]);
				exportProduct.setGrossWeight(mr_grossWeight[i]);
				exportProduct.setNetWeight(mr_netWeight[i]);
				exportProduct.setSizeLength(mr_sizeLength[i]);
				exportProduct.setSizeWidth(mr_sizeWidth[i]);
				exportProduct.setSizeHeight(mr_sizeHeight[i]);
				exportProduct.setExPrice(mr_exPrice[i]);

				exportProduct.setTax(mr_tax[i]);
			}
			exportProducts.add(exportProduct);

		}
		export.setExportProducts(exportProducts);
		exportService.saveOrUpdate(export);

		return "finish";
	}

	/**
	 * @return 新增报运单
	 * @throws Exception
	 */
	public String insert() throws Exception {
		// 获取session中的用户
		User user = (User) session.get(SysConstant.CURRENT_USER_INFO);
		String createByID = user.getId();
		String createDeptID = user.getDept().getId();
		model.setCreateBy(createByID);
		model.setCreateDept(createDeptID);

		exportService.saveOrUpdate(model);
		// 查询显示
		return "finish";
	}

	/**
	 * 新增完成报运单后跳转到保运单列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public String list() throws Exception {
		// 加载所有报运单 跳转到页面
		exportService.findPage("from Export", page, Export.class, null);
		pushToValueStack(page);

		return "list";
	}

	public String delete() throws Exception {
		String str = model.getId();
		System.out.println(str);
		String[] ids = str.split(", ");
		exportService.delete(Export.class, ids);
		return "finish";
	}

	public String submit() throws Exception {
		Export export = exportService.get(Export.class, model.getId());
		export.setState(1);
		exportService.saveOrUpdate(export);
		// 更改保存好状态后可以直接回到list
		return "finish";
	}

	public String cancel() throws Exception {
		Export export = exportService.get(Export.class, model.getId());
		export.setState(0);
		exportService.saveOrUpdate(export);
		// 更改保存好状态后可以直接回到list
		return "finish";
	}

	/**
	 * 在线进行电子报运 将报运单数据和报运商品进行json传递
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	public String exportE() throws Exception {
		// 获取报运单和报运商品
		Export export = exportService.get(Export.class, model.getId());
		Set<ExportProduct> exportProducts = export.getExportProducts();

		// 将转化成json数据

		/*
		 * "{exportId:'454534',inputDate:'2016-12-12',shipmentPort:'beijing',
		 * destinationPort:'shanghai',
		 * transportMode:'ship',priceCondition:'L/C',boxNums:'3',grossWeights:'
		 * 200',measurements:'500'"+",
		 * products:[{exportProductId:'ep001',factoryId:'111',productNo:'222',
		 * packingUnit:'333',
		 * cnumber:'444',boxNum:'555',grossWeight:'666',netWeight:'777',
		 * sizeLength:'888',sizeWidth:'999',
		 * sizeHeight:'1000',exPrice:'1001',price:'1002',tax:'1003'}]"+"}");
		 */
		Map<Object, Object> map = new HashMap<>();
		
		map.put("exportId", export.getId());
		map.put("inputDate", export.getInputDate());
		map.put("shipmentPort", export.getShipmentPort());
		map.put("destinationPort", export.getDestinationPort());
		map.put("transportMode", export.getTransportMode());
		map.put("priceCondition", export.getPriceCondition());
		map.put("boxNums", export.getBoxNums());
		map.put("grossWeights", export.getGrossWeights());
		map.put("measurements", export.getMeasurements());

		
		// 将报运商品也存入到map中去 先将所有报运的商品生成一个list集合 再将所有的商品
		List<Object> list = new ArrayList<>();

		/*
		 * products:[{exportProductId:'ep001',factoryId:'111',productNo:'222',
		 * packingUnit:'333',
		 * cnumber:'444',boxNum:'555',grossWeight:'666',netWeight:'777',
		 * sizeLength:'888',sizeWidth:'999',
		 * sizeHeight:'1000',exPrice:'1001',price:'1002',tax:'1003'}]
		 */

		for (ExportProduct ep : exportProducts) {
			Map<Object, Object> epMap = new HashMap<>();
			
			epMap.put("exportProductId", ep.getId());
			epMap.put("factoryId", ep.getFactory().getId());
			epMap.put("productNo", ep.getProductNo());
			epMap.put("packingUnit", ep.getPackingUnit());
			epMap.put("cnumber", ep.getCnumber());
			epMap.put("boxNum", ep.getBoxNum());
			epMap.put("grossWeight", ep.getGrossWeight());
			epMap.put("netWeight", ep.getNetWeight());
			epMap.put("sizeLength", ep.getSizeLength());
			epMap.put("sizeWidth", ep.getSizeWidth());
			epMap.put("sizeHeight", ep.getSizeHeight());
			epMap.put("exPrice", ep.getExPrice());
			epMap.put("price", ep.getPrice());
			epMap.put("tax", ep.getTax());

			list.add(epMap);
		}
		// 将保存了所有报运商品的list存入到map中
		map.put("products", list);

		String jsonString = JSON.toJSONString(map);

		String backString = epService.exportE(jsonString);
		/*
		 * { exportId:"", state:"", remark:"", products:[ { exportProductId:"",
		 * tax:"" }, { exportProductId:"", tax:"" } ] }
		 */
		Map map2 = JSON.parseObject(backString, Map.class);

		export.setState(Integer.parseInt((String) map2.get("state")));
		export.setRemark((String) map2.get("remark"));
		// 将返回的商品集合字符串解析成list
		String string = map2.get("products").toString();
		List<HashMap> eplist2 = JSON.parseArray(string,HashMap.class);

		Set<ExportProduct> set = new HashSet<>();
		for (HashMap hashMap : eplist2) {
			// 修改每一个商品的tax属性
			String epid = (String) hashMap.get("exportProductId");
			ExportProduct exportProduct = exportProductService.get(ExportProduct.class, epid);
			exportProduct.setTax(Double.parseDouble(hashMap.get("tax").toString()));
			set.add(exportProduct);
		}
		export.setExportProducts(set);
		exportService.saveOrUpdate(export);

		return "finish";
	}
}
