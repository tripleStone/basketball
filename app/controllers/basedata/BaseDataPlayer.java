package controllers.basedata;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.game.GameInfo;
import models.game.GamePlayerInfo;
import models.player.PlayerInfo;
import models.player.PlayerSeasonScore;
import models.player.PlayerTeamSeasonScore;

import play.mvc.Controller;
import play.mvc.Util;
import util.DateUtil;

public class BaseDataPlayer extends Controller{
	
	

	

	@Util
	public static void dividPosition(GameInfo game, List<PlayerInfo> players,
			List<PlayerInfo> pgs, List<PlayerInfo> centers,
			List<PlayerInfo> pfs, List<PlayerInfo> sfs, List<PlayerInfo> sgs) {
		for (PlayerInfo player : players){
			
			if (player.position.equalsIgnoreCase("pg")){
				pgs.add(player);
			}else if (player.position.equalsIgnoreCase("c")) {
				centers.add(player);
			}else if (player.position.equalsIgnoreCase("pf")){
				pfs.add(player);
			}else if (player.position.equalsIgnoreCase("sf")){
				sfs.add(player);
			}else{
				sgs.add(player);
			}
		}
	}
	
	
	/**
	 * url /basedata/player/recent/{<\d+>playerId}	
	 * @param playerId
	 * @param gameId
	 * @param page
	 * @param pageSize
	 * @throws ParseException 
	 */
	public static void recentPlayerInfos(Long playerId,Long gameId,Integer page,Integer pageSize) throws ParseException{
		GameInfo game = null;
		Date queryDate = new Date();
		
		if (page == null) page = 1;
		if (pageSize == null) pageSize = 20;
		
		if (gameId != null){
			game = GameInfo.findById(gameId);
			queryDate = game.play_date;
		}
		
		PlayerInfo playerInfo = PlayerInfo.findById(playerId);		
		
		List<GamePlayerInfo> gamePlayerInfos  = GamePlayerInfo.gets(playerId, queryDate, page, pageSize);	
		
		List<Long> ids  = new ArrayList<Long>();
		for (GamePlayerInfo player:gamePlayerInfos){
			ids.add(player.game_id);
		}		
		List<GameInfo> games = GameInfo.getGameInfos(ids);
		for (GamePlayerInfo player:gamePlayerInfos){
			for (GameInfo tmpGame:games){			
				if (tmpGame.id == player.game_id){
					player.gameInfo = tmpGame;
					games.remove(tmpGame);
					break;
				}
			}			
		}		
		
		PlayerSeasonScore pss = PlayerSeasonScore.get(playerId,game.play_date,2l);
		
		render("/basedata/player/recentPlayInfos.html",gamePlayerInfos,playerInfo,gameId,pss);	
	}

}
