package models.team;

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

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

import models.game.GameInfo;

@Entity
@Table(name="team_starters_game")
public class TeamStartersGame extends GenericModel{

	@Id
	@SequenceGenerator(name="SEQ_STARTERS",sequenceName="SEQ_STARTERS",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_STARTERS")
	@Column(name="id")
	public long id;
	
	@ManyToOne
	@JoinColumn(name = "GAME_ID")
	public GameInfo gameInfo;
	
	@ManyToOne
	@JoinColumn(name = "STARTERS_ID")
	public TeamStarters teamStarters;
	


	public static List<TeamStartersGame> getStartersGames(List gameIds){
		
		return find(" gameInfo.id in :ids").bind("ids", gameIds).fetch();
	}
}
