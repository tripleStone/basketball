package controllers.autojob;

import gsonmoudle.EspnGame;
import gsonmoudle.HupuMsg;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import models.game.GameInfo;
import models.team.TeamInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import controllers.Application;
import controllers.dataimport.EspnImport;
import controllers.dataimport.HupuImport;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import util.DateUtil;
import util.htmlparser.EspnHtmlParser;
import util.htmlparser.HupuHtmlParser;
import util.htmlparser.UrlUtil;

@Every("16min")
public class JobTeamMsg extends Job{
	public void doJob() {
		
	try {
		Logger.info("teamMsgJob - Begin  -----------------");
		String today = DateUtil.getDateStr(new Date());
		List<TeamInfo> teams = TeamInfo.getTeamsWithNoGame(today);
		for (int i = 0;i<teams.size();i++){
			TeamInfo team = teams.get(i);
			Logger.info("\n");
			Logger.info("teamMsgJob - team : %s ",team.team_name);
//			Logger.info("teamMsgJob - team's timeline : %s",   Application.lastestNewsMap.get(team.id).);
			String url =  Application.hupuMsg.replace("{1}",team.hupuId.toString());
			Document doc = Jsoup.connect(url)
					  .userAgent("Mozilla")
					  .timeout(30000)
					  .get();
			List<HupuMsg> hmsges = HupuHtmlParser.getTeamMsgs(doc);		
			Logger.info("teamMsgJob - team's msg count : %d",hmsges.size() );
			HupuImport.saveHupuMsg(team, hmsges,1);
			
		}
		
		Logger.info("teamMsgJob - end  -----------------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
		
}
