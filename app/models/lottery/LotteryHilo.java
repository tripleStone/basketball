package models.lottery;

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
@Table(name="LOTTERY_HILO")
public class LotteryHilo extends GenericModel  {
	
	@Id
	@SequenceGenerator(name="SEQ_LOTTERY",sequenceName="SEQ_LOTTERY",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_LOTTERY")
	@Column(name="id")
	public long id;
	
	@Column(name="game_id")
	public long game_id;	
	
	@Column(name="host_id")
	public long host_id;
	
	@Column(name="away_id")
	public long away_id;	
	
	@Column(name="host_name")
	public String host_name;
	
	@Column(name="away_name")
	public String away_name;
	
	@Column(name="game_date")
	public Date game_date;
	
	@Column(name="hilo")
	public double hilo;
	
	@Column(name="GREATER_THAN")
	public double greater_than;

	@Column(name="LESS_THAN")
	public double less_than;
	
	@Column(name="hit")
	public String hit;
	
	@Column(name="del_marker")
	public int del_marker;
	
	@Column(name="exp")
	public String exp;
	
	@Column(name="SUM_SCORE")
	public long sumScore;

	@Column(name="week_day")
	public int weekDay;
	
	@Column(name="CREATE_DATE")
	public Date createDate;	
	
	@Column(name="Lottery")
	public double lottery;
	
	@Column(name="season")
	public int season;
	
	public LotteryHilo(){}
	
	
	public LotteryHilo(long gameId,long awayId,long homeId,String awayName,String homeName,
			Date gameDate,Double hilo,Double lessThan,Double greaterThan,int del,int weekDay,
			Date createDate,int season){
		this.game_id = gameId;
		this.away_id = awayId;
		this.host_id = homeId;
		this.away_name = awayName;
		this.host_name = homeName;
		this.game_date = gameDate;
		this.hilo = hilo;
		this.less_than = lessThan;
		this.greater_than = greaterThan;
		this.del_marker = del;
		this.weekDay = weekDay;
		this.createDate = createDate;
		this.season = season;
	}
	
	public static LotteryHilo getLottery(long gameId){
		return find(" game_id = ? and del_marker = 0 order by game_id desc ",gameId).first();
	}	
	
	public static List<LotteryHilo> getLotteries(long gameId){
		return find(" game_id = ? order by createDate desc  ",gameId).fetch();
	}
	
	public static List<LotteryHilo>  getLotteries(List<Long> gameIds){
		return find(" game_id in :gameIds and del_marker = 0 order by game_id desc ").bind("gameIds", gameIds).fetch();
	}
	
	public static List<LotteryHilo> getHomeHilos(long teamId,Date gameDate,int page,int pageSize){
		return find(" host_id = ? and del_marker = 0 and game_date <= ? order by game_date desc",teamId,gameDate).from(page).fetch(pageSize);
	}
	
	public static List<LotteryHilo> getAwayHilos(long teamId,Date gameDate,int page,int pageSize){
		return find(" away_id = ? and del_marker = 0 and game_date <= ? order by game_date desc",teamId,gameDate)
				.from(page).fetch(pageSize);
	}
	
	public static List<LotteryHilo>  gethilos(Date gameDate){
		return find("select a from LotteryHilo a,GameInfo b where a.game_id = b.id and " +
				"a.game_date = ? and a.del_marker = 0  order by b.usa_play_date asc,b.play_time asc ",gameDate).fetch();
	}
	
	
	public static List<LotteryHilo>  getMatcherHilos(long homeId,long awayId,Date gameDate){
		return find(" ((away_id = ? and host_id = ? )  or  (away_id = ? and host_id = ? )) and game_date <=  ? and del_marker = 0   order by game_date desc  "
				,awayId,homeId,homeId,awayId,gameDate).fetch();
	}
	
	public static List<LotteryHilo> getHilos(long teamId,Date gameDate,int page, int pageSize){
		return find(" (away_id = ? or host_id = ?) and game_date <  ? and del_marker = 0  order by game_date desc ",teamId,teamId,gameDate).from(page -1 ).fetch(pageSize);		
	}

	
	public static List<LotteryHilo> getGreaterHilos(long teamId,double hilo, int pageSize){
		return find(" (away_id = ? or host_id = ?) and del_marker = 0 and hilo > ?  order by game_date desc ",teamId,teamId,hilo).fetch(pageSize);		
	}
	
	public static List<LotteryHilo> getLesserHilos(long teamId,double hilo, int pageSize){
		return find(" (away_id = ? or host_id = ?) and del_marker = 0 and hilo < ?  order by game_date desc ",teamId,teamId,hilo).fetch(pageSize);		
	}
	
	public static List<LotteryHilo> getEqualHilos(long teamId,double hilo, int pageSize){
		return find(" (away_id = ? or host_id = ?) and del_marker = 0 and hilo = ?  order by game_date desc ",teamId,teamId,hilo).fetch(pageSize);		
	}
	
	public static List<LotteryHilo> getTeamHilos(long teamId,String hit ,int season){
		return find(" (away_id = ? or host_id = ?) and season = ? and hit = ? and del_marker = 0 order by game_date desc ",teamId,teamId,season,hit).fetch();
	}
	
	public static List<LotteryHilo> getSameHilos(long teamId,Date gameDate,
			int daies,int season,String type){
		StringBuffer sql = new StringBuffer("SELECT b FROM GameInfo a,LotteryHilo b where a.id = b.game_id and b.del_marker = 0 ");
		if (type.equals("home")){
			sql.append(" and a.home_id = ? and a.h_btb = ? " )
			.append(" and b.season = ? and a.play_date < ? order by  a.play_date desc");
			Query query = JPA.em().createQuery(sql.toString(),LotteryHilo.class);
			query.setParameter(1, teamId);
			query.setParameter(2, daies);
			query.setParameter(3, season);
			query.setParameter(4, gameDate);
			return query.getResultList();
		}else if(type.equals("away")){
			sql.append(" and a.guest_id = ? and a.g_btb = ? " )
			.append(" and b.season = ? and a.play_date < ? order by  a.play_date desc");
			Query query = JPA.em().createQuery(sql.toString(),LotteryHilo.class);
			query.setParameter(1, teamId);
			query.setParameter(2, daies);
			query.setParameter(3, season);
			query.setParameter(4, gameDate);
			return query.getResultList();
		}else{
			sql.append(" and ((a.guest_id = ? and a.g_btb = ?) " )
			.append(" or (a.guest_id = ? and a.g_btb = ? ) )")
			.append(" and b.season = ? and a.play_date < ? order by  a.play_date desc");
			Query query = JPA.em().createQuery(sql.toString(),LotteryHilo.class);
			query.setParameter(1, teamId);
			query.setParameter(2, daies);
			query.setParameter(3, teamId);
			query.setParameter(4, daies);
			query.setParameter(5, season);
			query.setParameter(4, gameDate);
			return query.getResultList();
		}
	}
	
	
	public static List<LotteryHilo> getSameHilos(long teamId,Date gameDate,
			double lottery,int season,String type){
		if (type.equals("home")){
			return find(" home_id = ? and hilo = ? and play_date < ? and" +
					" season = ? order by play_date desc ",teamId,lottery,gameDate,season ).fetch();
		}else if(type.equals("away")){
			return find(" guest_id = ? and hilo = ? and play_date < ? and" +
					" season = ? order by play_date desc ",teamId,lottery,gameDate,season ).fetch();
		}else{
			return find(" (guest_id = ? or home_id =?) and hilo = ? and play_date < ? and" +
					" season = ? order by play_date desc ",teamId,teamId,lottery,gameDate,season ).fetch();
		}
		
	}
	
	public static Long countHit(Long teamId,Date gameDate,
			String hit,int season){
		return count(" (host_id = ? or away_id =?) and hit = ? and season = ? and game_date < ? and del_marker = 0 ",teamId,teamId,hit,season,gameDate);
	}
	
	public static List<Object[]> getHiloRank(String area,int season,
			Date gameDate,String homeType,String columns,String orderBy ){
		StringBuffer sql = new StringBuffer("select ti1.id,ti1.team_name,"+columns+" from (select  ")
		.append("count(t.id) greatercount,");
		
		StringBuffer sql1 = new StringBuffer("ti.id teamid ");
		if (homeType.equals("away"))
			sql1.append(" from lottery_hilo t,team_info ti where t.away_id = ti.id ");
		else if(homeType.equals("home"))
			sql1.append(" from lottery_hilo t,team_info ti where t.host_id = ti.id ");
		else
			sql1.append(" from lottery_hilo t,team_info ti where (t.host_id = ti.id  or t.away_id = ti.id) ");
		sql1.append(" and t.hit = ? and t.season = ? and t.game_date < ? and t.del_marker = 0  group by ti.id)  ");
				
		sql.append(sql1).append("res1, (select count(t.id) lesscount, ").append(sql1)
		.append(" res2,team_info ti1 where res1.teamid = res2.teamid ")
		.append(" and ti1.id = res2.teamid and ti1.bigarea= ?  order by "+orderBy+" desc ");
		
		Query query = JPA.em().createNativeQuery(sql.toString());
		query.setParameter(1, "greater");
		query.setParameter(2, season);
		query.setParameter(3, gameDate);
		query.setParameter(4, "less");
		query.setParameter(5, season);
		query.setParameter(6, gameDate);
		query.setParameter(7, area);
		
		return query.getResultList();
	}
	
	
	public static List<LotteryHilo> getHilos(String sql,Object... params){
		return find(sql,params).fetch();
	}
	
	public static void setHilos(long gameId,String hit,long score){
		Query query = JPA.em().createNativeQuery("update LOTTERY_HILO t set t.SUM_SCORE = ?,hit = ? " +
				"  where t.game_id = ? ");
		query.setParameter(1, score);
		query.setParameter(2, hit);
		query.setParameter(3, gameId);
		query.executeUpdate();
	}
	
	public static void delHilos(Date beginDate,Date endDate){
		Query query = JPA.em().createNativeQuery("delete from  LOTTERY_HILO t where t.game_date >= ? and t.game_date < ? + 1 ");
		query.setParameter(1, beginDate);
		query.setParameter(2, endDate);
		query.executeUpdate();
	}
	
	

}
