package kr.ac.snu.bi.web.api.manager;


public class APIException extends Exception {
	
	
	
	
	private int code;
	
	public APIException(String msg, int code) {
		super(msg);
		
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
}
