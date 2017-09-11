package cn.itcast.jk.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.mail.MailMessage;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import cn.itcast.jk.dao.BaseDao;
import cn.itcast.jk.domain.User;
import cn.itcast.jk.service.UserService;
import cn.itcast.jk.util.Encrypt;
import cn.itcast.jk.util.MailUtil;
import cn.itcast.jk.util.Page;
import cn.itcast.jk.util.SysConstant;
import cn.itcast.jk.util.UtilFuns;

public class UserServiceImpl implements UserService {
	private SimpleMailMessage mailMessage;
	private JavaMailSenderImpl mailSender;


	public void setMailMessage(SimpleMailMessage mailMessage) {
		this.mailMessage = mailMessage;
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	private BaseDao<User> baseDao;

	public void setBaseDao(BaseDao<User> baseDao) {
		this.baseDao = baseDao;
	}
	/*
	 * 添加了表的级联删除不需要再再这边设置了
	 * 
	 * //添加操作userinfo的dao层 private BaseDao<Userinfo> baseDao2;
	 * 
	 * public void setBaseDao2(BaseDao<Userinfo> baseDao) { this.baseDao2 =
	 * baseDao; }
	 */

	@Override
	public List<User> find(String hql, Class<User> entityClass, Object[] params) {
		List<User> list = baseDao.find(hql, entityClass, params);
		return list;
	}

	@Override
	public User get(Class<User> entityClass, Serializable id) {
		User dept = baseDao.get(entityClass, id);
		return dept;
	}

	/*
	 * return 将封装了分页结果集的Page组件返回到web层
	 * 
	 */
	@Override
	public Page<User> findPage(String hql, Page<User> page, Class<User> entityClass, Object[] params) {
		Page<User> findedPage = baseDao.findPage(hql, page, entityClass, params);
		return findedPage;
	}

	/*
	 * 更新:有id 有state 保存：没有id 没有state id自动生成 state手动赋值
	 */
	@Override
	public void saveOrUpdate( User entity) {

		// save功能手动设置id
		if (UtilFuns.isEmpty(entity.getId())) {
			
			// 发送一份邮件给用户提示用户注册成功
			final String email = entity.getUserinfo().getEmail();
			final String userName = entity.getUserName();
			final String pass = entity.getPassword();
			
			/*Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						MailUtil.send(email, "注册邮件", "你好，注册成功，用户名为:" + userName + "密码为:" + pass);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});*/
			mailMessage.setTo(email);
			mailMessage.setText( "你好，注册成功，用户名为:" + userName + "密码为:" + pass);
			mailMessage.setSubject("注册成功");
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					mailSender.send(mailMessage);
				}
			});
			
			thread.start();
			// 手动设置user的ids
			UUID randomUUID = UUID.randomUUID();
			String id = randomUUID.toString();
			entity.setId(id);
			entity.getUserinfo().setId(id);
			// 密码进行加密
			// entity.setPassword(Encrypt.md5(entity.getPassword(),
			// entity.getUserName()));
			entity.setPassword(Encrypt.md5(SysConstant.DEFAULT_PASS, entity.getUserName()));
			
			
		}
		baseDao.saveOrUpdate(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<User> entitys) {
		// TODO Auto-generated method stub
	}

	/*
	 * 删除User需要同时删除userinfo表,删除userinfo中的manager(MANAGER_ID)
	 * 
	 * 其他思路：改变父表使用状态为0（停用），设置子表的父id为null
	 */
	@Override
	public void deleteById(Class<User> entityClass, Serializable id) {
		// 直接删除用户
		// baseDao.deleteById(User.class, id);
		// 设置用户状态是离职状态
		User user = baseDao.get(User.class, id);
		user.setState(0);
		// 查询以该用户id为领导的用户信息表集合，设置这些集合的领导为空
		List<User> user_child = baseDao.find("from User where userinfo.manager.id=?", entityClass, new Object[] { id });
		if (UtilFuns.isNotEmpty(user_child)) {
			for (User child : user_child) {
				child.getUserinfo().setManager(null);
				baseDao.saveOrUpdate(child);
			}
		}
		baseDao.saveOrUpdate(user);
	}

	/*
	 * 同时删除多个对象表
	 */
	@Override
	public void delete(Class<User> entityClass, Serializable[] ids) {
		for (Serializable id : ids) {
			deleteById(entityClass, id);
		}
	}
}
