package controllers.analysis;

import java.util.List;

import models.game.GameInfo;
import controllers.Application;
import play.mvc.Controller;

public class AnalysisHDC extends Controller {

	public static void scoreSpan(Long gameId){
		GameInfo gameInfo = GameInfo.findById(gameId);
		List<GameInfo> hgame0t5w = GameInfo.getHomeGameInfos(gameInfo.home_id, 1,5, Application.season);
		List<GameInfo> hgame0t5l = GameInfo.getHomeGameInfos(gameInfo.home_id, -5,-1, Application.season);
		
		List<GameInfo> hgame6t10w = GameInfo.getHomeGameInfos(gameInfo.home_id, 6,10, Application.season);
		List<GameInfo> hgame6t10l = GameInfo.getHomeGameInfos(gameInfo.home_id, -10,-6, Application.season);
		
		List<GameInfo> hgame11t15w = GameInfo.getHomeGameInfos(gameInfo.home_id, 11,15, Application.season);
		List<GameInfo> hgame11t15l = GameInfo.getHomeGameInfos(gameInfo.home_id,-11, -15, Application.season);
		
		List<GameInfo> hgame16t20w = GameInfo.getHomeGameInfos(gameInfo.home_id, 16,20, Application.season);
		List<GameInfo> hgame16t20l = GameInfo.getHomeGameInfos(gameInfo.home_id,-16, -20, Application.season);
		
		List<GameInfo> hgame21t25w = GameInfo.getHomeGameInfos(gameInfo.home_id, 21,25, Application.season);
		List<GameInfo> hgame21t25l = GameInfo.getHomeGameInfos(gameInfo.home_id,-25, -21, Application.season);
		
		List<GameInfo> hgame26w = GameInfo.getHomeGameInfos(gameInfo.home_id, 26, Application.season);
		List<GameInfo> hgame26l = GameInfo.getHomeGameInfos(gameInfo.home_id, -26, Application.season);
		
		List<GameInfo> agame0t5w = GameInfo.getAwayGameInfos(gameInfo.guest_id, 1,5, Application.season);
		List<GameInfo> agame0t5l = GameInfo.getAwayGameInfos(gameInfo.guest_id, -5,-1, Application.season);
		
		List<GameInfo> agame6t10w = GameInfo.getAwayGameInfos(gameInfo.guest_id, 6,10, Application.season);
		List<GameInfo> agame6t10l = GameInfo.getAwayGameInfos(gameInfo.guest_id,-10, -6, Application.season);
		
		List<GameInfo> agame11t15w = GameInfo.getAwayGameInfos(gameInfo.guest_id, 11,15, Application.season);
		List<GameInfo> agame11t15l = GameInfo.getAwayGameInfos(gameInfo.guest_id,-15, -11, Application.season);
		
		List<GameInfo> agame16t20w = GameInfo.getAwayGameInfos(gameInfo.guest_id, 16,20, Application.season);
		List<GameInfo>agame16t20l = GameInfo.getAwayGameInfos(gameInfo.guest_id,-20, -16, Application.season);
		
		List<GameInfo> agame21t25w = GameInfo.getAwayGameInfos(gameInfo.guest_id, 21,25, Application.season);
		List<GameInfo> agame21t25l = GameInfo.getAwayGameInfos(gameInfo.guest_id,-25, -21, Application.season);
		
		List<GameInfo> agame26w = GameInfo.getAwayGameInfos(gameInfo.guest_id, 26, Application.season);
		List<GameInfo> agame26l = GameInfo.getAwayGameInfos(gameInfo.guest_id, -26, Application.season);
		
		render("/analysis/hdc/anlySpan.html",gameId,gameInfo,hgame0t5w,hgame0t5l,hgame6t10w,hgame6t10l,hgame11t15w,hgame11t15l,hgame16t20w,hgame16t20l,
				hgame21t25w,hgame21t25l,hgame26w,hgame26l,agame0t5w,agame0t5l,agame6t10w,agame6t10l,agame11t15w,agame11t15l,
				agame16t20w,agame16t20l,agame21t25w,agame21t25l,agame26w,agame26l);
		
	}
}
