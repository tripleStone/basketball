/**
 * 每节比分
 */
package models.game;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name="game_quarter_score")
public class GameQuarterScore extends GenericModel {
	
	@Id
	@SequenceGenerator(name="SEQ_GAME_QUARTER",sequenceName="SEQ_GAME_QUARTER",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_GAME_QUARTER")
	@Column(name="id")
	public long id;
	
	@Column(name="TEAM_ID")
	public long team_id;	

	@Column(name="TEAM_NAME")
	public String team_name;	
	
	@Column(name="GAME_ID")
	public long game_id;		
	
	@Column(name="SCORE")
	public int score;		
	
	@Column(name="QUARTER")
	public int quarter;	
	
	public static List<GameQuarterScore> getQuarterScores(long gameId,long teamId){
		return find(" game_id = ? and team_id = ? ",gameId,teamId).fetch();
	}
	
	public static void  delByGame(long game_id){
		delete(" game_id = ? ", game_id);
	}		
}
