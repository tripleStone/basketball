package controllers.dataimport;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.game.GameInfo;
import models.game.GameInfoStatic;
import models.lottery.LotteryWinPoint;
import models.team.TeamInfo;
import models.team.TeamStSeasonBase;
import models.team.TeamStSeasonScore;
import models.team.TeamStarters;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ning.http.util.DateUtil.DateParseException;

import controllers.Application;
import controllers.GameDataImport;


import play.Logger;
import play.mvc.Controller;
import util.DateUtil;
import util.htmlparser.NbaChinaHtmlParser;
import util.htmlparser.UrlUtil;

public class ChinaNbaImport extends Controller{
	

	public static void importGames(String date) throws ParseException{
		try {
			Date playDate = DateUtil.parseDate(date);
			List<GameInfo> games = GameInfo.getGameInfos(playDate);
			playDate = DateUtil.addDate(playDate, -1);		
			String url = "http://china.nba.com/stats/league/fullScoreBoard/" + DateUtil.getMMddyyyy(playDate) + "_00.html";
			Document doc = UrlUtil.getURLContent(url);
			Map<String,String> urlMap = NbaChinaHtmlParser.getGames(doc);
			for (GameInfo gameInfo : games){
				if (gameInfo.has_detail != 1 || urlMap.get(gameInfo.guest_name + "-" + gameInfo.home_name) == null){
					continue;
				}
				
				url = "http://china.nba.com/" + urlMap.get(gameInfo.guest_name + "-" + gameInfo.home_name);
				doc = UrlUtil.getURLContent(url);
				GameInfo tmpInfo = NbaChinaHtmlParser.getGameInfo(doc); 
				if (!tmpInfo.home_name.equals(gameInfo.home_name) || !tmpInfo.guest_name.equals(gameInfo.guest_name)){
					continue;
				}				
				
				gameInfo = NbaChinaHtmlParser.setGameInfo(doc, gameInfo);			
				gameInfo.save();
			}
			
			List<TeamInfo> teams = TeamInfo.getTeamsNoRgP(DateUtil.parseDate(date));
			for (TeamInfo teamTmp : teams){
				GameDataImport.saveTeamPlayers(teamTmp.id, date);
			}
			

			renderJSON("{\"error\":\"1\"}");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJSON("{\"error\":\"导入出错\"}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJSON("{\"error\":\"导入出错\"}");
		}
		
	}
	
	
	public static void importGameAdditionData(String dataUrl,Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		Document doc;
		try {
			doc = UrlUtil.getURLContent(dataUrl);
			GameInfo tmpInfo = NbaChinaHtmlParser.getGameInfo(doc); 
			if (!tmpInfo.home_name.equals(gameInfo.home_name) || !tmpInfo.guest_name.equals(gameInfo.guest_name)){
				renderJSON("{\"error\":\"2\",\"msg\":\"链接与比赛不对应\"}");
			}
			
			gameInfo = NbaChinaHtmlParser.setGameInfo(doc, gameInfo);			
			gameInfo.save();
			

			
			renderJSON("{\"error\":\"1\",\"msg\":\"导入成功\"}");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJSON("{\"error\":\"2\",\"msg\":\"异常错误！\"}");
		}		
	}
	

}
