package models.game;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import models.game.supermodel.GamePlayerInfoModel;
import models.player.PlayerInfo;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

@Entity
@Table(name="Game_player_Info")
public class GamePlayerInfo extends GamePlayerInfoModel {

	@Id
	@SequenceGenerator(name="SEQ_PLAYER_INFO",sequenceName="SEQ_PLAYER_INFO",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_PLAYER_INFO")
	@Column(name="id")
	public long id;
	
	@ManyToOne
	@JoinColumn(name="game_id",insertable=false,updatable=false)
	public GameInfo gameInfo;
	
	@Transient
	public Long espnId;
	
	public static List<GamePlayerInfo> getInfos(Integer season,Long playerId){
		return find(" gameInfo.season = ? and player_id = ? order by game_date ",season,playerId).fetch();
	}
	
	public static List<GamePlayerInfo> gets(long gameId,int page,int pageSize){
		return find(" game_id = ? order game_date desc ",gameId).from(page -1).fetch(pageSize);
	}
	
	public static List<GamePlayerInfo> gets(long gameId,long teamId){
		return find(" game_id = ? and team_id = ? order by first desc,time desc",gameId,teamId).fetch();
	}
	
	
	public static GamePlayerInfo get(long playerId,long gameId){
		return find(" player_id = ? and game_id = ? order by game_date desc ",playerId,gameId).first();
	}
	
	public static List<GamePlayerInfo> gets(long gameId,long teamId,String position){
		return find(" game_id = ? and team_id = ? and player_position = ? order by first desc ",gameId,teamId,position).fetch();
	}
	
	public static List<GamePlayerInfo> gets(long playerId, Date gameData,Integer page,Integer pageSize){
		return find(" player_id = ? and game_date < ? and time > 0 order by game_date desc ",playerId,gameData).from(page -1).fetch(pageSize);
	}
	
	public static List<GamePlayerInfo> gets(List<Long> playerIds,Long gameId){
		return find(" player_id = :ids and game_Id <= ? order by first desc,player_id ",gameId).bind("ids", playerIds).fetch();
	}
	
	public static void updatePlayerId(long teamId,Date gameDate){
		Query query = JPA.em().createNativeQuery("update game_player_info t  set t.player_id = " +
				" (select pi.id from player_info pi where t.player_name = pi.name ) where t.team_id = ? and t.player_id = -1 ");
		query.setParameter(1, teamId);
		query.executeUpdate();	
	}
	public static void  delByGame(long game_id){
		delete(" game_id = ? ", game_id);
	}	


	
}
