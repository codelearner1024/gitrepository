package cn.itcast.jk.test;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SpringAttachedImageMail {

	public static void main(String[] args) throws Exception {
		ApplicationContext cxa = new ClassPathXmlApplicationContext("spring/applicationContext-mail.xml");
		JavaMailSender mailSender = (JavaMailSender) cxa.getBean("mailSender");
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		// 第二个参数是true代表支持多媒体格式
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		// true代表前面的文本可以当成html标签进行解析
		helper.setText(
				"<html><head></head><body><h1>hello!!spring image html mail</h1><a href=http://www.baidu.com>baidu</a><img src='cid:image' /></body></html>",
				true);

		// 内容中添加了一个图片
		helper.addInline("image", new FileSystemResource(new File("C:/Users/DELL/Pictures/Saved Pictures/img3.jpg")));

		// 为邮箱添加附件
		helper.addAttachment("axis.log", new FileSystemResource(new File("C:/Users/DELL/Desktop/阿里巴巴Java开发手册v1.2.0.pdf")));

		mailSender.send(mimeMessage);
	}

}
