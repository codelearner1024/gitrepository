package cn.itcast.jk.domain;

import java.io.Serializable;
import java.util.Date;

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
public class Userinfo extends BaseEntity{
	private String id;
	private String name;
	private User manager;      //上级领导
	private Date joinDate;  //入职时间
	private Double salary;  //工资
	private Date birthday;  //生日
	private String gender;  //性别
	private String station; //职务
	private String telephone; //电话
	//新增邮件属性
	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	private Integer degree;   //级别
	private Integer orderNo;  //排序号
	private String remark;    //备注
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public User getManager() {
		return manager;
	}
	public void setManager(User manager) {
		this.manager = manager;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public Integer getDegree() {
		return degree;
	}
	public void setDegree(Integer degree) {
		this.degree = degree;
	}
	public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
