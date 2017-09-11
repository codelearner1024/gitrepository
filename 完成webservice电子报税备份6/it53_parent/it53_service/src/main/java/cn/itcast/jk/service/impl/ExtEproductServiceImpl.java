package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.ExtEproduct;
import cn.itcast.jk.service.ExtEproductService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class ExtEproductServiceImpl implements ExtEproductService {

	private BaseDao baseDao;
	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	 
	@Override
	public List<ExtEproduct> find(String hql, Class<ExtEproduct> entityClass, Object[] params) {
		List<ExtEproduct> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public ExtEproduct get(Class<ExtEproduct> entityClass, Serializable id) {
		ExtEproduct con = (ExtEproduct) baseDao.get(entityClass, id);
		return con;
	}

	@Override
	public Page<ExtEproduct> findPage(String hql, Page<ExtEproduct> page, Class<ExtEproduct> entityClass, Object[] params) {
		Page<ExtEproduct> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	 
	@Override
	public void saveOrUpdate( ExtEproduct entity) {
		
		// save功能手动设置id
		if (UtilFuns.isEmpty(entity.getId())) {
			 
		}
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<ExtEproduct> entitys) {
		// TODO Auto-generated method stub
	}

	 
	@Override
	public void deleteById(Class<ExtEproduct> entityClass, Serializable id) {
		ExtEproduct extEproduct =(ExtEproduct) baseDao.get(ExtEproduct.class, id);
		baseDao.deleteById(ExtEproduct.class, extEproduct.getId());
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<ExtEproduct> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}
}
