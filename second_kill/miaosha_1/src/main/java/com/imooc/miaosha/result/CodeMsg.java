package com.imooc.miaosha.result;

public class CodeMsg {
	private int code;
	private String msg;
	
	//通用异常
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
	// 参数绑定异常带了一个参数，需要定义一个方法，填充参数
	public static CodeMsg Bind_ERROR = new CodeMsg(500101, "参数绑定异常：%s");

	public CodeMsg fillArgs(Object... args){
		int code=this.code;
		// msg需要做填充处理
		String msg=String.format(this.msg,args);
		return new CodeMsg(code,msg);
	}


	//登录模块 5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "session不存在或者已经失效");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "密码不能为空");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
	public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500215, "手机号不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");

	//商品模块 5003XX
	
	//订单模块 5004XX
	
	//秒杀模块 5005XX
	
	
	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
}
