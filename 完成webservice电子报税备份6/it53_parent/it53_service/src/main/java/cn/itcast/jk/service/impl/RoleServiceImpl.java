package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.Role;
import cn.itcast.jk.service.RoleService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class RoleServiceImpl implements RoleService {
	private BaseDao<Role> baseDao;

	public void setBaseDao(BaseDao<Role> baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public List<Role> find(String hql, Class<Role> entityClass, Object[] params) {
		List<Role> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public Role get(Class<Role> entityClass, Serializable id) {
		Role dept = baseDao.get(entityClass, id);
		return dept;

	}

	/*
	 * return 将封装了分页结果集的Page组件返回到web层
	 * 
	 */
	@Override
	public Page<Role> findPage(String hql, Page<Role> page, Class<Role> entityClass, Object[] params) {
		Page<Role> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	@Override
	public void saveOrUpdate(Role entity) {
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<Role> entitys) {
		// TODO Auto-generated method stub
	}

	/*role与user是多对多的关系
	 * 删除Role需要同时删除与该role关联的所有中间表的中的数据
	 * 
	 *  
	 */
	@Override
	public void deleteById(Class<Role> entityClass, Serializable id) {
		Role role = baseDao.get(Role.class, id);
		//查询以该用户id为领导的用户信息表集合，设置这些集合的领导为空
		baseDao.deleteById(entityClass, id);
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<Role> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}

}
