package cn.itcast.jk.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface SqlService {
	//返回单值
		public String getSingleValue(String sql);
		public String getSingleValue(String sql, Object[] objs);
		
		public String[] toArray(String sql);
		
		public List executeSQL(String sql);
		
		public List executeSQL(String sql, Object[] objs);
		
		public List executeSQLForList(String sql, Object[] objs);
		
		public int updateSQL(String sql);
		
		public int updateSQL(String sql, Object[] objs);
		
		public int[] batchSQL(String[] sql);
}
