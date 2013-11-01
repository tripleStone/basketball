package models.lottery;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Query;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

@Entity
@Table(name="LOTTERY_WIN_POINT")
public class LotteryWinPoint extends GenericModel {

	@Id
	@SequenceGenerator(name="SEQ_LOTTERY",sequenceName="SEQ_LOTTERY",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_LOTTERY")
	@Column(name="id")
	public long id;
	
	@Column(name="game_id")
	public long game_id;	
	
	@Column(name="host_team")
	public String host_team;
	
	@Column(name="guest_team")
	public String guest_team;
	
	@Column(name="HOST_1TO5")
	public double host_1to5;

	@Column(name="HOST_6TO10")
	public double host_6to10;
	
	@Column(name="HOST_11TO15")
	public double host_11to15;
	
	@Column(name="HOST_16TO20")
	public double host_16to20;
	
	@Column(name="HOST_21TO25")
	public double host_21to25;
	
	@Column(name="HOST_26")
	public double host_26;
	
	@Column(name="GUEST_1TO5")
	public double guest_1to5;
	
	@Column(name="GUEST_6TO10")
	public double guest_6to10;
	
	@Column(name="GUEST_11TO15")
	public double guest_11to15;
	
	@Column(name="GUEST_16TO20")
	public double guest_16to20;
	
	@Column(name="GUEST_21TO25")
	public double guest_21to25;
	
	@Column(name="GUEST_26")
	public double guest_26;
	
	@Column(name="HIT")
	public String hit;
	
	@Column(name="HOST_SCORE")
	public int host_score;
	
	@Column(name="GUEST_SCORE")
	public int guest_score;
	
	@Column(name="GAME_DATE")
	public Date game_date;
	
	@Column(name="season")
	public int season;	
	
	@Column(name="del_marker")
	public int del_marker;
	
	@Column(name="exp")
	public String exp;
	
	@Column(name="win_point")
	public int winPoint;
	
	@Column(name="span")
	public int span;

	@Column(name="week_day")
	public int weekDay;
	
	@Column(name="CREATE_DATE")
	public Date createDate;	
	
	@Column(name="lottery")
	public double lottery;
	
	@Column(name="HOME_ID")
	public long homeId;
	
	@Column(name="away_id")
	public long awayId;
	
	
	public String getHitLottery(String span,double host,double guest){
		String result = "";
		if (this.hit != null){
			String[] hit = this.hit.split("_");
			if (span.equals(hit[1])){
				if (hit[0].equals("h")){
					result = guest + "<br/><font color=\"red\">"+host +"</font>" ;
				}else{
					result = "<font color=\"red\">" + guest +  "</font><br/>" + host;
				}
			}else{
				result = guest+"<br/>" + host ;
			}	
		}else {
			result =guest+"<br/>" +  host ;
		}
		return result;
	}
	
	
	public static LotteryWinPoint  getLottery(long game_id){
		return find(" game_id = ?  and del_marker = 0  order by game_id desc ",game_id).first();
	}
	
	public static List<LotteryWinPoint>  getLotteryHis(long game_id){
		return find(" game_id = ?  order by id desc  ",game_id).fetch();
	}

	
	public static List<LotteryWinPoint>  getLotteries(Date game_date){
//		return find(" select lwp from LotteryWinPoint lwp,GameInfo gi where lwp.game_id = gi.id and lwp.game_date = ? and lwp.del_marker = 0 order by gi.usa_play_date asc ,game_id desc ",game_date).fetch();
		return find(" game_date = ? and del_marker = 0 order by game_id desc ",game_date).fetch();
	}
	
	public static List<LotteryWinPoint> getLotteries(String team_name,Date game_date ,int  games){
		return find("  game_id in  ( select  id  from  GameInfo where  (home_name =? or guest_name = ?)  and  play_date < ?   )  and del_marker = 0   order by game_date desc ",
				team_name,team_name,game_date).fetch(games);
	}
	
	public static List<LotteryWinPoint> getLotteries(Long teamId,Integer season){
		return find("  (home_id = ? or away_id = ?) and season = ? and del_marker = 0   order by game_date desc ",
				teamId,teamId,season).fetch();
	}
	
	public static List<LotteryWinPoint>  getLotteries(String host_team,String guest_team,Date gameDate){
		return find("  ((guest_team = ? and host_team = ? )  or  (guest_team = ? and host_team = ? ))  and  game_date <= ? and del_marker = 0   order by game_date desc",
				guest_team,host_team,host_team,guest_team,gameDate).fetch();
	}
	
	public static void setLotteries(Long gameId,int score,int span,String hit,int hostScore,int guestScore){
		Query query = JPA.em().createNativeQuery(" update LOTTERY_WIN_POINT t set t.WIN_POINT=?, t.span = ? ,t.hit = ? ,t.host_score = ? ,t.guest_score = ?  where t.game_id = ?");
		query.setParameter(1, score);
		query.setParameter(2, span);
		query.setParameter(3, hit);
		query.setParameter(4, hostScore);
		query.setParameter(5, guestScore);
		query.setParameter(6, gameId);
		query.executeUpdate();		
	}
	
	public static void callPrcSetLottery(Date gameDate){
		Query query = JPA.em().createNativeQuery("{call prc_set_lottery(?)}");
		query.setParameter(1, gameDate);
		query.executeUpdate();
	}
	
	public static void del(Date beginDate,Date endDate){
		Query query = JPA.em().createNativeQuery("delete from  LOTTERY_WIN_POINT t where t.game_date >= ? and t.game_date < ? + 1 ");
		query.setParameter(1, beginDate);
		query.setParameter(2, endDate);
		query.executeUpdate();		
	}	
	
	
	public static void del(Long gameId){
		Query query = JPA.em().createNativeQuery("delete from  LOTTERY_WIN_POINT t where t.game_id = ? ");
		query.setParameter(1, gameId);
		query.executeUpdate();		
	}	
}
