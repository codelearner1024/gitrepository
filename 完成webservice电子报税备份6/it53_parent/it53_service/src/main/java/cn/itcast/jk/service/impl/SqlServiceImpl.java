package cn.itcast.jk.service.impl;

import java.util.List;

import cn.itcast.jk.dao.impl.SqlDao;
import cn.itcast.jk.service.SqlService;

public class SqlServiceImpl implements SqlService {
	private SqlDao sqlDao;

	public void setSqlDao(SqlDao sqlDao) {
		this.sqlDao = sqlDao;
	}

	@Override
	public String getSingleValue(String sql) {
		String singleValue = sqlDao.getSingleValue(sql);
		
		return singleValue;
	}

	@Override
	public String getSingleValue(String sql, Object[] objs) {
		return sqlDao.getSingleValue(sql, objs);
	}

	@Override
	public String[] toArray(String sql) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List executeSQL(String sql) {
		List list = sqlDao.executeSQL(sql);
		return list;
	}

	@Override
	public List executeSQL(String sql, Object[] objs) {
		List list = sqlDao.executeSQL(sql, objs);
		return list;
	}

	@Override
	public List executeSQLForList(String sql, Object[] objs) {
		List list = sqlDao.executeSQLForList(sql, objs);
		return list;
	}

	@Override
	public int updateSQL(String sql) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateSQL(String sql, Object[] objs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] batchSQL(String[] sql) {
		// TODO Auto-generated method stub
		return null;
	}
}
