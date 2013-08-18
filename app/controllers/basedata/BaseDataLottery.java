package controllers.basedata;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import models.game.GameInfo;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;
import models.team.TeamStSeasonBase;
import play.mvc.Controller;
import util.DateUtil;

public class BaseDataLottery  extends Controller{

	/**
	 * 获取盘口历史数据
	 * @param gameId
	 * @param ltype
	 */
	public static void  lotteryHistory(Long  gameId,String ltype){
		List<LotteryWinPoint> winPoints = LotteryWinPoint.getLotteryHis(gameId); 
		List<LotteryHandicap> handiCaps = LotteryHandicap.getLotteryHis(gameId);
		List<LotteryHilo>  hilos = LotteryHilo.getLotteries(gameId);
		List<LotteryWinLoss>  winLoses = LotteryWinLoss.getLotteries(gameId);
		render("/basedata/lottery/lotterHisAjax.html",winPoints,handiCaps,hilos,winLoses,ltype);
	}
	
	
	public static void lotteries(long gameId,Integer games){
		GameInfo gameInfo = GameInfo.findById(gameId);
		if (games == null){
			games= 10;
		}
		
		List<LotteryWinPoint> homeLotteries = LotteryWinPoint.getLotteries(gameInfo.home_name,gameInfo.play_date,games);
		List<LotteryWinPoint> guestLotteries = LotteryWinPoint.getLotteries(gameInfo.guest_name,gameInfo.play_date,games);
		
		List<LotteryHilo> homeHilos = LotteryHilo.getHomeHilos(gameInfo.home_id, gameInfo.play_date,1,games);
		List<LotteryHilo> awayHilos = LotteryHilo.getHomeHilos(gameInfo.guest_id, gameInfo.play_date,1,games);
		
		List<LotteryHandicap> homeHdcps = LotteryHandicap.getHmHndp(gameInfo.home_id, gameInfo.play_date,1,games);
		List<LotteryHandicap> awayHdcps = LotteryHandicap.getHmHndp(gameInfo.guest_id, gameInfo.play_date,1,games);
		
		List<LotteryWinLoss> homeWLs = LotteryWinLoss.getWLs(gameInfo.home_id, gameInfo.play_date,"home",1,games);
		List<LotteryWinLoss> awayWLs = LotteryWinLoss.getWLs(gameInfo.home_id, gameInfo.play_date,"away",1,games);
		
//		List<LotteryWinPoint> lotteries = LotteryWinPoint.getLotteries(gameInfo.home_name,gameInfo.guest_name,gameInfo.play_date);
		render("/analysis/analysis_lottery.html",gameId,gameInfo,homeLotteries,guestLotteries,homeHilos,awayHilos,
				homeHdcps,awayHdcps,homeWLs,awayWLs);		
	}
	
	

	
}
