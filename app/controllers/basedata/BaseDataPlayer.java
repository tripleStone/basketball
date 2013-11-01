package controllers.basedata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import controllers.Application;

import models.game.GameInfo;
import models.game.GamePlayerInfo;
import models.player.PlayerInfo;
import models.player.PlayerSeasonScore;
import models.player.PlayerTeamSeasonScore;

import play.mvc.Controller;
import play.mvc.Util;
import util.DateUtil;

public class BaseDataPlayer extends Controller{
	
	

	public static void information(Long playerId){
		PlayerInfo playerInfo = PlayerInfo.findById(playerId);
		String headUrl = "http://a.espncdn.com/combiner/i?img=/i/headshots/nba/players/full/"+playerInfo.espnId+".png&w=350&h=254";
		
		List<PlayerSeasonScore> psses = PlayerSeasonScore.getScoreBySeason(playerId, 2, 0);
		render("/basedata/player/information.html",playerInfo,headUrl,psses);
	}

	
	
	
	/**
	 * url /basedata/player/recent/{<\d+>playerId}	
	 * @param playerId
	 * @param gameId
	 * @param page
	 * @param pageSize
	 * @throws ParseException 
	 */
	public static void recentPlayerInfos(Long playerId,Long gameId,Integer isChart,
			Integer page,Integer pageSize) throws ParseException{
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
		PlayerSeasonScore pss = PlayerSeasonScore.get(playerId,queryDate,2);
		
		
		render("/basedata/player/recentPlayInfos.html",gamePlayerInfos,playerInfo,gameId,pss);	
	}
	
	public static void recentPlayerInfosChart(Long playerId,Long gameId,String view,
			Integer page,Integer pageSize) throws ParseException{
		Date queryDate = new Date();
		GameInfo game = null;
		if (page == null) page = 1;
		if (pageSize == null) pageSize = 20;
		
		if (gameId != null){
			game = GameInfo.findById(gameId);
			queryDate = game.play_date;
		}
		
		List<GamePlayerInfo> tmpUsers  = GamePlayerInfo.gets(playerId, queryDate, page, pageSize);
		List<GamePlayerInfo> gamePlayerInfos  = new ArrayList<GamePlayerInfo>();
		
		Gson gson = new Gson();
		String xAxis = "";
		SimpleDateFormat format = new SimpleDateFormat("MMdd");
		Map<String,Object> result = new HashMap<String, Object>();
		List<Object> results = new ArrayList<Object>();
		List<Integer> points = new ArrayList<Integer>();
		List<Integer> assists = new ArrayList<Integer>();
		for (int i=tmpUsers.size() -1 ;i>=0;i--){
			GamePlayerInfo player = tmpUsers.get(i);
			gamePlayerInfos.add(player);
			if (i == tmpUsers.size() - 1 ){
				xAxis =  "\'" + format.format(player.game_date) + "\'";
			}else{
				xAxis = xAxis + ",\'" + format.format(player.game_date) + "\'";
			}
			points.add(player.point);
			assists.add(player.assist);
		}
		result.put("name", "µÃ·Ö");
		result.put("data", points);
		results.add(result);
		result = new HashMap<String, Object>();
		result.put("name", "Öú¹¥");
		result.put("data", assists);
		results.add(result);
		String yAxis = gson.toJson(results);
		PlayerInfo playerInfo = PlayerInfo.findById(playerId);
		
		
		render("/basedata/player/recentPlayInfosChart.html",gamePlayerInfos,playerInfo,gameId,xAxis,yAxis);		
	
	}
	
	public static void seasonBoxScore(Integer year,Long playerId){
		if (year == null)
			year = Application.season;
		
		PlayerInfo playerInfo = PlayerInfo.findById(playerId);
		List<GamePlayerInfo> playerInfos = GamePlayerInfo.getInfos(year, playerId);
		
		render("/basedata/player/seasonBoxScore.html",playerInfos,playerId,playerInfo,year);
	}
	
	
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

}
