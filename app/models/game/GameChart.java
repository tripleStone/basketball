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
@Table(name="Game_CHART")
public class GameChart extends GenericModel  {
	
	@Id
	@SequenceGenerator(name="SEQ_GAME_CHART",sequenceName="SEQ_GAME_CHART",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_GAME_CHART")
	@Column(name="id")
	public long id;
	
	
	@Column(name="game_id")
	public long gameId;
	
	
	@Column(name="CHART_HTML")
	public String chartHtml;
	
	@Column(name="PIECES")
	public int pieces;

	public static List<GameChart> getGameCharts(long gameId){
		return find(" gameId = ? order by pieces",gameId).fetch();
	}
	
}
