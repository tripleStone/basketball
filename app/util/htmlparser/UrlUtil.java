package util.htmlparser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.game.GameInfo;
import models.game.GamePlayerInfo;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;
import models.player.PlayerInfo;
import models.team.TeamInfo;

public class UrlUtil  {
	
	private static  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");


	public static List<LotteryWinPoint> getLotteries(String url) throws Exception{
		List<LotteryWinPoint> lotteries = new ArrayList<LotteryWinPoint>();
		Document doc = Jsoup.connect(url)
				  .userAgent("Mozilla")
				  .timeout(30000)
				  .get();
		 Element table = doc.getElementsByTag("table").get(1);
		 Elements trs = table.getElementsByTag("tr");
		 trs.remove(0);
		 for (Element tr:trs){
			 Elements tds = tr.getElementsByTag("td");
			 String name = tds.get(1).text();
			 if (tds.get(1).text().equals("美国职业篮球联盟")){
				 LotteryWinPoint lottery = new LotteryWinPoint();
				 String teams = tds.get(2).getElementsByTag("a").text();
				 lottery.host_team = NbaLotteryHtmlParser.changeTeamName(teams.split("VS")[1].trim()+"队");
				 lottery.guest_team = NbaLotteryHtmlParser.changeTeamName(teams.split("VS")[0].trim() +"队");
				 
				 String gameDate = "20" +tds.get(3).text();
				 lottery.game_date = sdf.parse(gameDate);
				 
				 String[] rate =  tds.get(5).text().split(" ");
				 lottery.host_1to5 = Double.valueOf(rate[1].trim()).doubleValue();
				 lottery.guest_1to5 = Double.valueOf(rate[0].trim()).doubleValue();
				 rate =  tds.get(6).text().split(" ");
				 lottery.host_6to10 = Double.valueOf(rate[1].trim()).doubleValue();
				 lottery.guest_6to10 = Double.valueOf(rate[0].trim()).doubleValue();
				 
				 rate =  tds.get(7).text().split(" ");
				 lottery.host_11to15 = Double.valueOf(rate[1].trim()).doubleValue();
				 lottery.guest_11to15 = Double.valueOf(rate[0].trim()).doubleValue();
				 rate =  tds.get(8).text().split(" ");
				 lottery.host_16to20 = Double.valueOf(rate[1].trim()).doubleValue();
				 lottery.guest_16to20 = Double.valueOf(rate[0].trim()).doubleValue();
				 
				 rate =  tds.get(9).text().split(" ");
				 lottery.host_21to25 = Double.valueOf(rate[1].trim()).doubleValue();
				 lottery.guest_21to25 = Double.valueOf(rate[0].trim()).doubleValue();				 
				 rate =  tds.get(10).text().split(" ");
				 lottery.host_26 = Double.valueOf(rate[1].trim()).doubleValue();
				 lottery.guest_26 = Double.valueOf(rate[0].trim()).doubleValue();
				 
				 lotteries.add(lottery);
			 }
			 
		 }
		 return lotteries;
	}
	
	public static List<LotteryHandicap> getLotteryHandicap(String url) throws Exception{
		List<LotteryHandicap> lotteries = new ArrayList<LotteryHandicap>();
		Document doc = Jsoup.connect(url)
				  .userAgent("Mozilla")
				  .timeout(30000)
				  .get();
		 Element basElement = doc.getElementsByTag("table").get(1);
		 Elements trElements = basElement.getElementsByTag("tr");
		 trElements.remove(0);
		 for (Element tr : trElements){
			 Elements tds  = tr.getElementsByTag("td");
			 String tmp = tds.get(1).text();
			 if  (!tmp.equals("美国职业篮球联盟")) {
				  continue;
			 }
			 LotteryHandicap handicap = new LotteryHandicap();
			 String teams = tds.get(2).getElementsByTag("a").text();
			 tmp = teams.split("VS")[1].trim();
			 handicap.handicap = Double.valueOf(tmp.substring(tmp.indexOf("(")+1,tmp.indexOf(")")));
			 handicap.host_name = NbaLotteryHtmlParser.changeTeamName(tmp.substring(0,tmp.lastIndexOf("("))  +"队");
			 handicap.away_name = NbaLotteryHtmlParser.changeTeamName(teams.split("VS")[0].trim() +"队");
			 
			 String gameDate ="20" + tds.get(3).text(); 
			 handicap.game_date = sdf.parse(gameDate);			 
			 handicap.lose = Double.valueOf(tds.get(4).text());
			 handicap.win = Double.valueOf(tds.get(5).text());
			 lotteries.add(handicap);
			 
		 }
		 return lotteries;
	}
	
	public static List<LotteryHilo> getLotteryHilo(String url) throws Exception{
		List<LotteryHilo> lotteries = new ArrayList<LotteryHilo>();
		Document doc = Jsoup.connect(url)
				  .userAgent("Mozilla")
				  .timeout(30000)
				  .get();
		 Element basElement = doc.getElementsByTag("table").get(1);
		 Elements trElements = basElement.getElementsByTag("tr");
		 trElements.remove(0);
		 for (Element tr : trElements){
			 Elements tds  = tr.getElementsByTag("td");
			 String tmp = tds.get(1).text();
			 if  (!tmp.equals("美国职业篮球联盟")) {
				  continue;
			 }
			 LotteryHilo handicap = new LotteryHilo();
			 String teams = tds.get(2).getElementsByTag("a").text();
			 tmp = teams.split("VS")[1].trim();
//			 String hilo =tmp.substring(tmp.length() - 5 ,tmp.length());
			 handicap.hilo = Double.valueOf(tmp.substring(tmp.length() - 5 ,tmp.length()));
			 handicap.host_name = NbaLotteryHtmlParser.changeTeamName(tmp.substring(0,tmp.length() - 5)  +"队");
			 handicap.away_name = NbaLotteryHtmlParser.changeTeamName(teams.split("VS")[0].trim() +"队");
			 String gameDate = "20" +  tds.get(3).text(); 
			 handicap.game_date = sdf.parse(gameDate);
			 
			 handicap.greater_than = Double.valueOf(tds.get(4).text());
			 handicap.less_than = Double.valueOf(tds.get(5).text());
			 lotteries.add(handicap);
		 }
		 return lotteries;
	}

	public static List<LotteryWinLoss> getLotteryWin(String url) throws Exception{
		List<LotteryWinLoss> lotteries = new ArrayList<LotteryWinLoss>();
		Document doc = Jsoup.connect(url)
				  .userAgent("Mozilla")
				  .timeout(30000)
				  .get();
		Element basElement = doc.getElementsByTag("table").get(1);
		 Elements trElements = basElement.getElementsByTag("tr");
		 trElements.remove(0);
		 for (Element tr : trElements){
			 Elements tds  = tr.getElementsByTag("td");
			 String tmp = tds.get(1).text();
			 if  (!tmp.equals("美国职业篮球联盟")) {
				  continue;
			 }
			 LotteryWinLoss winLose = new LotteryWinLoss();
			 String teams = tds.get(2).getElementsByTag("a").text();
//			 String hilo =tmp.substring(tmp.length() - 5 ,tmp.length());
//			 winLose.hilo = Double.valueOf(tmp.substring(tmp.length() - 5 ,tmp.length()));
			 winLose.host_name = NbaLotteryHtmlParser.changeTeamName(teams.split("VS")[1].trim() +"队");
			 winLose.away_name = NbaLotteryHtmlParser.changeTeamName(teams.split("VS")[0].trim() +"队");
			 String gameDate = "20" +  tds.get(3).text(); 
			 winLose.game_date = sdf.parse(gameDate);
			 
			 winLose.guest_win = Double.valueOf(tds.get(4).text());
			 winLose.home_win = Double.valueOf(tds.get(5).text());
			 lotteries.add(winLose);
		 }
		 return lotteries;
	}
	
	
	
	
	
	
	public static Document getURLContent(String url) throws IOException {
		Document doc = Jsoup.connect(url)
		  .userAgent("Mozilla")
		  .timeout(60000)
		  .get();
		return doc;
	}
	
	public static Document getURLContentPost(String url,Map<String,String> para) throws IOException {
		Document doc = Jsoup.connect(url).data(para)
		  .userAgent("Mozilla")
		  .timeout(30000)
		  .post();
		return doc;
	}
	
	public static void main(String[] args){

		try {
//			Map<String,String> para = new HashMap<String,String>();
//			para.put("start_date","2013-03-15");
//			para.put("end_date","2013-03-15");
//			para.put("lid","1");
//			Document doc= getURLContentPost("http://info.sporttery.cn/basketball/match_result.php",para);
//			System.out.println(doc.html());
//			Map<String,String> para = new HashMap<String,String>();
//			para.put("start_date","2013-03-15");
//			para.put("end_date","2013-03-15");
//			para.put("lid","1");	
			

			Map<String,String> para = new HashMap<String,String>();
			para.put("userId", "1001");
			para.put("grade", "一年级");
			Document doc = getURLContentPost("http://221.130.6.212:4889/zypt/web/queryUserZy.action",para);
			System.out.println(doc.body());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

	
	 public   static   String StringFilter(String   str)   throws   PatternSyntaxException   {     
         // 只允许字母和数字       
         // String   regEx  =  "[^a-zA-Z0-9]";                     
       // 清除掉所有特殊字符  
	   String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  
	   Pattern   p   =   Pattern.compile(regEx);     
	   Matcher   m   =   p.matcher(str);     
	   return   m.replaceAll("").trim();     
	}
	 
	 

	
}
