package models.game.supermodel;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

@MappedSuperclass
public class GamePlayerInfoModel extends GenericModel {


	
	@Column(name="game_id")
	public long game_id;
	
	@Column(name="team_id")
	public long team_id;
	
	@Column(name="player_id")
	public long player_id;
	
	@Column(name="player_name")
	public String player_name;
	
	@Column(name="player_position")
	public String player_position;
	
	@Column(name="time")
	public long time;
	
	@Column(name="shoot_all")
	public int shoot_all;
	
	@Column(name="shoot_hit")
	public int shoot_hit;
	
	@Column(name="three_all")
	public int three_all;
	
	@Column(name="three_hit")
	public int three_hit;
	
	@Column(name="free_all")
	public int free_all;
	
	@Column(name="free_hit")
	public int free_hit;
	
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
	
	@Column(name="turn_out")
	public int turn_out;
	
	@Column(name="block")
	public int block;
	
	@Column(name="point")
	public int point;
	
	@Column(name="per")
	public int per;
	
	@Column(name="first")
	public int first;
	
	@Column(name="BE_BLOCKED")
	public int be_blocked;	
	
	@Column(name="game_date")
	public Date game_date;
	
	@Column(name="comments")
	public String comments;	
		
	
}
