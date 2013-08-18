package controllers.analysis;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import controllers.Application;

import models.game.GameInfo;
import models.game.GamePlayerInfo;
import models.lottery.LotteryHilo;
import models.team.TeamInfo;
import models.team.TeamStSeasonBase;
import play.Logger;
import play.mvc.Controller;
import util.DateUtil;

public class AnalysisHilo extends Controller{
	

	
	/**
	 * 大小分赔率页面
	 * /analysis/hilo/lottery/{<\d+>gameId}
	 * @param gameId
	 * @param pageSize
	 */
	public static void lottery(Long gameId,Integer pageSize){
		LotteryHilo lottery = LotteryHilo.getLottery(gameId);
		GameInfo gameInfo = GameInfo.findById(gameId);
		
		LotteryHilo hilo = LotteryHilo.getLottery(gameId);
		List<LotteryHilo> hilos = LotteryHilo.getMatcherHilos(gameInfo.home_id, gameInfo.guest_id, gameInfo.play_date);	
		
		int h_day = gameInfo.h_btb;
		int g_day = gameInfo.g_btb;
		
		List<LotteryHilo> hDayLty= LotteryHilo.getSameHilos(gameInfo.home_id,gameInfo.play_date, h_day, gameInfo.season, "home");
		List<LotteryHilo> gDayLty = LotteryHilo.getSameHilos(gameInfo.guest_id,gameInfo.play_date, g_day, gameInfo.season, "away");
		
		
		render("/analysis/hilo/anlyHiloLotteries.html",hilo,gameInfo,hilos,hDayLty,gDayLty);
	}
	

	
	/**
	 * 两队最近赔率
	 * url /analysis/hilo/ajax/recent	
	 * @param gameId
	 */
	public static void recent(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<LotteryHilo> homeHilos = LotteryHilo.getHilos(gameInfo.home_id,gameInfo.play_date,1,10);
		List<LotteryHilo> awayHilos = LotteryHilo.getHilos(gameInfo.guest_id,gameInfo.play_date,1,10);
		render("/analysis/hilo/ajax/anlyHiloRecnet.html",gameInfo,homeHilos,awayHilos);		
	}
	
	
	
	/**
	 * 赔率变更历史记录
	 * url /analysis/hilo/ajax/recent
	 * @param gameId
	 */
	public static void history(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<LotteryHilo> hilos = LotteryHilo.getLotteries(gameId);
		String type = "analysis";
		render("/analysis/hilo/ajax/anlyHiloHistory.html",hilos,gameInfo,type);	
	}
	
	
	/**
	 * url /analysis/hilo/similarHiloIndex	
	 * @param usaPlayDate
	 * @param playTime
	 * @param hilo
	 * @param homeId
	 * @param awayId
	 * @param season
	 * @param weekDay
	 */
	public static void similarHiloIndex(String usaPlayDate,String playTime,Double hilo,
			Long homeId,Long awayId,Integer season,Integer weekDay){
		render("/analysis/hilo/anlyHiloSimilarIndex.html", usaPlayDate, playTime, hilo,
				 homeId, awayId,season, weekDay);
	}
	
	/**
	 * /analysis/hilo/ajax/similarHilos
	 * @param usaPlayDate
	 * @param playTime
	 * @param hilo
	 * @param homeId
	 * @param awayId
	 * @param season
	 * @param weekDay
	 */
	public static void similarHilos(String usaPlayDate,String playTime,Double hilo,
			Long homeId,Long awayId,Integer season,Integer weekDay){
		try {
			StringBuffer sqlBuffer = new StringBuffer(" from LotteryHilo hilo,GameInfo gi ");
	//		StringBuffer tablesBuffer = new StringBuffer("");
			StringBuffer whereBuffer = new StringBuffer(" where gi.season = ? and hilo.game_id = gi.id and hilo.del_marker = 0 ");
			List<Object> params = new ArrayList<Object>();
			if (season == null)
				season = 2013;
			
			params.add(season);
			if (homeId != null){
				whereBuffer = whereBuffer.append(" and (gi.home_id = ? or gi.guest_id = ? ) ");
				params.add(homeId);
				params.add(homeId);
			}
			
			if (awayId != null){
				whereBuffer = whereBuffer.append(" and (gi.home_id = ? or gi.guest_id = ? ) ");
				params.add(awayId);
				params.add(awayId);
			}	
			
			if (hilo != null){
				whereBuffer = whereBuffer.append(" and hilo.hilo = ?");
				params.add(hilo);
			}
			
			if (usaPlayDate != null){
				whereBuffer = whereBuffer.append(" and gi.usa_play_date < ?");
				params.add(DateUtil.parseDate(usaPlayDate, "yyyy-MM-dd"));
			}else{
				whereBuffer = whereBuffer.append(" and gi.has_detail = 1");
			}
			
			if (playTime != null){
				whereBuffer =  whereBuffer.append(" and play_time = ?");
				params.add(playTime);
			}
			
			if (weekDay != null){
				whereBuffer =  whereBuffer.append(" and gi.week_day = ?");
				params.add(weekDay);
			}
			
			Long lessThanCount = LotteryHilo.count(sqlBuffer.toString() + whereBuffer.toString() + " and hilo.hit = 'less' ", (Object[])params.toArray());
			Long greaterThanCount = LotteryHilo.count(sqlBuffer.toString() + whereBuffer.toString() + " and hilo.hit = 'greater' ", (Object[])params.toArray());
			Long count = LotteryHilo.count(sqlBuffer.toString() + whereBuffer.toString(), (Object[])params.toArray());
			
			
			sqlBuffer.append(whereBuffer.append(" order by gi.play_date desc "));			
			List<LotteryHilo> hilos = LotteryHilo.find("select hilo " + sqlBuffer.toString(), (Object[])params.toArray()).fetch();
			
			render("/analysis/hilo/ajax/anlySimiliarHilos.html",hilos,usaPlayDate,playTime,hilo,
					homeId,awayId,season,weekDay,lessThanCount,greaterThanCount,count);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			render("/analysis/hilo/ajax/anlySimiliarHilos.html");
		}
	}
	

	
}
