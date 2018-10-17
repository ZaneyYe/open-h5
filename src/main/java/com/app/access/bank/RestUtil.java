/**
 * 
 */
package com.app.access.bank;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RestUtil{
	
	private static Client c = null;
	private static Client secureClient =null;
	private static Gson gson = new Gson();
	
	static{
		initalizationJersyClient();
		initalizationSecureJersyClient();
	}
	
	private static void initalizationJersyClient(){
		try {
			c = Client.create();
			c.setFollowRedirects(true);
			c.setConnectTimeout(10000);
			c.setReadTimeout(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initalizationSecureJersyClient(){
		try {
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				@Override
				public void checkClientTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {}
				@Override
				public void checkServerTrusted(
				java.security.cert.X509Certificate[] chain,
				String authType) throws CertificateException {}
			} }, new SecureRandom());
			HostnameVerifier hv = new HostnameVerifier(){
		        public boolean verify(String urlHostName, SSLSession session){
		            return true;
		        }
		    };
		    
	        HTTPSProperties prop = new HTTPSProperties(hv, context);
	        DefaultClientConfig dcc = new DefaultClientConfig();
	        dcc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);
			secureClient = Client.create(dcc);
			secureClient.setFollowRedirects(true);
			secureClient.setConnectTimeout(10000);
			secureClient.setReadTimeout(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String sendPOSTRequest(String url, Object map, String contentTpye){
		if(null==c){
			initalizationJersyClient();
		}
		Client client = null;
		if(url.indexOf("https://") == 0){
			if(null==secureClient){
				initalizationJersyClient();
			}
			client = secureClient;
		}else{
			if(null==c){
				initalizationJersyClient();
			}
			client = c;
		}
		WebResource resource = client.resource(url);
		String resultStr = null;
		try {
			Builder builder = resource.accept("*/*");
			ClientResponse res = builder.type(contentTpye).entity(map).post(ClientResponse.class);
			if(res.getStatus() != 200){
				throw new Exception("url:"+url+",response code:" + res.getStatus());
			}
			resultStr = res.getEntity(String.class);
			System.out.println(resultStr);
			return resultStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String sendPostGson(String url, Map<String, String> map){
		String ps = gson.toJson(map);
		return sendPOSTRequest(url, ps, MediaType.APPLICATION_JSON);
	}

	public static String sendPostForm(String url, Map<String, String> map){
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		if(map != null && map.size() > 0){
			for(String key : map.keySet()){
				params.add(key, map.get(key));
			}
		}
		return sendPOSTRequest(url, params, MediaType.APPLICATION_FORM_URLENCODED);
	}
	
	
	
}
