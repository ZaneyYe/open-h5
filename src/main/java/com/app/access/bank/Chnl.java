package com.app.access.bank;

public enum Chnl {

	APP("1", "银行APP"),
	WebCharNumber("2", "银行公众号");
	
	private Chnl(String code, String label){
		this.code = code;
		this.label = label;
	}
	
	private String code;
	
	private String label;
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
}
