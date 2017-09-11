package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.Dept;
import cn.itcast.jk.service.DeptService;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.UtilFuns;

public class DeptServiceImpl implements DeptService {
	private BaseDao<Dept> baseDao;

	public void setBaseDao(BaseDao<Dept> baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public List<Dept> find(String hql, Class<Dept> entityClass, Object[] params) {
		List<Dept> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public Dept get(Class<Dept> entityClass, Serializable id) {
		Dept dept = baseDao.get(entityClass, id);
		return dept;

	}

	/*
	 * return 将封装了分页结果集的Page组件返回到web层
	 * 
	 */
	@Override
	public Page<Dept> findPage(String hql, Page<Dept> page, Class<Dept> entityClass, Object[] params) {
		Page<Dept> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	/*
	 * 更新:有id 有state 保存：没有id 没有state id自动生成 state手动赋值
	 */
	@Override
	public void saveOrUpdate(Dept entity) {

		if (UtilFuns.isEmpty(entity.getId())) {
			// 添加state
			entity.setState(1);
		}
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<Dept> entitys) {
		// TODO Auto-generated method stub

	}

	/*
	 * 删除单个表 （需要考虑表继承关系表即你通过id删除一张表，需要查找引用该id的其他子表，先删除部门子表再删除本身表），
	 * 其他思路：改变父表使用状态为0（停用），设置子表的父id为null
	 */
	@Override
	public void deleteById(Class<Dept> entityClass, Serializable id) {
		// 删除该表之前先确定该表是否还存在，因为有可能已经被父表查找子表删除了
		Dept dept_self = baseDao.get(entityClass, id);
		if (dept_self == null) {
			return;
		}
		// 这里使用删除子父表
		// 查询该id对应的所有子表
		List<Dept> deptList = baseDao.find("from Dept where parent.id=?", entityClass, new String[] { (String) id });
		// 判断有无子表，有递归查询，没有:删除当前子表
		if (UtilFuns.isNotEmpty(deptList)) {
			// 遍历删除子表
			for (Dept dept : deptList) {
				// 删除子表前先递归删除子表和子表的子表
				deleteById(entityClass, dept.getId());
				//baseDao.deleteById(entityClass, dept.getId());
			}
			// 判断没有子表了，删除当前的子表
		}
		// 删除父表
		baseDao.deleteById(entityClass, id);

		/*
		 * String hql = "from Dept where parent.id = ?";
		 * 
		 * List<Dept> deptList = baseDao.find(hql, Dept.class, new
		 * Object[]{id});
		 * 
		 * //递归删除所有子部门后，删除当前的部门 if(deptList != null && deptList.size() > 0 ){
		 * //代表传入的id查询后，有子部门 for(Dept dept:deptList){ deleteById(Dept.class,
		 * dept.getId()); } }
		 * 
		 * //查询一下当前id对应的部门还是否存在 Dept dept = (Dept) baseDao.get(Dept.class, id);
		 * if(dept != null){ baseDao.deleteById(entityClass, id); }
		 */
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<Dept> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}

}
