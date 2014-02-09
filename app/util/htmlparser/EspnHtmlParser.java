package util.htmlparser;

import gsonmoudle.EspnGame;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.game.GameInfo;
import models.game.GameInfoStatic;
import models.game.GameInjury;
import models.game.GamePlayByPlay;
import models.game.GamePlayerInfo;
import models.game.GameQuarterScore;
import models.player.PlayerInfo;
import models.team.TeamInfo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import play.Logger;
import play.jobs.Every;
import util.DateUtil;
import util.StringUtil;

import controllers.Application;

public class EspnHtmlParser {
	
	private static  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	
	/**
	 * 获取球员比赛信息
	 * @param doc
	 * @param begin 0 主，3客
	 * @param teamInfo
	 * @param game_id
	 * @return
	 */
	public static List<GamePlayerInfo> getPlayerInfos(Document doc,int begin,TeamInfo teamInfo,GameInfo gameInfo){
		List<GamePlayerInfo> result = new ArrayList<GamePlayerInfo>();
		Element tableEle = doc.getElementsByClass("mod-data").get(0);
		Elements tbodies = tableEle.getElementsByTag("tbody");
		Elements trs = tbodies.get(begin).getElementsByTag("tr");
		begin++;
		trs.addAll(tbodies.get(begin).getElementsByTag("tr"));
		for (int i=0;i<trs.size();i++){
			Elements tdEles = trs.get(i).getElementsByTag("td");
			if (tdEles == null || tdEles.size() == 0   || (i == 0 && tdEles.size() < 15)){
				break;
			}
			
			 GamePlayerInfo gamePlayerInfo = new GamePlayerInfo();	 
			 gamePlayerInfo.team_id = teamInfo.id;
			 gamePlayerInfo.game_id = gameInfo.id;
			 gamePlayerInfo.game_date = gameInfo.play_date;
			 gamePlayerInfo.player_id = -1;
			 
			 String td1 =  tdEles.get(0).text();
			 Element aTag = tdEles.get(0).getElementsByTag("a").get(0);
			 String href = aTag.attr("href");
			 
			Pattern patt = Pattern.compile("\\d+");
			Matcher matc = patt.matcher(href);
			if (matc.find()){
				gamePlayerInfo.espnId = Long.valueOf(matc.group());
			}
			
			 gamePlayerInfo.player_name = td1.split(",")[0];
			 gamePlayerInfo.player_position = td1.split(",")[1];
			 if (tdEles.get(1).text().indexOf("DNP")>=0 || tdEles.get(1).text().indexOf("Has")>=0 ){
				 gamePlayerInfo.comments = tdEles.get(1).text();
				 gamePlayerInfo.time = 0;
			 }else {			
				 gamePlayerInfo.time = Long.valueOf(tdEles.get(1).text());				 
				 String[] shoot = tdEles.get(2).text().split("-");
				 gamePlayerInfo.shoot_hit = Integer.valueOf(shoot[0]);
				 gamePlayerInfo.shoot_all = Integer.valueOf(shoot[1]);
				 String[] three = tdEles.get(3).text().split("-");
				 gamePlayerInfo.three_hit = Integer.valueOf(three[0]);
				 gamePlayerInfo.three_all = Integer.valueOf(three[1]);				 
				 
				 String[] free = tdEles.get(4).text().split("-");
				 gamePlayerInfo.free_hit = Integer.valueOf(free[0]);
				 gamePlayerInfo.free_all = Integer.valueOf(free[1]);	
				 gamePlayerInfo.rebound_front = Integer.valueOf(tdEles.get(5).text());
				 gamePlayerInfo.rebound_after= Integer.valueOf(tdEles.get(6).text());
				 
				 gamePlayerInfo.rebound_all= Integer.valueOf(tdEles.get(7).text());
				
				 gamePlayerInfo.assist = Integer.valueOf(tdEles.get(8).text());
				 gamePlayerInfo.stolen = Integer.valueOf(tdEles.get(9).text());
				 gamePlayerInfo.block = Integer.valueOf(tdEles.get(10).text());
				 gamePlayerInfo.turn_out = Integer.valueOf(tdEles.get(11).text());
				 gamePlayerInfo.foul = Integer.valueOf(tdEles.get(12).text());			 
				 
//				 gamePlayerInfo.be_blocked = Integer.valueOf(tdEles.get(13).text());
				 gamePlayerInfo.point = Integer.valueOf(tdEles.get(14).text());
				 
				 String per = tdEles.get(13).text();
				 if(per.startsWith("-")){
					 gamePlayerInfo.per = Integer.valueOf(per);
				 }else if (per.startsWith("+")){
					 gamePlayerInfo.per = Integer.valueOf(per.substring(1));
				 }else{
					 gamePlayerInfo.per = 0;
				 }
				 
				 if (i<=4){
					 gamePlayerInfo.first = 1;
				 }else{
					 gamePlayerInfo.first = 0;
				 }				 
				
		}
		result.add(gamePlayerInfo);
		
	}		
		return result;
	}
	
	/**
	 * 获取比赛统计
	 * @param doc
	 * @param begin 2客队   5主队
	 * @return
	 */
	public static GameInfoStatic getGameStatistics(Document doc,int begin,TeamInfo teamInfo,GameInfo gameInfo){
		GameInfoStatic result = new GameInfoStatic();
		Element tableEle = doc.getElementsByClass("mod-data").get(0);
		Elements tbodies = tableEle.getElementsByTag("tbody");
		Elements trs = tbodies.get(begin).getElementsByTag("tr");
		Elements tdEles = trs.get(0).getElementsByTag("td");
		if (tdEles.size() <14){
			return null;
		}
		
		result.game_id = gameInfo.id;
		result.game_date = gameInfo.play_date;
		result.team_id = teamInfo.id;
		result.team_name = teamInfo.team_name;
		
		String[] shoot = tdEles.get(1).text().split("-");
		result.shoot_hit = Integer.valueOf(shoot[0]);
		result.shoot_all = Integer.valueOf(shoot[1]);
		 String[] three = tdEles.get(2).text().split("-");
		 result.three_hit = Integer.valueOf(three[0]);
		 result.three_all = Integer.valueOf(three[1]);	
		 String[] free = tdEles.get(3).text().split("-");
		 result.free_hit = Integer.valueOf(free[0]);
		 result.free_all = Integer.valueOf(free[1]);	
		 
		
		 result.rebound_front = Integer.valueOf(tdEles.get(4).text());
		 result.rebound_after= Integer.valueOf(tdEles.get(5).text());		 
		 result.rebound_all= Integer.valueOf(tdEles.get(6).text());
		
		 result.assist = Integer.valueOf(tdEles.get(7).text());
		 result.stolen = Integer.valueOf(tdEles.get(8).text());
		 result.block = Integer.valueOf(tdEles.get(9).text());
		 result.turn_out = Integer.valueOf(tdEles.get(10).text());
		 result.foul = Integer.valueOf(tdEles.get(11).text());
		 result.score = Integer.valueOf(tdEles.get(13).text());
		 
		 tdEles = trs.get(1).getElementsByTag("td");
		 result.shoot_percent = Double.valueOf(tdEles.get(1).text().replaceAll("%", ""));
		 result.three_percent = Double.valueOf(tdEles.get(2).text().replaceAll("%", ""));
		 result.free_percent = Double.valueOf(tdEles.get(3).text().replaceAll("%", ""));	 
		 
		Element tdEle = trs.get(2).getElementsByTag("td").get(0);
		Element divEle = tdEle.getElementsByTag("div").get(0);
//		System.out.println(divEle.html());
		String[] htmls = divEle.html().replaceAll("\n", "").split("<br />");
		try{
			result.fast_break_points = Integer.valueOf(htmls[0].substring(htmls[0].lastIndexOf(";")+1,htmls[0].length()));
		}catch(NumberFormatException ex){
			result.fast_break_points = 0 ;
		}
		try{
			result.paint_points = Integer.valueOf(htmls[1].substring(htmls[1].lastIndexOf(";")+1,htmls[1].length()));
		}catch(NumberFormatException ex){
			result.paint_points = 0 ;
		}
		
		String teamOff = htmls[2].substring(htmls[2].lastIndexOf(";")+1,htmls[2].length());		
		result.team_offs = Integer.valueOf(teamOff.substring(0,teamOff.indexOf("(")-1));
		result.team_points_off = Integer.valueOf(teamOff.substring(teamOff.indexOf("(")+1,teamOff.indexOf(")")));
		return result;
	}
	
	public static List<String> getOldStatistic(Document doc) throws ParseException{
		List<String> urls = new ArrayList<String>();
		Element tableEle = doc.getElementsByClass("mod-content").get(0);
		Elements trs = tableEle.getElementsByTag("tr");
		
		for (int i=0;i<trs.size();i++){
			Element trEle = trs.get(i);
			String trClass = trEle.attr("class");
			if (trClass.indexOf("oddrow") >=0 ||trClass.indexOf("evenrow")>=0){
				Element li = trEle.getElementsByAttributeValue("class","score").get(0);
				String url = "http://espn.go.com" + li.getElementsByTag("a").get(0).attr("href").replace("recap", "boxscore");
				urls.add(url);
			}
		}
		
		return urls;
	}
	
	
	public static List<GameInfo> getScheduleMap(Document doc,TeamInfo team) throws ParseException{
		List<GameInfo>  result = new ArrayList<GameInfo>();
		Element tableEle = doc.getElementsByClass("mod-content").get(0);
		Elements trs = tableEle.getElementsByTag("tr");
		for (int i=0;i<trs.size();i++){
			Element trEle = trs.get(i);
			String trClass = trEle.attr("class");
			if (trClass.indexOf("oddrow") >=0 ||trClass.indexOf("evenrow")>=0){
				
				GameInfo game = new GameInfo();
				
				Elements tdEles = trEle.getElementsByTag("td");
				String month =  tdEles.get(0).text().split(",")[1].split(" ")[1];
				String day =  tdEles.get(0).text().split(",")[1].split(" ")[2];
				if (day.length() == 1){
					day = "0" + day;
				}
				String guest = tdEles.get(1).getElementsByAttributeValue("class","game-status").get(0).text();
				String cityname = tdEles.get(1).getElementsByAttributeValue("class","team-name").get(0).text().toLowerCase();
				
				TeamInfo anotherTeam = Application.teamMap.get(cityname);
				if (cityname.equalsIgnoreCase("Los Angeles")){
					String url = tdEles.get(1).getElementsByAttributeValue("class","team-name").get(0).getElementsByTag("a").get(0).attr("href");
					if (url.indexOf("clippers") >= 0){
						anotherTeam = Application.teamMap.get("16");
					}else{
						anotherTeam = Application.teamMap.get("15");
					}
					
				}
				if (anotherTeam == null){
					return null;
				}
				if (guest.equals("@")){
					game.home_name = anotherTeam.team_name;
					game.home_id = anotherTeam.id;
					game.guest_name = team.team_name;
					game.guest_id = team.id;
				}else{
					game.guest_name = anotherTeam.team_name;
					game.guest_id = anotherTeam.id;
					game.home_name = team.team_name;
					game.home_id = team.id;
				}
				Logger.info("%s vs %s", game.guest_name,game.home_name);
				
				if (tdEles.get(2).getElementsByTag("a").size() > 0 ){
					String espnId = tdEles.get(2).getElementsByTag("a").get(0).attr("href");
					Pattern patt = Pattern.compile("\\d+");
					Matcher matc = patt.matcher(espnId);
					if (matc.find()){
						game.espnId = Long.valueOf(matc.group());
					}
				}
				
				String gameDate = "";
				if (Application.monthMap.get(month) >=10)
					gameDate = (Application.season -1) + "-" +Application.monthMap.get(month) + "-" + day; 
				else
					gameDate = Application.season + "-0" +Application.monthMap.get(month) + "-" + day; 
				game.usa_play_date = sdf.parse(gameDate);
				game.play_date = DateUtil.addDate(game.usa_play_date, 1);
				game.play_time = tdEles.get(2).text();
				game.has_detail = 0;		
				result.add(game);				
			}
			
		}
		
		return result;
	}
	
	/**
	 * 获取比赛每节
	 * @param doc
	 * @param begin 1客队，2主队
	 * @return
	 */
	public static List<GameQuarterScore> getGameQuarters(Document doc,int begin,Long gameId){
		List<GameQuarterScore> result = new ArrayList<GameQuarterScore>();
		Element trEle = doc.getElementsByClass("line-score-container").get(0).getElementsByTag("tr").get(begin);
		Elements tdEles = trEle.getElementsByAttributeValue("Style","text-align:center");
		for (int  i=1;i<=tdEles.size() - 1;i++){
			GameQuarterScore quarter = new GameQuarterScore();
			quarter.game_id = gameId;
			quarter.quarter = i;
			quarter.score = Integer.parseInt(tdEles.get(i-1).text().replaceAll(" ", ""));
//			quarter.team_id = teamInfo.id;
//			quarter.team_name  = teamInfo.team_name;
			result.add(quarter);
		}		
		return result;
	}

	public static GameInfo getGame(Document doc) throws ParseException{
		GameInfo gameInfo = new GameInfo();
		Elements as = doc.getElementsByClass("team-info").get(0).getElementsByTag("h3");
		if (doc.getElementsByClass("team-info").get(0).getElementsByTag("h3").get(0).getElementsByTag("a").size() == 0){
			gameInfo.guest_name = doc.getElementsByClass("team-info").get(0).getElementsByTag("h3").get(0).text().trim().split(" ")[0];
		}
		else{
			gameInfo.guest_name = doc.getElementsByClass("team-info").get(0).getElementsByTag("h3").get(0).getElementsByTag("a").get(0).text();
		}
		if (doc.getElementsByClass("team-info").get(1).getElementsByTag("h3").get(0).getElementsByTag("a").size() == 0){
			gameInfo.home_name = doc.getElementsByClass("team-info").get(1).getElementsByTag("h3").get(0).text().trim().split(" ")[0];
		}else{
			gameInfo.home_name = doc.getElementsByClass("team-info").get(1).getElementsByTag("h3").get(0).getElementsByTag("a").get(0).text();
		}
		Element date = doc.getElementsByAttributeValue("class", "game-time-location").get(0).getElementsByTag("p").get(0);
		String[] dateArr = date.text().trim().split(",");
		String playDate = dateArr[2]+"-"+Application.monthMap.get(dateArr[1].trim().split(" ")[0])+ "-" +dateArr[1].trim().split(" ")[1];
//		String playDate = dateArr[2]+"-"+dateArr[1].trim().split(" ")[0] + "-" + dateArr[1].trim().split(" ")[1];
		gameInfo.play_date = sdf.parse(playDate);
		return gameInfo;
	}	
	
	public static PlayerInfo getTeamPlayerInfo(Document doc,PlayerInfo player){
		player.name = doc.getElementsByTag("h1").get(1).text();
//		System.out.println(player.name);
		Element ele = doc.getElementsByClass("general-info").get(0);
		Element first = ele.getElementsByClass("first").get(0);
		String firstStr = first.text().trim();
		Logger.info(" %s is %s ", player.name,firstStr);
		if (firstStr.indexOf("#") >= 0){
			player.play_num = Integer.valueOf(firstStr.split(" ")[0].replace("#","")).intValue();
			player.position = firstStr.split(" ")[1];
		}else{
			player.play_num = -1;
			player.position = firstStr;
		}
		Logger.info(" %s is %s ", player.name,firstStr);
		
//		System.out.println(first.text().split(" ")[0].replace("#",""));
//		System.out.println(first.text().split(" ")[1]);
		
		Element second = ele.getElementsByTag("li").get(1);
		String height = second.text().split(",")[0].replaceAll(" ", "").replace("'", ".").replace("\"", "");
		String weight = second.text().split(",")[1].replace(" ", "").replace("lbs", "");
		player.hight = Double.valueOf(height).doubleValue();
		player.weight = Double.valueOf(weight).doubleValue();
//		System.out.println(Double.valueOf(height)*0.30);
//		System.out.println(Double.valueOf(weight)*0.45);
		
		Elements lis = doc.getElementsByClass("player-bio").get(0).getElementsByTag("ul").get(2).getElementsByTag("li");
		Element yearEle = lis.get(0);
		String yearStr = yearEle.text();
		Pattern patt = Pattern.compile("19[6-9][0-9]");
		Matcher matc = patt.matcher(yearStr);
		if (matc.find()){
			player.birthyear = matc.group();
		}	
		
		player.experience = 0;
		for (int i = 1;i<lis.size();i++){
			if ( lis.get(i).getElementsByTag("span").get(0).text().equalsIgnoreCase("Experience")){
				yearEle = lis.get(i);
				yearStr = yearEle.text();
				
				patt = Pattern.compile("1?[0-9]");
				matc = patt.matcher(yearStr);
				if (matc.find()){
					player.experience = Integer.valueOf(matc.group()).intValue();
				}
			}
		}
		return player;
	}
	/**
	 * 获取球队球员名
	 * @param doc
	 * @return
	 */
	
	public static List<PlayerInfo> getTeamPlayers(Document doc){
		Elements odds = doc.getElementsByClass("oddrow");
		Elements evens = doc.getElementsByClass("evenrow");
		List<PlayerInfo> players = new ArrayList<PlayerInfo>();
		for (Element ele: evens){
			odds.add(ele);
		}
		
		for (Element odd:odds){
			Elements tds = odd.getElementsByTag("td");
			int depth = 1;
			
			for(int i = 1;i<tds.size() ;i++){				
				Element ele = tds.get(i);
				if (ele.getElementsByTag("nobr").get(0).text().equals("")){
					break;
				}
				
				PlayerInfo player = new PlayerInfo();
				player.name = ele.getElementsByTag("a").get(0).text().trim();
				player.additional = ele.getElementsByTag("a").get(0).attr("href");
				Pattern patt = Pattern.compile("\\d+");
				Matcher matc = patt.matcher(player.additional);
				if (matc.find()){
					player.espnId = Long.valueOf(matc.group());
				}					
				player.depth = depth;
				depth++;
				players.add(player);
			}			
		}
		
		for (PlayerInfo player : players){
			 try {
				doc = UrlUtil.getURLContent(player.additional);
				player = getTeamPlayerInfo(doc,player);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		return players;
	}
	
	public static String getReportString(Document doc){
		String result = "";
		Element ele = doc.getElementById("flashViewsDiv");
		result = ele.html();
		return result;
	}
	
	public static List<EspnGame> getTodayGames(Document doc){
		
		String value = "";
		List<EspnGame> espnGames = new ArrayList<EspnGame>();
		Gson gson = new Gson();
		Elements eles = doc.getElementsByAttributeValue("type", "text/javascript");
		for (Element ele :eles){
			if (ele.html().contains("var sbMaster =")){
				value = ele.html();
				value = value.substring(value.indexOf("{"), value.length());
				break;				
			}
		}
		
		JsonParser parser = new JsonParser();
		JsonObject rootObject = parser.parse(value).getAsJsonObject();
		JsonArray ObjectArraies = rootObject.get("sports").getAsJsonArray();
		
		for (int i=0;i<ObjectArraies.size();i++){	
			JsonObject arrObject =  ObjectArraies.get(i).getAsJsonObject();
			if (arrObject.get("sport").getAsString().equals("nba")){
				JsonObject nbaObject = arrObject.get("leagues").getAsJsonArray().get(0).getAsJsonObject();
				JsonArray games = nbaObject.get("games").getAsJsonArray();
				for(int j=0;j<games.size();j++){
					EspnGame espnGame = gson.fromJson(games.get(j), EspnGame.class);
					espnGames.add(espnGame);
				}
				break;
			}			
		}
		
		return espnGames;		
	}
	
	public static List<String> getNextDayGameUrl(Document doc){
		Element table = doc.getElementsByAttributeValue("class", "tablehead").get(0);
		Elements tds = table.getElementsByAttributeValue("align", "right");
		List<String> urls = new ArrayList<String>();
		
		for (Element td : tds){
			Elements a = td.getElementsByTag("a");
			if (a.size() == 0){
				continue;
			}
			String url = td.getElementsByTag("a").get(0).attr("href");
			urls.add(url);
		}		
		return urls;
	}
	
	public static List<String> getGameStatisticUrls(Document doc){
		Element table = doc.getElementsByAttributeValue("class", "tablehead").get(0);
		Elements trs = table.getElementsByTag("tr");
		List<String> urls = new ArrayList<String>();
		
		for (Element tr : trs){
			if (tr.attr("class").equals("stathead") || tr.attr("class").equals("colhead") )
				continue;
			
			Element td = tr.getElementsByTag("td").get(0);
			Elements a = td.getElementsByTag("a");
			if (a.size() == 0){
				continue;
			}
			String url = td.getElementsByTag("a").get(0).attr("href");
			urls.add(url);
		}		
		return urls;
	}
	
	public static GameInfo preview(Document doc) throws ParseException{
		GameInfo game = new GameInfo();
		Long espnId = StringUtil.getLongReg(doc.baseUri());
		game.espnId = espnId;
		Element ele = doc.getElementById("matchup-nba-"+espnId);
		Elements h3s = ele.getElementsByTag("h3");
		if (h3s.get(0).text().indexOf("76ers") >= 0)
			game.guest_name = "76ers";
		else
			game.guest_name = h3s.get(0).text().replaceAll("\\d","").trim(); 
		
		if (h3s.get(1).text().indexOf("76ers") >= 0)
			game.home_name = "76ers";
		else
			game.home_name = h3s.get(1).text().replaceAll("\\d","").trim();
		
		String time = doc.getElementsByAttributeValue("class", "game-time-location").get(0).getElementsByTag("p").get(0).text();
		time = time.replace(" ET,", ",");
		Logger.info(time);
		Date USAdate = DateUtil.parse(time,"h:mm aa, MMMM dd, yyyy",Locale.ENGLISH);
		game.usa_play_date = USAdate;
		game.play_time = DateUtil.format(USAdate,"hh:mm aaa",Locale.ENGLISH);
		
		Elements ps= doc.getElementsByAttributeValue("class","bg-opaque pad-16 article").get(0).getElementsByTag("p");
		game.reportTitle ="";
		for(Element p:ps){
			if (!p.html().trim().equals(""))
				game.reportTitle = game.reportTitle + "<p>" + p.text() + "</p>";
		}
		return game;
	}

	public static List<GameInjury> injury(Document doc,int home){
		List<GameInjury> injuries = new ArrayList<GameInjury>();
		Elements els = doc.getElementsByAttributeValue("class", "mod-container mod-no-footer mod-open mod-open-gamepack");
		for (Element mod : els){
			Elements headers = mod.getElementsByAttributeValue("class", "mod-header");
			if (headers.size() <= 0 || headers.get(0).html().indexOf("Injury Report") < 0){
				continue;
			}
			Elements links = mod.getElementsByTag("tbody").get(home).getElementsByAttributeValue("class", "bulletlinks");
			for (Element link:links){
				Elements lis = link.getElementsByTag("li");
				for (Element li:lis){
					GameInjury injury = new GameInjury();
					String liStr = li.text().trim();
					String array[] = liStr.split(":");
					injury.brief = array[1].trim();
					array = array[0].trim().split("-")[0].trim().split(" ");
					String name =  "";
					for (int i =0 ;i<array.length;i++){
						if (i == array.length -1 ){
							injury.position = array[i];
						}else if (i == 0){
							name = array[i];
						}else{
							name = name + " " + array[i];
						}
					}
					injury.playerName = name;
//					System.out.println(array[0].trim().split("-")[0].trim());
//					System.out.println(array[0].trim().split("-")[1].trim());	
					injuries.add(injury);
				}
				
			}		
		}
		
		return injuries;
	}	
	
	public static List<GamePlayByPlay> getPlayByPlay(Document doc,Map<String,Long> teamMap){
		Element ele = doc.getElementsByClass("mod-data").get(0);
		Elements tbodies = ele.getElementsByTag("tbody");
		List<GamePlayByPlay> plays = new ArrayList<GamePlayByPlay>();
		int homePoint = 0;
		int awayPoint = 0;
		
		for (int i = 0 ; i < tbodies.size() ; i++){
			
			Elements trs = tbodies.get(i).getElementsByTag("tr");
			for (int j = 0 ; j < trs.size() ; j++){
				Element tr = trs.get(j);
				Elements tds =  tr.getElementsByTag("td");
				GamePlayByPlay play = new GamePlayByPlay();
				play.player = null;
				play.assister = null;
				play.quarter = tbodies.size() - i;
				
				if ( tds.size() == 1){
					continue;
				}else {
					play.time = tds.get(0).text().trim();
					String[] second = tds.get(0).text().trim().split(":");
					play.second = Integer.valueOf(second[0]) * 60 + Integer.valueOf(second[1]);					
					play.get_point = 0;
					
					if ( tds.size() == 4){		
						play.score = tds.get(2).text().trim();
						Element td1 = tds.get(1);
						Element td3 = tds.get(3);
						if (!td1.html().equals("&nbsp;")){
							play = setPlayByPlay(td1,play);
							play.team_id = teamMap.get("away");
							play.get_point = Integer.valueOf(play.score.split("-")[0].trim()) - awayPoint;
							play.team_point = Integer.valueOf(play.score.split("-")[0].trim());
						}else{
							play = setPlayByPlay(td3,play);	
							play.team_id = teamMap.get("home");
							play.get_point = Integer.valueOf(play.score.split("-")[1].trim()) - homePoint;
							play.team_point = Integer.valueOf(play.score.split("-")[1].trim());
						}
						homePoint = Integer.valueOf(play.score.split("-")[1].trim());
						awayPoint = Integer.valueOf(play.score.split("-")[0].trim());
					}else if (tds.size() == 2) {
						play.content = tds.get(1).text();
						play.score = awayPoint + "-" + homePoint;
					}
				}
				plays.add(play);
			}
		}
		return plays;
	}
	
	public static GamePlayByPlay setPlayByPlay(Element td,GamePlayByPlay play){
		String text = td.text().trim();
		String[] make = text.split("makes");
		if (td.html().indexOf("<b>") >= 0 ){
			play.playerName = make[0].trim();
			
			if (make.length > 1) {
				if (make[1].indexOf("-foot") > 0){
					play.shoot_distance =  Integer.valueOf(make[1].split("-foot")[0].trim());
				}else{
					play.shoot_distance = 0;
				}
				
				if (make[1].indexOf("assist") > 0 ){
					play.assisterName = make[1].substring(make[1].indexOf("(") + 1,make[1].indexOf("assist")).trim();
				}
			}
			
		}
		
		play.content = td.text();
		
		return play;
	}
	
	
	public static void main(String[] args){
		Document doc;
		try {

			Map<String,Long> teamMap = new HashMap<String, Long>();
			teamMap.put("home", 1L);
			teamMap.put("away", 2L);
			
			doc = UrlUtil.getURLContent("http://espn.go.com/");
			
			getTodayGames(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy h:mm:ss aaa",Locale.ENGLISH);
//		try {
//		 Date date = df.parse("Nov 29, 2012 3:30:30 PM");
//		 df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		 System.out.println(df.format(date));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}
}
