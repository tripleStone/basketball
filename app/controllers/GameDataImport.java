package controllers;

import gsonmoudle.EspnGame;
import gsonmoudle.EspnGameTeam;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.game.GameChart;
import models.game.GameInfo;
import models.game.GameInfoStatic;
import models.game.GamePlayerInfo;
import models.game.GameQuarterScore;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;
import models.player.PlayerInfo;
import models.team.TeamInfo;
import models.team.TeamMsg;
import models.team.TeamStSeasonBase;

import play.Logger;
import play.jobs.Every;
import play.mvc.Controller;
import play.mvc.Util;
import util.DateUtil;
import util.htmlparser.EspnHtmlParser;
import util.htmlparser.HupuHtmlParser;
import util.htmlparser.NbaChinaHtmlParser;
import util.htmlparser.UrlUtil;


public class GameDataImport  extends Controller{

	private static final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Calendar   calendar   =   new   GregorianCalendar(); 

	/**
	 * 队伍首页
	 */
	public static void  teamScoreIndex(){
		List<TeamInfo> teams = TeamInfo.getTeams();		
		render("/gameDateImport/indexTeamScore.html",teams);	
	}
	
    /**
     * url /gameData/gameDataImport
     * @param playDate
     */
    public static void gameDataImport(String playDate){
    	String preDate = "";
    	String nextDate = "";
    	
    	if (playDate == null)
    		playDate = sdf.format(new Date());
    	List<GameInfo> gameInfos = null;
    	try {
    		gameInfos  = GameInfo.getGameInfos(sdf.parse(playDate));
    		
        	calendar.setTime(sdf.parse(playDate)); 
    		calendar.add(calendar.DATE, -1);
    		preDate = sdf.format( calendar.getTime());
    		calendar.add(calendar.DATE, +2);
    		nextDate = sdf.format( calendar.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	render("/gameDateImport/gameDataImport.html",gameInfos,playDate,preDate,nextDate);
    }
    


	
    public static void teamScheduleImport(){
    	List<TeamInfo> teams = TeamInfo.getTeams(Integer.valueOf(Application.season));
    	
    	List<TeamInfo> tmpTeams = TeamInfo.getTeams();
    	render("/gameDateImport/teamScheduleImport.html",teams,tmpTeams);
    }
    
    
    public static void importScheduleByUrl(String dataUrl,Long teamId){
    	try {
    		TeamInfo team = TeamInfo.findById(teamId);
			String url =  "http://espn.go.com/nba/team/schedule/_/name/"+ team.eng_simple_name + "/seasontype/2/" + team.comments.replace(" ", "-") + "-" +team.eng_name;
			Document doc = UrlUtil.getURLContent(url);
			
			List<GameInfo> gameInfos = EspnHtmlParser.getScheduleMap(doc,team);
			
			if (gameInfos == null){
				renderText ("0");
			}
			
			for (GameInfo tmpGame:gameInfos){
				GameInfo game =  GameInfo.getGameInfo(tmpGame.home_id, tmpGame.guest_id,tmpGame.play_date);
				if (game == null ){	
					tmpGame.season = Application.season;
					tmpGame.has_detail = 0;
					tmpGame.save();
				}else{	
					continue;
				}
			}
			team.year = Application.season;
			team.save();
			renderText ("1");
		} catch (Exception e) {
			e.printStackTrace();
			renderText("可能是链接超时导致的出错");
			// TODO Auto-generated catch block		
		}
    	
    }
    
    /**
     * 
     * @param year
     * @param teamId
     */
    public static void importOldScheduleById(String year,Long teamId){
    	TeamInfo team = TeamInfo.findById(teamId);
    	year="2012";
    	String url = "http://espn.go.com/nba/team/schedule/_/name/"+team.eng_simple_name+"/year/"+year+"/seasontype/2/"+ team.comments.replace(" ", "-") + "-" +team.eng_name;
    	Document tmpDoc;
    	List<GamePlayerInfo> homePlayers = null;
		List<GamePlayerInfo> guestPlayers = null;
		try {
			tmpDoc = UrlUtil.getURLContent(url);			
			List<String> urls = EspnHtmlParser.getOldStatistic(tmpDoc);
			
			for (String tmpurl : urls){
				tmpurl = tmpurl.replace("recap", "boxscore");
				Document doc = UrlUtil.getURLContent(tmpurl);
				GameInfo tmpGameInfo = EspnHtmlParser.getGame(doc);
				
				TeamInfo homeTeam = TeamInfo.getTeamByEngName(tmpGameInfo.home_name.toLowerCase());
				TeamInfo guestTeam = TeamInfo.getTeamByEngName(tmpGameInfo.guest_name.toLowerCase());
				Calendar   calendar   =   new   GregorianCalendar(); 
			     calendar.setTime(tmpGameInfo.play_date); 
			     calendar.add(calendar.DATE,1);
				GameInfo gameInfo = GameInfo.getGameInfo(homeTeam.id, guestTeam.id,calendar.getTime());
				 
				if (homeTeam.id != gameInfo.home_id || guestTeam.id != gameInfo.guest_id  ){
					continue;
				}else  if (gameInfo.has_detail == 1){
					continue;
				}
				
				 //保存球员和比赛数据统计
				GamePlayerInfo.delByGame(gameInfo.id);
				TeamStSeasonBase.delByGame(gameInfo.home_id, gameInfo.play_date);
				TeamStSeasonBase.delByGame(gameInfo.guest_id, gameInfo.play_date);
				GameInfoStatic.delByGame(gameInfo.id);
						 	
				//球员数据--begin				
				homePlayers =   EspnHtmlParser.getPlayerInfos(doc,0,guestTeam,gameInfo);
				for (GamePlayerInfo gamePlayer : homePlayers ){
					gamePlayer.save();
				} 
					
				guestPlayers =    EspnHtmlParser.getPlayerInfos(doc,3,homeTeam,gameInfo);
				for (GamePlayerInfo gamePlayer :guestPlayers){
					gamePlayer.save();				
				}
				//球员数据--end
				
				//球队数据--begin
				GameInfoStatic guestInfoStatic = EspnHtmlParser.getGameStatistics(doc, 2, guestTeam, gameInfo); 
				guestInfoStatic.save();
				GameInfoStatic homeInfoStatic = EspnHtmlParser.getGameStatistics(doc, 5, homeTeam, gameInfo);
				homeInfoStatic.save();
				//球队数据--end
				
				//保存每节比赛成绩--begin				
				gameInfo.static_url = tmpurl;
				gameInfo.has_detail = 1;
				gameInfo.save();
				
				doc = UrlUtil.getURLContent(tmpurl.replace("boxscore", "shotchart"));
				String charStr = EspnHtmlParser.getReportString(doc);
				int i = 1;
				
				while (charStr.length() > 0){
					GameChart chart = new GameChart();
					chart.gameId = gameInfo.id;
					chart.pieces  = i;
					String tmp = charStr.length() > 2500 ? charStr.substring(0, 2500):charStr;
					chart.chartHtml = tmp;
					chart.save();
					if (charStr.length() > 2500){
						charStr = charStr.substring(2500,charStr.length());					
					}	else{
						break;
					}
					i++;
				}
				gameInfo.has_chart = 1;
				gameInfo.save();
				
					
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//校验比赛信息
		 
    	
    }
    
    /**
     * 获取好友
     * @param teamId
     */
    public static String importTeamPlayers(Long teamId,String gameDate){
    	Logger.info("import players begin");
    	String result = saveTeamPlayers(teamId, gameDate);    	
    	Logger.info("import players end");
    	return(result);
    }
    
    @Util
	public static String saveTeamPlayers(Long teamId, String gameDate) {
		TeamInfo team = TeamInfo.findById(teamId);
    	String url = "http://espn.go.com/nba/team/depth/_/name/{1}/{2}";
    	url = url.replace("{1}", team.eng_simple_name);
    	url = url.replace("{2}", team.city_name.replace(" ", "-"));
    	List<Long> ids = new ArrayList<Long>();
    	Date updateDate = new Date();
    	try {
			Document doc = UrlUtil.getURLContent(url);
			List<PlayerInfo> players =  EspnHtmlParser.getTeamPlayers(doc);
			for (PlayerInfo player : players){
				PlayerInfo dataPlayer = PlayerInfo.findPlayerEng(player.name);
				if (dataPlayer == null){
					dataPlayer = new PlayerInfo();
					dataPlayer.team_id = teamId;
				}else{
					if (dataPlayer.team_id != teamId.longValue()){
						dataPlayer.team_id = teamId;
					}
					
				}
				ids.add(dataPlayer.id);
				dataPlayer.updateDate = updateDate;
				dataPlayer.name = player.name.trim();
				dataPlayer.birthyear = player.birthyear.trim();
				dataPlayer.espnId = player.espnId;
				dataPlayer.depth = player.depth;
				dataPlayer.hight = player.hight;
				dataPlayer.weight = player.weight;
				dataPlayer.experience = player.experience;
				dataPlayer.position = player.position.trim();
				dataPlayer.play_num = player.play_num;
				dataPlayer.del_marker = 0l;
				dataPlayer.save();
			}
			
			GamePlayerInfo.updatePlayerId(teamId,DateUtil.parseDate(gameDate));
			PlayerInfo.delPlayer(teamId, ids);
			
			return ("{\"error\":\"1\"}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ("{\"error\":\"2\",\"msg\":\"出错\"}");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ("{\"error\":\"2\",\"msg\":\"出错\"}");
		}
	}
  

    
    public static void getLotteriesAjax(String playDate){
    	List<LotteryWinPoint> lotteries =  null;
    	List<LotteryHilo> hilos = null;
    	List<LotteryHandicap> handicaps = null; 
    	String preDate = "";
    	String nextDate="";
    	try {
    		calendar.setTime(sdf.parse(playDate)); 
			calendar.add(calendar.DATE, -1);
			preDate = sdf.format( calendar.getTime());
			calendar.add(calendar.DATE, +2);
			nextDate = sdf.format( calendar.getTime());
    		
			lotteries = LotteryWinPoint.getLotteries(sdf.parse(playDate));
			hilos = LotteryHilo.gethilos(sdf.parse(playDate));
			handicaps = LotteryHandicap.getLotteries(sdf.parse(playDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		render("/gameDateImport/ajax/lotteriesAjax.html",lotteries,handicaps,hilos,preDate,nextDate);
    }
    
    
    public static void  gameDataEdit(String playDate){
		List<GameInfo> gameInfos = new ArrayList<GameInfo>();
		if (playDate == null)
			playDate = sdf.format(new Date());
		String preDate = "";
		String nextDate="";
		
		
		
		try {
			calendar.setTime(sdf.parse(playDate)); 
			calendar.add(calendar.DATE, -1);
			preDate = sdf.format( calendar.getTime());
			calendar.add(calendar.DATE, +2);
			nextDate = sdf.format( calendar.getTime());
			gameInfos = GameInfo.getGameInfos(sdf.parse(playDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	
    	render("/gameDateImport/gameDataEdit.html",gameInfos,playDate,preDate,nextDate);
    }
    
    public static void gamesByDayAjax(String playDate){    	
		List<GameInfo> gameInfos = new ArrayList<GameInfo>();
		try {
			 gameInfos = GameInfo.getGameInfos(sdf.parse(playDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		render("/gameDateImport/ajax/gamesForEdit.html",gameInfos);
    }
    

//	private static String saveTeamSeasonStatistics(GameInfo gameInfo, int score,
//			TeamSeasonStatistics gLastStatistics,
//			TeamSeasonStatistics hLastStatistics) {
//		TeamSeasonStatistics gSeasonSatistic = new TeamSeasonStatistics();
//		TeamSeasonStatistics hSeasonStatistics = new TeamSeasonStatistics();
//		
//		if (score < 0){
//			hSeasonStatistics.games = hLastStatistics.games + 1;
//			hSeasonStatistics.win = hLastStatistics.win;
//			hSeasonStatistics.loss = hLastStatistics.loss + 1;
//			hSeasonStatistics.home_all = hLastStatistics.home_all + 1;
//			hSeasonStatistics.guest_win = hLastStatistics.guest_win;
//			hSeasonStatistics.guest_all = hLastStatistics.guest_all;
//			
//			gSeasonSatistic.games = gLastStatistics.games  + 1;
//			gSeasonSatistic.win = gLastStatistics.win + 1;
//			gSeasonSatistic.loss = gLastStatistics.loss;
//			gSeasonSatistic.guest_all = gLastStatistics.guest_all  + 1;
//			gSeasonSatistic.guest_win = gLastStatistics.guest_win + 1;
//			gSeasonSatistic.home_win = gLastStatistics.home_win;
//			gSeasonSatistic.home_all = gLastStatistics.home_all;			
//		}else {
//			hSeasonStatistics.games = hLastStatistics.games + 1;
//			hSeasonStatistics.win = hLastStatistics.win + 1;
//			hSeasonStatistics.loss = hLastStatistics.loss;
//			hSeasonStatistics.home_win = hLastStatistics.home_win + 1;
//			hSeasonStatistics.home_all = hLastStatistics.home_all + 1;
//			hSeasonStatistics.guest_win = hLastStatistics.guest_win;
//			hSeasonStatistics.guest_all = hLastStatistics.guest_all ;
//			
//			
//			gSeasonSatistic.games = gLastStatistics.games  + 1;
//			gSeasonSatistic.loss = gLastStatistics.loss + 1;
//			gSeasonSatistic.win = gLastStatistics.win;
//			gSeasonSatistic.guest_all = gLastStatistics.guest_all  + 1;		
//			gSeasonSatistic.guest_win = gLastStatistics.guest_win;
//			gSeasonSatistic.home_win = gLastStatistics.home_win;
//			gSeasonSatistic.home_all = gLastStatistics.home_all;						
//		}
//		hSeasonStatistics.season_percent = Double.valueOf(numberFormat.format( hSeasonStatistics.win*100 / (hSeasonStatistics.win+hSeasonStatistics.loss)));
//		hSeasonStatistics.guest_percent = Double.valueOf(numberFormat.format( hSeasonStatistics.guest_win*100/ hSeasonStatistics.guest_all));
//		hSeasonStatistics.home_percent = Double.valueOf(numberFormat.format( hSeasonStatistics.home_win*100/ hSeasonStatistics.home_all));
//		
//		gSeasonSatistic.season_percent = Double.valueOf(numberFormat.format( gSeasonSatistic.win*100 / (gLastStatistics.win+gSeasonSatistic.loss)));
//		gSeasonSatistic.guest_percent = Double.valueOf(numberFormat.format( gSeasonSatistic.guest_win*100/ gSeasonSatistic.guest_all));
//		gSeasonSatistic.home_percent = Double.valueOf(numberFormat.format( gSeasonSatistic.home_win*100/ gSeasonSatistic.home_all));			
//		
//		hSeasonStatistics.deadline_date = gameInfo.play_date;
//		hSeasonStatistics.team_id = gameInfo.home_id;
//		hSeasonStatistics.team_name = gameInfo.home_name;
//		hSeasonStatistics.season = Application.season;
//		
//		
//		gSeasonSatistic.deadline_date = gameInfo.play_date;
//		gSeasonSatistic.team_id = gameInfo.guest_id;
//		gSeasonSatistic.team_name = gameInfo.guest_name;
//		gSeasonSatistic.season = Application.season;
//		
//		hSeasonStatistics.save();
//		gSeasonSatistic.save();
//		TeamSeasonStatistics.updateFirst(gameInfo.home_id, Application.startTime,hSeasonStatistics.id);
//		TeamSeasonStatistics.updateSecond(gameInfo.home_id, Application.season,hSeasonStatistics.id);
//		TeamSeasonStatistics.updateFirst(gameInfo.guest_id, Application.startTime,gSeasonSatistic.id);
//		TeamSeasonStatistics.updateSecond(gameInfo.guest_id, Application.season,gSeasonSatistic.id);
//		
//		return "0";
//	}
//
//    
//    private static PlayerInfo  getPlayerInfo(GamePlayerInfo player,long teamId){
//    	PlayerInfo result = PlayerInfo.findPlayer(player.player_name,teamId);
//    	if (result == null){
//    		result = PlayerInfo.findPlayer(player.player_name);
//    		if (result == null){
//    			result = new PlayerInfo();
//    			result.name_chi = player.player_name;
//    			result.team_id = teamId;
//    			result.save();
//    		}else{
//    			result.team_id =teamId;
//    			result.save();
//    		}    		
//    	}    	
//    	return result;
//    }
    
    public static void importTeaMsg(String today){
    	Date gameDate = new Date();
    	if  ( today == null)
    		today = sdf.format(gameDate);
    	
    	try {
				gameDate = sdf.parse(today);    			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    	List<TeamInfo> teams = TeamInfo.getTeams();
    	List<TeamInfo> todayTeams = TeamInfo.getTeams(gameDate);
    	Map<Long,TeamInfo> todayMap = new HashMap<Long, TeamInfo>();
    	for (TeamInfo teamInfo : todayTeams){
    		todayMap.put(teamInfo.id, teamInfo);
    	}	
    	render("/gameDateImport/teamMsgImport.html",teams,todayMap,today);
    }
    
    
	
    
    
    public static void main(String[] args){
//    	importTodayGames();
    }
	
}
