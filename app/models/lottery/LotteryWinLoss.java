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
@Table(name="LOTTERY_WINLOSE")
public class LotteryWinLoss extends GenericModel{
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
	
	@Column(name="HOME_WIN")
	public double home_win;

	@Column(name="GUEST_WIN")
	public double guest_win;
	
	@Column(name="hit")
	public Integer hit;
	
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
	
	public LotteryWinLoss(){
		
	}
	
	public LotteryWinLoss(long gameId,long awayId,long homeId,String awayName,
			String homeName,Date gameDate,Double guestWin,Double homeWin,
			int weekDay,Date createDate,int season){
		this.game_id = gameId;
		this.away_id = awayId;
		this.host_id = homeId;
		this.away_name  = awayName;
		this.host_name = homeName;
		this.game_date = gameDate;
		this.guest_win = guestWin;
		this.home_win = homeWin;
		this.weekDay = weekDay;
		this.createDate = createDate;
		this.season = season;
	}
	
	public static LotteryWinLoss getLottery(long gameId){
		return find(" game_id = ? and del_marker = 0 order by game_id desc ",gameId).first();
	}
	
	public static List<LotteryWinLoss> getLotteries(long gameId){
		return find(" game_id = ? order by id desc  ",gameId).fetch();
	}
	
	public static List<LotteryWinLoss> getWLs(long teamId,Date gameDate,String type,int page,int pageSize){
		if (type.equals("home"))
			return find( " host_id = ? and game_date <= ? and del_marker = 0 order by game_date desc ",teamId,gameDate).from(page -1).fetch(pageSize);
		else 
			return find( " away_id = ? and game_date <= ? and del_marker = 0 order by game_date desc ",teamId,gameDate).from(page -1).fetch(pageSize);
	}
	
	
	public static List<LotteryWinLoss>  getLotteries(Date gameDate){
		return find(" game_date = ? and del_marker = 0  order by game_id desc ",gameDate).fetch();
	}
	
	
	public static List<LotteryWinLoss>  getWinLoses(long homeId,long awayId,Date gameDate){
		return find(" ((away_id = ? and host_id = ? )  or  (away_id = ? and host_id = ? )) and game_date <=  ? and del_marker = 0   order by game_date desc  "
				,awayId,homeId,homeId,awayId,gameDate).fetch();
	}
	
	public static void del(Date beginDate,Date endDate){
		Query query = JPA.em().createNativeQuery("delete from  LOTTERY_WINLOSE t where t.game_date >= ? and t.game_date < ? + 1 ");
		query.setParameter(1, beginDate);
		query.setParameter(2, endDate);
		query.executeUpdate();		
	}
}
