package controllers.dataimport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import controllers.Application;

import models.game.GameInfo;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;
import models.team.TeamInfo;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Util;
import util.DateUtil;
import util.htmlparser.NbaLotteryHtmlParser;
import util.htmlparser.UrlUtil;

public class NBALotteryImport extends Controller{
	private static final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Calendar   calendar   =   new   GregorianCalendar(); 
	
	
	public static void importAllLottery(String dataUrl){
		String result = getAllLotteries(dataUrl);
		if (result.equals("1")){
			renderJSON("{\"error\":\"1\"}");
		}else{
			renderJSON("{\"error\":\"2\",\"msg\":\"错误\"}");
		}
	}

	

	/**
	 * url /lotteryData/lotteryImport 
	 * @param playDate
	 */
    public static void lotteryImport(String playDate){
    	List<LotteryWinPoint> lotteries =  null;
    	if (playDate == null)
    		playDate = sdf.format(new Date());
    	
    	List<LotteryHilo> hilos = null;
    	List<LotteryHandicap> handicaps = null; 
    	List<LotteryWinLoss> winLoses = null; 
    	String preDate = "";
    	String nextDate="";
    	try {
    		calendar.setTime(sdf.parse(playDate)); 
			calendar.add(calendar.DATE, -1);
			preDate = sdf.format( calendar.getTime());
			calendar.add(calendar.DATE, +2);
			nextDate = sdf.format( calendar.getTime());
			lotteries = LotteryWinPoint.getLotteries(sdf.parse(playDate));
			hilos = LotteryHilo.gethilos(sdf.parse(playDate));
			handicaps = LotteryHandicap.getLotteries(sdf.parse(playDate));			
			winLoses = LotteryWinLoss.getLotteries(sdf.parse(playDate));			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	render("/gameDateImport/lotteryImport.html",lotteries,winLoses,playDate,hilos,handicaps,preDate,nextDate);
    } 
    
    
    public static void lotteryImportByUrl(String dataUrl){
    	try {
			getWNMLottery(dataUrl);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	renderText("wnm import complete");
    }


	public static String getWNMLottery(String dataUrl) throws Exception {
		List<LotteryWinPoint> lotteries = UrlUtil.getLotteries(dataUrl);
		for(LotteryWinPoint lottery:lotteries){
			Logger.info(lottery.host_team  + " " +lottery.guest_team );
			Logger.info(sdf.format( lottery.game_date));
			TeamInfo home = TeamInfo.getTeamByFullName(lottery.host_team);
			if (home == null){
				Logger.info(" team name is  %s ", lottery.host_team);
				System.out.println(lottery.host_team);
			}
			TeamInfo guest = TeamInfo.getTeamByFullName(lottery.guest_team);
			if (guest == null){
				Logger.info(" team name is  %s ", lottery.guest_team);
				System.out.println(lottery.guest_team);
			}
			GameInfo game = GameInfo.getGameInfo(home.id, guest.id, lottery.game_date);
			if (home == null || guest == null){
				Logger.info(" %s vs %s", lottery.host_team,lottery.guest_team);
			}
			if (game == null){
				game = new GameInfo(0,home.id,guest.id,home.team_name,guest.team_name,lottery.game_date);
				Date regularEndDate = DateUtil.parseDate(Play.configuration.getProperty(game.season + "_regular_season_end"));
				
				if (regularEndDate.getTime() < game.play_date.getTime()){
					game.game_type = 1;
					
				}
				Date home_date = GameInfo.getGameInfos(game.home_id,game.play_date,1).get(0).play_date;
				Date away_date = GameInfo.getGameInfos(game.guest_id,game.play_date,1).get(0).play_date;
				
				game.h_btb = Integer.valueOf(String.valueOf(DateUtil.getDayBetween(home_date, game.play_date)));
				game.g_btb = Integer.valueOf(String.valueOf(DateUtil.getDayBetween(away_date, game.play_date)));
				
				game.save();
			}
			
			LotteryWinPoint dataLottery = LotteryWinPoint.getLottery(game.id);
			if (dataLottery != null){
				 if (checkWinPoint(dataLottery,lottery)){
					 dataLottery.del_marker = 1;
					 dataLottery.save();
				 }else{
					 continue;
				 }
			}
			lottery.weekDay = game.week_day;
			lottery.host_team= home.team_name;
			lottery.guest_team = guest.team_name;
			lottery.game_id = game.id;
			lottery.season = game.season;
			lottery.createDate = new Date();
			lottery.homeId = home.id;
			lottery.awayId = guest.id;
			lottery.save();				
		}
		return "";
	}
    
   public static boolean checkWinPoint(LotteryWinPoint dataLottery,LotteryWinPoint lottery){
	   boolean result = false;
	   if (dataLottery.guest_11to15 != lottery.guest_11to15 || dataLottery.guest_16to20 != lottery.guest_16to20
			   || dataLottery.guest_1to5 != lottery.guest_1to5 || dataLottery.guest_21to25 != lottery.guest_21to25
			   || dataLottery.guest_26 !=  lottery.guest_26 || dataLottery.guest_6to10 != lottery.guest_6to10){
		   result =  true;
	   }else if (dataLottery.host_11to15!= lottery.host_11to15 || dataLottery.host_16to20 != lottery.host_16to20
			   || dataLottery.host_1to5 != lottery.host_1to5 || dataLottery.host_21to25 != lottery.host_21to25
			   || dataLottery.host_26 !=  lottery.host_26 || dataLottery.host_6to10 != lottery.host_6to10){
		   result =  true;
	   }
	   
	   return result;
   }
    
   /**
    * dataImport
    * url /lotteryData/ajax/handiCapImportByUrl
    * @param dataUrl
    */
    public static void handiCapImportByUrl(String dataUrl){
    	try {
			handiCapLottery(dataUrl);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	renderText("handicap import complete");
    }


	public static String handiCapLottery(String dataUrl) throws Exception {
		List<LotteryHandicap> lotteries = UrlUtil.getLotteryHandicap(dataUrl);
		for(LotteryHandicap lottery:lotteries){
			TeamInfo home = TeamInfo.getTeamByFullName(lottery.host_name);
			TeamInfo guest = TeamInfo.getTeamByFullName(lottery.away_name);
			GameInfo game = GameInfo.getGameInfo(home.id, guest.id, lottery.game_date);
			if (home == null || guest == null){
				Logger.info(" %s vs %s", lottery.host_name,lottery.away_name);
			}
			if (game == null){
				game = new GameInfo(0,home.id,guest.id,home.team_name,guest.team_name,lottery.game_date);
			}
			
			
			LotteryHandicap dataLottery = LotteryHandicap.getLottery(game.id);
			if (dataLottery != null){
				if (dataLottery.handicap ==  lottery.handicap && dataLottery.win == lottery.win
						&& dataLottery.lose == lottery.lose){
					continue;
				}else{
					dataLottery.del_marker = 1;
					dataLottery.save();
				}
			}				

			lottery.weekDay = game.week_day;
			lottery.host_name= home.team_name;
			lottery.away_name = guest.team_name;
			lottery.host_id = home.id;
			lottery.away_id = guest.id;
			lottery.game_id = game.id;
			lottery.del_marker = 0;
			lottery.createDate = new Date();
			lottery.season = game.season;
			lottery.save();				
		}
		return "";
	}    
    
    public static void HiloImportByUrl(String dataUrl){
    	try {
			hiloLottery(dataUrl);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	renderText("hilo import completed");
    }


	public static String hiloLottery(String dataUrl) throws Exception {
		List<LotteryHilo> lotteries = UrlUtil.getLotteryHilo(dataUrl);
		for(LotteryHilo hilo:lotteries){
			TeamInfo home = TeamInfo.getTeamByFullName(hilo.host_name);
			TeamInfo guest = TeamInfo.getTeamByFullName(hilo.away_name);
			GameInfo game = GameInfo.getGameInfo(home.id, guest.id, hilo.game_date);
			if (home == null || guest == null){
				Logger.info(" %s vs %s", hilo.host_name,hilo.away_name);
			}
			if (game == null){
				game = new GameInfo(0,home.id,guest.id,home.team_name,guest.team_name,hilo.game_date);
			}
			
			LotteryHilo dataLottery = LotteryHilo.getLottery(game.id);
			if (dataLottery != null){
				if (dataLottery.hilo ==  hilo.hilo && dataLottery.greater_than ==  hilo.greater_than 
						&& dataLottery.less_than ==  hilo.less_than ){
					continue;
				}else{
					dataLottery.del_marker = 1;
					dataLottery.save();
				}
			}			
			
			hilo.weekDay = game.week_day;
			hilo.host_name= home.team_name;
			hilo.away_name = guest.team_name;
			hilo.host_id = home.id;
			hilo.away_id = guest.id;
			hilo.game_id = game.id;
			hilo.createDate = new Date();
			hilo.season = game.season;
			hilo.save();
			
		}
		return "";
	} 
    
    public static void winLoseImportByUrl(String dataUrl){
    	try {
			winLoseLottery(dataUrl);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	renderText("winlose import completed");
    }


	public static String winLoseLottery(String dataUrl) throws Exception {
		List<LotteryWinLoss> lotteries = UrlUtil.getLotteryWin(dataUrl);
		for(LotteryWinLoss winLose:lotteries){
//				System.out.println(winLose.host_name  + " " +winLose.away_name );
//				System.out.println(sdf.format( winLose.game_date));
			TeamInfo home = TeamInfo.getTeamByFullName(winLose.host_name);
			TeamInfo guest = TeamInfo.getTeamByFullName(winLose.away_name);
			GameInfo game = GameInfo.getGameInfo(home.id, guest.id, winLose.game_date);
			if (home == null || guest == null){
				Logger.info(" %s vs %s", winLose.host_name,winLose.away_name);
			}
			if (game == null){
				game = new GameInfo(0,home.id,guest.id,home.team_name,guest.team_name,winLose.game_date);
			}
			
			LotteryWinLoss dataLottery = LotteryWinLoss.getLottery(game.id);
			if (dataLottery != null){
				if (dataLottery.home_win ==  winLose.home_win 
						&& dataLottery.guest_win ==  winLose.guest_win ){
					continue;
				}else{
					dataLottery.del_marker = 1;
					dataLottery.save();
				}
			}			
			
			winLose.weekDay = DateUtil.getWeekDay(DateUtil.addDate(winLose.game_date,-1));
			winLose.host_name= home.team_name;
			winLose.away_name = guest.team_name;
			winLose.host_id = home.id;
			winLose.away_id = guest.id;
			winLose.game_id = game.id;
			winLose.createDate = new Date();
			winLose.season = game.season;
			winLose.save();				
		}
		return "";
	}
	
	
	/**
	 * 赔率历史记录
	 * url /lottery/ajax/importMatchResult
	 * @param beginDateStr
	 * @param endDateStr
	 */
	
	public static String getResultHistory(String beginDateStr,String endDateStr){
		try {
			Date beginDate = DateUtil.parseDate(beginDateStr);
			Date endDate = DateUtil.parseDate(endDateStr);
			
			LotteryHandicap.del(beginDate, endDate);
			LotteryHilo.delHilos(beginDate, endDate);
			LotteryWinLoss.del(beginDate, endDate);
			
			
			List<GameInfo> gameInfos = GameInfo.getGameInfos(beginDate,DateUtil.addDate(endDate, 1));
			
			Map<String,String> para = new HashMap<String,String>();
			para.put("start_date",beginDateStr);
			para.put("end_date",endDateStr);
			para.put("lid","1");
			Document doc = UrlUtil.getURLContentPost(Application.matchResult, para);			
			Map<String,String> result = NbaLotteryHtmlParser.getMatchResults(doc);
			
			for (GameInfo gi :gameInfos){
//				if (gi.has_sync == 1){
//					continue;
//				}
				LotteryWinPoint.del(gi.id);			
				
				TeamInfo home = Application.teamMap.get(String.valueOf(gi.home_id));
				TeamInfo away = Application.teamMap.get(String.valueOf(gi.guest_id));
				String key = DateUtil.getDateStr(gi.play_date)+"_" + away.full_name + "-" + home.full_name;
				String url = result.get(key);
				
				if (url == null){
					Logger.info("lottery key is %s is not in :",key);
					continue;
				}
				
				doc = UrlUtil.getURLContent(url);
				
				LotteryHandicap handicapData = LotteryHandicap.getLottery(gi.id);
				LotteryWinLoss	wlData = LotteryWinLoss.getLottery(gi.id);
				LotteryHilo	hiloData = LotteryHilo.getLottery(gi.id);
				
				List<LotteryHandicap> handicaps = NbaLotteryHtmlParser.getMacthResultHandcaps(doc);
				for(LotteryHandicap hd : handicaps){
					if (handicapData == null || handicapData.createDate.getTime() < hd.createDate.getTime() ){
						hd.away_id = away.id;
						hd.away_name = away.team_name;
						hd.host_id = home.id;
						hd.host_name = home.team_name;
						hd.game_id = gi.id;
						hd.game_date = gi.play_date;
						hd.weekDay = DateUtil.getWeekDay(gi.usa_play_date);
						hd.season = gi.season;
						hd.save();
						
						if ( handicapData != null && handicapData.del_marker == 0){
							handicapData.del_marker =1;
							handicapData.save();
						}
					}
				}
				
				List<LotteryWinLoss> winLosses =  NbaLotteryHtmlParser.getMacthResultWinLos(doc);
				for(LotteryWinLoss wl : winLosses){
					if (wlData == null || wlData.createDate.getTime() < wl.createDate.getTime() ){
						wl.away_id = away.id;
						wl.away_name = away.team_name;
						wl.host_id = home.id;
						wl.host_name = home.team_name;
						wl.game_id = gi.id;
						wl.game_date = gi.play_date;
						wl.weekDay = DateUtil.getWeekDay(gi.usa_play_date);
						wl.season =  gi.season;
						wl.save(); 
						
						if (wlData != null && wlData.del_marker == 0){
							wlData.del_marker = 1;
							wlData.save();
						}
					}
				}
				
				List<LotteryHilo> hilos = NbaLotteryHtmlParser.getMacthResultHilos(doc);
				for(LotteryHilo hilo : hilos){
					if ( hiloData == null  || hiloData.createDate.getTime() < hilo.createDate.getTime() ){
						hilo.away_id = away.id;
						hilo.away_name = away.team_name;
						hilo.host_id = home.id;
						hilo.host_name = home.team_name;
						hilo.game_id = gi.id;
						hilo.game_date = gi.play_date;
						hilo.weekDay = DateUtil.getWeekDay(gi.usa_play_date);
						hilo.season =  gi.season;
						hilo.save(); 	
						
						if ( hiloData != null && hiloData.del_marker == 0){
							hiloData.del_marker = 1;
							hiloData.save();
						}
					}
				}
				
				List<LotteryWinPoint> winPoints = NbaLotteryHtmlParser.getMacthResultWinPoints(doc);
				for(LotteryWinPoint wp : winPoints){
					wp.guest_team = away.team_name;
					wp.host_team = home.team_name;
					wp.game_id = gi.id;
					wp.game_date = gi.play_date;
					wp.weekDay = DateUtil.getWeekDay(gi.usa_play_date);
					wp.season =  gi.season;
					wp.save(); 					
				}
				
				if (gi.has_detail == 1){
					EspnImport.saveLotteryWinPoint(gi);
					EspnImport.saveLotteryHandicap(gi);
					EspnImport.saveLotteryHilo(gi);
					EspnImport.saveLotteryWinLoss(gi);
				}
				
				gi.has_sync = 1;
				gi.save();
			}			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "1";
	}
	
	
    public static void getDate(String gamedate) throws ParseException{
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Date now = format.parse(gamedate);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(now);
    	cal.set(Calendar.DAY_OF_MONTH, -1);
    	String preDay = format.format(cal.getTime());
    	cal.set(Calendar.DAY_OF_MONTH, +2);
    	String nextDay = format.format(cal.getTime());
    	renderJSON("{\"preDay\":\""+ preDay+ "\",{\"nextDay\":\""+nextDay+"\"}");
    	
    }
    
    
    @Util
    public static String getAllLotteries(String url) {
		try {
			List<List<String>> results = NbaLotteryHtmlParser.getLotteryUrl(url);
			for (List<String> result : results){
				TeamInfo home = Application.teamMap.get(result.get(0).split("_")[1]);
				TeamInfo away = Application.teamMap.get(result.get(0).split("_")[0]);
				Date date = DateUtil.parseDate(result.get(1));
				
				GameInfo gi = GameInfo.getGameInfo(home.id, away.id, date);
				if (gi != null){
					String lurl = result.get(2);
					Document doc = UrlUtil.getURLContent(lurl);
					Map<String,List> lMap = NbaLotteryHtmlParser.getAllLotteries(doc);
					
					List<LotteryHandicap> handicpas = lMap.get("handicap");
					List<LotteryWinLoss> wls = lMap.get("wl");
					List<LotteryHilo> hilos = lMap.get("hilo");
					
					LotteryHandicap handicapData = LotteryHandicap.getLottery(gi.id);
					LotteryWinLoss	wlData = LotteryWinLoss.getLottery(gi.id);
					LotteryHilo	hiloData = LotteryHilo.getLottery(gi.id);
					
					//胜分差
					for (int i= handicpas.size() -1;i >= 0;i--){
						LotteryHandicap handi = handicpas.get(i);
						if (handicapData == null || handi.createDate.getTime() > handicapData.createDate.getTime()){
							
							handi = new LotteryHandicap(gi.id, gi.home_name, gi.guest_name, gi.home_id, gi.guest_id, gi.play_date, 
									handi.handicap, handi.win, handi.lose, gi.week_day,
									handi.createDate, gi.season);
							if (i == handicpas.size() -1 ){
								handi.del_marker = 0;
								if (handicapData != null){
									handicapData.del_marker = 1;
									handicapData.save();
								}
							}else{
								handi.del_marker = 1;
							}
							handi.save();		
							
						}
					}					
					
					//胜负
					for (int i= wls.size() - 1;i>=0;i--){
						LotteryWinLoss wl = wls.get(i);
						if (wlData == null || wl.createDate.getTime() > wlData.createDate.getTime()){
							
							wl = new LotteryWinLoss(gi.id, gi.guest_id, gi.home_id, gi.guest_name, gi.home_name, 
									gi.play_date,wl.guest_win, wl.home_win, gi.week_day, wl.createDate, wl.season);
							
							if (i ==  wls.size() - 1){
								if (wlData != null){
									wlData.del_marker = 1;
									wlData.save();
								}
								wl.del_marker = 0;
							}else{
								wl.del_marker = 1;
							}							
							wl.save();							
						}					
					}
					
					//胜分差
					for (int i= hilos.size() -1 ;i>=0;i--){
						LotteryHilo hilo = hilos.get(i);
						if (hiloData == null || hilo.createDate.getTime() > hiloData.createDate.getTime()){
							hilo = new LotteryHilo(gi.id,  gi.guest_id, gi.home_id, gi.guest_name, gi.home_name,gi.play_date,
									hilo.hilo,hilo.less_than, hilo.greater_than, 1, gi.week_day, hilo.createDate, gi.season);
							
							if (i == hilos.size() -1){
								if (hiloData != null){
									hiloData.del_marker = 1;
									hiloData.save();
								}
								hilo.del_marker = 0;
							}else{
								hilo.del_marker = 1;
							}
							
							hilo.save();
						}
					}
					
				}				
			}
			return "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "2";
		}
	}
}
