package WebMagicStuday.WebMagic;

import javax.management.JMException;

import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private Jedis jedis = RedisPool.getJedis();

    public void process(Page page) {
    	
    	//保存结果title，这个结果会最终保存到ResultItems中
    	page.putField("title", page.getHtml().xpath("//h1[@id='questionTitle']/a/text()").toString());
    	if (page.getResultItems().get("title")==null){
            //设置skip之后，这个页面的结果不会被Pipeline处理
            page.setSkip(true);
        }
        page.putField("author", page.getHtml().xpath("//div[@class='question__author']/a/strong/text()").toString());
    	
        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().links().regex("(https://segmentfault\\.com/q/\\w+)").all());
        
        if(page.getResultItems().get("title")!=null){
        	String title = page.getResultItems().get("title").toString();
            String author = page.getResultItems().get("author").toString();
            System.err.println(title + ":" + author);
            jedis.hset("WebMagic", title, author);
        }

    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws JMException {
    	
    	Spider testSpider = Spider.create(new TestPageProcessor());
    	
    	testSpider.addUrl("https://segmentfault.com/");
    	testSpider.addPipeline(new ConsolePipeline()).addPipeline(new FilePipeline("D:\\webmagic\\"));
        testSpider.thread(5);
        
        //注册监控
        SpiderMonitor.instance().register(testSpider);
        
        testSpider.start();
        
    }
}
