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
@Table(name="LOTTERY_HDC")
public class LotteryHandicap  extends GenericModel  {
	
	@Id
	@SequenceGenerator(name="SEQ_LOTTERY",sequenceName="SEQ_LOTTERY",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_LOTTERY")
	@Column(name="id")
	public long id;
	
	@Column(name="game_id")
	public long game_id;	
	
	@Column(name="host_name")
	public String host_name;
	
	@Column(name="away_name")
	public String away_name;
	
	@Column(name="HOME_SCORE")
	public int homeScore;

	@Column(name="AWAY_SCORE")
	public int awayScore;
	
	@Column(name="host_id")
	public long host_id;
	
	@Column(name="away_id")
	public long away_id;
	
	@Column(name="game_date")
	public Date game_date;
	
	@Column(name="HANDICAP")
	public double handicap;
	
	@Column(name="REAL_HDC")
	public double realHdc;
	
	@Column(name="win")
	public double win;

	@Column(name="lose")
	public double lose;
	
	@Column(name="hit")
	public String hit;
	
	@Column(name="del_marker")
	public int del_marker;
	
	@Column(name="exp")
	public String exp;
	
	@Column(name="week_day")
	public int weekDay;
	
	@Column(name="CREATE_DATE")
	public Date createDate;
	
	@Column(name="season")
	public int season;
	
	@Column(name="lottery")
	public double lottery;
	
	public LotteryHandicap(){
		
	}
	
	public LotteryHandicap(Long gameId,String homeName,String awayName,
			Long homeId,Long awayId,Date gameDate,double handicap,double win,double lose,
			int weekDay,Date createDate,int season){
		this.game_id  = gameId;
		this.host_name = homeName;
		this.away_name = awayName;
		this.host_id = homeId;
		this.away_id = awayId;
		this.game_date = gameDate;
		this.handicap = handicap;
		this.win = win;
		this.lose = lose;
		this.weekDay = weekDay;
		this.createDate = createDate;
		this.season = season;
		
	}

	
	public static LotteryHandicap getLottery(long game_id){
		return find(" game_id = ? and del_marker = 0 order by game_id desc ",game_id).first();
	}
	
	public static List<LotteryHandicap> getLotteryHis(long game_id){
		return find(" game_id = ?  order by id desc",game_id).fetch();
	}	
	
	public static List<LotteryHandicap> getLotteries(Date game_date){
		return find(" game_date = ? and del_marker = 0  order by game_id desc ",game_date).fetch();
	}
	
	public static List<LotteryHandicap> getLotteries(List<Long> gameIds){
		return find(" game_id in :gameIds and del_marker = 0  order by game_id desc ").bind("gameIds",gameIds).fetch();
	}
	
	public static List<LotteryHandicap> getHmHndp(long teamId,Date gamedate,int page,int pageSize){
		return find(" host_id = ? and del_marker = 0  and game_date <= ? order by game_date desc ",teamId,gamedate).from(page -1).fetch(pageSize);
	}	

	public static List<LotteryHandicap> getAyHndp(long teamId,Date gamedate,int page,int pageSize){
		return find(" away_id = ? and del_marker = 0  and game_date <= ? order by game_date desc ",teamId,gamedate).from(page -1).fetch(pageSize);
	}	
	
	public static List<LotteryHandicap> getLotteries(long homeId,long awayId,Date gameDate){
		return find(" ((away_id = ? and host_id = ? )  or  (away_id = ? and host_id = ? ))  and game_date <= ? and del_marker = 0   order by game_date desc  "
				,awayId,homeId,homeId,awayId,gameDate).fetch();
	}
	
	public static List<LotteryHandicap> getLotteries(long teamId,int season ,Date gameDate){
		return find("(away_id = ? or host_id = ? ) and season = ? and game_date < ? and del_marker = 0 " +
				" order by game_date desc ",teamId,teamId,season,gameDate).fetch();
	}
	
	public static List<LotteryHandicap> getLotteries(long teamId,int season ){
		return find("(away_id = ? or host_id = ? ) and season = ? and del_marker = 0 " +
				" order by game_date desc ",teamId,teamId,season).fetch();
	}
	
	public static void setHc(long gameId,int handicap,int homeScore,int awayScore,String hit){
		Query query = JPA.em().createNativeQuery("update LOTTERY_HDC t set t.REAL_HDC = ?,hit = ?,home_score = ?,AWAY_SCORE = ?" +
				" where game_id = ? ");
		query.setParameter(1, handicap);
		query.setParameter(2, hit);
		query.setParameter(3, homeScore);
		query.setParameter(4, awayScore);
		query.setParameter(5, gameId);
		query.executeUpdate();
	}
	
	public static void del(Date beginDate,Date endDate){
		Query query = JPA.em().createNativeQuery("delete from  LOTTERY_HDC t where t.game_date >= ? and t.game_date < ? + 1 ");
		query.setParameter(1, beginDate);
		query.setParameter(2, endDate);
		query.executeUpdate();
	}
	
}
