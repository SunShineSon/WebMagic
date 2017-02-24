package WebMagicStuday.WebMagicAnnotation;

import java.util.List;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;

@TargetUrl("https://segmentfault.com/q/\\w+")
@HelpUrl("https://segmentfault.com/q/\\w+")
public class Model {
	
	@ExtractBy(value = "//h1[@id='questionTitle']/a/text()", notNull=true)
	private List<String> title;
	
	@ExtractBy(value = "//div[@class='question__author']/a/strong/text()", notNull=true)
	private List<String> author;
	

	public static void main(String[] args) {
        OOSpider.create(Site.me().setRetryTimes(3).setSleepTime(1000)
                , new ConsolePageModelPipeline(), Model.class)
                .addUrl("https://segmentfault.com/").addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(5).run();
    }

}
