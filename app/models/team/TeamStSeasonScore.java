package models.team;

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
@Table(name="TEAM_ST_SEASON_SCORE")
public class TeamStSeasonScore extends GenericModel {

	@Id
	@SequenceGenerator(name="SEQ_TEAM_STATISTIC",sequenceName="SEQ_TEAM_STATISTIC",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_TEAM_STATISTIC")
	@Column(name="id")
	public long id; 
	
	@Column(name="team_id")
	public long teamId;
	
	@Column(name="shoot_all")
	public int shootAll;
	
	@Column(name="shoot_hit")
	public int shootHit;
	
	@Column(name="shoot_percent")
	public double shootPercent;
	
	@Column(name="three_all")
	public int threeAll;
	
	@Column(name="three_hit")
	public int threeHit;
	
	@Column(name="three_percent")
	public double threePercent;
	
	@Column(name="free_all")
	public int freeAll;
	
	@Column(name="free_hit")
	public int freeHit;

	@Column(name="free_percent")
	public double freePercent;
	
	@Column(name="rebound_front")
	public int reboundFront;
	
	@Column(name="rebound_after")
	public int reboundAfter;
	
	@Column(name="rebound_all")
	public int reboundAll;	
	
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
	
	@Column(name="score")
	public double score;	
	
	@Column(name="SUBSTITUTE_SCORE")
	public double sub_score;		
	
	@Column(name="FIRST_SCORE")
	public double first_score;
	
	@Column(name="FAST_BREAK_POINTS")
	public double fast_break_points;
	
	@Column(name="PAINT_POINTS")
	public double paint_points;
	
	@Column(name="TEAM_OFFS")
	public double team_offs;
	
	@Column(name="TEAM_POINTS_OFF")
	public double team_points_off;
	
	@Column(name="game_date")
	public Date gameDate;	

	@Column(name="STAT_TYPE")
	public int statType;	
	
	@Column(name="season")
	public int season;	
	
	@Column(name="GAME_TYPE")
	public int game_type;
	
	public static Object lastDate(){
		Query query = JPA.em().createNativeQuery("select to_char(max(t.game_date),'yyyy-mm-dd')  from TEAM_ST_SEASON_SCORE t ");	
		return query.getSingleResult();
	}
	
	public static List<TeamStSeasonScore> getScores(long teamId,Date gamedate){
		return find(" teamId = ?  and gameDate = ? order by gameDate desc,statType asc",teamId,gamedate).fetch();
	}	
	
	public static List<TeamStSeasonScore> getScores(long teamId,int gameType,Date gamedate){
		return find(" teamId = ? and statType = ?   and gameDate = ? order by gameDate desc,statType asc",teamId,gameType,gamedate).fetch();
	}
		
	public static List<TeamStSeasonScore> getScores(long teamId,Date gamedate,int page,int pageSize){
		return find(" teamId = ?  and gameDate <=? order by gameDate desc,statType asc",teamId,gamedate).from(page - 1).fetch(pageSize);
	}	
	
	public static List<TeamStSeasonScore> getScores(long teamId,int gameType,Date gamedate,int page,int pageSize){
		return find(" teamId = ? and gameType = ? and gameDate <=? order by gameDate desc,statType asc",teamId,gameType,gamedate).from(page - 1).fetch(pageSize);
	}
	
	public static void callPrcTSSS(String seasonBeginDate,String beginDate,String endDate,Integer season){
		Query query = JPA.em().createNativeQuery("{call prc_team_ST_season_score(?,?,?,?)}");		
		query.setParameter(1, beginDate);
		query.setParameter(2, endDate);
		query.setParameter(3, seasonBeginDate);
		query.setParameter(4, season);
//		query.setParameter(5, gameType);
		query.executeUpdate();
	}
	
	public static List<Object[]> getRank(String range,Date dateLine,String columns,String orderBy){
		StringBuffer sql = new StringBuffer("select tssb.team_id,ti.team_name," + columns + " from")
		.append(" TEAM_ST_SEASON_score tssb,team_info ti where tssb.id in (select res.statisticId from ( ")
		.append(" (select max(t.id) statisticId ,t.team_id from TEAM_ST_SEASON_score t   ")
		.append(" ,team_info t1 where t.game_date < ?  " )
		.append(" and t.team_id = t1.id");
		if (!range.equalsIgnoreCase("all"))
			sql.append(" and t1.bigarea = ? ");
		sql.append(" and t.team_id = t1.id  group by t.team_id ) res)   ) and ti.id = tssb.team_id order by " + orderBy + " desc");
		
		System.out.println(sql.toString());
		Query query = JPA.em().createNativeQuery(sql.toString());
		query.setParameter(1, dateLine);
		if (!range.equalsIgnoreCase("all"))
			query.setParameter(2, range);
		return query.getResultList();		
	}	
	
}
