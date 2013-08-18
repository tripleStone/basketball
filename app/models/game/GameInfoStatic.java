package models.game;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Query;

import models.game.supermodel.GameInfoStatisticModel;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;


@Entity
@Table(name="Game_Info_Statistic")
public class GameInfoStatic  extends GameInfoStatisticModel{
	
	@Id
	@SequenceGenerator(name="SEQ_STATISTIC",sequenceName="SEQ_STATISTIC",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_STATISTIC")
	@Column(name="ID")
	public long id;	
	
	public static GameInfoStatic lastOne(){
		return find(" order by id desc").first();
	}
	
	public static void  delByGame(long game_id){
		delete(" game_id = ? ", game_id);
	}	
	
	public static List<GameInfoStatic> getGameStatics(long teamId,Date gameDate,int page,int pageSize){
		return find("  team_id = ? and game_date < ? order by game_date desc, is_host asc",teamId,gameDate).from(page -1 ).fetch(pageSize);
	}
	
	public static List<GameInfoStatic> getGameStaticsHomeAway(long teamId,Date gameDate,int games){
		return find(" game_id in  ( select  id  from  GameInfo where  (home_id =? or guest_id = ?)  and  play_date < ?   )   order by game_date desc, is_host asc",teamId,teamId,gameDate).fetch(games*2);
	}
	
	public static List<GameInfoStatic> getHomeStatics(long teamId,Date gameDate,int games){
		return find(" game_id in  ( select  id  from  GameInfo where  home_id =?  and  play_date < ?   )   order by game_date desc, is_host asc",teamId,gameDate).fetch(games*2);
	}
	
	public static List<GameInfoStatic> getAwayStatics(long teamId,Date gameDate,int games){
		return find(" game_id in  ( select  id  from  GameInfo where  guest_id =?  and  play_date < ?   )   order by game_date desc, is_host asc",teamId,gameDate).fetch(games*2);
	}
	
	public static List<GameInfoStatic> getGTDate(long teamId,Date gameDate,int games){
		return find(" game_id in  ( select  id  from  GameInfo where  (home_id =? or guest_id = ?)  and  play_date > ?   )   order by game_date desc, is_host asc",teamId,teamId,gameDate).fetch(games*2);
	}
	
	public static List<GameInfoStatic> getGTDateHome(long teamId,Date gameDate,int games){
		return find(" game_id in  ( select  id  from  GameInfo where  home_id =?   and  play_date > ?   )   order by game_date desc, is_host asc",teamId,gameDate).fetch(games*2);
	}
	
	public static List<GameInfoStatic> getGTDateAway(long teamId,Date gameDate,int games){
		return find(" game_id in  ( select  id  from  GameInfo where   guest_id = ? and  play_date > ?   )   order by game_date desc, is_host asc",teamId,gameDate).fetch(games*2);
	}
	
	public static GameInfoStatic getGameStatic(long teamId,long gameId){
		return find(" team_id = ? and game_id = ? ",teamId,gameId).first();
	}
	
	public static List<GameInfoStatic> getGameStatics(long homeId,long guestId,Date gameDate,int season){
		String sql = " game_id in  ( select  id  from  GameInfo where ( (home_id =? and guest_id = ?)  or   (guest_id = ? and home_id =?) ) and  play_date < ?   and season = ? " +
				" )    " +
				" order by game_date desc , team_id desc ";		
		return find(sql,homeId,guestId,homeId,guestId,gameDate,season).fetch();
	}
	
	public static List<GameInfoStatic> getGameStatics(long homeId,long guestId,int games,Date gameDate){
		String sql = " game_id in  ( select  id  from  GameInfo where ( (home_id =? and guest_id = ?)  or   (guest_id = ? and home_id =?) ) and  play_date <= ?   " +
				" )    " +
				" order by game_date desc , is_host  ";		
		return find(sql,homeId,guestId,homeId,guestId,gameDate).fetch();
	}
	
	
	public static List<GameInfoStatic> getGameStatics(List<Long> teamIs,long teamId,Date gameDate){
		
		return null;
	}
	
	public static void callPrcSet(String queryDate){
		Query query = JPA.em().createNativeQuery("{call set_game_info_statistics(?)}");
		query.setParameter(1, queryDate);
		query.executeUpdate();
	}

	
}
