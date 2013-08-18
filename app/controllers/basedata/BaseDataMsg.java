package controllers.basedata;

import java.util.List;

import models.game.GameInfo;
import models.team.TeamMsg;
import play.mvc.Controller;

public class BaseDataMsg extends Controller {

	/**
	 * GET /basedata/game/msg/{<\d+>gameId}/{lytype} 
	 * 比赛球队消息
	 * @param gameId
	 * v 1.0
	 */
	public static void gameMsgs(Long gameId,String lytype){
		GameInfo gameInfo = GameInfo.findById(gameId);
		render("/basedata/msg/gamemsg.html",gameInfo,lytype);		
	}
	
	/**
	 * /basedata/team/ajax/tmsg
	 * @param gameId
	 * @param teamId
	 * @param page
	 * @param pageSize
	 */
	public static void ajaxtmsg(Long gameId,Long teamId,Integer page,Integer pageSize){
		GameInfo game = GameInfo.findById(gameId);
		if (page == null) page = 1;
		if (pageSize == null) pageSize = 30;
		
		List<TeamMsg> msgs = TeamMsg.getMsgs(teamId,game.play_date,page,pageSize);
		render("/basedata/msg/ajax/msg.html",msgs);		
	}
	
	
}
