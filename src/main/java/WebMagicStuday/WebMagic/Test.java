package WebMagicStuday.WebMagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class Test implements PageProcessor{
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

	public void process(Page page) {
		
		page.putField("name", page.getHtml().xpath("//dl[@class='geek_list']/dd/span/a/text()").toString());
		if (page.getResultItems().get("name") == null) {
            //skip this page
            page.setSkip(true);
        }
		
	}

	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) {
        Spider.create(new GithubRepoPageProcessor())
                .addUrl("http://geek.csdn.net/")
                .addPipeline(new JsonFilePipeline("D:\\webmagic\\"))
                //开启1个线程抓取
                .thread(1)
                //启动爬虫
                .run();
    }

}
