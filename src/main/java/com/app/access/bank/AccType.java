package com.app.access.bank;

public enum AccType {

	ACCTYPE1("01", "一类账户"),
	ACCTYPE2("02", "二类账户"),
	ACCTYPE3("03", "三类账户");
	
	private AccType(String code, String label){
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
