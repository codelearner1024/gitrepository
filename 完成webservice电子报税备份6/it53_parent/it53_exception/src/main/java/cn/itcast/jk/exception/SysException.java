package cn.itcast.jk.exception;

	public class SysException extends Exception{
		
		private static final long serialVersionUID = -2035467795506025791L;
		
		// 提示信息
		private String message;
		public String getMessage() {
			return message;
		}
		
		public SysException(String message) {
			this.message = message;
		}
	}
