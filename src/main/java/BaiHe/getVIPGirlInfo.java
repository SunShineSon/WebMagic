package BaiHe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.JMException;

import com.alibaba.fastjson.JSON;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

/**
 * 因为百合网的优质会员页面是ajax生成的，所以需要分析ajax调用接口，然后获取json数据解析
 * @author Administrator
 *
 */
public class getVIPGirlInfo implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000)
			.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

    public void process(Page page) {
    	
    	Pattern r = Pattern.compile("(?<=\\()(.+?)(?=\\))");
    	Matcher m = r.matcher(page.getRawText());
    	if (m.find( )) {
            List<String> list = new JsonPathSelector("$.result.members[*]").selectList(m.group(0));
            String girlInfo;
            for(int i = 0;i<list.size();i++){
            	girlInfo = list.get(i);
            	VIPGirl girlObject = JSON.parseObject(girlInfo,VIPGirl.class);
            	
            	String url = girlObject.getHeadUrl();
        		double value = FaceTest.getInfo(url);
        		if(value>80){
        			String imgFileName = url.substring(url.lastIndexOf("/")+1);
        			
        			String[] fileName = imgFileName.split("\\.");
        			
        			StringBuffer str = new StringBuffer(fileName[0]+"("+value+")");
        			str.append("."+fileName[1]);
                	
                	byte[] btImg = GetImage.getImageFromNetByUrl(url);  
                    if(null != btImg && btImg.length > 0){  
                        System.out.println("读取到：" + btImg.length + " 字节");  
                        GetImage.writeImageToDisk(btImg, str.toString());  
                    }else{  
                        System.out.println("没有从该连接获得内容");  
                    }  
        		}
            }
            
         } 
    	
    	page.addTargetRequest("http://crm.baihe.com/baihe-services/vipInfo/getVipMemberList?jsoncallback=jQuery18308372247013949272_1488177231251&pageSize=50&page=2&gender=1");

    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws JMException {
    	
    	Spider testSpider = Spider.create(new getVIPGirlInfo());
    	
    	testSpider.addUrl("http://crm.baihe.com/baihe-services/vipInfo/getVipMemberList?jsoncallback=jQuery18308372247013949272_1488177231251&pageSize=50&page=1&gender=1");
    	testSpider.addPipeline(new ConsolePipeline());
        testSpider.thread(5);
        
        //注册监控
        SpiderMonitor.instance().register(testSpider);
        
        testSpider.start();
        
    }

}
