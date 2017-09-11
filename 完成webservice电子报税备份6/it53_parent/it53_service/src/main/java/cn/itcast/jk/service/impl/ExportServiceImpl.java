package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.domain.ContractProduct;
import cn.itcast.jk.domain.Export;
import cn.itcast.jk.domain.ExportProduct;
import cn.itcast.jk.domain.ExtCproduct;
import cn.itcast.jk.domain.ExtEproduct;
import cn.itcast.jk.service.ExportService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class ExportServiceImpl implements ExportService {

	private BaseDao baseDao;

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public List<Export> find(String hql, Class<Export> entityClass, Object[] params) {
		List<Export> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public Export get(Class<Export> entityClass, Serializable id) {
		Export con = (Export) baseDao.get(entityClass, id);
		return con;
	}

	@Override
	public Page<Export> findPage(String hql, Page<Export> page, Class<Export> entityClass, Object[] params) {
		Page<Export> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	@Override
	public void saveOrUpdate(Export entity) {
		// 取出页面的最新的合同id
		String contractIds = entity.getContractIds();
		String[] ids = UtilFuns.splitStr(contractIds.trim(), ", ");
		// save功能手动设置id
		if (UtilFuns.isEmpty(entity.getId())) {
			String id = UUID.randomUUID().toString();
			entity.setId(id);
			entity.setInputDate(new Date());
			entity.setState(0);
			// 保存报运单，级联保存保运商品明细
			// 保存报运商品 通过购销合同id获取到每一个商品存入到集合中
			HashSet<ExportProduct> exportProducts = new HashSet<>();

			for (String cid : ids) {
				List<ContractProduct> CPlist = baseDao.find("from ContractProduct where contract.id=?",
						ContractProduct.class, new Object[] { cid });
				for (ContractProduct cp : CPlist) {
					ExportProduct ep = new ExportProduct();
					// 设置报运商品明细表的属性
					ep.setBoxNum(cp.getBoxNum());
					ep.setCnumber(cp.getCnumber());
					ep.setFactory(cp.getFactory());
					ep.setId(cp.getId());
					ep.setOrderNo(cp.getOrderNo());
					ep.setPackingUnit(cp.getPackingUnit());
					ep.setPrice(cp.getPrice());
					ep.setProductNo(cp.getProductNo());
					
					//多方进行外键维护
					ep.setExport(entity);
					// 获取商品对应的商品附件列表
					List<ExtCproduct> extClist = baseDao.find("from ExtCproduct where contractProduct.id=?",
							ExtCproduct.class, new Object[] { cp.getId() });
					// 创建报运商品附件表列空集合
					Set<ExtEproduct> extEproducts = new HashSet<>();
					for (ExtCproduct extCproduct : extClist) {
						ExtEproduct extEproduct = new ExtEproduct();
						// 循环附件列表，设置报运附件明细
						extEproduct.setAmount(extCproduct.getAmount());
						extEproduct.setCnumber(extCproduct.getCnumber());
						extEproduct.setFactory(extCproduct.getFactory());
						extEproduct.setId(extCproduct.getId());
						extEproduct.setOrderNo(extCproduct.getOrderNo());
						extEproduct.setPackingUnit(extCproduct.getPackingUnit());
						extEproduct.setPrice(extCproduct.getPrice());
						extEproduct.setProductDesc(extCproduct.getProductDesc());
						extEproduct.setProductImage(extCproduct.getProductImage());
						extEproduct.setProductNo(extCproduct.getProductNo());
						extEproduct.setProductRequest(extCproduct.getProductRequest());
						
						//多方进行外键的维护
						extEproduct.setExportProduct(ep);
						// 保存报运附件到集合中交给报运商品明细
						extEproducts.add(extEproduct);
					}
					// 当前报运商品添加他的报运附件列表
					ep.setExtEproducts(extEproducts);
					// 当前报运商品添加到报运商品集合中
					exportProducts.add(ep);
				}
				//完成购销合同的报运，需要将购销合同的state设置为2（已报运）
				Contract contract = (Contract) baseDao.get(Contract.class, cid);
				contract.setState(2);
				baseDao.saveOrUpdate(contract);
			}
			// 最终将包含报运商品（包含报运商品对应的报运报运附件）存入到报运单对象中，同时也就级联保存了所有的报运商品和报运附件
			entity.setExportProducts(exportProducts);
		}
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<Export> entitys) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteById(Class<Export> entityClass, Serializable id) {
		Export export = (Export) baseDao.get(Export.class, id);
		//删除报运单的时候需要将购销合同的状态改成1
		String contractIds = export.getContractIds();
		String[] ids = UtilFuns.splitStr(contractIds.trim(), ", ");
		for (String string : ids) {
			Contract contract = (Contract)baseDao.get(Contract.class, string);
			contract.setState(1);
			baseDao.saveOrUpdate(contract);
		}
		
		baseDao.deleteById(Export.class, export.getId());
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<Export> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}
}
