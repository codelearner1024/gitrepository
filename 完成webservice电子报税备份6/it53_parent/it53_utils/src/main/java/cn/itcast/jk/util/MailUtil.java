package cn.itcast.jk.util;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailUtil {
	
	public static void sendMail(String toAdd,String subject,String text) throws Exception{
		// 创建Properties属性文件
		Properties pro = new Properties();
		// 主机地址是163，如果采用其他服务器可以设置，例如：smtp.qq.com smtp.126.com smtp.sina.com
		pro.put("mail.smtp.host","smtp.163.com");
		// 设置是否需要认证
		pro.put("mail.smtp.auth",true);
		
		// 获取到Session的连接对象
		Session session = Session.getInstance(pro);
		session.setDebug(true);//控制台打印提示信息
		// 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		 
		Address fromAddr = new InternetAddress("5231352313@163.com");
		// 邮件对象设置发件人
		message.setFrom(fromAddr);
		
		// 收件人地址
		Address toAddr = new InternetAddress(toAdd);
		// 邮件对象设置收件人to：发送  bcc:抄送   
		message.setRecipient(RecipientType.TO, toAddr);
		
		// 设置邮件的主题
		message.setSubject(subject);
		// 设置邮件的正文
		message.setText(text);
		// 保存这封邮件
		message.saveChanges();
		
		// 获取发送邮件对象  smtp是发送协议
		Transport transport = session.getTransport("smtp");
		// 设置邮件的账号和密码	发送方的服务器  端口号  								//发件密码
		transport.connect("smtp.163.com", 25,"5231352313@163.com", "9494wwg123123");
		// 发送邮件（第一个参数）  到所有收件人（第二个参数）
		transport.sendMessage(message, message.getAllRecipients());
		// 关闭资源
		transport.close();
	}
}
