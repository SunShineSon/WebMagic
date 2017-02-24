package WebMagicStuday.WebMagic;

import javax.management.JMException;

import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

public class ProxyPageProcessor implements PageProcessor {
	
	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000)
    		.setHttpProxy(new HttpHost("47.89.15.79",11011)).setUsernamePasswordCredentials(new UsernamePasswordCredentials("caogen"));

	public void process(Page page) {
		// 部分二：定义如何抽取页面信息，并保存下来
    	page.putField("title", page.getHtml().xpath("//h3[@class='r']/a/text()").all());
        if (page.getResultItems().get("title") == null) {
            //skip this page
            page.setSkip(true);
        }
        
	}

	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	public static void main(String[] args) throws JMException {
		Spider proxySpider = Spider.create(new ProxyPageProcessor());
    	
		proxySpider.addUrl("https://plus.google.com/u/0/");
		proxySpider.thread(5);
        
        SpiderMonitor.instance().register(proxySpider);
        
        proxySpider.start();
	}
	
}
