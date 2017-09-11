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
import cn.itcast.jk.service.ContractService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class ContractServiceImpl implements ContractService {

	private BaseDao<Contract> baseDao;
	public void setBaseDao(BaseDao<Contract> baseDao) {
		this.baseDao = baseDao;
	}
	private BaseDao<ContractProduct> pDao;
	private BaseDao<ExtCproduct> eDao;

	public void setpDao(BaseDao<ContractProduct> pDao) {
		this.pDao = pDao;
	}

	public void seteDao(BaseDao<ExtCproduct> eDao) {
		this.eDao = eDao;
	}

	@Override
	public List<Contract> find(String hql, Class<Contract> entityClass, Object[] params) {
		List<Contract> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public Contract get(Class<Contract> entityClass, Serializable id) {
		Contract con = baseDao.get(entityClass, id);
		return con;
	}

	/*
	 * return 将封装了分页结果集的Page组件返回到web层
	 * 
	 */
	@Override
	public Page<Contract> findPage(String hql, Page<Contract> page, Class<Contract> entityClass, Object[] params) {
		Page<Contract> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	/*合同的保存或者更新
	 * 新增：直接保存即可 状态和id需要手动给
	 * 更新:有id 有state 保存：没有id 没有state id自动生成 state手动赋值
	 */
	@Override
	public void saveOrUpdate( Contract entity) {
		
		// save功能手动设置id
		if (UtilFuns.isEmpty(entity.getId())) {
			entity.setState(0);
			//设置订单初始的金额
			entity.setTotalAmount(0d);
		}
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<Contract> entitys) {
		// TODO Auto-generated method stub
	}

	 
	@Override
	public void deleteById(Class<Contract> entityClass, Serializable id) {
		Contract contract = baseDao.get(Contract.class, id);
		
		/*Set<ContractProduct> contractProducts = contract.getContractProducts();
		for (ContractProduct contractProduct : contractProducts) {
			Set<ExtCproduct> extCproducts = contractProduct.getExtCproducts();
			for (ExtCproduct extCproduct : extCproducts) {
				//首先删除辅料
				eDao.deleteById(ExtCproduct.class, extCproduct.getId());
			}
			//辅料删除结束后删除商品
			pDao.deleteById(ContractProduct.class, contractProduct.getId());
		}*/
		//最后将合同本身删除
		baseDao.deleteById(Contract.class, contract.getId());
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<Contract> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}
}
