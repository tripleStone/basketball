package models.game;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

@Entity
@Table(name="GAME_INJURY")
public class GameInjury extends GenericModel {
	
	@Id
	@SequenceGenerator(name="SEQ_PLAYER_INFO",sequenceName="SEQ_PLAYER_INFO",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_PLAYER_INFO")
	@Column(name="id")
	public long id;
	
	@Column(name="PLAYER_ID")
	public long playerId;
	
	@Column(name="GAME_ID")
	public long gameId;
	
	@Column(name="TEAM_ID")
	public long teamId;	
	
	@Column(name="BRIEF")
	public String brief;	
	
	@Column(name="player_name")
	public String playerName;
	
	@Column(name="POSITION")
	public String position;
	
	@Column(name="CREATE_DATE")
	public Date ceateDate;	
	
	public static void del(long gameId){
		delete(" gameId = ?",gameId);
	}
	
	public static void updatePlaeryId(Date date){
		Query query = JPA.em().createNativeQuery(" update GAME_INJURY t set t.PLAYER_ID " +
				" = (select t1.id from player_info t1 where t1.name = t.player_name) " +
				" where t.CREATE_DATE >= ? and t.create_date < ? + 1 ");
		query.setParameter(1, date);
		query.setParameter(2, date);
		query.executeUpdate();
		
	}
	
	public static List<GameInjury> getInjuries(Long gameId){
		return find( "gameId = ?  order by teamId ",gameId).fetch();
	}
	
	public static List<GameInjury> getInjuries(Long gameId , Long teamId){
		return find( "gameId = ? and teamId = ?  ",gameId,teamId).fetch();
	}
	

}
