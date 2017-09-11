package cn.itcast.jk.domain;

import java.io.Serializable;

/** 
 * PO类规范：
 * 1.是一个公有类
 * 2.属性私有
 * 3.提供属性的公有的getter与setter
 * 4.存在一个公有的无参构造
 * 5.不能使用final修饰
 * 6.一般都实现java.io.Serializable接口
 * 7.如果是基本类型，请使用它们的包装类
 */
public class Dept implements Serializable{
	private String id;
	private String deptName;
	private Dept parent;//父部门   自关联   子部门与父部门   多对一关系
	
	private Integer state;//状态  1启用0停用 默认为1
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public Dept getParent() {
		return parent;
	}
	public void setParent(Dept parent) {
		this.parent = parent;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
}
