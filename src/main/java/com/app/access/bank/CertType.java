package com.app.access.bank;

public enum CertType {

	CertType01("01", "身份证"),
	CertType03("03", "护照"),
	CertType04("04", "回乡证"),
	CertType05("05", "台胞证");
	
	private CertType(String code, String label){
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
