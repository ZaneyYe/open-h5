package com.app.access.bank;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;



public class SNUtil {

	public static Gson gson = new Gson();

	/**
	 * 解析应答结果
	 * @param resultStr	应答结果
	 * @return	SN
	 */
	@SuppressWarnings("unchecked")
	public static String analysisResultStr(String resultStr){
		//解析应答结果
		Map<String, Object> resultMap = gson.fromJson(resultStr, Map.class);
		if(resultMap != null && resultMap.size() > 0){
			Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("params");
			if(dataMap != null && dataMap.size() > 0){
				return String.valueOf(dataMap.get("sn"));
			}
		}
		return null;
	}

	/**
	 * 根据用户ID查询用户并返回SN
	 * @param host	域名
	 * @param appId	接入方ID
	 * @param insUsrId	用户ID
	 * @param chnl	渠道 1-银行APP，2-银行公众号
	 * @param signKey	签名密钥
	 * @param isHttps	是否使用https
	 * @return resultStr
	 * @throws Exception
	 */
	public static String findUser(String host, String appId, String insUsrId, Chnl chnl, String signKey, boolean isHttps) throws Exception{
		//拼装参数Map
		Map<String, String> params = new HashMap<String, String>(0);
		params.put("appId", appId);
		params.put("indUsrId", insUsrId);
		String nonceStr = createNonceStr();
		params.put("nonceStr", nonceStr);
		String timestamp = String.valueOf(System.currentTimeMillis()/1000);
		params.put("timestamp", timestamp);
		params.put("chnl", chnl.getCode());

		//签名
		String signature = sign(params, signKey);
		params.put("signature", signature);

		//发送请求
		String url = null;
		if(isHttps){
			url = "https://" + host + "/app/access/bank/findUser";
		}else{
			url = "http://" + host + "/app/access/bank/findUser";
		}
		String resultStr = RestUtil.sendPostGson(url, params);
		return resultStr;
	}

	/**
	 * 绑定用户并返回SN(请注意证件类型列表和卡号列表)
	 * @param host	域名
	 * @param appId	接入方ID
	 * @param cardNos	卡号列表
	 * @param accTypes	账户类型：01-为Ⅰ类账户，02-为Ⅱ类账户，03-为Ⅲ类账户，若上送的卡号有多个，上送的账户类型就也应该有多个，且排序要和卡号一一对应
	 * @param mobile	手机号码
	 * @param realNm	真实姓名
	 * @param certType	证件类型 01-身份证，03-护照，04-回乡证，05-台胞证
	 * @param certifId	证件号码
	 * @param insUsrId	用户ID
	 * @param chnl	渠道 1-银行APP，2-银行公众号
	 * @param signKey	签名密钥
	 * @param publicKey	非对称公钥
	 * @param isHttps	是否使用https
	 * @return	resultStr
	 * @throws Exception
	 */
	public static String bindUser(String host, String appId, List<String> cardNos, List<AccType> accTypes, String mobile, 
			String realNm, CertType certType, String certifId, String insUsrId, Chnl chnl, 
			String signKey, String publicKey, boolean isHttps) throws Exception{
		//生成对称密钥
		String symmetricKey = DESedeSymmeticKeyGenerator.getInstance().genHexSecretKey();

		//准备参数Map
		Map<String, String> params = new HashMap<String, String>(0);
		params.put("appId", appId);

		//拼接卡号和账户类型
		String accTypeStr = "";
		String cardNoStr = "";
		if(accTypes == null){
			accTypeStr = AccType.ACCTYPE1.getCode();
			cardNoStr = cardNos.get(0);
		}else{
			if(accTypes.size() <= 1){
				accTypeStr = accTypes.get(0).getCode();
				cardNoStr = cardNos.get(0);
			}else{
				for(int i = 0 ; i < accTypes.size() ; i ++){
					if(i > 0){
						accTypeStr = accTypeStr + "|";
						cardNoStr = cardNoStr + "|";
					}
					accTypeStr = accTypeStr + accTypes.get(i).getCode();
					cardNoStr = cardNoStr + cardNos.get(i);
				}
			}
		}
		params.put("accType", accTypeStr);
		params.put("cardNo", cardNoStr);
		params.put("mobile", mobile);
		params.put("realNm", realNm);
		params.put("certType", certType.getCode());
		params.put("certifId", certifId);
		params.put("indUsrId", insUsrId);
		String nonceStr = createNonceStr();
		params.put("nonceStr", nonceStr);
		String timestamp = String.valueOf(System.currentTimeMillis()/1000);
		params.put("timestamp", timestamp);
		params.put("chnl", chnl.getCode());

		//签名
		String signature = sign(params, signKey);
		params.put("signature", signature);

		//针对敏感字段加密
		encryptedParamMap(symmetricKey, params, "accType", "cardNo", "mobile", "realNm", "certType", "certifId");

		//使用公钥加密对称密钥
		byte[] keys = BytesUtil.hexToBytes(symmetricKey);
		keys = encrypt(publicKey, keys);
		symmetricKey = Base64.encodeBase64String(keys);
		params.put("symmetricKey", symmetricKey);

		//发送请求
		String url = null;
		if(isHttps){
			url = "https://" + host + "/app/access/bank/bindUser";
		}else{
			url = "http://" + host + "/app/access/bank/bindUser";
		}
		String resultStr = RestUtil.sendPostGson(url, params);
		return resultStr;
	}

	/**
	 * 使用公钥加密对称密钥
	 * @param publicKey	公钥
	 * @param symmetricKeyByte	对称密钥字节
	 * @return	加密后的对称密钥字节
	 * @throws Exception
	 */
	public static byte[] encrypt(String publicKey, byte[] symmetricKeyByte) throws Exception {
		byte[] encodedKey = Base64.decodeBase64(publicKey);
		KeySpec keySpec = new X509EncodedKeySpec(encodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pk = keyFactory.generatePublic(keySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, pk);
		byte[] result = cipher.doFinal(symmetricKeyByte);
		return result;
	}

	private static void encryptedParamMap(String key, Map<String, String> params, String ... encryptKeys) throws Exception{
		if(encryptKeys != null && encryptKeys.length > 0){
			for(String encryptKey : encryptKeys){
				params.put(encryptKey, getEncryptedValue(params.get(encryptKey), key));
			}
		}
	}

	/**
	 * 3DES加密
	 * @param value	待加密的字符串
	 * @param key	加密密钥
	 * @return	加密后的字符串
	 * @throws Exception
	 */
	private static String getEncryptedValue(String value, String key) throws Exception { 
		if (null == value || "".equals(value)) { 
			return ""; 
		} 
		byte[] valueByte = value.getBytes(); 
		byte[] sl = encrypt3DES(valueByte, BytesUtil.hexToBytes(key)); 
		String result = Base64.encodeBase64String(sl); 
		// String result = BytesUtil.bytesToHex(sl); 
		return result; 
	} 

	/**
	 * 3DES加密
	 * @param input	待加密的字节
	 * @param key	密钥
	 * @return	加密后的字节
	 * @throws Exception
	 */
	private static byte[] encrypt3DES(byte[] input, byte[] key) throws Exception { 
		Cipher c = Cipher.getInstance("DESede/ECB/PKCS5Padding"); 
		c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "DESede")); 
		return c.doFinal(input); 
	}

	/**
	 * 获取随机字符串
	 * @return	随机字符串
	 */
	private static String createNonceStr(){
		String sl = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < 16 ; i ++){
			sb.append(sl.charAt(new Random().nextInt(sl.length())));
		}
		return sb.toString();
	}

	/**
	 * 签名
	 * @param param	待签名的参数
	 * @param signKey	签名密钥
	 * @return	签名结果字符串
	 * @throws Exception
	 */
	private static String sign(Map<String, String> param, String signKey) throws Exception {
		String value = sortMap(param);
		byte[] keyBytes = Base64.decodeBase64(signKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(keySpec);
		Signature signature = Signature.getInstance("SHA256WithRSA");
		signature.initSign(priKey);
		signature.update(value.getBytes());
		byte[] signed = signature.sign();
		String result = Base64.encodeBase64String(signed);
		return result;
	}

	/**
	 * 排序
	 * @param param	待排序的参数
	 * @return	排序结果字符串
	 */
	private static String sortMap(Map<String, String> param){
		StringBuilder result = new StringBuilder();
		Collection<String> keySet = param.keySet();
		List<String> list = new ArrayList<String>(keySet);
		Collections.sort(list);
		for (int i = 0; i < list.size(); ++i) {
			String key = list.get(i);
			if("symmetricKey".equals(key)){
				continue;
			}
			if(param.get(key) == null || "".equals(param.get(key).trim())){
				continue;
			}
			result.append(key).append("=").append(param.get(key)).append("&");
		}
		return result.substring(0, result.length() - 1);
	}

}
