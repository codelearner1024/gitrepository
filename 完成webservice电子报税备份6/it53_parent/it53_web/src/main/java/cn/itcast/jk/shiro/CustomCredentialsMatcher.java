package cn.itcast.jk.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

import cn.itcast.jk.util.Encrypt;

public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		//进行比较
		//比较前先进行加密
		UsernamePasswordToken uPToken=(UsernamePasswordToken)token;
		char[] password =uPToken.getPassword();
		String pass = new String(password);
		//md5加密
		pass = Encrypt.md5(pass, uPToken.getUsername());
		String info_pass = info.getCredentials().toString();
		return equals(pass, info_pass);
	}
}
