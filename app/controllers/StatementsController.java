package controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;

import play.mvc.Controller;

import models.game.GameInfo;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.statements.StatementsConfig;
import models.team.TeamInfo;
import models.team.TeamStSeasonBase;
import models.team.TeamStSeasonScore;
import util.DateUtil;

public class StatementsController  extends Controller {
	
	
	@Before
	public static void before(){
		
	}
	/**
	 * 横向对比
	 * @param gameId
	 * GET /statements/rank/{<\d+>gameId}
	 */
	public static void rankCompare(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);		
		render("/statements/rank.html",gameInfo);
	}
	
	public static void rankReport(Long teamId,Long gameId,String targetTeams){
		TeamInfo team = TeamInfo.findById(teamId);
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<Long> ids = new ArrayList<Long>();
		Map<Long,LotteryHilo> hiloMap = new HashMap<Long, LotteryHilo>();
		Map<Long,LotteryHandicap> handiCapMap = new HashMap<Long, LotteryHandicap>();
		
		String[] targetTeamArray = targetTeams.split(",");
		for (String id : targetTeamArray){
			ids.add(Long.valueOf(id));
		}
		
		List<GameInfo> gameInfos = GameInfo.getGameInfos(ids,teamId, Integer.valueOf(gameInfo.season));
		
		ids = new ArrayList<Long>();
		for (GameInfo gi : gameInfos){
			ids.add(gi.id);
		}
		
		
		List<LotteryHilo> hilos = LotteryHilo.getLotteries(ids);
		for(LotteryHilo hilo : hilos){
			hiloMap.put(hilo.game_id, hilo);
		}
		
		List<LotteryHandicap> caps = LotteryHandicap.getLotteries(ids);
		for(LotteryHandicap cap : caps){
			handiCapMap.put(cap.game_id, cap);
		}
		
		render("/statements/rankReport.html",gameInfos,hiloMap,handiCapMap);
		
	}	
	
	/**
	 * POST /statements/rank/{<\d+>gameId}/rank
	 * @param gameId
	 * @param ranktype
	 * @param orderBy
	 */
	public static void rankAjax(Long gameId,String ranktype,String colus,String colusName,String orderBy){
		Date queryDate = new Date();
		Integer season = Application.season;
		List<Object[]> eastRank = new ArrayList<Object[]>();
		List<Object[]> westRank = new ArrayList<Object[]>();
		Integer columnCount = 4;
		List<String> columnNameList = new ArrayList<String>();
		
		if (gameId != null){
			GameInfo gameInfo = GameInfo.findById(gameId);
			queryDate = gameInfo.play_date;
			season = gameInfo.season;
		}
		StatementsConfig config = StatementsConfig.getConfig(ranktype);
		
		if (config.table_name.equals("TEAM_ST_SEASON_BASE")){		
			columnCount = config.columns_count;
			eastRank = TeamStSeasonBase.getRank("east",queryDate,config.columns,config.order_by);
			westRank = TeamStSeasonBase.getRank("west",queryDate,config.columns,config.order_by);			
		}else if (config.table_name.equals("LOTTERY_HILO")){
			eastRank = LotteryHilo.getHiloRank("east", season, queryDate, "all",config.columns,config.order_by);
			westRank = LotteryHilo.getHiloRank("west", season, queryDate, "all",config.columns,config.order_by);
		}else if (config.table_name.equalsIgnoreCase("TEAM_ST_SEASON_Score")){
			columnNameList = Arrays.asList(colusName.split(","));
			columnCount = columnNameList.size() + 2;
			eastRank = TeamStSeasonScore.getRank("east", queryDate,colus,colus.split(",")[0]);
			westRank = TeamStSeasonScore.getRank("west", queryDate,colus,colus.split(",")[0]);
		}

		render("/statements/ajax/rankAjax.html",eastRank,westRank,ranktype,columnCount,colus,colusName,columnNameList);
	}
	
	/**
	 * POST /statements/rankRe/{<\d+>gameId}/{ranktype}/${area}/{<\d+>orderBy}
	 * @param gameId
	 * @param ranktype
	 * @param orderBy
	 * @param area
	 */
	public static void rankReAjax(Long gameId,String ranktype,
			Integer orderBy,String area,String colus,String colusName){
		Date queryDate = new Date();
		Integer season = Application.season;
		List<Object[]> _ranks = new ArrayList<Object[]>();
		List<String> _columnNameList = new ArrayList<String>();
		
		if (gameId != null){
			GameInfo gameInfo = GameInfo.findById(gameId);
			queryDate = gameInfo.play_date;
			season = gameInfo.season;
		}
		
		StatementsConfig config = StatementsConfig.getConfig(ranktype);
		String order = config.order_by;
		Integer _columnCount = 4;
			
		if (orderBy != 0 && config.columns != null){
			String[] orderArr = config.columns.split(",");
			order = orderArr[orderBy -1];
		}
		
		if (config.table_name.equals("TEAM_ST_SEASON_BASE")){		
			_columnCount = config.columns_count;
			_ranks = TeamStSeasonBase.getRank(area,queryDate,config.columns,order);
		}else if (config.table_name.equals("LOTTERY_HILO")){
			_columnCount = config.columns_count;
			_ranks = LotteryHilo.getHiloRank(area, season, queryDate, "all",config.columns,order);
		}else if (config.table_name.equalsIgnoreCase("TEAM_ST_SEASON_Score")){
			_columnNameList = Arrays.asList(colusName.split(","));
			_columnCount = _columnNameList.size() + 2;
			_ranks = TeamStSeasonScore.getRank(area, queryDate,colus,colus.split(",")[orderBy]);
		}
		
		String _ranktype = ranktype; 
		render("/tags/statements/tRank.html",_ranks,_ranktype,_columnCount,_columnNameList);
		
	}
	


	
}
