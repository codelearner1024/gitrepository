package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.Module;
import cn.itcast.jk.service.ModuleService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class ModuleServiceImpl implements ModuleService {
	private BaseDao<Module> baseDao;

	public void setBaseDao(BaseDao<Module> baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public List<Module> find(String hql, Class<Module> entityClass, Object[] params) {
		List<Module> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public Module get(Class<Module> entityClass, Serializable id) {
		Module dept = baseDao.get(entityClass, id);
		return dept;

	}

	/*
	 * return 将封装了分页结果集的Page组件返回到web层
	 * 
	 */
	@Override
	public Page<Module> findPage(String hql, Page<Module> page, Class<Module> entityClass, Object[] params) {
		Page<Module> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	@Override
	public void saveOrUpdate(Module entity) {
		
		/*if (UtilFuns.isEmpty(entity.getId())) {
		}*/
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<Module> entitys) {
		// TODO Auto-generated method stub
	}

	/*module与user是多对多的关系
	 * 删除Module需要同时删除与该module关联的所有中间表的中的数据
	 * 
	 *  
	 */
	@Override
	public void deleteById(Class<Module> entityClass, Serializable id) {
		//查询以该用户id为领导的用户信息表集合，设置这些集合的领导为空
		baseDao.deleteById(entityClass, id);
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<Module> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}

}
