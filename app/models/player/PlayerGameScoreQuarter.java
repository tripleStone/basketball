package models.player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import models.game.GamePlayerInfo;
import models.game.supermodel.GamePlayerInfoModel;

@Entity
@Table(name="PLAYER_GAMESCORE_QUARTER")
public class PlayerGameScoreQuarter extends GamePlayerInfoModel {

	@Id
	@SequenceGenerator(name="SEQ_GAME_EACH_QUARTER",sequenceName="SEQ_GAME_EACH_QUARTER",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_GAME_EACH_QUARTER")
	@Column(name="id")
	public long id;
	
	@Column(name="quarter")
	public String quarter;
	
	
	public PlayerGameScoreQuarter(GamePlayerInfo info,String quarter){
		this.quarter = quarter;
		this.assist = info.assist;
		this.be_blocked = info.be_blocked;
		this.block = info.block;
		this.first = info.first;
		this.foul = info.foul;
		this.free_all = info.free_all;
		this.free_hit = info.free_hit;
		this.game_date = info.game_date;
		this.game_id = info.game_id;
		this.id = info.id;
		this.per = info.per;
		this.player_position = info.player_position;
		this.player_name = info.player_name;
		this.point = info.point;
		this.player_id = info.player_id;
		this.rebound_after = info.rebound_after;
		this.rebound_all = info.rebound_all;
		this.rebound_front = info.rebound_front;
		this.shoot_all = info.shoot_all;
		this.shoot_hit = info.shoot_hit;
		this.team_id = info.team_id;
		this.three_all = info.three_all;
		this.three_hit = info.three_hit;
		this.time = info.time;
		this.turn_out = info.turn_out;
		this.stolen = info.stolen;		
	}
	
	public static Long countQuarter(Long gameId,String quarter){
		return count(" game_id = ? and quarter = ? ",gameId,quarter);
	}
}
