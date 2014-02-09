package controllers.dataimport;


import gsonmoudle.EspnGame;
import gsonmoudle.EspnGameTeam;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.game.GameChart;
import models.game.GameInfo;
import models.game.GameInfoReport;
import models.game.GameInfoStatic;
import models.game.GameInjury;
import models.game.GamePlayByPlay;
import models.game.GamePlayerInfo;
import models.game.GameQuarterScore;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;
import models.player.PlayerInfo;
import models.team.TeamInfo;
import models.team.TeamStSeasonBase;

import org.jsoup.nodes.Document;

import controllers.Application;
import controllers.GameDataImport;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Util;
import util.DateUtil;
import util.htmlparser.EspnHtmlParser;
import util.htmlparser.HupuHtmlParser;
import util.htmlparser.UrlUtil;

public class EspnImport extends Controller {
	
	/**
	 * 集中导入今天一天所有的比赛数据
	 * @param postUrl
	 */
	public static String importTodayGames(String postUrl) {
    	Date todayDate = new Date();
    	String url = postUrl;
    	String today =  DateUtil.getDateStr(todayDate);
    	Document doc;
    	
		try {
			doc = UrlUtil.getURLContent(url);
			List<EspnGame> espns = EspnHtmlParser.getTodayGames(doc);
			for(EspnGame espn : espns){
				GameInfo gameInfo =  getGameByEspnGame(espn, today);
				boolean noplayer = false ;
				if (espn.getStatus() != 3){
					continue;
				}			
				
				Logger.info("espn id is %s", gameInfo.espnId);
				if (gameInfo.has_detail != 1){
					String dataUrl = "http://scores.espn.go.com/nba/boxscore?gameId=" + espn.getGameId();
					Document tmpDoc = UrlUtil.getURLContent(dataUrl);
					String gmInfoResult = saveGameInfo(tmpDoc, gameInfo.id, gameInfo);
					if (gmInfoResult.equalsIgnoreCase("noplayer")){
						noplayer = true;
					}
				}
				if (gameInfo.has_chart != 1){
//					String dataUrl = "http://scores.espn.go.com/nba/shotchart?gameId=" + espn.getGameId();
					saveGameChart( gameInfo.id, gameInfo);
				}
				if (noplayer){
					
				}
			}
			
			
			return("{\"error\":\"1\"}");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		
		return("{\"error\":\"1\"}");
    }

	/**按日导入比赛信息
	 * POST /import/espn/importGamesByDay
	 * @param date
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String importGamesByDay(String date) throws IOException, ParseException{
		String tmpDate = DateUtil.getDateStr(DateUtil.addDate(DateUtil.parseDate(date),-1));
		String url = Application.espnSchedule + tmpDate.replaceAll("-", "");
		Document doc = UrlUtil.getURLContent(url);
		List<String> urls = EspnHtmlParser.getGameStatisticUrls(doc);
		List<GameInfo> gameInfos = GameInfo.getGameInfos(DateUtil.parseDate(date));
		for ( String tmp : urls){
			String espnId = tmp.substring(tmp.lastIndexOf("=")+1,tmp.length() );
			String dataUrl = "http://scores.espn.go.com/nba/boxscore?gameId=" + espnId;
			Document tmpDoc = UrlUtil.getURLContent(dataUrl);
			
			for (int i=gameInfos.size() -1;i >= 0 ;i--){
				GameInfo gi = gameInfos.get(i);
				String result = saveGameInfo(tmpDoc, gi.id, gi);	
				if (result.indexOf("error") >= 0){
					continue;
				}
				
				gi.espnId = Long.valueOf(espnId);
				gi.save();
				if (gi.has_chart != 1){
					saveGameChart( gi.id, gi);
				}
				
			}
		}		
		
		return("{\"error\":\"1\"}");
	}
	
	/**
	 * 单个从从espn导入flashshot图
	 * @param gameId
	 */
	public static void importChart(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		try {
			saveGameChart(gameId, gameInfo);
			
			renderJSON("{\"error\":\"1\",\"msg\":\"导入成功\"}");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJSON("{\"error\":\"2\",\"msg\":\"异常错误！\"}");
		}		
	}
	
    /**
    * 导入单场比赛数据
    * @param dataUrl
    */
   public static void impotGameData(String dataUrl,Long gameId){    	
		GameInfo gameInfo = GameInfo.findById(gameId);

		
		try {
			Document tmpDoc = UrlUtil.getURLContent(dataUrl);
			saveGameInfo(tmpDoc, gameId, gameInfo);			
			renderJSON("{\"error\":\"导入成功\"}");		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJSON("{\"error\":\"出错了\"}");
			
		}
   }


    @Util
	private static String saveGameInfo(Document doc, Long gameId,
			GameInfo gameInfo) throws IOException, ParseException {
		List<GamePlayerInfo> homePlayers = null;
		List<GamePlayerInfo> guestPlayers = null;
		boolean noplayer = false;	
		
		//校验比赛信息
		 GameInfo tmpGameInfo = EspnHtmlParser.getGame(doc);
		 TeamInfo homeTeam = TeamInfo.getTeamByEngName(tmpGameInfo.home_name.toLowerCase());
		 TeamInfo guestTeam = TeamInfo.getTeamByEngName(tmpGameInfo.guest_name.toLowerCase());
		 
		 if (homeTeam.id != gameInfo.home_id || guestTeam.id != gameInfo.guest_id  ){
			 return("{\"error\":\"获取比赛信息失败，未找到该场比赛数据\"}");
		 }else  if (gameInfo.has_detail == 1){
			 return("{\"error\":\"这场比赛已经录入过了\"}");
		 }

		 //保存球员和比赛数据统计
		GamePlayerInfo.delByGame(gameInfo.id);
		TeamStSeasonBase.delByGame(gameInfo.home_id, gameInfo.play_date);
		TeamStSeasonBase.delByGame(gameInfo.guest_id, gameInfo.play_date);
		GameInfoStatic.delByGame(gameId);
		GameQuarterScore.delByGame(gameInfo.id);
		
		//球队数据--begin
		GameInfoStatic guestInfoStatic = EspnHtmlParser.getGameStatistics(doc, 2, guestTeam, gameInfo);
		if (guestInfoStatic == null)
			return "2";
		guestInfoStatic.save();
		GameInfoStatic homeInfoStatic = EspnHtmlParser.getGameStatistics(doc, 5, homeTeam, gameInfo);
		homeInfoStatic.save();
		//球队数据--end
				 	
		//球员数据--begin				
		guestPlayers =   EspnHtmlParser.getPlayerInfos(doc,0,guestTeam,gameInfo);
		for (GamePlayerInfo gamePlayer : guestPlayers ){
			PlayerInfo playerInfo = PlayerInfo.findPlayer(gamePlayer.espnId);
			if (playerInfo != null){
				gamePlayer.player_id = playerInfo.id;
			}else{
				noplayer = true;
				Logger.info(" %s is not in team ", gamePlayer.player_name);
			}
			gamePlayer.save();
		} 
			
		homePlayers =    EspnHtmlParser.getPlayerInfos(doc,3,homeTeam,gameInfo);
		for (GamePlayerInfo gamePlayer :homePlayers){
			PlayerInfo playerInfo = PlayerInfo.findPlayer(gamePlayer.espnId);
			if (playerInfo != null){
				gamePlayer.player_id = playerInfo.id;
			}else{
				noplayer = true;
				Logger.info(" %s is not in team ", gamePlayer.player_name);
			}
			gamePlayer.save();				
		}
		//球员数据--end		
		//保存每节比赛成绩--begin		
		gameInfo = saveGameQuarters(doc,gameInfo);							
//			gameInfo = UrlUtil.setGameInfo(doc, gameInfo);
		
//		gameInfo.static_url = dataUrl;
		gameInfo.has_detail = 1;
		gameInfo.save();
		
		//赔率设置
		saveLotteryWinPoint(gameInfo);
		saveLotteryHandicap(gameInfo);
		saveLotteryHilo(gameInfo);
		saveLotteryWinLoss(gameInfo);
		String result = "OK";
		
		if (noplayer)
			result = "noplayer";
		
		return result;
	}
	
	/**
	 * 批量导入espn预测
	 * /dataimport/espn/importPreviews
	 * @param gameId
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String importPreviews(String date) throws IOException, ParseException{
		Date gameDate = DateUtil.parseDate(date);
		String url = Application.espnSchedule + date.replaceAll("-", "");
		Document doc = UrlUtil.getURLContent(url);
		List<String> urls = EspnHtmlParser.getNextDayGameUrl(doc);
		if (urls != null && urls.size() > 0){
			for (String preurl : urls){
				preurl = "http://espn.go.com" + preurl;
				doc = UrlUtil.getURLContent(preurl);
				gameDate = DateUtil.addDate(DateUtil.parseDate(date),1);
				savePreviews(gameDate, doc);
			}
		}
		

		return("{\"error\":\"录入成功\"}");
	}
	
	/**
	 * 单个导入espn预测
	 * /dataimport/espn/importPreview
	 * @param gameId
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String importPreview(Long gameId) throws IOException, ParseException{
		GameInfo game = GameInfo.findById(gameId);	
		String url = "http://espn.go.com/nba/preview?id="+game.espnId;
		Document doc = UrlUtil.getURLContent(url);		
		savePreviews(game.play_date, doc);
		return("{\"error\":\"录入成功\"}"); 
	}

	/**
	 * 
	 * @param date
	 * @param doc
	 * @throws ParseException
	 */
	private static void savePreviews(Date date, Document doc)
			throws ParseException {
		GameInfo gameInfo = EspnHtmlParser.preview(doc);
		TeamInfo hteam = Application.teamMap.get(gameInfo.home_name.toLowerCase());
		TeamInfo ateam = Application.teamMap.get(gameInfo.guest_name.toLowerCase());
		
		GameInfo gameData = GameInfo.getGameInfo(hteam.id, ateam.id, date);
		
		List<GameInfoReport> reports = HupuImport.cutContent(gameInfo.reportTitle);
		List<GameInfoReport> dataReports = GameInfoReport.getReports(gameInfo.id, 1);
		
		if (dataReports == null || dataReports.size()==0 || dataReports.size() != reports.size() ){
			GameInfoReport.delReports(gameData.id,2);
			for (GameInfoReport report : reports){
				report.gameId = gameData.id;
				report.rtype = 2;
				report.save();					
			}
			
			gameData.espnId = gameInfo.espnId;
			gameData.usa_play_date = gameInfo.usa_play_date;
			gameData.play_time = gameInfo.play_time;
			gameData.hasPreview = 1;
			gameData.save();
		}
		
		GameInjury.del(gameData.id);				
		List<GameInjury> injuries = EspnHtmlParser.injury(doc,0);
		for ( GameInjury injury : injuries ){
			injury.gameId = gameData.id;
			injury.teamId = gameData.guest_id;
			injury.ceateDate = new Date();
			injury.save();
		}
		
		
		injuries = EspnHtmlParser.injury(doc,1);
		for ( GameInjury injury : injuries ){
			injury.gameId = gameData.id;
			injury.teamId = gameData.home_id;
			injury.ceateDate = new Date();
			injury.save();
		}
	}
	
	
	/**
	 * url : /dataimport/espn/importPlayByPlay
	 * @param date
	 */
	public static void importPlayByPlay(String date){
		try {
			List<GameInfo> gameInfos = GameInfo.getGameInfos(DateUtil.parseDate(date));
			String url = Application.espnPlayByPlay;
			List<Long> teamIds = new ArrayList<Long>();
			Map<String,PlayerInfo> playerMap = new HashMap<String, PlayerInfo>();
			
			for (GameInfo game : gameInfos){
				teamIds.add(game.home_id);
				teamIds.add(game.guest_id);
			}
			
			List<PlayerInfo> players = PlayerInfo.findPlayers(teamIds);
			for(PlayerInfo player : players){
				playerMap.put( player.name, player);
			}
			
			
			for (GameInfo game : gameInfos){
				if (game.has_playbyplay == 1)
					continue;
				
				Logger.info( "import playbyplay url is %s",url.replace("{1}", String.valueOf(game.espnId) ) );
				Document doc = UrlUtil.getURLContent(url.replace("{1}", String.valueOf(game.espnId)));
				Map<String,Long> teamMap = new HashMap<String, Long>();
				teamMap.put("home", game.home_id);
				teamMap.put("away", game.guest_id);
				try {
					List<GamePlayByPlay> playes = EspnHtmlParser.getPlayByPlay(doc, teamMap);
					for (GamePlayByPlay play : playes){
						play.game = game;
						if (play.playerName != null){
							if (playerMap.get(play.playerName) != null)
								play.player = playerMap.get(play.playerName);
							else
								play.player = PlayerInfo.findPlayerEng(play.playerName);
						}
						if (play.assisterName != null){
							if (playerMap.get(play.playerName) != null)
								play.assister = playerMap.get(play.assisterName);
							else
								play.assister = PlayerInfo.findPlayerEng(play.assisterName);
						}
	//					System.out.println("=================");
	//					System.out.println(play.team_id);
	//					System.out.println(play.get_point);
	//					System.out.println(play.time);
	//					System.out.println(play.quarter);
	//					System.out.println(play.score);
	//					System.out.println(play.shoot_distance);
	//					System.out.println(play.team_point);
	//					System.out.println(play.second);
	//					System.out.println(play.score);
	//					System.out.println(play.assisterName);
	//					System.out.println(play.content);
						if (play.get_point > 0 && Math.abs(play.get_point) < 10)
							play.save();
					}
					game.has_playbyplay = 1;
					game.save();
				}catch(Exception ex){
					Logger.info( "importatnt error playbyplay url is %s",url.replace("{1}", String.valueOf(game.espnId) ) );
				}
				
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 工具方法：保存比赛图标。
	 * @param gameId
	 * @param gameInfo
	 * @throws IOException
	 */
	@Util
	private static void saveGameChart(Long gameId, GameInfo gameInfo)
			throws IOException {
		Document doc;
//		if (gameInfo.static_url.indexOf("espn") < 0){
//			renderJSON("{\"error\":\"2\",\"msg\":\"数据不是来自espn\"}");
//		}
		doc = UrlUtil.getURLContent("http://espn.go.com/nba/shotchart?gameId="+gameInfo.espnId);
		String charStr = EspnHtmlParser.getReportString(doc);
		int i = 1;
		
		while (charStr.length() > 0){
			GameChart chart = new GameChart();
			chart.gameId = gameId;
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
	
	@Util
    public static void saveLotteryHandicap(GameInfo gameInfo){
    	LotteryHandicap handiCap = LotteryHandicap.getLottery(gameInfo.id);
    	if (handiCap != null){
    		String hit = "";
	    	if((gameInfo.home_score + handiCap.handicap) > gameInfo.guest_score){
	    		hit = "win";
	    	}else{
	    		hit = "lose";
	    	}
	    	LotteryHandicap.setHc(handiCap.game_id, gameInfo.guest_score - gameInfo.home_score,
	    			gameInfo.home_score, gameInfo.guest_score, hit);
	    	
    	}
    }
    
    @Util
    public static void saveLotteryHilo(GameInfo gameInfo){
    	LotteryHilo hilo = LotteryHilo.getLottery(gameInfo.id);
    	if (hilo != null){
    		String hit = "";
	    	if( gameInfo.home_score + gameInfo.guest_score > hilo.hilo){
	    		hit = "greater";
	    	}else{
	    		hit = "less";
	    	}
	    	long sumScore = gameInfo.home_score + gameInfo.guest_score;
	    	LotteryHilo.setHilos(hilo.game_id,hit, sumScore);
    	}
    }
    
    @Util
    public static void saveLotteryWinLoss(GameInfo gameInfo){
    	LotteryWinLoss winLoss = LotteryWinLoss.getLottery(gameInfo.id);
    	if (winLoss != null){
	    	if( gameInfo.home_score > gameInfo.guest_score ){
	    		winLoss.hit = 1;
	    	}else{
	    		winLoss.hit = 0;
	    	}
	    	winLoss.save();
    	}
    }

    @Util
	public static void saveLotteryWinPoint(GameInfo gameInfo) {
		LotteryWinPoint lottery = LotteryWinPoint.getLottery(gameInfo.id);
		if (lottery != null){
			
			int score = gameInfo.home_score - gameInfo.guest_score;
			int span = 1;
			String hit = "h_";
			if (score < 0){
				hit = "g_";
				span = -1;
			}
			
			score = Math.abs(score);
			if (score>0 && score<=5){
				hit = hit + "1";
				span = span * 1;
			} else if (score>5 && score<=10){
				hit = hit + "2";
				span = span * 2;
			}else if (score>10 && score<=15){
				hit = hit + "3";
				span = span * 3;
			}else if (score>15 && score<=20){
				hit = hit + "4";
				span = span * 4;
			}else if (score>20 && score<=25){
				hit = hit + "5";
				span = span * 5;
			}else{
				hit = hit + "6";
				span = span * 6;
			}
			score = gameInfo.home_score - gameInfo.guest_score;
			
			LotteryWinPoint.setLotteries(lottery.game_id, score, span, hit,gameInfo.home_score,gameInfo.guest_score);
//			
//			lottery.hit = hit;
//			lottery.save();
		}
	}
	
    @Util
    private static GameInfo saveGameQuarters(Document doc,GameInfo gameInfo){
			 List<GameQuarterScore> gameQuartersHome = new ArrayList<GameQuarterScore>();
			 List<GameQuarterScore> gameQuartersGuest = new ArrayList<GameQuarterScore>();
    	String quarterString = "";
    	gameQuartersGuest = EspnHtmlParser.getGameQuarters(doc, 1,gameInfo.id);
    	gameQuartersHome = EspnHtmlParser.getGameQuarters(doc, 2,gameInfo.id);
    	gameInfo.home_score = 0;
    	gameInfo.guest_score = 0;
		for (int i=0;i<gameQuartersGuest.size();i++){
			if(i == 0){
				quarterString =  gameQuartersGuest.get(i).score  +"-" + gameQuartersHome.get(i).score;
			}else{
				quarterString =  quarterString + "," + gameQuartersGuest.get(i).score  +"-" + gameQuartersHome.get(i).score;
			}
			
			 gameQuartersHome.get(i).game_id = gameInfo.id;
			 gameQuartersHome.get(i).team_id = gameInfo.home_id;
			 gameQuartersHome.get(i).team_name = gameInfo.home_name;
			 gameQuartersHome.get(i).save();
			 gameInfo.home_score = gameInfo.home_score  + gameQuartersHome.get(i).score;
			
			 gameQuartersGuest.get(i).game_id = gameInfo.id;
			 gameQuartersGuest.get(i).team_id =gameInfo.guest_id;
			 gameQuartersGuest.get(i).team_name = gameInfo.guest_name;
			 gameQuartersGuest.get(i).save();
			 gameInfo.guest_score = gameInfo.guest_score  + gameQuartersGuest.get(i).score;
		}
		gameInfo.each_quarter = quarterString;   
    	
    	return gameInfo;
    }
	  
    @Util
	public static TeamInfo checkTeam(TeamInfo team,EspnGameTeam eteam){
    	if (team == null){
    		team = TeamInfo.getTeamByEngName(eteam.getNickname().toLowerCase());
    		team.espnId = Long.valueOf(eteam.getId());
    		team.save();
    	}
    	return team;
	}
	
    @Util
    public static GameInfo getGameByEspnGame(EspnGame espn,String today) throws ParseException{
    	
    	
		TeamInfo home = TeamInfo.getTeamByEspnId(Long.valueOf(espn.getHome().getId()));
		home = checkTeam(home,espn.getHome());
		TeamInfo away = TeamInfo.getTeamByEspnId(Long.valueOf(espn.getAway().getId()));
		away = checkTeam(away,espn.getAway());			
		if (away == null || home == null){
			Logger.info("waining : %s vs %s team can not find", espn.getHome().getNickname(),espn.getAway().getNickname());
			return null;
		}
		GameInfo gameInfo = GameInfo.getGameInfo(home.id, away.id, DateUtil.parseDate(today));
		if (gameInfo == null){
			Logger.info("waining : %s vs %s game can not find", espn.getHome().getNickname(),espn.getAway().getNickname());
			return null;					
		}
		
		
		
		
		if (gameInfo.espnId == 0){
			gameInfo.espnId = espn.getGameId();
			gameInfo.save();
		}
		
		if (espn.getStatus() == 3 && gameInfo.status !=3 ){
			gameInfo.status = 3;
			gameInfo.save();
		}
		return gameInfo;
    }
}
