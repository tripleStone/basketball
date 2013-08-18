package controllers;

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

import com.ning.http.util.DateUtil.DateParseException;

import controllers.basedata.BaseDataPlayer;



import models.game.GameInfo;
import models.game.GameInfoReport;
import models.game.GameInfoStatic;
import models.game.GamePlayerInfo;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;
import models.player.PlayerInfo;
import models.player.PlayerSeasonScore;
import models.player.PlayerTeamSeasonScore;
import models.team.TeamInfo;
import models.team.TeamMsg;
import models.team.TeamStSeasonBase;
import models.team.TeamStarters;
import models.team.TeamStartersGame;

import play.mvc.Controller;
import util.DateUtil;

public class AnalysisController  extends Controller{
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Calendar fromCal=Calendar.getInstance();
	/**
	 * 分析首页
	 * @param gameId
	 * @param pageSize
	 */
	public static void index(Long gameId,Integer pageSize,String lytype){
		LotteryHilo lottery = LotteryHilo.getLottery(gameId);
		GameInfo gameInfo = GameInfo.findById(gameId);
		if (pageSize == null)
			pageSize = 10;
		
//		String lyType = "hilo";
		render("/analysis/analysisIndex.html",gameInfo,lytype);
		
	}
	
	/**
	 * 
	 * 球队基本数据报表
	 * @param gameId
	 * @param date
	 * @param lytype
	 */
	public static void base(Long gameId,String date,String lytype){
		GameInfo gameInfo = GameInfo.findById(gameId);
		TeamInfo homeT = Application.teamMap.get(String.valueOf(gameInfo.home_id));
		TeamInfo awayT = Application.teamMap.get(String.valueOf(gameInfo.guest_id));
		Date gameDate = new Date();
		try {
			if (date != null)
				gameDate = DateUtil.parseDate(date);
			else
				gameDate = gameInfo.play_date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TeamStSeasonBase hTeamStatistic = TeamStSeasonBase.getStatistic(homeT.id, gameDate);
		TeamStSeasonBase aTeamStatistic = TeamStSeasonBase.getStatistic(awayT.id, gameDate);
		LotteryHilo hilo = LotteryHilo.getLottery(gameId);
		
		Long homeGreater = LotteryHilo.countHit(gameInfo.home_id, gameInfo.play_date, "greater", gameInfo.season);
		Long homeLess = LotteryHilo.countHit(gameInfo.home_id, gameInfo.play_date, "less", gameInfo.season);
		Long awayGreater = LotteryHilo.countHit(gameInfo.guest_id, gameInfo.play_date, "greater", gameInfo.season);
		Long awayLess = LotteryHilo.countHit(gameInfo.guest_id, gameInfo.play_date, "less", gameInfo.season);
		
		
		render("/analysis/analysisBase.html",hTeamStatistic,aTeamStatistic,
				gameDate,gameId,gameInfo,hilo,lytype,
				homeGreater,homeLess,awayGreater,awayLess);
		
	}
	
	/**
	 * 分析比赛双方最近几场比赛
	 * @param gameId
	 * @param games
	 * @param Lytype
	 */
	public static void recent(Long gameId,Integer games,String lytype){
		GameInfo gameInfo = GameInfo.findById(gameId);
		if (games == null){
			games= 10;
		}
		fromCal.setTime(gameInfo.play_date);
		fromCal.add(Calendar.DATE, -1);		
		List<GameInfo> homeRenGames = GameInfo.getRecentGames(gameInfo.home_id,fromCal.getTime(),games,"all");
		List<GameInfo> guestRenGames = GameInfo.getRecentGames(gameInfo.guest_id,fromCal.getTime(),games,"all");
		
		List<GameInfoStatic> homeStatics  = GameInfoStatic.getGameStaticsHomeAway(gameInfo.home_id,fromCal.getTime(),games);
		List<GameInfoStatic> guestStatics  = GameInfoStatic.getGameStaticsHomeAway(gameInfo.guest_id,fromCal.getTime(),games);	
		
		TeamStSeasonBase hTeamStatistic = TeamStSeasonBase.getStatistic(gameInfo.home_id, fromCal.getTime());
		TeamStSeasonBase aTeamStatistic = TeamStSeasonBase.getStatistic(gameInfo.guest_id, fromCal.getTime());
		
		render("/analysis/analysisRecent.html",gameId,gameInfo,hTeamStatistic,aTeamStatistic,
				homeStatics,guestStatics,homeRenGames,guestRenGames,lytype);		
	}
	
		
	
	/**
	 * 
	 * @param gameId
	 */
	public static void report(Long gameId,int reportType){
		List<GameInfoReport> reports = GameInfoReport.getReports(gameId,2);
		GameInfo gameInfo = GameInfo.findById(gameId);
		StringBuffer sb = new StringBuffer("");
		for (GameInfoReport report : reports ){
			sb = sb.append(report.content);
		}
		String preview = sb.toString();
		render("/analysis/analysisGameReport.html",preview,gameInfo);
	}
	
	/**
	 * 获取最近比赛详细数据
	 * url :/analysis/ajax/recnet/gamestat
	 * @param gameDeadLine
	 * @param homeAway
	 * @param teamId
	 * @param gameId
	 */
	public static void ajaxRecentGameStat(String gameDeadLine,String homeAway,long teamId,long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<GameInfoStatic> gameStatics  =  null;
		if (gameDeadLine.equalsIgnoreCase("trade")){
			try {
				if (homeAway.equalsIgnoreCase("all"))
					gameStatics = GameInfoStatic.getGTDate(teamId, DateUtil.parseDate(Application.tradeTime), 15);
				else if (homeAway.equalsIgnoreCase("home"))
					gameStatics = GameInfoStatic.getGTDateHome(teamId, DateUtil.parseDate(Application.tradeTime), 15);
				else
					gameStatics = GameInfoStatic.getGTDateAway(teamId, DateUtil.parseDate(Application.tradeTime), 15);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}else{
			if (homeAway.equalsIgnoreCase("all"))
				gameStatics = GameInfoStatic.getGameStaticsHomeAway(teamId, gameInfo.play_date, Integer.parseInt(gameDeadLine));
			else if (homeAway.equalsIgnoreCase("home"))
				gameStatics = GameInfoStatic.getHomeStatics(teamId, gameInfo.play_date, Integer.parseInt(gameDeadLine));
			else
				gameStatics = GameInfoStatic.getAwayStatics(teamId, gameInfo.play_date, Integer.parseInt(gameDeadLine));
		}
		render("/analysis/ajax/anlyGameStat.html",gameStatics); 
		
	}
	
	public static void analysiSeason(Long gameId,Integer games){
		GameInfo gameInfo = GameInfo.findById(gameId);
		if (games == null)
			games = 10;
		
		//赛季记录
		TeamStSeasonBase hsi = TeamStSeasonBase.getStatistic(gameInfo.home_id, gameInfo.season,gameInfo.play_date);
		TeamStSeasonBase gsi = TeamStSeasonBase.getStatistic(gameInfo.guest_id, gameInfo.season,gameInfo.play_date);
		//最近10场
		List<GameInfo> hgames  = GameInfo.getRecentGames(gameInfo.home_id, gameInfo.play_date, games,"all");
		List<GameInfo> ggames  = GameInfo.getRecentGames(gameInfo.guest_id, gameInfo.play_date, games,"all");
		TeamStSeasonBase hRecent = getRecentTeamStatics(hgames,gameInfo.home_id);
		TeamStSeasonBase gRecent = getRecentTeamStatics(ggames,gameInfo.guest_id);
		List<GameInfoStatic> homeStatics = new ArrayList<GameInfoStatic>();
		List<GameInfoStatic> awayStatics  = new ArrayList<GameInfoStatic>();
				
		render("/analysis/analysis_season.html",gameId,gameInfo,hsi,gsi,hgames,ggames,
				 hRecent,gRecent,games);
	}
	
	/**
	 * 获取相互比赛成绩
	 * @param gameId
	 * 
	 * v1.0
	 */
	public static void mutual(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
	    //交手记录
		List<GameInfo> mutualGames = GameInfo.getGameInfos(gameInfo.home_id,gameInfo.guest_id,gameInfo.play_date);
		//盘口记录
		List<LotteryWinPoint> mutualWinPoints = LotteryWinPoint.getLotteries(gameInfo.home_name, gameInfo.guest_name,gameInfo.play_date);
		List<LotteryHandicap> mutualHandicaps= LotteryHandicap.getLotteries(gameInfo.home_id, gameInfo.guest_id,gameInfo.play_date);
		List<LotteryHilo> mutualHilos= LotteryHilo.getMatcherHilos(gameInfo.home_id, gameInfo.guest_id,gameInfo.play_date);
		List<LotteryWinLoss>  mutualWinLoses = LotteryWinLoss.getWinLoses(gameInfo.home_id, gameInfo.guest_id,gameInfo.play_date);
		//相互比赛数据统计
		List<GameInfoStatic> statistics = GameInfoStatic.getGameStatics(gameInfo.home_id,gameInfo.guest_id,20,gameInfo.play_date);
		
		render("/analysis/analysis_mutual.html",mutualGames,gameId,gameInfo,mutualWinLoses,
				mutualWinPoints,mutualHandicaps,mutualHilos,statistics);		
	}
	
	/**
	 * 
	 * @param gameId
	 */
	public static void playerRecents(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		render("/analysis/PlayerRecents.html",gameInfo);
	}
	
	/**
	 * POST /analysis/ajax/forecastPlayers/{<\d+>gameId}
	 * @param gameId
	 */
	public static void teamPlayers(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<Long> ids = new ArrayList<Long>();
		Map<Long,PlayerSeasonScore> pssMap = new HashMap<Long, PlayerSeasonScore>();
	
		List<PlayerInfo> players = new ArrayList<PlayerInfo>();
		List<PlayerInfo> homePgs = new ArrayList<PlayerInfo>();
		List<PlayerInfo> homeC = new ArrayList<PlayerInfo>();
		List<PlayerInfo> homePfs = new ArrayList<PlayerInfo>();
		List<PlayerInfo> homeSfs = new ArrayList<PlayerInfo>();
		List<PlayerInfo> homeSgs = new ArrayList<PlayerInfo>();
		
		if (gameInfo.has_detail == 1 ){
			players = PlayerInfo.getDepthPlayersInGame(gameInfo.id,gameInfo.home_id);
		}else{
			players = PlayerInfo.getDepthPlayers(gameInfo.home_id);
		}
			
		BaseDataPlayer.dividPosition(gameInfo, players, homePgs, homeC, homePfs, homeSfs, homeSgs);
		for (PlayerInfo player : players){
			ids.add(player.id);
		}
		
		List<PlayerInfo> awayPgs = new ArrayList<PlayerInfo>();
		List<PlayerInfo> awayC = new ArrayList<PlayerInfo>();
		List<PlayerInfo> awayPfs = new ArrayList<PlayerInfo>();
		List<PlayerInfo> awaySfs = new ArrayList<PlayerInfo>();
		List<PlayerInfo> awaySgs = new ArrayList<PlayerInfo>();
		
		if (gameInfo.has_detail == 1 ){
			players = PlayerInfo.getDepthPlayersInGame(gameInfo.id,gameInfo.guest_id);
		}else{
			players = PlayerInfo.getDepthPlayers(gameInfo.guest_id);		
		}
		BaseDataPlayer.dividPosition(gameInfo, players, awayPgs, awayC, awayPfs, awaySfs, awaySgs);
		for (PlayerInfo player : players){
			ids.add(player.id);
		}
		
		List<PlayerSeasonScore> psses =  PlayerSeasonScore.gets(ids,gameInfo.play_date,2);
		for (PlayerSeasonScore pss:psses){
			pssMap.put(pss.player_id, pss);
		}
				
		render("/basedata/player/playerInfos.html",homePgs,homeC,homePfs,homeSfs,homeSgs,gameInfo,
				awayPgs, awayC, awayPfs, awaySfs, awaySgs,pssMap,gameInfo);	
	}	
	
	/**
	 * POST /analysis/ajax/forecastPlayers/{<\d+>gameId}
	 * @param gameId
	 */
	public static void forecastPlayers(Long gameId){
		GameInfo gameInfo = null;
		if (gameId != null)
			gameInfo = GameInfo.findById(gameId);
		
		List<Long> userIds = new ArrayList<Long>();
		Map<Long,PlayerSeasonScore> pssMap = new HashMap<Long, PlayerSeasonScore>();
		List<PlayerInfo> homePlayers = new ArrayList<PlayerInfo>();
		List<PlayerInfo> awayPlayers = new ArrayList<PlayerInfo>();
		
		if (gameInfo.has_detail == 1 ){
			homePlayers = PlayerInfo.getDepthPlayersInGame(gameInfo.id, gameInfo.home_id);
			awayPlayers = PlayerInfo.getDepthPlayersInGame(gameInfo.id,gameInfo.guest_id);
		}else{
			homePlayers = PlayerInfo.getDepthPlayers(gameInfo.home_id);			
			awayPlayers = PlayerInfo.getDepthPlayers(gameInfo.guest_id);
		}
		
		for (PlayerInfo player :homePlayers ){
			userIds.add(player.id);
		}
		
		for (PlayerInfo player :awayPlayers ){
			userIds.add(player.id);
		}
		
		List<PlayerSeasonScore> psses =  PlayerSeasonScore.gets(userIds,gameInfo.play_date,2);
		for (PlayerSeasonScore pss:psses){
			pssMap.put(pss.player_id, pss);
		}
		
		render("/analysis/ajax/anlyForecastPlayers.html", gameInfo,homePlayers,awayPlayers,pssMap);
	}
	
	/**
	 * 分析首发阵容
	 * @param gameId
	 */
	public static void teamStarters(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		GameInfo firstGame = GameInfo.getGameInfos(gameInfo.home_id, Integer.valueOf(gameInfo.season)).get(0);
		List<TeamStarters> homeStartersList =  TeamStarters.getStarters(gameInfo.home_id, 
				firstGame.play_date, gameInfo.play_date);
		
		firstGame = GameInfo.getGameInfos(gameInfo.guest_id, Integer.valueOf(gameInfo.season)).get(0);
		List<TeamStarters> awayStartersList =  TeamStarters.getStarters(gameInfo.guest_id, 
				firstGame.play_date, gameInfo.play_date);
		
		List<GameInfo> gameInfos = GameInfo.getGameInfos(gameInfo.home_id,gameInfo.guest_id,
				firstGame.play_date,gameInfo.play_date);
		List<TeamStartersGame> startersGames =  new ArrayList<TeamStartersGame>();
		Map<Long,TeamStartersGame> gamesMap = new HashMap<Long, TeamStartersGame>();
		if (gameInfos != null && gameInfos.size() > 0){
			List<Long> gameIds = new ArrayList<Long>();
			for (GameInfo gi : gameInfos){
				gameIds.add(gi.id);
			}
			
			startersGames = TeamStartersGame.getStartersGames(gameIds);
			for (TeamStartersGame tmp : startersGames){
				gamesMap.put(tmp.teamStarters.id, tmp);
			}
		}
		
		
		render("/analysis/ajax/anlyGameStarters.html",homeStartersList,awayStartersList,gameInfo,gamesMap);
		
	} 
	
	/**
	 * 经验页
	 * @param gameId
	 */
	public static void experience(Long gameId,String lyType){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<GameInfoReport> reports = GameInfoReport.getReports(gameId,3);
		String report = "";
		for (GameInfoReport greport : reports){
			report =  report + greport.content;
		}
		
		render("/analysis/analysisExperience.html",gameInfo,report,lyType);
	}


	
/***********method*******/
	
	private static TeamStSeasonBase getRecentTeamStatics(List<GameInfo> gameInfos ,long teamId){
		TeamStSeasonBase tmp = new TeamStSeasonBase();
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		
		for (GameInfo game:gameInfos){
				if (game.home_id == teamId){
					
					tmp.home_score = tmp.home_score + game.home_score;
					tmp.home_lost_score = tmp.home_lost_score + game.guest_score;
					if (game.home_score > game.guest_score){
						tmp.win = tmp.win + 1;
					}else{
						tmp.loss = tmp.loss + 1;
					}
					
				}else{
					
					tmp.home_score = tmp.home_score + game.guest_score;
					tmp.home_lost_score = tmp.home_lost_score + game.home_score;
					if (game.guest_score > game.home_score){
						tmp.win = tmp.win + 1;
					}else{
						tmp.loss = tmp.loss + 1;
					}
					
				}
		}
		if (gameInfos.size() > 0){
			tmp.home_score = Double.valueOf(format.format(tmp.home_score / gameInfos.size()));
			tmp.home_lost_score = Double.valueOf(format.format(tmp.home_lost_score / gameInfos.size()));
			tmp.home_net_score = Double.valueOf(format.format(tmp.home_score - tmp.home_lost_score));
			tmp.home_percent = Double.valueOf(format.format(tmp.win*100/(tmp.win + tmp.loss)));
		}
		return tmp;
		
	} 

	

	
	
	
}
