package BaiHe;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLException;

import com.alibaba.fastjson.JSON;


public class FaceTest {
	
	private final static String detectUrl = "https://api-cn.faceplusplus.com/facepp/v3/detect";
	private final static String api_key = "EYvzPRMq61rhKq8u2iUesvtMw-0zJaKh";
	private final static String api_secret = "goW5yb-UgcGc0dKvQUwMPtzJ77vf70E0";
	private final static int CONNECT_TIME_OUT = 30000;
    private final static int READ_OUT_TIME = 50000;
    private static String boundaryString = getBoundary();
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected static byte[] post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
        HttpURLConnection conne;
        URL url1 = new URL(url);
        conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
        Iterator iter = map.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry) iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key
                    + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }
        if(fileMap != null && fileMap.size() > 0){
            Iterator fileIter = fileMap.entrySet().iterator();
            while(fileIter.hasNext()){
                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();
                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey()
                        + "\"; filename=\"" + encode(" ") + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.write(fileEntry.getValue());
                obos.writeBytes("\r\n");
            }
        }
        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();
        InputStream ins = null;
        int code = conne.getResponseCode();
        try{
            if(code == 200){
                ins = conne.getInputStream();
            }else{
                ins = conne.getErrorStream();
            }
        }catch (SSLException e){
            e.printStackTrace();
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int len;
        while((len = ins.read(buff)) != -1){
            baos.write(buff, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        ins.close();
        return bytes;
    }
    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }
    private static String encode(String value) throws Exception{
        return URLEncoder.encode(value, "UTF-8");
    }
    
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }
    
    public static double getInfo(String image_url){
    	//File file = new File("D:\\imgTest\\timg.jpg");
		//byte[] buff = getBytesFromFile(file);
		String url = detectUrl;
        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, byte[]> byteMap = new HashMap<String, byte[]>();
        map.put("api_key", api_key);
        map.put("api_secret", api_secret);
        //map.put("return_landmark", "1");
        map.put("return_attributes", "facequality,age");
        map.put("image_url", image_url);
        //byteMap.put("image_file", buff);
        try{
            byte[] bacd = post(url, map, byteMap);
            String str = new String(bacd);
            
            Map<String,Object> maps = JsonAnalysis(str);
            if(maps.isEmpty()){
            	return 0;
            }
            double value = Double.parseDouble(maps.get("value").toString());
            return value;
        }catch (Exception e) {
        	e.printStackTrace();
		}
        return 0;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String,Object> JsonAnalysis(String str){
    	Map<String,Object> facequality = new HashMap<String, Object>();
    	
    	Map<String,Object> maps = JSON.parseObject(str,HashMap.class);
    	if(maps.get("error_message")!=null){
    		String errorStr = maps.get("error_message").toString();
    		switch (errorStr) {
			case "AUTHENTICATION_ERROR":
				errorStr = "api_key和api_secret不匹配";
				break;
			case "CONCURRENCY_LIMIT_EXCEEDED":
				errorStr = "并发数超过限制。";
				break;
			case "COEXISTENCE_ARGUMENTS":
				errorStr = "同时传入了要求是二选一或多选一的参数。如有特殊说明则不返回此错误。";
				break;
			case "Request Entity Too Large":
				errorStr = "客户发送的请求大小超过了2MB限制。该错误的返回格式为纯文本，不是json格式。";
				break;
			case "API_NOT_FOUND":
				errorStr = "所调用的API不存在。";
				break;
			case "INTERNAL_ERROR":
				errorStr = "服务器内部错误，当此类错误发生时请再次请求，如果持续出现此类错误，请及时联系技术支持团队。";
				break;

			default:
				break;
			}
    		System.err.println(errorStr);
    		return new HashMap<String,Object>();
    	}
        List<Map<String,Object>> faceList = JSON.parseObject(maps.get("faces").toString(),ArrayList.class);
        
        if(faceList.isEmpty() || faceList.get(0).get("attributes") == null){
        	return facequality;
        }
        
        
        Map<String,Object> attributes = JSON.parseObject(faceList.get(0).get("attributes").toString(),HashMap.class);
        
        if(attributes.isEmpty() || attributes.get("facequality")==null){
        	return facequality;
        }
        
        facequality = JSON.parseObject(attributes.get("facequality").toString(),HashMap.class);
        
        return facequality;
        
    }
    
    public static void main(String[] args) throws Exception{
		getInfo("https://imgsa.baidu.com/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=f91f202c5b6034a83defb0d3aa7a2231/cefc1e178a82b9012aaa6a1e748da9773912ef9c.jpg");
	}

}
