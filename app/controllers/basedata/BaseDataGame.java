package controllers.basedata;

import java.text.ParseException;
import java.util.List;

import models.game.GameChart;
import models.game.GameInfo;
import models.game.GameInfoReport;
import models.game.GameInfoStatic;
import models.game.GameInjury;
import models.game.GamePlayerInfo;
import models.game.GameQuarterScore;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;
import models.team.TeamStSeasonBase;
import models.team.TeamStSeasonScore;
import play.mvc.Controller;
import util.DateUtil;

/**
 * 基础数据，所有从网页上获取的数据都是基础数据
 * @author zhuangl
 *
 */
public class BaseDataGame extends Controller{
	
	/**
	 * 单场比赛数据
	 * basedata
	 * url /basedata/game/score/{<\d+>gameId}
	 * @param gameId
	 */
	public static void gameScore(long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);		
		render("/basedata/game/gameScore.html",gameInfo,gameId);
	}
	
	/**
	 * 比赛战报
	 * basedata
	 * /basedata/game/report/{<\d+>gameId} 
	 * @param gameId
	 * @param reportType
	 */
	public static void gameReport(long gameId,int reportType){
		List<GameInfoReport> reports = GameInfoReport.getReports(gameId,reportType);
		StringBuffer sb = new StringBuffer("");
		for (GameInfoReport report : reports ){
			sb = sb.append(report.content);
		}
		String report = sb.toString();
		String position = "baseData";
		GameInfo gameInfo = GameInfo.findById(gameId);
		render("/basedata/game/gameReport.html",gameId,report,position,gameInfo);
	}
	
	/**
	 * 比赛博彩赔率
	 * basedata
	 * /basedata/game/lottery/{<\d+>gameId}
	 * @param gameId
	 */
	public static void gameLottery(long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);		
		List<LotteryWinPoint> lwps = LotteryWinPoint.getLotteryHis(gameInfo.id);
		List<LotteryHandicap> lhandicaps = LotteryHandicap.getLotteryHis(gameInfo.id);
		List<LotteryHilo> hilos = LotteryHilo.getLotteries(gameInfo.id);
		List<LotteryWinLoss> winloses = LotteryWinLoss.getLotteries(gameInfo.id);
		
		render("/basedata/game/gameLottery.html",gameId,gameInfo,lwps,lhandicaps,hilos,winloses);
	}
	
	/**
	 * 比赛博彩赔率
	 * basedata
	 * /basedata/game/espnchart/${_gameId}
	 * @param gameId
	 */
	public static void gameEspnChart(long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);		
		List<GameChart> charts = GameChart.getGameCharts(gameId);
		for (GameChart chart : charts){
			gameInfo.chart = gameInfo.chart  + chart.chartHtml;
		}
		
		render("/basedata/game/gameEspnChart.html",gameId,gameInfo);
	}	
	
	/**
	 * 经验
	 * @param gameId
	 */
	public static void experience(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<GameInfoReport> reports = GameInfoReport.getReports(gameId,3);
		String report = "";
		for (GameInfoReport greport : reports){
			report =  report + greport.content;
		}
		
		render("/basedata/game/gameExperience.html",gameInfo,report);
	}
	
	/**
	 * GET /basedata/game/teamStatements/{<\d+>gameId}
	 * @param gameId
	 * @throws ParseException
	 */
	public static void gameStatements(long gameId) throws ParseException{
		GameInfo gameInfo = GameInfo.findById(gameId);
		GameInfoStatic homeGameStatistic = GameInfoStatic.getGameStatic(gameInfo.home_id, gameId);
		GameInfoStatic awayGameStatistic = GameInfoStatic.getGameStatic(gameInfo.guest_id, gameId);
		
		TeamStSeasonBase homeSeason = TeamStSeasonBase.getStatistic(gameInfo.home_id, gameInfo.season, 
				DateUtil.addDate(gameInfo.play_date,-1));		
		TeamStSeasonBase awaySeason = TeamStSeasonBase.getStatistic(gameInfo.guest_id, gameInfo.season, 
				DateUtil.addDate(gameInfo.play_date,-1));
		
		
		List<TeamStSeasonScore> homeTeamStScores = TeamStSeasonScore.getScores(gameInfo.home_id, 
				DateUtil.addDate(gameInfo.play_date,-1),1,4);		
		List<TeamStSeasonScore> awayTeamStScores = TeamStSeasonScore.getScores(gameInfo.guest_id, 
				DateUtil.addDate(gameInfo.play_date,-1),1,4);
		
		render("/basedata/game/gameStatements.html",homeGameStatistic,awayGameStatistic,homeSeason,awaySeason
				,homeTeamStScores,awayTeamStScores,gameInfo);
	}
	
	/**
	 * POST  /basedata/game/ajaxreport/{<\d+>gameId}
	 * @param gameId
	 * @param reportType
	 */
	public static void ajaxGameReport(long gameId,int reportType){
		List<GameInfoReport> reports = GameInfoReport.getReports(gameId,reportType);
		StringBuffer sb = new StringBuffer("");
		for (GameInfoReport report : reports ){
			sb = sb.append(report.content);
		}
		String report = sb.toString();
		render("/basedata/game/ajax/gameReport.html",report);
	}	
	
	/**
	 * 获取比赛的球队信息
	 * @param gameId
	 * @param teamId
	 */
	public static void gamePlayersInfo(long gameId,long teamId){
		List<GamePlayerInfo> gamePlayers = GamePlayerInfo.gets(gameId, teamId);
		GameInfoStatic  gameStatistic =  GameInfoStatic.getGameStatic(teamId, gameId);
		render("/basedata/game/ajax/gamePlayersInfo.html",gamePlayers,gameStatistic);		
	}
	
	/**
	 * 获取比赛的每节比分
	 * @param gameId
	 * @param homeId
	 * @param awayId
	 */
	public static void gameQuarters(long gameId,Long homeId,Long awayId  ){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<GameQuarterScore> gameQuartersHome = GameQuarterScore.getQuarterScores(gameId, homeId);
		List<GameQuarterScore> gameQuartersAway = null;
		if (awayId != null)
			gameQuartersAway = GameQuarterScore.getQuarterScores(gameId, awayId);	
		render("/basedata/game/ajax/gameQuarters.html",gameQuartersHome,gameQuartersAway,gameInfo);
	}
	
	/**
	 * POST /basedata/game/ajax/injury
	 * @param gameId
	 */
	public static void injuries(long gameId){
		GameInfo game = GameInfo.findById(gameId);
		List<GameInjury> homeInjuries = GameInjury.getInjuries(game.id,game.home_id);
		List<GameInjury> awayInjuries = GameInjury.getInjuries(game.id,game.guest_id);
		StringBuffer playerBuffer = new StringBuffer("");
		for (int i=0;i<homeInjuries.size();i++){
			if (i==0){
				playerBuffer.append(homeInjuries.get(i).playerName);
			}else{
				playerBuffer.append(",").append(homeInjuries.get(i).playerName);
			}
		}
		
		for (int i=0;i<awayInjuries.size();i++){
			if (i==0 && homeInjuries.size() == 0){
				playerBuffer.append(awayInjuries.get(i).playerName);
			}else{
				playerBuffer.append(",").append(awayInjuries.get(i).playerName);
			}
		}
		
		String names = playerBuffer.toString();
		
		render("/basedata/game/ajax/injuries.html",homeInjuries,awayInjuries,game,names);
	}
	
	

	
	
	

}
