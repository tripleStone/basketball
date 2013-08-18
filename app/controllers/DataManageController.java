package controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.management.Query;

import controllers.dataimport.HupuImport;

import models.game.GameInfo;
import models.game.GameInfoReport;
import models.game.GameInfoStatic;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinPoint;
import models.player.PlayerInfo;
import models.player.PlayerSeasonScore;
import models.player.PlayerTeamSeasonScore;
import models.team.TeamInfo;
import models.team.TeamStSeasonBase;
import models.team.TeamStSeasonScore;

import play.Play;
import play.mvc.Controller;
import util.DateUtil;
import util.htmlparser.HupuHtmlParser;
import util.htmlparser.UrlUtil;

public class DataManageController extends Controller{
	
	public static void infoTeams(){
		List<TeamInfo> teamsInfo = TeamInfo.findAll();
		render("/informanage/infoTeams.html",teamsInfo);
	}
	
	public static void statisticGames(String gameDate) throws ParseException{
		if (gameDate == null)
			gameDate = DateUtil.getDateStr(new Date());
		GameInfoStatic gis = GameInfoStatic.lastOne();
		TeamStSeasonBase tss = TeamStSeasonBase.lastOne();
		String tsssDate = (String)TeamStSeasonScore.lastDate();
		
		PlayerSeasonScore pss = PlayerSeasonScore.lastOne();
		PlayerTeamSeasonScore ptss = PlayerTeamSeasonScore.lastOne();
		
		
		List<TeamInfo> teams = TeamInfo.getTeamsNoRgP(DateUtil.parseDate(gameDate));
		
		render("/datamanage/statisticGames.html",teams,gameDate,gis,tss,tsssDate,pss,ptss);
	}
	
	public static void editTeamInfo(long teamId){
		TeamInfo teamInfo = null;
		List<PlayerInfo> players = null;
		if (teamId != 0){
			teamInfo = TeamInfo.findById(teamId);
			players = PlayerInfo.findPlayers(teamId);
		}
		render("/informanage/editTeamInfo.html",teamInfo,teamId,players);	
	}

	public static void saveTeamInfo(long teamId,String team_name,String full_name,
			String bigarea,String smallarea,String comments){
		TeamInfo teamInfo = null;
		if (teamId != 0)
			teamInfo = TeamInfo.findById(teamId);
		else
			teamInfo = new TeamInfo();
		teamInfo.team_name = team_name;
		teamInfo.full_name = full_name;
		teamInfo.bigarea = bigarea;
		teamInfo.smallarea = smallarea;
		teamInfo.comments = comments;
		teamInfo.save();
		renderJSON("{\"error\":\"1\"}");		
	}
	

	
	/**
	 * 调用
	 * @param lotteryType
	 * @param lotteryId
	 */
	public static void callLotteryExp(String lotteryType,Long lotteryId){
		String typeDesc = "";
		if (lotteryType.equals("winpoint")){
			LotteryWinPoint lottery = LotteryWinPoint.findById(lotteryId);
			typeDesc = "胜分差";
			
			render("/datamanage/ajax/edit_lottery_exp.html",typeDesc,lotteryType,lotteryId,lottery);
		}else if (lotteryType.equals("hilo")){
			LotteryHilo lottery = LotteryHilo.findById(lotteryId);
			typeDesc = "大小分";
			
			render("/datamanage/ajax/edit_lottery_exp.html",typeDesc,lotteryType,lotteryId,lottery);
		}else{
			LotteryHandicap lottery = LotteryHandicap.findById(lotteryId);
			typeDesc = "大小分";			
			render("/datamanage/ajax/edit_lottery_exp.html",typeDesc,lotteryType,lotteryId,lottery);
		}		
	}
	
	/**
	 * 保存投注经验
	 * @param exp
	 * @param lotteryId
	 * @param lotteryType
	 */
	public static void saveLotteryExp(String exp,Long lotteryId,String lotteryType){
		if (lotteryType.equals("winpoint")){
			LotteryWinPoint lottery = LotteryWinPoint.findById(lotteryId);
			lottery.exp = exp;
			lottery.save();
			
			renderJSON("{\"error\":\"1\"}");
		}else if (lotteryType.equals("hilo")){
			LotteryHilo lottery = LotteryHilo.findById(lotteryId);
			lottery.exp = exp;
			lottery.save();
			
			renderJSON("{\"error\":\"1\"}");
		}else{
			LotteryHandicap lottery = LotteryHandicap.findById(lotteryId);
			lottery.exp = exp;
			lottery.save();
			
			renderJSON("{\"error\":\"1\"}");
		}		
		
	}
	
//    public static void editExperience(long gameId){    	
//		GameInfo gameInfo = GameInfo.findById(gameId);
//		List<GameInfoReport> reports = GameInfoReport.getReports(gameId,3);
//		String report = "";
//		for (GameInfoReport greport : reports){
//			report =  report + greport.content;
//		}
//		render("/datamanage/ajax/gameExperienceEdit.html",gameInfo,report);
//    }
    
    
    public static void saveGameExperience(long gameId,String content){    	
		GameInfo gameInfo = GameInfo.findById(gameId);
//		GameInfoReport.delReports(gameId);
//		List<GameInfoReport> newreports = HupuImport.cutContent(report);
//		for (GameInfoReport gamereport : newreports){
//			gamereport.save();			
//		}
		List<GameInfoReport>  reports = GameInfoReport.getReports(gameId, 3);
		
		if (reports == null || reports.size() == 0){
			GameInfoReport report =  new GameInfoReport();
			report.gameId = gameInfo.id;
			report.content = content;
			report.piece = 0;
			report.rtype = 3;
			report.save();
		}else{
			GameInfoReport report = reports.get(0);
			report.content = content;
			report.save();
		}
		renderJSON("{\"error\":\"1\"}");
    }    
    
    
    public static void execGameStatistic(String date){
    	GameInfoStatic.callPrcSet(date);    	
    	renderJSON("{\"error\":\"1\",\"msg\":\"更新成功\"}");
    }
    
    public static void execTeamSeason(String beginDate,String endDate){
    	Date regularEndDate;
		try {
			
			regularEndDate = DateUtil.parseDate(Play.configuration.getProperty(Application.season + "_regular_season_end"));
			Date begin = DateUtil.parseDate(beginDate);
	    	Date end = DateUtil.parseDate(endDate);
	    	if ((begin.getTime() < regularEndDate.getTime()  &&  end.getTime() > regularEndDate.getTime() )){
	    		renderJSON("{\"error\":\"0\",\"msg\":\"跨越常规和季后赛了\"}");
	    	}
	    	
			Integer gameType = 0;
			String statisticBeginDate = Application.seasonStartDate;
			if (begin.getTime() > regularEndDate.getTime() ){
				gameType = 1;
				statisticBeginDate = Play.configuration.getProperty(Application.season + "_regular_season_end");
			}
	    	
	    	TeamStSeasonBase.callPrcTss(statisticBeginDate, beginDate, endDate, Application.season,gameType);
					
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    	
    	renderJSON("{\"error\":\"1\",\"msg\":\"更新成功\"}");
    }
    public static void execTeamSeasonScore(String beginDate,String endDate){
    	Date regularEndDate;
		try {
			regularEndDate = DateUtil.parseDate(Play.configuration.getProperty(Application.season + "_regular_season_end"));
			Date begin = DateUtil.parseDate(beginDate);
	    	Date end = DateUtil.parseDate(endDate);
	    	if ((begin.getTime() < regularEndDate.getTime()  &&  end.getTime() > regularEndDate.getTime() )){
	    		renderJSON("{\"error\":\"0\",\"msg\":\"跨越常规和季后赛了\"}");
	    	}
	    	
//			Integer gameType = 0;
			String statisticBeginDate = Application.seasonStartDate;
			if (begin.getTime() > regularEndDate.getTime() ){
//				gameType = 1;
				statisticBeginDate = Play.configuration.getProperty(Application.season + "_regular_season_end");
			}
	    	
	    	
	    	TeamStSeasonScore.callPrcTSSS(statisticBeginDate, beginDate, endDate, Application.season);    	
	    	renderJSON("{\"error\":\"1\",\"msg\":\"更新成功\"}");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			renderJSON("{\"error\":\"0\",\"msg\":\"出错\"}");
			e.printStackTrace();
		}
    	
    }
    
    public static void execPlayerTeamSS(String beginDate,String endDate){
    	PlayerTeamSeasonScore.callPrcPTSS(Application.seasonStartDate, beginDate, endDate, Application.season);    	
    	renderJSON("{\"error\":\"1\",\"msg\":\"更新成功\"}");
    }
    
    public static void execPlayerSS(String beginDate,String endDate){
    	PlayerSeasonScore.callPrcPSS(Application.seasonStartDate, beginDate, endDate, Application.season);    	
    	renderJSON("{\"error\":\"1\",\"msg\":\"更新成功\"}");
    }
    
    
    
    
    
   
	
}
