package controllers.basedata;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.Application;

import models.game.GameInfo;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinPoint;
import models.team.TeamInfo;
import models.team.TeamMsg;
import models.team.TeamStSeasonBase;
import play.mvc.Controller;
import util.DateUtil;

public class BaseDataTeam extends Controller{
	
	public static void index(){
		List<TeamInfo> teams = TeamInfo.getTeams();	
		List<TeamInfo> atlantics = teams.subList(0,5);
		List<TeamInfo> eastsouths = teams.subList(5,10);
		List<TeamInfo> middles = teams.subList(10,15);
		List<TeamInfo> parcifics = teams.subList(15,20);
		List<TeamInfo> westnorths = teams.subList(20,25);
		List<TeamInfo> westsouths = teams.subList(25,30);
		
		render("/basedata/team/teams.html",atlantics,eastsouths,middles
				,parcifics,westsouths,westnorths);
	}
	
	public static void games(long teamId,String year){
		Integer season = Integer.valueOf(Application.season);
		if (year != null)
			season = Integer.valueOf(year);
		TeamInfo team = TeamInfo.findById(teamId);
		List<GameInfo> gameInfos = GameInfo.getGameInfos(teamId,season);
		
		List<LotteryHilo> hilos = LotteryHilo.getHilos(teamId, DateUtil.addDate(new Date(), 1), 1, 83);
		Map<Long,LotteryHilo> mapHilo = new HashMap<Long, LotteryHilo>();
		for(LotteryHilo hilo : hilos){
			mapHilo.put(hilo.game_id, hilo);
		}
		
		List<LotteryWinPoint> winPoints = LotteryWinPoint.getLotteries(team.team_name, DateUtil.addDate(new Date(), 1), 83);
		Map<Long,LotteryWinPoint> mapWinPoint  = new HashMap<Long,LotteryWinPoint>();
		for(LotteryWinPoint winPoint :winPoints){
			mapWinPoint.put(winPoint.game_id, winPoint);
		}
		
		List<LotteryHandicap> handiCaps = LotteryHandicap.getLotteries(team.id,season,DateUtil.addDate( new Date(),1));
		Map<Long,LotteryHandicap> mapHandicap = new HashMap<Long, LotteryHandicap>();
		for (LotteryHandicap handicap : handiCaps){
			mapHandicap.put(handicap.game_id, handicap);
		}
		
		
		render("/basedata/team/teamGames.html",gameInfos,team,season,mapHilo,mapWinPoint,mapHandicap);
	}
	

	/**
	 * /basedata/team/ajax/tmsg/
	 * @param teamId
	 * @param gameId
	 * @param page
	 * @param pageSize
	 */
	public static void ajaxTeamMsgs(long teamId,Long gameId,Integer page,Integer pageSize){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<TeamMsg> msgs = TeamMsg.getMsgs(teamId, gameInfo.play_date, page, pageSize);
	}
	
	


}
