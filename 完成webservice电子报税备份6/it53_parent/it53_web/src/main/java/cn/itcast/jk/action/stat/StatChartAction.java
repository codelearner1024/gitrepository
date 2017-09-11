package cn.itcast.jk.action.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionSupport;

import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.FactorySaleView;
import cn.itcast.jk.service.SqlService;

/**经过该action最终转向到三个文件夹下(factory  onlineinfo  productsale)的三个不同的index.html 
 * @author DELL
 *
 */
public class StatChartAction extends ActionSupport{
	private SqlService sqlService;
	
	private BaseDao baseDao;
	
	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}
	public String productsale() throws Exception {
		
//	<%=request.getParameter("forward")%>
		
		return "productsale";
	}
	/**
	 * @return 获取到工厂销售情况的数据以json形式返回给页面
	 * @throws Exception
	 */
	public String factorysale() throws Exception {
		return "factorysale";
	}
	public String factorysale2() throws Exception {
		return "factorysale2";
	}
	
	
	
	
	public void getFactorySaleData() throws Exception {
		//从货物列表获取工厂销售情况
		
				String sql = "select factory_name,count(amount) from contract_product_c group by factory_name";
				List<Object> list = sqlService.executeSQL(sql);
				//将list转化成json字符串：数组里面放集合[南皮开发, 3, 光华, 23, 天顺, 8, 汇越, 1, 精艺, 11, 宏艺, 9, 民鑫, 1, 平遥鸿艺, 4, 文水志远, 1, 华艺, 6, 升华, 4, 平遥远江, 3, 晶晨, 1, 会龙, 3]
				List<Map> jList = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = new HashMap<>();	
					map.put("factoryname", list.get(i++));
					//去掉引号
					map.put("amount", list.get(i));
					jList.add(map);
				}
				String jsonString = JSON.toJSONString(jList);
				// 响应
				HttpServletResponse response = ServletActionContext.getResponse();
				// 设置编码等
				response.setContentType("application/json;charset=UTF-8");
				response.setHeader("Cache-Control", "no-cache");// 设置响应结束时，不使用缓存
				// 设置返回的json格式
				response.getWriter().print(jsonString);
	}
	/**使用hibernate语句创建视图，对视图进行查询
	 * @throws Exception
	 */
	public void getFactorySaleData2() throws Exception {
		//从货物列表获取工厂销售情况
		String hql = "from FactorySaleView";
		List<FactorySaleView> list = baseDao.find(hql, FactorySaleView.class,null);
		System.out.println(list);
		//将list转化成json字符串：数组里面放集合[南皮开发, 3, 光华, 23, 天顺, 8, 汇越, 1, 精艺, 11, 宏艺, 9, 民鑫, 1, 平遥鸿艺, 4, 文水志远, 1, 华艺, 6, 升华, 4, 平遥远江, 3, 晶晨, 1, 会龙, 3]
		List<List> jList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			List list2 = new ArrayList();	
			list2.add(list.get(i).getName());
			list2.add(list.get(i).getAmount());
			jList.add(list2);
		}
		String jsonString = JSON.toJSONString(jList);
		// 响应
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码等
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");// 设置响应结束时，不使用缓存
		// 设置返回的json格式
		response.getWriter().print(jsonString);
	}
	
	/**生成产品销售情况的柱状图
	 * @throws Exception
	 */
	public void getproductsale() throws Exception {


//查询产品销售情况前15名
		String sql = "select * from (select product_no ,sum(amount) totalamount  from contract_product_c group by product_no order by totalamount desc) where rownum<16";
		List<Object> list = sqlService.executeSQL(sql);
		//将list转化成json字符串：数组里面放集合[南皮开发, 3, 光华, 23, 天顺, 8, 汇越, 1, 精艺, 11, 宏艺, 9, 民鑫, 1, 平遥鸿艺, 4, 文水志远, 1, 华艺, 6, 升华, 4, 平遥远江, 3, 晶晨, 1, 会龙, 3]
		List<Map> jList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<>();	
			map.put("product_no", list.get(i++));
			//去掉引号
			map.put("totalamount", list.get(i));
			jList.add(map);
		}
		
		String jsonString = JSON.toJSONString(jList);
		// 响应
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码等
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");// 设置响应结束时，不使用缓存
		// 设置返回的json格式
		response.getWriter().print(jsonString);
	}
	
	
	
	/** 生成访问压力的线性图
	 * @throws Exception
	 */
	public void getonlineinfo() throws Exception {
		//从货物列表获取工厂销售情况
		String sql = "select a1,(select count(*) from login_log_p where to_char(login_time,'HH24') = a1 ) from online_info_t";
		List<Object> list = sqlService.executeSQL(sql);
		//将list转化成json字符串：数组里面放集合[南皮开发, 3, 光华, 23, 天顺, 8, 汇越, 1, 精艺, 11, 宏艺, 9, 民鑫, 1, 平遥鸿艺, 4, 文水志远, 1, 华艺, 6, 升华, 4, 平遥远江, 3, 晶晨, 1, 会龙, 3]
		List<Map> jList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<>();	
			map.put("time", list.get(i++));
			//去掉引号
			map.put("number", list.get(i));
			jList.add(map);
		}
		String jsonString = JSON.toJSONString(jList);
		// 响应
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置编码等
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");// 设置响应结束时，不使用缓存
		// 设置返回的json格式
		response.getWriter().print(jsonString);
	}
	
	
	public String onlineinfo() throws Exception {
		
		return "onlineinfo";
	}

}
