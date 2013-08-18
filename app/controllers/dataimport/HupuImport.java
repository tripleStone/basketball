package controllers.dataimport;

import gsonmoudle.HupuMsg;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import controllers.Application;

import models.game.GameInfo;
import models.game.GameInfoReport;
import models.game.GameInfoStatic;
import models.game.GameInjury;
import models.lottery.LotteryWinPoint;
import models.team.TeamInfo;
import models.team.TeamMsg;
import models.team.TeamStSeasonBase;
import models.team.TeamStSeasonScore;
import models.team.TeamStarters;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.results.RenderText;
import util.DateUtil;
import util.htmlparser.HupuHtmlParser;
import util.htmlparser.UrlUtil;

public class HupuImport extends Controller{
	
    public static void importGameReport(long gameId,String url){   
    	GameInfo game = GameInfo.findById(gameId);
    	String report = "";
    	if (game.hasRecap == 1 ){
    		
    	}else{ 
    		report = HupuHtmlParser.getGameReport(url);
    	}
		renderJSON("{\"error\":\""+report+"\"}");
    }
    
	public static void importGameReports(String date){
		try {
			String url = Application.hupuBoxScore + date;
			Map<String,String> urlMaper = HupuHtmlParser.getGameReports(UrlUtil.getURLContent(url));
			Date queryDate = DateUtil.parseDate(date);
			List<GameInfo> games = GameInfo.getGameInfos(queryDate);
			
			String key = "";
			String reportUrl = "";
			String reportContent = "";
			
			for (GameInfo game : games){	
				String content = "";
				key = game.home_name  + "-" + game.guest_name;
				if ( game.has_detail != 1)
					continue;
				reportUrl =  urlMaper.get(key);	
				if (reportUrl == null)
					continue;
				
				if ( game.hasRecap  == 1){
					List<GameInfoReport> reports = GameInfoReport.getReports(game.id,1);
					for (GameInfoReport report : reports){
						content = content + report.content;
					}
				}
				
				
				Logger.info("  %s VS %s  game report import url : %s",game.guest_name,game.home_name, reportUrl);
				reportContent  = HupuHtmlParser.getGameReport(reportUrl.replace("boxscore", "recap"));
				if (content.length() != reportContent.length() ){
					GameInfoReport.delReports(game.id,1);
					List<GameInfoReport> reports = cutContent(reportContent);
					for (GameInfoReport report : reports){
						report.gameId = game.id;
						report.rtype = 1;
						report.save();					
					}
				}	
					
				game.reportTitle =  urlMaper.get(key+"_title");
				game.hasRecap = 1;	
				game.save();
				
				
			}
			Integer gameType = 0;
			Date regularEndDate = DateUtil.parseDate(Play.configuration.getProperty(Application.season + "_regular_season_end"));
			String beginDate = Application.seasonStartDate;
			if (queryDate.getTime() > regularEndDate.getTime() ){
				gameType = 1;
				beginDate = Play.configuration.getProperty(Application.season + "_regular_season_end");
			}
			
			
			GameInfoStatic.callPrcSet(date);
			TeamStSeasonBase.callPrcTss(beginDate, date, date, Application.season,gameType);
			TeamStSeasonScore.callPrcTSSS(beginDate, date, date, Application.season);
			LotteryWinPoint.callPrcSetLottery(games.get(0).play_date);
			TeamStarters.callPrc(beginDate, date, date, Application.season);
//			EspnImport.impNextDayGames(date);
			renderJSON("{\"msg\":\"导入成功\"}");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void saveTeamsId(){
		try {
			List<TeamInfo> teams = TeamInfo.findAll();
			for (TeamInfo team:teams){	
//				Logger.info(team.host_page);
				Long teamId = HupuHtmlParser.getTeamId(UrlUtil.getURLContent(team.host_page));
				team.hupuId = teamId;
				team.save();				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("2");
			
		}
		
		renderText("1");
		
	}
	
	
	public static void importMsg(Long teamId){
    	if (teamId == null){
	    	List<TeamInfo> teams = TeamInfo.findAll();
	    	for (TeamInfo team : teams){
	    		saveTeamMsg(team);    		
	    	}   
	    	renderJSON("{\"error\":\"1\"}");
    	}else{
    		TeamInfo team = TeamInfo.findById(teamId);  
    		saveTeamMsg(team); 
    		renderJSON("{\"error\":\""+team.full_name+"\"}");
    	}   	
    }
 
	private static void saveTeamMsg(TeamInfo team) {
		String url;
		url =  Application.hupuMsg.replace("{1}",team.hupuId.toString());
		List<HupuMsg> msges;
		TeamMsg lastestMsg = TeamMsg.getNewestMsg(team.id);
		try {
			msges = HupuHtmlParser.getTeamMsgs(UrlUtil.getURLContent(url));
			for (HupuMsg msg : msges){
				if (msg.getTimeline() <= lastestMsg.timeLine){
					break;
				}
				TeamMsg teamMsg = new TeamMsg(msg,team);
				teamMsg.msg_type = 1;
				teamMsg.save();

			}
			saveHupuMsg(team,msges,1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static TeamMsg saveHupuMsg(TeamInfo homeT, List<HupuMsg> hmsges,int msgType) {
		TeamMsg lastMsg =  Application.lastestNewsMap.get(homeT.id);
		for (int i = 0;i<hmsges.size();i++){
			HupuMsg msg = hmsges.get(i);			
			if ( lastMsg != null && 
					(  msg.getTimeline() <= lastMsg.timeLine || msg.getText().trim().equals(lastMsg.msg_content)  )){
				break;
			}
			
			TeamMsg teamMsg = new TeamMsg(msg,homeT);
			teamMsg.msg_type = msgType;
			teamMsg.save();
			if (i == 0){
				Application.lastestNewsMap.put(homeT.id, teamMsg);
			}
		}
		return Application.lastestNewsMap.get(homeT.id);
	}
	
	
	public static List<GameInfoReport> cutContent(String content){
		int MAX_CONTENT_LENGTH = 2000;
		List<GameInfoReport> reports  = new ArrayList<GameInfoReport>();
		String report = "";
		
		int stringArrayLength = (content.length() - 1) / MAX_CONTENT_LENGTH;
		for (int i = 0; i <= stringArrayLength; i++) {
			
			if (i == stringArrayLength) {
				report = content.substring(i * MAX_CONTENT_LENGTH,
						content.length());
			} else {
				report = content.substring(i * MAX_CONTENT_LENGTH,
						i * MAX_CONTENT_LENGTH + MAX_CONTENT_LENGTH);
			}
			
			reports.add(new GameInfoReport(0l, i, report));			
		}
		
		return reports;
		
	}

}
