package models.game.supermodel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import play.db.jpa.GenericModel;

@MappedSuperclass
public class GameInfoStatisticModel extends GenericModel {

	
	@Column(name="game_id")
	public long game_id;
	
	@Column(name="team_id")
	public long team_id;
	
	@Column(name="shoot_all")
	public int shoot_all;
	
	@Column(name="shoot_hit")
	public int shoot_hit;
	
	@Column(name="shoot_percent")
	public double shoot_percent;
	
	@Column(name="three_all")
	public int three_all;
	
	@Column(name="three_hit")
	public int three_hit;
	
	@Column(name="three_percent")
	public double three_percent;
	
	@Column(name="free_all")
	public int free_all;
	
	@Column(name="free_hit")
	public int free_hit;

	@Column(name="free_percent")
	public double free_percent;
	
	@Column(name="rebound_front")
	public int rebound_front;
	
	@Column(name="rebound_after")
	public int rebound_after;
	
	@Column(name="rebound_all")
	public int rebound_all;	
	
	@Column(name="assist")
	public int assist;		
	
	@Column(name="foul")
	public int foul;	
	
	@Column(name="stolen")
	public int stolen;		
	
	@Column(name="game_date")
	public Date game_date;	
	
	@Column(name="turn_out")
	public int turn_out;	
	
	@Column(name="block")
	public int block;		

	@Column(name="team_name")
	public String team_name;		
	
	@Column(name="is_host")
	public int is_host;		
	
	@Column(name="score")
	public int score;	
	
	@Column(name="is_win")
	public int is_win;	
	
	@Column(name="SUBSTITUTE_SCORE")
	public int sub_score;		
	
	@Column(name="FIRST_SCORE")
	public int first_score;
	
	@Column(name="FAST_BREAK_POINTS")
	public int fast_break_points;
	
	@Column(name="PAINT_POINTS")
	public int paint_points;
	
	@Column(name="TEAM_OFFS")
	public int team_offs;
	
	@Column(name="TEAM_POINTS_OFF")
	public int team_points_off;
}
