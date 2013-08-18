package controllers.analysis;

import java.util.ArrayList;
import java.util.List;

import models.game.GameInfo;
import models.game.GamePlayerInfo;
import models.player.PlayerSeasonScore;
import play.mvc.Controller;

public class AnalysisTeam extends Controller{
	
	public static void playerOderByPoint(Long teamId,Long gameId,Integer stat){
		if (stat == null){
			stat = 2;
		}
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<GamePlayerInfo> players = GamePlayerInfo.gets(gameId, teamId);
		List<Long> ids = new ArrayList<Long>();
		for (GamePlayerInfo player:players){
			ids.add(player.id);
		}
		
//		List<PlayerSeasonScore> playerSeasonScores = PlayerSeasonScore.gets(ids, gameInfo.play_date, stat,1,ids.size());
		
	}
	
	

}
