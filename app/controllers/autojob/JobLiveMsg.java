package controllers.autojob;

import gsonmoudle.EspnGame;
import gsonmoudle.HupuMsg;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import models.game.GameInfo;
import models.game.GameInfoStatic;
import models.game.GamePlayerInfo;
import models.team.TeamInfo;
import models.team.TeamMsg;

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

@Every("5min")
public class JobLiveMsg extends Job{
	
	public void doJob() {
		Logger.info("auto liveMsg begin-------");
		if (Application.todayGames != null && Application.todayGames.keySet().size() > 0 ){
			try {
				String today = DateUtil.getDateStr(new Date());
				Document doc = Jsoup.connect(Application.espnNba)
						  .userAgent("Mozilla")
						  .timeout(60000)
						  .get();
				List<EspnGame> espns = EspnHtmlParser.getTodayGames(doc);
				GameInfo game  = null;
				
				for (EspnGame espnGame : espns){
					if (Application.todayGames.get(espnGame.getGameId()) != null){
						game = GameInfo.findById(Application.todayGames.get(espnGame.getGameId()).id);					
					}else{
						game  =  EspnImport.getGameByEspnGame(espnGame,today);
						Application.todayGames.put(espnGame.getGameId(), game);
					}	
					TeamInfo homeT = Application.teamMap.get(String.valueOf(game.home_id));	
					TeamInfo awayT = Application.teamMap.get(String.valueOf(game.guest_id));
					
//					if (espnGame.getStatus() == 22 && GameInfoStaticQuarter.countQuarter(game.id,espnGame.getStatusText()) <= 0){
						
//						String url = "http://scores.espn.go.com/nba/boxscore?gameId=" + espnGame.getGameId();
//						doc = UrlUtil.getURLContent(url);
//						
//						//球队数据--begin
//						GameInfoStatic guestInfoStatic = EspnHtmlParser.getGameStatistics(doc, 2, awayT, game);
//						if (guestInfoStatic != null){
//							GameInfoStaticQuarter quarter = new GameInfoStaticQuarter(guestInfoStatic,espnGame.getStatusText());
//							quarter.save();
//							GameInfoStatic homeInfoStatic = EspnHtmlParser.getGameStatistics(doc, 5, homeT, game);
//							quarter = new GameInfoStaticQuarter(homeInfoStatic,espnGame.getStatusText());
//							quarter.save();
//						}
//						//球队数据--end
//						
//						
//						List<GamePlayerInfo> guestPlayers =   EspnHtmlParser.getPlayerInfos(doc,0,awayT,game);
//						for (GamePlayerInfo gamePlayer : guestPlayers ){
//							if (Application.todayPlayer.get(awayT.id + "_" + gamePlayer.player_name) != null){
//								gamePlayer.player_id = Application.todayPlayer.get(awayT.id + "_" + gamePlayer.player_name).id;
//							}
//							GamePlayerQuarter playerQuarter = new GamePlayerQuarter(gamePlayer,espnGame.getStatusText());
//							playerQuarter.save();
//						}
//						List<GamePlayerInfo> homePlayers =    EspnHtmlParser.getPlayerInfos(doc,3,homeT,game);
//						for (GamePlayerInfo player : homePlayers ){
//							if (Application.todayPlayer.get(homeT.id + "_" + player.player_name) != null){
//								player.player_id = Application.todayPlayer.get(homeT.id + "_" + player.player_name).id;
//							}
//							GamePlayerQuarter playerQuarter = new GamePlayerQuarter(player,espnGame.getStatusText());
//							playerQuarter.save();
//						}
//						
//					}else{					
					if (espnGame.getStatus() != 2 ){
						Application.todayGames.remove(espnGame.getGameId());
						continue;
					}
					
					if (espnGame.getStatus() == 2 && game.status != 2){
						game.status = 2;
						game.save();
					}else if (espnGame.getStatus() == 3){
						if (game.status != 3){
							game.status = 3;
							game.save();
						}
						Application.todayGames.remove(game.espnId);
						continue;
					}
					Logger.info("liveMsgCheck game is %s(away) VS %s(home)",game.guest_name,game.home_name);
					
					String url =  Application.hupuMsg.replace("{1}",homeT.hupuId.toString());
					doc = Jsoup.connect(url)
							  .userAgent("Mozilla")
							  .timeout(30000)
							  .get();
					List<HupuMsg> hmsges = HupuHtmlParser.getTeamMsgs(doc);					
					
					url =  Application.hupuMsg.replace("{1}",awayT.hupuId.toString());
					doc = Jsoup.connect(url)
							  .userAgent("Mozilla")
							  .timeout(30000)
							  .get();
					List<HupuMsg> amsges = HupuHtmlParser.getTeamMsgs(doc);
					
					Logger.info("teamMsgJob - %s 's msg count : %d",game.home_name,hmsges.size() );					
					HupuImport.saveHupuMsg(homeT, hmsges,2);
					Logger.info("teamMsgJob - %s 's msg count : %d",game.guest_name,amsges.size() );
					HupuImport.saveHupuMsg(awayT, amsges,2);
					Logger.info("\n");
//					}
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logger.info("Job live MSG errors!");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Logger.info("auto liveMsg end-------");
		
	}



}
