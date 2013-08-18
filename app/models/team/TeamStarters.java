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

import models.game.GameInfo;
import models.player.PlayerInfo;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

@Entity
@Table(name="team_starters")
public class TeamStarters extends GenericModel{
	
	@Id
	@SequenceGenerator(name="SEQ_STARTERS",sequenceName="SEQ_STARTERS",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_STARTERS")
	@Column(name="id")
	public long id;
	
	@ManyToOne
	@JoinColumn(name = "player1_id")
	public PlayerInfo player1;
	
	@ManyToOne
	@JoinColumn(name = "player2_id")
	public PlayerInfo player2;
	
	@ManyToOne
	@JoinColumn(name = "player3_id")
	public PlayerInfo player3;
	
	@ManyToOne
	@JoinColumn(name = "player4_id")
	public PlayerInfo player4;
	
	@ManyToOne
	@JoinColumn(name = "player5_id")
	public PlayerInfo player5;
	
	@Column(name="SERIAL_KEY")
	public String serial_key;
	
	@Column(name="USE_COUNT")
	public long use_count;	
	
	@Column(name="team_id")
	public long team_id;
	
	@Column(name="CREATE_DATE")
	public Date create_date;
	
	public static void callPrc(String seasonBeginDate,String beginDate,String endDate,Integer season){
		Query query = JPA.em().createNativeQuery("{call PRC_INSERT_TEAM_STATERS(?,?,?,?)}");		
		query.setParameter(1, beginDate);
		query.setParameter(2, endDate);
		query.setParameter(3, seasonBeginDate);
		query.setParameter(4, season);
		query.executeUpdate();
	}
	
	public static List<TeamStarters> getStarters(Long teamId,Date beginDate,Date endDate){
		StringBuffer sql = new StringBuffer(" select ts from TeamStarters ts where ts.id in " +
				" (SELECT distinct(tsg.teamStarters.id) from TeamStartersGame tsg  " +
				" where tsg.gameInfo.play_date >= ? and tsg.gameInfo.play_date < ? " +
				" and tsg.teamStarters.team_id = ? ) order by ts.use_count desc" ); 
		
//		Query query = JPA.em().createNamedQuery(sql.toString());
//		query.setParameter(1, beginDate);
//		query.setParameter(2,endDate);
//		query.setParameter(3, teamId);
//		return query.getResultList();
		
		return find(sql.toString(),beginDate,endDate,teamId).fetch();
	}
	


}
