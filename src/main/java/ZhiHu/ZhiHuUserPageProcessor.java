package ZhiHu;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class ZhiHuUserPageProcessor implements PageProcessor {
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000)
			.setDomain("www.zhihu.com")
			.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
			.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
			.addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8")
			.addHeader("Cache-Control", "max-age=0")
			.addHeader("Connection", "keep-alive")
			.addHeader("Referer", "https://www.zhihu.com/")
			.setCharset("UTF-8")
            .addCookie("__utma", "51854390.1957518131.1487995956.1487995956.1487995956.2")
            .addCookie("__utmb", "51854390.0.10.1487995956")
            .addCookie("__utmc", "51854390")
            .addCookie("__utmv", "51854390.100--|2=registration_date=20151008=1^3=entry_date=20151008=1")
            .addCookie("__utmz", "51854390.1487995956.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)")
            .addCookie("_xsrf", "b4c0254290cdabfdd4b94f4088fdbd48")
            .addCookie("_zap", "32e8cb85-a048-4b29-97e3-1877ebc2d4fd")
            .addCookie("aliyungf_tc", "AQAAAKiAD3C8aQQA0JoltxvKvEf65QM0")
            .addCookie("cap_id", "ODU1Mjk4MzM4NjUyNGI4NTgxNzIwMDQxYTY4MmY0MmY=|1487999661|93aa97b7ec9d4ceae1991f3e7ea776192e95dc6e")
            .addCookie("d_c0", "AICCnt_xXAuPTkb5LEvhzpUUmWCjPnASDyk")
            .addCookie("l_cap_id", "MmE3OTUyZjNjMzJlNGYwZmI2YTBkOGM2ZmVkMWRiNmY")
            .addCookie("nweb_qa", "heifetz")
            .addCookie("q_c1", "14890ac2265848fd9523786902dffc30")
            .addCookie("s-i", "1")
            .addCookie("s-q", "JAVA")
            .addCookie("sid", "qee7cjhg")
            .addCookie("z_c0", "Mi4wQUJCTTd6ZHgwUWdBZ0lLZTNfRmNDeGNBQUFCaEFsVk5GNkRZV0FEcTdBWGJMY2owX3M3WU1xblFuRGJaOUdGYkdR")
			;

	public Site getSite() {
		return site;
	}

	public void process(Page page) {
		page.putField("title", page.getHtml().xpath("//div[@id='zh-home-list-title']/text()").all());
	}

	public static void main(String[] args) throws JMException {
		
		Spider testSpider = Spider.create(new ZhiHuUserPageProcessor());
		
		testSpider.addUrl("https://www.zhihu.com/");
		testSpider.addPipeline(new ConsolePipeline());
	    testSpider.thread(5);
	    
	    //注册监控
	    SpiderMonitor.instance().register(testSpider);
	    
	    testSpider.start();
	    
	}
	
}
