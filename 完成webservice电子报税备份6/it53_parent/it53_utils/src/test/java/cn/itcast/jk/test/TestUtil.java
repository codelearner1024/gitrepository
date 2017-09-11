package cn.itcast.jk.test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

public class TestUtil {
	@Test
	public void run1() throws Exception{
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
		// 发件人地址
		Address fromAddr = new InternetAddress("5231352313@163.com");
		// 邮件对象设置发件人
		message.setFrom(fromAddr);
		
		// 收件人地址
		Address toAddr = new InternetAddress("wuweigang11@gmail.com");
		// 邮件对象设置收件人to：发送  bcc:抄送   
		message.setRecipient(RecipientType.TO, toAddr);
		
		// 设置邮件的主题
		message.setSubject("邮件的主题:测试一");
		// 设置邮件的正文
		message.setText("这是邮件的正文一");
		// 保存这封邮件
		message.saveChanges();
		
		// 获取发送邮件对象  smtp是发送协议
		Transport transport = session.getTransport("smtp");
		// 设置邮件的账号和密码	发送方的服务器  端口号  								//发件密码
		transport.connect("smtp.163.com", 25,"5231352313@163.com", "               ");
		// 发送邮件（第一个参数）  到所有收件人（第二个参数）
		transport.sendMessage(message, message.getAllRecipients());
		// 关闭资源
		transport.close();
	}
	
	/**
	 * POI技术的入门
	 * @throws Exception 
	 */
	@Test
	public void run2() throws Exception{
		// 创建工作簿，创建2003版本的工作簿
		Workbook wb = new HSSFWorkbook();
		// 创建sheet工作表
		Sheet sheet = wb.createSheet();
		//合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));
		// 创建第1行，下标从0开始
		Row row = sheet.createRow(0);
		//设置行高
		row.setHeightInPoints((short)36);
		//设置列宽
		sheet.setColumnWidth(1, 26*256);
		// 创建单元格，创建第3列,下标从0开始
		Cell cell = row.createCell(1);
		// 给单元格设置内容
		cell.setCellValue("2012年报表");
		// 先创建样式对象
		CellStyle style = wb.createCellStyle();
		// 创建字体对象
		Font font = wb.createFont();
		//设置宽度
		font.setBold(true);
		// 设置字体大小
		font.setFontHeightInPoints((short) 16);
		// 设置字体的名称
		font.setFontName("楷体");
		// 设置字体
		style.setFont(font);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		// 给单元格设置样式
		cell.setCellStyle(style);
		
		// 创建输出流
		OutputStream os = new FileOutputStream("D:\\abc.xls");
		// 写入
		wb.write(os);
		// 关闭流
		os.close();
		// 下载（暂时测试不了）
	}
}

