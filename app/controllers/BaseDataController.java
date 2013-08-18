package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.game.GameChart;
import models.game.GameInfo;
import models.game.GameInfoReport;
import models.game.GameInfoStatic;
import models.game.GamePlayerInfo;
import models.game.GameQuarterScore;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;
import models.team.TeamInfo;
import models.team.TeamStSeasonBase;
import play.mvc.Controller;
import util.DateUtil;

public class BaseDataController extends Controller{
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Calendar   calendar   =   new   GregorianCalendar(); 
		
	public static void schedule(String playDate){
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
			
			List<LotteryHilo> hilos = LotteryHilo.gethilos(sdf.parse(playDate));
			Map<Long,LotteryHilo> hiloMap = new HashMap<Long, LotteryHilo>();
			for(LotteryHilo hilo : hilos){
				hiloMap.put(hilo.game_id, hilo);
			}
			
			
			List<LotteryHandicap> caps = LotteryHandicap.getLotteries(sdf.parse(playDate));
			Map<Long,LotteryHandicap> handiCapMap = new HashMap<Long, LotteryHandicap>();
			for(LotteryHandicap cap : caps){
				handiCapMap.put(cap.game_id, cap);
			}
			
			render("/basedata/schedule.html",gameInfos,playDate,preDate,nextDate,hiloMap,handiCapMap);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}

}
