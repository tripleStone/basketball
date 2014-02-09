package controllers.autojob;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import controllers.dataimport.NBALotteryImport;

import models.game.GameInfo;
import models.lottery.LotteryWinPoint;
import models.team.TeamInfo;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import util.htmlparser.UrlUtil;

@Every("10min")
public class JobLottery  extends Job{

	private static final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final  SimpleDateFormat sdf24 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final DecimalFormat df  = new DecimalFormat("#00.00");
	private static final  NumberFormat numberFormat = NumberFormat.getNumberInstance();
	private static Calendar   calendar   =   new   GregorianCalendar(); 

	
	public void doJob() {
		
		Logger.info("auto lottery start !!!!");
		
		
		try {
//			String url = "http://info.sporttery.com/basketball/wnm_single.php";
//			NBALotteryImport.getWNMLottery(url);
//			
//			url = "http://info.sporttery.com/basketball/hdc_list.php";
//			NBALotteryImport.handiCapLottery(url);
//			
//			url = "http://info.sporttery.com/basketball/hilo_list.php";
//			NBALotteryImport.hiloLottery(url);
			
//			url = "http://info.sporttery.com/basketball/mnl_list.php";
//			NBALotteryImport.getAllLotteries(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Logger.info("auto lottery end !!!!");
		
				
	}
	

}
