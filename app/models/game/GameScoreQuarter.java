package models.game;
/**
 * 每节比分明细
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import models.game.supermodel.GameInfoStatisticModel;

@Entity
@Table(name="GAME_SCORE_QUARTER")
public class GameScoreQuarter extends GameInfoStatisticModel {
	@Id
	@SequenceGenerator(name="SEQ_GAME_EACH_QUARTER",sequenceName="SEQ_GAME_EACH_QUARTER",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_GAME_EACH_QUARTER")
	@Column(name="ID")
	public long id;	
	
	@Column(name="quarter")
	public String quarter;
	
	public GameScoreQuarter(){
		
	}
	
	public GameScoreQuarter(GameInfoStatic s ,String quarter){
		this.assist = s.assist;
		this.block = s.block;
		this.fast_break_points = s.block;
		this.first_score = s.first_score;
		this.free_all = s.free_all;
		this.foul = s.is_host;
		this.free_hit = s.free_hit;
		this.free_percent = s.free_percent;
		this.game_date = s.game_date;
		this.game_id = s.game_id;
		this.is_host = s.is_host;
		this.is_win = s.is_win;
		this.paint_points = s.paint_points;
		this.quarter = quarter;
		this.rebound_after = s.rebound_after;
		this.rebound_all = s.rebound_all;
		this.rebound_front = s.rebound_front;
		this.score = s.score;
		this.shoot_all = s.shoot_all;
		this.shoot_hit = s.shoot_hit;
		this.shoot_percent = s.shoot_percent;
		this.stolen = s.stolen;
		this.sub_score = s.sub_score;
		this.team_id = s.team_id;
		this.team_name = s.team_name;
		this.team_offs = s.team_offs;
		this.team_points_off = s.team_points_off;
		this.three_all = s.three_all;
		this.three_hit = s.three_hit;
		this.three_percent = s.three_percent;
		this.turn_out = s.turn_out;	
	}
	public static Long countQuarter(Long gameId,String quarter){
		return count(" game_id = ? and quarter = ? ",gameId,quarter);
	}
	
}
