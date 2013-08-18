package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import models.game.GameInfo;
import models.player.PlayerInfo;
import models.team.TeamInfo;
import models.team.TeamMsg;

public class Application extends Controller {

	public static int season;
	
	public static String seasonStartDate;
	
	public static String tradeTime;
	
	public static String espnNba;
	
	public static String hupuBoxScore;
	
	public static String hupuMsg;
	
	public static String espnSchedule;
	
	public static String espnPlayByPlay;
	
	public static String poolResult;
	
	public static String matchResult;
	
	public static Map<String,Integer> monthMap;
	
	public static Map<String,TeamInfo> teamMap;
	
	public static Map<Long,GameInfo> todayGames = new HashMap<Long, GameInfo>();
	
	public static Map<Long,TeamMsg> lastestNewsMap = new HashMap<Long, TeamMsg>();
	
//	public static Map<String,PlayerInfo> AllPlayers = new HashMap<String, PlayerInfo>();
	
	
    public static void index() {
        render();
    }


}