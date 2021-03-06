package com.test;

import com.app.access.bank.AccType;
import com.app.access.bank.CertType;
import com.app.access.bank.Chnl;
import com.app.access.bank.SNUtil;

import java.util.ArrayList;
import java.util.List;

public class OpenH5User {
	
	public static String appId = "5949221470";
	public static String insUsrId = "c6602156836";
	public static Chnl chnl = Chnl.APP;
	public static CertType certType = CertType.CertType01;
	public static String certifId = "430211198412100435";
	public static String mobile = "16602156836";
	public static String realNm = "chenweiming";
	public static List<AccType> accTypes = new ArrayList<AccType>(0);
	public static List<String> cardNos = new ArrayList<String>(0);
	public static String signKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIApSXeqcT3pKCBDKGpKqf+ByvrSdwMzDnmUb+6eVsms/GpbIhCHrGr+/fPRuq/COZuEsiNmkjusEeNgJCEoga/AzQ3xDklx6VZ7UDMfOVtK4KRsWHSMfYCtcXm2btppxcM6dOFwimtt1vPptgYfxsbdmtrxJw0ziccj0jAV4wpPAgMBAAECgYAHh+WMRZSv6aJ0+t1GGasRm4Pc5z8dDgP8uu8021MIOMUATuiahg5onyE3EYzhxQzziYGaOO3A2eSXMtAMrr+oCdwN7gqwjShgGkB/2cDvDnJ0wFHntvCYXjp13QEFJ8CO5fkYWLVxFtJ6VrdLUktUvhR+Fw4JLuTho/11lYdhGQJBAMFdi3RD2XEyoAoH4mkZ5siPfyW6gu5qkBBroAb3WJaAYxL0bwRmFYI+Q5YAmjYZwJlnm8AC3bMJREpFslP0NOUCQQCprNCXNQal6XuzyQGngy6eAOVGLKp/inGWyRW/wuFu6TJAGTAonbwTpNfeEfQ3aOJGgt/DHWOfvdVJ9BbraqMjAkEArX/2BRhsHrnCB74TVSK8hPDcsUms+af8I/+t0xJVFpWUUAmrI1NFsVuU4R8hP7HTstHYWm0359FEyS/IVrQkUQJAXaWG3t2iVLHf12OKaTTq5sPhxvBiDdCQTsOfIF5j474LQPtl7BTauBDUH7nTCz31HSugamTvFjxE2vNALyCE9wJAEE6G0W9IZDm6w+5nbiZ2mAhd0VBfMI2apa09/yMQGcqt2974bw/42chPoO9Vcwua+x3LsQ1stxl3+6jADQp7Fw==";
	public static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlCK4TIi4XUijd2bEseATK1FRcypNwaHtXKU9ouV6lzYpxMy1UA1VUTMCfato31VV0Pj+LDFHTm62it05vIKbJ+at4LUE6BjskL5VTnxZXAyGKC4tvWFuUrMJYh2z57AnMtk697OYxJAxD9/il0mCRs2ftqvCM1VM3BlyeCurKJ2ZfGXBD9WujCOWIzbSFrZARkflJ/ZmOfoP7ks2Pi1wmWNHy2Hnc+qx/ohYIf8/t7rfC93nEpTdhO+Nbd9I8otwQz8gpIH5IOEq/8QgfKM86tMIiOdTDiNE+7Pm+7ZVJdZ98RuzcSeyDB42B7kbS8sAoGEyn4WlnPRKSyeIh2+JYQIDAQAB";
	public static String host = "wallet.95516.com";
	public static boolean isHttps = true;
	
	static{
		accTypes.add(AccType.ACCTYPE1);
		cardNos.add("166225882123823376");
	}
	
	public static void main(String[] args) throws Exception {
		String sn = SNUtil.analysisResultStr(SNUtil.findUser(host, appId, insUsrId, chnl, signKey, isHttps));
		System.out.println("------------------" + sn + "-----------------");
		sn = SNUtil.analysisResultStr(SNUtil.bindUser(host, appId, cardNos, accTypes, mobile, 
				realNm, certType, certifId, insUsrId, chnl, 
				signKey, publicKey, isHttps));
		System.out.println("------------------" + sn + "-----------------");
	}

	public static String finduser() throws Exception {
		return SNUtil.findUser(host, appId, insUsrId, chnl, signKey, isHttps);
	}
	
	public static String binduser(String mobile) throws Exception {
		return SNUtil.bindUser(host, appId, cardNos, accTypes, mobile, 
				realNm, certType, certifId, insUsrId, chnl, 
				signKey, publicKey, isHttps);
	}
}
