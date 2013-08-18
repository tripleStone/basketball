package models.team;

import java.math.BigDecimal;
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
@Table(name="TEAM_ST_SEASON_BASE")
public class TeamStSeasonBase extends GenericModel  {
	
	@Id
	@SequenceGenerator(name="SEQ_STATISTIC",sequenceName="SEQ_STATISTIC",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_STATISTIC")
	@Column(name="id")
	public long id;
	
	@Column(name="team_id")
	public long team_id;
	
	@Column(name="team_name")
	public String team_name;
	
	@Column(name="win")
	public int win;
	
	@Column(name="loss")
	public int loss;
	
	@Column(name="games")
	public int games;
	
	@Column(name="home_win")
	public int home_win;
	
	@Column(name="home_all")
	public int home_all;
	
	@Column(name="guest_win")
	public int guest_win;
	
	@Column(name="guest_all")
	public int guest_all;	
	
	@Column(name="season")
	public int season;	
	
	@Column(name="SEASON_PERCENT")
	public double season_percent;	
	
	@Column(name="GUEST_PERCENT")
	public double guest_percent;		
	
	@Column(name="HOME_PERCENT")
	public double home_percent;	
	
	@Column(name="HOME_SCORE")
	public double home_score;		
	
	@Column(name="HOME_LOST_SCORE")
	public double home_lost_score;	
	
	@Column(name="HOME_NET_SCORE")
	public double home_net_score;		
	
	@Column(name="GUEST_SCORE")
	public double guest_score;		
	
	@Column(name="GUEST_LOST_SCORE")
	public double guest_lost_score;	
	
	@Column(name="GUEST_NET_SCORE")
	public double guest_net_score;		

	@Column(name="Season_SCORE")
	public double season_score;		
	
	@Column(name="Season_LOST_SCORE")
	public double season_lost_score;	
	
	@Column(name="Season_NET_SCORE")
	public double season_net_score;		
	
	@Column(name="deadline_date")
	public Date deadline_date;
	
	
	@Column(name="STREAK")
	public int streak;
	
	@Column(name="STREAK_home")
	public int streakHome;
	
	@Column(name="STREAK_AWAY")
	public int streakAway;
	
	@Column(name="GAME_TYPE")
	public int game_type;
	
	public static double sum(double d1,double d2){
		BigDecimal b = new BigDecimal(d1+d2);  	
		return b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static TeamStSeasonBase lastOne(){
		return find( " order by id desc ").first();
	}
	
	public static TeamStSeasonBase getInfo(long team_id,int season){
		return find( " team_id = ? and season = ? order by deadline_date desc ", team_id,season).first();
	}
	
	public static TeamStSeasonBase getStatistic(long team_id,int season,Date gameDate){
		return find( " team_id = ? and season = ? and deadline_date <= ?  order by deadline_date desc", team_id,season,gameDate).first();
	}
	
	public static TeamStSeasonBase getStatistic(long team_id,Date gameDate){
		return find( " team_id = ? and deadline_date <= ?  order by deadline_date desc", team_id,gameDate).first();
	}
	
	public static List<TeamStSeasonBase> getStatistics(Long team_id,Date gameDate,Integer page,Integer pageSize){
		return find( " team_id = ? and deadline_date <= ?  order by deadline_date desc", team_id,gameDate).from(page -1).fetch(pageSize);
	}
	
	public static void  delByGame(long team_id,Date gameDate){
		delete(" team_id = ? and deadline_date = ?  ", team_id,gameDate);
	}	

	public static List<Object[]> getRank(String range,Date dateLine,String columns,String orderBy){
		StringBuffer sql = new StringBuffer("select tssb.team_id,tssb.team_name," + columns + " from")
		.append(" TEAM_ST_SEASON_BASE tssb where id in (select res.statisticId from (")
		.append(" (select max(t.id) statisticId ,t.team_id from TEAM_ST_SEASON_BASE t  ")
		.append(" ,team_info t1 where t.deadline_date < ? " )
		.append(" and t.team_id = t1.id");
		if (!range.equalsIgnoreCase("all"))
			sql.append(" and t1.bigarea = ? ");
		sql.append(" and t.team_id = t1.id  group by t.team_id ) res)) order by " + orderBy + " desc");
		
		Query query = JPA.em().createNativeQuery(sql.toString());
		query.setParameter(1, dateLine);
		if (!range.equalsIgnoreCase("all"))
			query.setParameter(2, range);
		return query.getResultList();		
	}

	
	
	public static void callPrcTss(String seasonBeginDate,String beginDate,String endDate,Integer season,Integer gameType){
		Query query = JPA.em().createNativeQuery("{call insert_team_statistics(?,?,?,?,?)}");
		query.setParameter(1, seasonBeginDate);
		query.setParameter(2, beginDate);
		query.setParameter(3, endDate);
		query.setParameter(4, season);
		query.setParameter(5, gameType);
		query.executeUpdate();
	}
	
	

}
