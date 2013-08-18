package util.htmlparser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import models.game.GameInfo;
import models.game.GameInfoStatic;
import models.game.GamePlayerInfo;
import models.game.GameQuarterScore;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.Logger;


public class NbaChinaHtmlParser {
	
	private static  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

	public static Map<String,String> getGames(Document doc){
		Map<String,String> result = new HashMap<String,String>();
		Elements tables = doc.getElementsByAttributeValue("class", "gameScoreBoardContainer");
		for (Element table : tables){
			String status = table.getElementsByAttributeValue("class","status").get(0).text();
//			Logger.info("#%s#", status.trim());
			if (status.indexOf("结束") >= 0){
				String away = table.getElementsByAttributeValue("class","odd").get(0).getElementsByAttributeValue("class","statsText").get(0).text();				
				String home = table.getElementsByAttributeValue("class","even").get(0).getElementsByAttributeValue("class","statsText").get(0).text();
				String Links = table.getElementsByAttributeValue("class","links").get(0).getElementsByTag("a").get(1).attr("href");
				Logger.info("#%svs%s  url is %s in NbaChinaHtmlParser  !", away,home,Links);
				result.put(away+"-"+home, Links);
			}
		}
		return result;
	}
	
	
	public static GameInfo getGameInfo(Document doc) {
		GameInfo gameInfo = new GameInfo();
		String gameDate = doc.getElementsByClass("gameDate").get(0).text();
		try {
			gameInfo.play_date = sdf.parse(gameDate);
			String home_name = doc.getElementsByClass("boxscoreDetailContainer").get(1).getElementsByTag("td").get(1).text();
			gameInfo.home_name = home_name.split(" ")[1];
			String guest_name = doc.getElementsByClass("boxscoreDetailContainer").get(0).getElementsByTag("td").get(1).text();
			gameInfo.guest_name = guest_name.split(" ")[1];
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return gameInfo;
	}
	
	
	public static List<GamePlayerInfo> getPlayers(Document doc,int i) throws Exception{
		List<GamePlayerInfo> resultList = new ArrayList<GamePlayerInfo>();
		//球员信息
		 Element  element = doc.getElementsByClass("boxscoreDetailContainer").get(i);
		 Elements trEles = element.getElementsByTag("tr");
		 int j = 1;		 
		 for (Element trEle : trEles){
			 if ( !trEle.attr("class").equals("odd") && !trEle.attr("class").equals("even"))
				 continue;
			 Elements tdEles = trEle.getElementsByTag("td");
			 if (tdEles == null || tdEles.size() == 0){
				 continue;
			 }
			 GamePlayerInfo gamePlayerInfo = new GamePlayerInfo();	 
			 if (tdEles.get(1).attr("class").equals("dnp")){
				 gamePlayerInfo.player_name = tdEles.get(0).text();
				 gamePlayerInfo.time = -1;
			 }else {	 
				 String playName =  tdEles.get(0).text();
				 if (playName.indexOf("*") > 0){
					 playName =  playName.substring(0,playName.indexOf(" "));
				 }
				 gamePlayerInfo.player_name =playName;
				 gamePlayerInfo.player_position = tdEles.get(1).text();
				 gamePlayerInfo.time = Long.valueOf(tdEles.get(2).text());
				 
				 String[] shoot = tdEles.get(3).text().split("-");
				 gamePlayerInfo.shoot_hit = Integer.valueOf(shoot[0]);
				 gamePlayerInfo.shoot_all = Integer.valueOf(shoot[1]);
				 String[] three = tdEles.get(4).text().split("-");
				 gamePlayerInfo.three_hit = Integer.valueOf(three[0]);
				 gamePlayerInfo.three_all = Integer.valueOf(three[1]);				 
				 
				 String[] free = tdEles.get(5).text().split("-");
				 gamePlayerInfo.free_hit = Integer.valueOf(free[0]);
				 gamePlayerInfo.free_all = Integer.valueOf(free[1]);	
				 gamePlayerInfo.rebound_front = Integer.valueOf(tdEles.get(7).text());
				 gamePlayerInfo.rebound_after= Integer.valueOf(tdEles.get(8).text());
				 
				 gamePlayerInfo.rebound_all= Integer.valueOf(tdEles.get(9).text());
				 gamePlayerInfo.assist = Integer.valueOf(tdEles.get(10).text());
				 gamePlayerInfo.foul = Integer.valueOf(tdEles.get(11).text());
				 gamePlayerInfo.stolen = Integer.valueOf(tdEles.get(12).text());
				 gamePlayerInfo.turn_out = Integer.valueOf(tdEles.get(13).text());
				
				 
				 gamePlayerInfo.block = Integer.valueOf(tdEles.get(14).text());
				 gamePlayerInfo.be_blocked = Integer.valueOf(tdEles.get(15).text());
				 gamePlayerInfo.point = Integer.valueOf(tdEles.get(16).text());
				 
				 String per = tdEles.get(6).text();
				 if(per.startsWith("-")){
					 gamePlayerInfo.per = 0 -  Integer.valueOf(per.substring(1));
				 }else{
					 gamePlayerInfo.per = Integer.valueOf(per);
				 }
				 
				 if (j<=5){
					 gamePlayerInfo.first = 1;
				 }else{
					 gamePlayerInfo.first = 0;
				 }
			 }
			 resultList.add(gamePlayerInfo);
			 j++;
		 }	
		return resultList;
	}
	
	
	public static List<GameQuarterScore> getQuarters(Document doc,int row) {
		List<GameQuarterScore> quarters = new ArrayList<GameQuarterScore>();
		Elements tds = doc.getElementsByClass("scoreBreakDownContainer").get(0).getElementsByTag("tr").get(row).getElementsByTag("td");
		for (int i = 1;i<=tds.size() -2 ;i++){
			GameQuarterScore quarter = new GameQuarterScore();
			Element td = tds.get(i);
			quarter.quarter = i;
			String scoreStr = td.html();
			if (td.html().indexOf("&") > 0) {
				scoreStr = scoreStr.substring(0,scoreStr.indexOf("&"));
			}
			quarter.score = Integer.valueOf(scoreStr);
			quarters.add(quarter);
		}
		
		return quarters;
	}
	
	
	public static GameInfo setGameInfo(Document doc,GameInfo gameInfo) {
		 Element  element = doc.getElementsByClass("boxscoreSummary").get(1);
		 Elements trEles = element.getElementsByTag("tr");
		 
		 Element tdEle = trEles.get(1).getElementsByTag("td").get(1);
		 if (!tdEle.text().trim().equals(""))
			 gameInfo.lead_cha = Integer.valueOf(tdEle.text());
		 else
			 gameInfo.lead_cha = 0 ;
		 tdEle = trEles.get(2).getElementsByTag("td").get(1);
		 if (!tdEle.text().trim().equals(""))
			 gameInfo.equal = Integer.valueOf(tdEle.text());
		 else
			 gameInfo.equal = 0;
		
		 
		 element = doc.getElementsByClass("boxscoreSummary2").get(0);
		 trEles = element.getElementsByTag("tr");
		 tdEle = trEles.get(2).getElementsByTag("td").get(1);
		 if (!tdEle.text().trim().equals(""))
			 gameInfo.viewers = Integer.valueOf(tdEle.text());
		 else
			 gameInfo.viewers = 0;
		 
		 tdEle = trEles.get(3).getElementsByTag("td").get(1);
		 if (!tdEle.text().trim().equals(""))
			 gameInfo.cost_time = Integer.valueOf(tdEle.text().substring(0,tdEle.text().indexOf(":"))); 
		 else
			 gameInfo.cost_time = 0;
		 return gameInfo;
	}
	

	
	public static void main(String[] args){
		Document doc;
		try {
			doc = UrlUtil.getURLContent("http://china.nba.com/stats/league/fullScoreBoard/11-11-2012_00.html");
			getGames(doc);
//			getPlayerStatisticEspn(doc,2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
