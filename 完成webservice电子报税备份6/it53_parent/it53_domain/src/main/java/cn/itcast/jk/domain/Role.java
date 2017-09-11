package cn.itcast.jk.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * PO类规范： 1.是一个公有类 2.属性私有 3.提供属性的公有的getter与setter 4.存在一个公有的无参构造 5.不能使用final修饰
 * 6.一般都实现java.io.Serializable接口 7.如果是基本类型，请使用它们的包装类
 */
public class Role extends BaseEntity {

	private String id;
	private String name;
	private String remark;
	private Integer orderNo;

	private Set<Module> modules = new HashSet<Module>();
	
	public Set<Module> getModules() {
		return modules;
	}

	public void setModules(Set<Module> modules) {
		this.modules = modules;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

}
