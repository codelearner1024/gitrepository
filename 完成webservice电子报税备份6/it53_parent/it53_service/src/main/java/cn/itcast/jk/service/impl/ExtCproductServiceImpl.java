package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.validator.internal.util.Contracts;

import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.domain.ExtCproduct;
import cn.itcast.jk.service.ExtCproductService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class ExtCproductServiceImpl implements ExtCproductService {

	private BaseDao<ExtCproduct> baseDao;

	public void setBaseDao(BaseDao<ExtCproduct> baseDao) {
		this.baseDao = baseDao;
	}
	private BaseDao<Contract> cDao;

	public void setcDao(BaseDao<Contract> cDao) {
		this.cDao = cDao;
	}

	@Override
	public List<ExtCproduct> find(String hql, Class<ExtCproduct> entityClass, Object[] params) {
		List<ExtCproduct> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public ExtCproduct get(Class<ExtCproduct> entityClass, Serializable id) {
		ExtCproduct con = baseDao.get(entityClass, id);
		return con;
	}

	/*
	 * return 将封装了分页结果集的Page组件返回到web层
	 * 
	 */
	@Override
	public Page<ExtCproduct> findPage(String hql, Page<ExtCproduct> page, Class<ExtCproduct> entityClass, Object[] params) {
		Page<ExtCproduct> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	/* 附件保存或者更改
	 *  
	 */
	@Override
	public void saveOrUpdate( ExtCproduct entity) {
		/*保存附件修改合同总金额*/
		String cid = entity.getContractProduct().getContract().getId();
		//从数据库调取合同
		Contract contract = cDao.get(Contract.class, cid);
		Double totalAmount = contract.getTotalAmount();
		//最新的数量
		Integer cnumber = entity.getCnumber();
		//最新的价格
		Double price = entity.getPrice();
		double extAmount =0d;
		if (UtilFuns.isNotEmpty(cnumber)||UtilFuns.isNotEmpty(price)) {
		extAmount =	cnumber*price;
		}
		// save功能手动设置id
		if (UtilFuns.isEmpty(entity.getId())) {
			contract.setTotalAmount(totalAmount+extAmount);
		}else {
			//更新  减去旧的金额加上新的金额
			contract.setTotalAmount(totalAmount-entity.getAmount()+extAmount);
		}
		cDao.saveOrUpdate(contract);
		//保存最新的金额
		entity.setAmount(extAmount);
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<ExtCproduct> entitys) {
		
	}

	 
	@Override
	public void deleteById(Class<ExtCproduct> entityClass, Serializable id) {
		ExtCproduct extCproduct = baseDao.get(ExtCproduct.class, id);
		//删除附件 修改合同的价格
		String id2 = extCproduct.getContractProduct().getContract().getId();
		Contract contract = cDao.get(Contract.class, id2);
		Double totalAmount = contract.getTotalAmount();
		Double amount = extCproduct.getAmount();
		contract.setTotalAmount(totalAmount-amount);
		cDao.saveOrUpdate(contract);
		//最后删除附件
		baseDao.deleteById(ExtCproduct.class, id);
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<ExtCproduct> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}
}
