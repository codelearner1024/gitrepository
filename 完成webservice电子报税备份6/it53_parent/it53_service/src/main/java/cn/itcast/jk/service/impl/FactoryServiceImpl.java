package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.Factory;
import cn.itcast.jk.service.FactoryService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class FactoryServiceImpl implements FactoryService {

	private BaseDao<Factory> baseDao;

	public void setBaseDao(BaseDao<Factory> baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public List<Factory> find(String hql, Class<Factory> entityClass, Object[] params) {
		List<Factory> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public Factory get(Class<Factory> entityClass, Serializable id) {
		Factory con = baseDao.get(entityClass, id);
		return con;
	}

	/*
	 * return 将封装了分页结果集的Page组件返回到web层
	 * 
	 */
	@Override
	public Page<Factory> findPage(String hql, Page<Factory> page, Class<Factory> entityClass, Object[] params) {
		Page<Factory> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	/*合同的保存或者更新
	 * 更新:有id 有state 保存：没有id 没有state id自动生成 state手动赋值
	 */
	@Override
	public void saveOrUpdate( Factory entity) {

		// save功能手动设置id
		if (UtilFuns.isEmpty(entity.getId())) {
			
		}
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<Factory> entitys) {
		// TODO Auto-generated method stub
	}

	 
	@Override
	public void deleteById(Class<Factory> entityClass, Serializable id) {
		
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<Factory> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}
}
