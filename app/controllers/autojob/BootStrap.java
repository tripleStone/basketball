package controllers.autojob;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hibernate.mapping.Array;

import controllers.Application;
import controllers.dataimport.EspnImport;

import models.game.GameInfo;
import models.player.PlayerInfo;
import models.team.TeamInfo;
import models.team.TeamMsg;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.JobsPlugin;
import play.jobs.OnApplicationStart;
import util.DateUtil;

@OnApplicationStart
public class BootStrap extends Job {
	public void doJob()  {
		
		Logger.info("Begin to initial, read the configs from applicatioin.conf");
		Logger.info(DateUtil.getDateStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
		Application.season = Integer.valueOf(Play.configuration.getProperty("game_season"));		
		Application.seasonStartDate = Play.configuration.getProperty("start_time");		
		Application.tradeTime = Play.configuration.getProperty("trade_time");
		Application.espnNba = Play.configuration.getProperty("espn_nba");
		Application.hupuBoxScore = Play.configuration.getProperty("hupu_boxscore");
		Application.hupuMsg = Play.configuration.getProperty("hupu_msg");
		Application.espnSchedule = Play.configuration.getProperty("espnSchedule");
		Application.poolResult = Play.configuration.getProperty("pool_result");
		Application.matchResult = Play.configuration.getProperty("match_result");
		Application.espnPlayByPlay = Play.configuration.getProperty("espnPlayByPlay");
		
		Map<String,Integer> monthMap = new HashMap<String, Integer>();
		monthMap.put("October", 10);
		monthMap.put("Oct", 10);
		monthMap.put("November", 11);
		monthMap.put("Nov", 11);
		monthMap.put("December", 12);
		monthMap.put("Dec", 12);
		monthMap.put("Jan",1);
		monthMap.put("January",1);
		monthMap.put("Feb", 2);
		monthMap.put("February", 2);
		monthMap.put("Mar", 3);
		monthMap.put("March", 3);
		monthMap.put("Apr", 4);
		monthMap.put("April", 4);
		monthMap.put("May", 5);
		monthMap.put("June", 6);	
		
		Application.monthMap = monthMap;
		
		List<TeamInfo> teams = TeamInfo.getTeams();
		Map<String,TeamInfo> teamMap = new HashMap<String, TeamInfo>();
		
		for (TeamInfo teamInfo : teams){
			teamMap.put(teamInfo.city_name, teamInfo);
			teamMap.put(String.valueOf(teamInfo.id), teamInfo);
			teamMap.put(teamInfo.full_name, teamInfo);
			teamMap.put(teamInfo.eng_name, teamInfo);
			TeamMsg msg = TeamMsg.getNewestMsg(teamInfo.id);
			Application.lastestNewsMap.put(teamInfo.id,msg);			
		}
		
		Application.teamMap = teamMap;			
		
		List<PlayerInfo> players;
//		players = PlayerInfo.getAllPlayers();
//		for(PlayerInfo player : players){
//			Application.AllPlayers.put(player.team_id + "_" + player.name, player);
//		}

//		EspnImport.importTodayGames(Application.espnNba);		
		List<GameInfo> todyGames = GameInfo.getGameInfos(DateUtil.getDateStr(new Date()));
		List<Long> teamIds = new ArrayList<Long>();
		for(GameInfo game : todyGames){
			teamIds.add(game.home_id);
			teamIds.add(game.guest_id);
			Application.todayGames.put(game.espnId, game);
		}
		
		
		
		
	}
	

}
