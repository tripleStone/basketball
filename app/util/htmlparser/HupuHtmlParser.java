package util.htmlparser;

import gsonmoudle.HupuMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.game.GameInfo;
import models.game.GamePlayerInfo;
import models.player.PlayerInfo;
import models.team.TeamMsg;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.io.xml.DocumentWriter;

import controllers.Application;

public class HupuHtmlParser {

	private static  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	private static  SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public static List<GameInfo> getScheduleMap(Document doc) throws ParseException{
		List<GameInfo>  result = new ArrayList<GameInfo>();
		Element tbody = doc.getElementsByTag("tbody").get(7);
		Elements trs = tbody.getElementsByTag("tr");
		
		for (Element tr :trs){
			if (tr.attr("class").equalsIgnoreCase("blod")){
				continue;
			}
			GameInfo game = new GameInfo();
			game.home_name = tr.getElementsByTag("td").get(1).getElementsByTag("a").get(0).text().trim();
			game.guest_name = tr.getElementsByTag("td").get(1).getElementsByTag("a").get(1).text().trim();
			String playDate = tr.getElementsByTag("td").get(4).text();
			game.play_date = sdf.parse(playDate);
			game.has_detail = 0;
			if (!tr.getElementsByTag("td").get(2).equals("-")){
				game.static_url =  tr.getElementsByTag("td").get(2).getElementsByTag("a").attr("href");
			}
			result.add(game);
		}
		return result;
	}
	
	public static  String getGameReport(String url){
		StringBuffer result = new StringBuffer("");
		Document doc;
		try {
			doc = UrlUtil.getURLContent(url);
			Element bfg = doc.getElementsByClass("content").get(0);
//			Element reportBox = bfg.getElementsByTag("div").get(2);
			Elements childs =  bfg.getElementsByTag("p");
			for (int i=0;i<childs.size()  ;i++){
				Element ele = childs.get(i);

				if (ele.tagName().equalsIgnoreCase("p") && ele.text().indexOf("竞猜球队胜负")<=0 && ele.text().indexOf("http")<=0){
					result.append("<p>  "+ele.text()+"</p>");
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return result.toString();
	}
	
	public static Map<String,String> getGameReports(Document doc){
//		String url = "http://nba.hupu.com/boxscore/boxscore.php?league=NBA&day="+day;
		Map<String,String> map = new HashMap<String, String>();
		String home = "";
		String away = "";
		String url = "";
		String key = "";
		String title = "";
		
		Elements boxes = doc.getElementsByAttributeValue("class", "list_box");
		for (Element box : boxes){
			Element homeEle = box.getElementsByAttributeValue("class", "team_vs_a_1 clearfix").get(0);
			home = homeEle.getElementsByTag("span").get(1).getElementsByTag("a").text();
			
			Element awayEle = box.getElementsByAttributeValue("class", "team_vs_a_2 clearfix").get(0);
			away = awayEle.getElementsByTag("span").get(1).getElementsByTag("a").text();
			
			Element a = box.getElementsByAttributeValue("class", "tips").get(0).getElementsByTag("a").get(0);
			key = home + "-" + away;
			title = a.text();
			url = "http://g.hupu.com" + a.attr("href");
			map.put(key, url);
			map.put(key+"_title", title);			
			
		}
		
		
		
//		Elements games = div.children();
//		
//		for (Element game : games){
//			Elements names = game.getElementsByAttributeValue("class", "name");
//			home =  names.get(0).text();
//			away =  names.get(1).text();
//			
//			url = "http://g.hupu.com" + game.getElementsByAttributeValue("class", "vsExplain").get(0).getElementsByTag("a").get(0).attr("href");
//			title = game.getElementsByAttributeValue("class", "vsExplain").get(0).getElementsByTag("a").get(0).text();
//			key = home + "-" + away;
//			map.put(key, url);
//			map.put(key+"_title", title);
//		}

//		
//		for (Element table : tables){			
//			Elements tds = table.getElementsByTag("tr").get(0).getElementsByTag("td");
//			Element td = tds.get(1);			
//			home =  td.getElementsByTag("a").get(0).attr("title");
//			td =  tds.get(4);
//			away =  td.getElementsByTag("a").get(0).attr("title");
//			td =  tds.get(2);
//			key = home + "-" + away;			
//			url = td.getElementsByTag("a").get(0).attr("href");			
//			map.put(key, url);			
//		}
		
		return map;
	}
	
	public static Long getTeamId(Document doc){
		Elements eles = doc.getElementsByAttributeValue("class", "blue");
		Pattern patt = Pattern.compile("\\d+");
		Long result = 0L;	
		
		for (Element ele:eles){
			if (ele.text().indexOf("新闻") >= 0){
				String href = ele.attr("href");
				Matcher matc = patt.matcher(href);
				if (matc.find()){
					result = Long.valueOf(matc.group());
				}	
			}
		}		
		return result;
	}
	
	
	public static  List<HupuMsg> getTeamMsgs(Document doc){
		List<HupuMsg> resultList = new ArrayList<HupuMsg>();
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		
		try {
			Elements childs =  doc.getElementsByAttributeValue("type", "text/javascript");
			for (Element child : childs){
				if (child.html().indexOf("var messages =") >= 0){
					String jsonStr = child.html();
					jsonStr = jsonStr.substring(jsonStr.indexOf("(")+1,jsonStr.lastIndexOf(")"));
					JsonArray rootObject = parser.parse(jsonStr).getAsJsonArray();
					resultList = gson.fromJson(rootObject, new TypeToken<List<HupuMsg>>(){}.getType());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultList;
	}

	
	
	public static void main(String[] args){
		try {
			Document doc = UrlUtil.getURLContent("http://g.hupu.com/nba/2013-10-30");
			getGameReports(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}	
}
