package com.zxz.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class WeiXinUtil {

	private static Logger logger = Logger.getLogger(WeiXinUtil.class);  
	
//	static final String appid = "wx2ffdd6142f15121d";//楹诲皢娴嬭瘯
//	static final String appsecret = "c931b873b47992135fc646e6b95ce215";//楹诲皢娴嬭瘯
	
	
	static final String appid = "wx94f72c9b837792b7";//
	static final String appsecret = "02579f457f8e66f3d510831340f31591";//
	
	
	static Map<String,Map<String, Object>> openId_access_tokenMap_web = null;
	
	
	public static void main(String[] args) {
//		WeiXinUtil util = new WeiXinUtil();
//		JSONObject weinxinOpenIdJson = WeiXinUtil.getAccessTokenJson("041CEQ500MTHHB1jn5600ySR500CEQ5w");
//		String openid = weinxinOpenIdJson.getString("openid");
//		System.out.println("openid:"+openid);
//		String access_token = weinxinOpenIdJson.getString("access_token");
//		System.out.println("access_token:"+access_token);
//		JSONObject userInfo = getUserInfo(access_token, openid);
//		System.out.println("userInfo:"+userInfo);
//		String accessToken = WeiXinUtil.getAccessTokenWithRefreshToken("Ajc0EXxE_28PebMcoqzyRHWmtSNlAmCLTskDHVzpWaPZJttSe6Si2nB5LyHVX2uSzfCTNAMqh7qNhOEPDgBGrz1W4O_zq6zkQ-1i2ZfkXEo");
//		System.out.println(accessToken);
//		JSONObject userInfo = getUserInfo(accessToken, "obhqFxLZ4us8x2300oPIf5fVFN3M");
//		System.out.println(userInfo);
		
		String sendPost = sendPost("http://exc.ihekdzws.cn/disc/queryLotteryResult", null);
//		String sendPost = sendPost("http://www.baidu.com", null);
		System.out.println(sendPost);
	}
	
	
	/**鏍规嵁refreshToken鑾峰彇access_token
	 * @param refreshToken
	 * @return
	 */
	public static String getAccessTokenWithRefreshToken(String refreshToken){
		String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+appid+"&grant_type=refresh_token&refresh_token="+refreshToken;
		String returnStr = sendPost(url, null);
		JSONObject outJsonObject = new JSONObject(returnStr);
		if(outJsonObject.has("errcode")){
			throw new IllegalArgumentException(outJsonObject.toString());
		}
		return outJsonObject.getString("access_token");
	}
	
	/**
	 * 鑾峰彇openid
	 * @param code
	 * @return
	 */
	public static JSONObject getAccessTokenJson(String code){
		String returnStr = "";
		if(code!=null&&!code.trim().equals("")){
			String getopenId_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";
//			returnStr = httpRequest(getopenId_url, "POST", null);
			returnStr = sendPost(getopenId_url, null);
		}
		logger.info("寰俊鎺堟潈杩斿洖:"+returnStr);
		JSONObject responseJson = new JSONObject(returnStr);
		if(responseJson.has("errcode")){
			logger.fatal("寰俊鎺堟潈:"+code);
			throw new IllegalArgumentException(responseJson.getString("errmsg"));
		}
		return responseJson;
	}
	
	
	/**妫�楠屾巿鏉冨嚟璇佹槸鍚︽湁鏁�
	 * @param openid
	 * @return
	 */
	public boolean isAccessTokenEffective(String openid){
		String url = "https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid="+openid;
//		String returnStr = httpRequest(url, "POST", null);
		String returnStr = sendPost(url, null);
		logger.info("妫�楠屾巿鏉冨嚟璇侊紙access_token锛夋槸鍚︽湁鏁�:"+returnStr);
		JSONObject jsonObject = new JSONObject(returnStr);
		int errorcode = jsonObject.getInt("errcode");
		if(errorcode==0){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 寰楀埌鐢ㄦ埛鐨勪俊鎭�
	 */
	public static JSONObject getUserInfo(String accesstoekn,String openid){
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+accesstoekn+"&openid="+openid;
		//System.out.println("url:"+url);
		String returnStr = httpRequest(url, "POST", null);
//		String returnStr = sendPost(url,  null);
		//System.out.println("鑾峰彇鍒扮殑鐢ㄦ埛淇℃伅"+returnStr);
		JSONObject jsonObject = new JSONObject(returnStr);
		if(jsonObject.has("errcode")){
			logger.fatal("鑾峰彇鐢ㄦ埛淇℃伅鐨勬椂鍊欏嚭閿�:"+jsonObject.getString("errcode"));
			throw new IllegalArgumentException(jsonObject.getString("errcode"));
		}
		return jsonObject;
	}
	
	
	 public static String sendPost(String url, Map<String,String> params) {
	        PrintWriter out = null;
	        BufferedReader in = null;
	        String result = "";
	        try {
//	        	param = "method=鍖椾含";
	            URL realUrl = new URL(url);
	            StringBuffer paramBuffer = new StringBuffer();
	            if(params!=null&&params.size()>0){
	            	Iterator<Entry<String, String>> it = params.entrySet().iterator();
	            	int count = 0;
	            	while(it.hasNext()){
	            		Entry<String, String> ent = it.next();
	            		if(ent.getKey()!=null&&ent.getValue()!=null){
	            			if(count!=0){
		            			paramBuffer.append("&"+ent.getKey()+"="+URLEncoder.encode(ent.getValue(), "utf-8"));
		            		}else{
		            			paramBuffer.append(ent.getKey()+"="+URLEncoder.encode(ent.getValue(), "utf-8"));
		            		}
		            		count++;
	            		}
	            	}
	            }
	            // 鎵撳紑鍜孶RL涔嬮棿鐨勮繛鎺�
	            URLConnection conn = realUrl.openConnection();
	            // 璁剧疆閫氱敤鐨勮姹傚睘鎬�
	            conn.setRequestProperty("Accept-Charset", "utf-8");
	            conn.setRequestProperty("contentType", "utf-8");
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 鍙戦�丳OST璇锋眰蹇呴』璁剧疆濡備笅涓よ
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            // 鑾峰彇URLConnection瀵硅薄瀵瑰簲鐨勮緭鍑烘祦
	            out = new PrintWriter(conn.getOutputStream());
	            // 鍙戦�佽姹傚弬鏁�
	            out.print(paramBuffer.toString());
	            // flush杈撳嚭娴佺殑缂撳啿
	            out.flush();
	            // 瀹氫箟BufferedReader杈撳叆娴佹潵璇诲彇URL鐨勫搷搴�
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("鍙戦�� POST 璇锋眰鍑虹幇寮傚父锛�"+e);
	            e.printStackTrace();
	        }
	        //浣跨敤finally鍧楁潵鍏抽棴杈撳嚭娴併�佽緭鍏ユ祦
	        finally{
	            try{
	                if(out!=null){
	                    out.close();
	                }
	                if(in!=null){
	                    in.close();
	                }
	            }
	            catch(IOException ex){
	                ex.printStackTrace();
	            }
	        }
	        return result;
	    }   

	
	
	/** 
	 * 鍙戣捣https璇锋眰骞惰幏鍙栫粨鏋� 
	 * @param requestUrl 璇锋眰鍦板潃 
	 * @param requestMethod 璇锋眰鏂瑰紡锛圙ET銆丳OST锛� 
	 * @param outputStr 鎻愪氦鐨勬暟鎹� 
	 * @return JSONObject(閫氳繃JSONObject.get(key)鐨勬柟寮忚幏鍙杍son瀵硅薄鐨勫睘鎬у��) 
	 */
	private static String httpRequest(String requestUrl, String requestMethod, String xml) {
		String resultStr = "";
		StringBuffer buffer = new StringBuffer();
		try {
			// 鍒涘缓SSLContext瀵硅薄锛屽苟浣跨敤鎴戜滑鎸囧畾鐨勪俊浠荤鐞嗗櫒鍒濆鍖�  
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 浠庝笂杩癝SLContext瀵硅薄涓緱鍒癝SLSocketFactory瀵硅薄  
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setRequestMethod(requestMethod);
			httpUrlConn.connect();
			if(xml!=null){
				OutputStream os = httpUrlConn.getOutputStream();
				os.write(xml.toString().getBytes("utf-8"));
				os.close();
			}
			// 灏嗚繑鍥炵殑杈撳叆娴佽浆鎹㈡垚瀛楃涓�  
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 閲婃斁璧勬簮  
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			resultStr = buffer.toString();
		} catch (ConnectException ce) {
		} catch (Exception e) {
		}
		return resultStr;
	}
	
}
