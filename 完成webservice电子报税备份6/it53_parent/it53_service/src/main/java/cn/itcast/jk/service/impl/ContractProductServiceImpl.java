package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.domain.ContractProduct;
import cn.itcast.jk.domain.ExtCproduct;
import cn.itcast.jk.service.ContractProductService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class ContractProductServiceImpl implements ContractProductService {

	private BaseDao<ContractProduct> baseDao;

	public void setBaseDao(BaseDao<ContractProduct> baseDao) {
		this.baseDao = baseDao;
	}
	private BaseDao<Contract> cDao;
	public void setcDao(BaseDao<Contract> cDao) {
		this.cDao = cDao;
	}

	@Override
	public List<ContractProduct> find(String hql, Class<ContractProduct> entityClass, Object[] params) {
		List<ContractProduct> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public ContractProduct get(Class<ContractProduct> entityClass, Serializable id) {
		ContractProduct con = baseDao.get(entityClass, id);
		return con;
	}

	/*
	 * return 将封装了分页结果集的Page组件返回到web层
	 * 
	 */
	@Override
	public Page<ContractProduct> findPage(String hql, Page<ContractProduct> page, Class<ContractProduct> entityClass, Object[] params) {
		Page<ContractProduct> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	/*合同的保存或者更新
	 * 更新:有id 有state 保存：没有id 没有state id自动生成 state手动赋值
	 */
	@Override
	public void saveOrUpdate( ContractProduct entity) {
		
		//手动计算当前商品最新的总金额
		Integer cnumber = entity.getCnumber();
		Double price = entity.getPrice();
		Double amount=0d;
		if (UtilFuns.isNotEmpty(cnumber)||UtilFuns.isNotEmpty(price)) {
			 amount = cnumber*price;
		}
		
		//获取合同对象
		Contract contract = cDao.get(Contract.class, entity.getContract().getId());
		Double totalAmount = contract.getTotalAmount();
		// save功能手动设置id
		if (UtilFuns.isEmpty(entity.getId())) {
			//更新合同总金额
			contract.setTotalAmount(totalAmount+amount);
			//更新合同总金额后保存合同
		}else {
			//进行更新操作获取原来的商品金额以更新合同总金额
			Double oldAmount = entity.getAmount();
			contract.setTotalAmount(totalAmount-oldAmount+amount);
		}
		cDao.saveOrUpdate(contract);
		//设置商品最新总金额
		entity.setAmount(amount);
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<ContractProduct> entitys) {
		// TODO Auto-generated method stub
	}
	 
	@Override
	public void deleteById(Class<ContractProduct> entityClass, Serializable id) {
		//删除商品    对合同总金额会产生影响   删除商品  会删除该商品对应的辅料
		//获取商品的价格以及对应的辅料的价格
		ContractProduct contractProduct = baseDao.get(ContractProduct.class, id);
		Double amount = contractProduct.getAmount();
		Set<ExtCproduct> extCproducts = contractProduct.getExtCproducts();
		double ext =0d;
		for (ExtCproduct extCproduct : extCproducts) {
			Double extAmount = extCproduct.getAmount();
			ext+=extAmount;
		}
		//获取对应的合同对象，修改合同总价
		Contract contract = contractProduct.getContract();
		Double totalAmount = contract.getTotalAmount();
		contract.setTotalAmount(totalAmount-amount-ext);
		cDao.saveOrUpdate(contract);
		//删除商品(自动级联删除辅料)
		baseDao.deleteById(ContractProduct.class, id);
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<ContractProduct> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}
}
