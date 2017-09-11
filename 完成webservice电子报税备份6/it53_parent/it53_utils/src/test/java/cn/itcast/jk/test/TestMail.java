package cn.itcast.jk.test;

import org.junit.Test;

import cn.itcast.jk.util.MailUtil;

public class TestMail {
	@Test
	public void testMail() throws Exception{
		MailUtil.sendMail("wuweigang11@outlook.com", "test", "neng成功码");
	}
}
