package models.game;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import models.player.PlayerInfo;
import models.team.TeamInfo;

import play.db.jpa.GenericModel;

@Entity
@Table(name="GAME_PLAYBYPLAY")
public class GamePlayByPlay extends GenericModel {

	@Id
	@SequenceGenerator(name="SEQ_PLAYBYPLAY",sequenceName="SEQ_PLAYBYPLAY",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_PLAYBYPLAY")
	@Column(name="id")
	public long id;
	
	@ManyToOne
	@JoinColumn(name="GAME_ID")
	public GameInfo game;
		
	@Column(name="team_id")
	public long team_id;
	
	@Column(name="time")
	public String time;
	
	@Column(name="second")
	public int second; 
	
	@Column(name="QUARTER")
	public int quarter;
	
	@Column(name="GET_POINT")
	public int get_point;
	
	@ManyToOne
	@JoinColumn(name="player_id")
	public PlayerInfo player;
	
	@Column(name="SCORE")
	public String score;
	
	@ManyToOne
	@JoinColumn(name="ASSISTER")
	public PlayerInfo assister;
	
	@Column(name="content")
	public String content;
	
	@Column(name="SHOOT_DISTANCE")
	public int shoot_distance;	
	
	@Column(name="TEAM_POINT")
	public int team_point;		
	
	@Transient
	public String playerName;
	
	@Transient
	public String assisterName;
	
	
}
