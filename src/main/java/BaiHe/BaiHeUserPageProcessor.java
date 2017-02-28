package BaiHe;

import java.util.ArrayList;
import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class BaiHeUserPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    public void process(Page page) {
    	page.putField("url", page.getHtml().xpath("//div[@class='hUserItem']/img/@src").all());
    	
    	ArrayList<String> list = page.getResultItems().get("url");
    	
    	for(int i = 0;i < list.size(); i++){
    		String url = list.get(i); 
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

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws JMException {
    	
    	Spider testSpider = Spider.create(new BaiHeUserPageProcessor());
    	
    	testSpider.addUrl("http://www.baihe.com/");
    	testSpider.addPipeline(new ConsolePipeline());
        testSpider.thread(5);
        
        //注册监控
        SpiderMonitor.instance().register(testSpider);
        
        testSpider.start();
        
    }

}
