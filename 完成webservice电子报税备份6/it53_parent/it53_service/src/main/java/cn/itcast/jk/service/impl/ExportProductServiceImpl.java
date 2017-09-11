package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.ExportProduct;
import cn.itcast.jk.service.ExportProductService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class ExportProductServiceImpl implements ExportProductService {

	private BaseDao baseDao;
	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	 
	@Override
	public List<ExportProduct> find(String hql, Class<ExportProduct> entityClass, Object[] params) {
		List<ExportProduct> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public ExportProduct get(Class<ExportProduct> entityClass, Serializable id) {
		ExportProduct con = (ExportProduct) baseDao.get(entityClass, id);
		return con;
	}

	@Override
	public Page<ExportProduct> findPage(String hql, Page<ExportProduct> page, Class<ExportProduct> entityClass, Object[] params) {
		Page<ExportProduct> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	 
	@Override
	public void saveOrUpdate( ExportProduct entity) {
		
		// save功能手动设置id
		if (UtilFuns.isEmpty(entity.getId())) {
			 
		}
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<ExportProduct> entitys) {
		// TODO Auto-generated method stub
	}

	 
	@Override
	public void deleteById(Class<ExportProduct> entityClass, Serializable id) {
		ExportProduct exportProduct =(ExportProduct) baseDao.get(ExportProduct.class, id);
		baseDao.deleteById(ExportProduct.class, exportProduct.getId());
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<ExportProduct> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}
}
