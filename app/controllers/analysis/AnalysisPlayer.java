package controllers.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.game.GameInfo;
import models.game.GamePlayerInfo;
import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.player.PlayerInfo;
import models.player.PlayerSeasonScore;
import play.mvc.Controller;
import util.DateUtil;

public class AnalysisPlayer extends Controller {

	
	public static void getRivals(long id){
		GamePlayerInfo gPlayer = GamePlayerInfo.findById(id);		
		GameInfo gameInfo = GameInfo.findById(gPlayer.game_id);
		
		long rivalTeamId = 0l;
		if (gPlayer.team_id == gameInfo.home_id){
			rivalTeamId = gameInfo.guest_id;
		}else{
			rivalTeamId = gameInfo.home_id;
		}	
		
		List<GamePlayerInfo> rivalGPIs = GamePlayerInfo.gets(gPlayer.game_id, rivalTeamId, gPlayer.player_position);

		List<Long> rivalids = new ArrayList<Long>();
		for(GamePlayerInfo tmp : rivalGPIs){
			rivalids.add(tmp.player_id);
		}
		if (gPlayer.player_position.equalsIgnoreCase("PF") ){
			rivalGPIs =GamePlayerInfo.gets(gPlayer.game_id, rivalTeamId, "C");	
			for(GamePlayerInfo tmp : rivalGPIs){
				rivalids.add(tmp.player_id);
			}
		}

		
		List<PlayerSeasonScore> rivalPSSes = PlayerSeasonScore.gets(rivalids, DateUtil.addDate(gameInfo.play_date,-1),2);
		Map<Long,PlayerSeasonScore> rivalPSSmap = new HashMap<Long,PlayerSeasonScore>();
		for (PlayerSeasonScore pss : rivalPSSes){
			rivalPSSmap.put(pss.player_id, pss);
		}
		
		List<PlayerInfo> rivalPIs = PlayerInfo.getPlayers(rivalids);
		Map<Long,PlayerInfo> rivalPImap = new HashMap<Long, PlayerInfo>();
		for (PlayerInfo pi : rivalPIs){
			rivalPImap.put(pi.id, pi);
		}
		
		LotteryHilo hilo = null;
		LotteryWinLoss wl = null;
		LotteryHandicap handicap = null;
		if (gameInfo.has_detail == 1){
			hilo = LotteryHilo.getLottery(gameInfo.id);
			wl = LotteryWinLoss.getLottery(gameInfo.id);
			handicap = LotteryHandicap.getLottery(gameInfo.id);
		}
		
		render("/analysis/player/rivals.html",rivalGPIs,rivalPSSmap,rivalPImap,gPlayer
				,gameInfo,hilo,wl,handicap);		
	}
}
