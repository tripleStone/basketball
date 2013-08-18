package models.player;

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
@Table(name="PLAYER_TEAM_SEASON_SCORE")
public class PlayerTeamSeasonScore extends GenericModel {

	@Id
	@Column(name="id")
	public long id;
	
	@Column(name="team_id")
	public long team_id;
	
	@Column(name="player_id")
	public long player_id;
	
	@Column(name="player_name")
	public String player_name;
	
	@Column(name="player_position")
	public String player_position;
	
	@Column(name="season")
	public int season;	
	
	@Column(name="play_time")
	public int time;
	
	@Column(name="shoot_all")
	public double shoot_all;
	
	@Column(name="shoot_hit")
	public double shoot_hit;
	
	@Column(name="three_all")
	public double three_all;
	
	@Column(name="three_hit")
	public double three_hit;
	
	@Column(name="free_all")
	public double free_all;
	
	@Column(name="free_hit")
	public double free_hit;
	
	@Column(name="rebound_front")
	public double rebound_front;
	
	@Column(name="rebound_after")
	public double rebound_after;
	
	@Column(name="rebound_all")
	public double rebound_all;
	
	@Column(name="assist")
	public double assist;
	
	@Column(name="foul")
	public double foul;
	
	@Column(name="stolen")
	public double stolen;
	
	@Column(name="turn_out")
	public double turn_out;
	
	@Column(name="block")
	public double block;
	
	@Column(name="point")
	public double point;
	
	@Column(name="per")
	public double per;
	
	@Column(name="first_count")
	public int first_count;
	
	@Column(name="SECOND_COUNT")
	public int second_count;
	
	@Column(name="games")
	public int games;
	
	@Column(name="game_date")
	public Date game_date;
	
	public static PlayerTeamSeasonScore lastOne(){
		return find(" order by id desc ").first();
	}
	
	public static List<PlayerTeamSeasonScore> getPlayers(long teamId,Date gameDate){
		return find(" team_id = ? and game_date = ? order by first_count desc,second_count desc",teamId,gameDate).fetch();
	}
	
	
	public static void callPrcPTSS(String seasonBeginDate,String beginDate,String endDate,Integer season){
		Query query = JPA.em().createNativeQuery("{call prc_player_team_season_score(?,?,?,?)}");
		query.setParameter(1, beginDate);
		query.setParameter(2, endDate);
		query.setParameter(3, seasonBeginDate);
		query.setParameter(4, season);
		query.executeUpdate();
	}
	
	
	
}
