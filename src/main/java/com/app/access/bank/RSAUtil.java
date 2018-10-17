package com.app.access.bank;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class RSAUtil {
	
    public static final String SIGN_ALGORITHMS = "SHA256WithRSA";

    public static String sign(String value, String privateKey) throws Exception {
        byte[] keyBytes = BytesUtil.hexToBytes(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyf.generatePrivate(keySpec);
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(priKey);
        signature.update(value.getBytes());
        byte[] signed = signature.sign();
        String result = Base64.encodeBase64String(signed);
        return result;
    }
    
	public static boolean signValidate(String value, String sign, String publicKey) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] encodedKey = Base64.decodeBase64(publicKey);
		PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
		Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
		signature.initVerify(pubKey);
		signature.update(value.getBytes());
		byte[] bytes = Base64.decodeBase64(sign);
		return signature.verify(bytes);
	}
	
	public static byte[] encrypt(String pkStr, byte[] input) throws Exception {
		byte[] encodedKey = Base64.decodeBase64(pkStr);
		KeySpec keySpec = new X509EncodedKeySpec(encodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pk = keyFactory.generatePublic(keySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, pk);
		byte[] result = cipher.doFinal(input);
		return result;
	}

	public static byte[] decrypt(String skStr, byte[] input) throws Exception {
		byte[] encodedKey = Base64.decodeBase64(skStr);
		KeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey sk = keyFactory.generatePrivate(keySpec);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, sk);
		byte[] result = cipher.doFinal(input);
		return result;
	}

}
