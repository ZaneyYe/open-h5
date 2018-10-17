package com.app.access.bank;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class DESedeSymmeticKeyGenerator {

	public static DESedeSymmeticKeyGenerator getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {

		static DESedeSymmeticKeyGenerator instance = new DESedeSymmeticKeyGenerator();

	}

	private DESedeSymmeticKeyGenerator() {
		
	}

	public byte[] genSecretKey()
			throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance("DESede");
		SecureRandom sr = new SecureRandom();
		kg.init(112,sr);
		return kg.generateKey().getEncoded();
	}

	public String genBase64SecretKey()
			throws NoSuchAlgorithmException {
		byte[] key = genSecretKey();
		return Base64.encodeBase64String(key);
	}

	public String genHexSecretKey()
			throws NoSuchAlgorithmException {
		byte[] key = genSecretKey();
		return Hex.encodeHexString(key);
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException{
		System.out.println(DESedeSymmeticKeyGenerator.getInstance().genHexSecretKey());
	}

}
