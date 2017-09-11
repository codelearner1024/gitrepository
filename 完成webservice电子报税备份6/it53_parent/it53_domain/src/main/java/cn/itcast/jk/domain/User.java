package cn.itcast.jk.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.criteria.Predicate;

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
public class User extends BaseEntity{
	
	private String id;
	private Dept dept;
	private String userName;
	private String password;
	private Integer state;  //0.离职  1.在职
	
	private Set<Role> roles =new HashSet<Role>();
	
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	private Userinfo userinfo;
	
	public Userinfo getUserinfo() {
		return userinfo;
	}
	public void setUserinfo(Userinfo userinfo) {
		this.userinfo = userinfo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Dept getDept() {
		return dept;
	}
	public void setDept(Dept dept) {
		this.dept = dept;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	
}
