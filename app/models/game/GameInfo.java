package models.game;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import controllers.Application;

import play.db.jpa.GenericModel;
import util.DateUtil;

@Entity
@Table(name="Game_Info")
public class GameInfo extends GenericModel {
	
	@Id
	@SequenceGenerator(name="SEQ_GAME",sequenceName="SEQ_GAME",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_GAME")
	@Column(name="id")
	public long id;
	
	@Column(name="home_id")
	public long home_id;
	
	@Column(name="HOME_NAME")
	public String home_name;	
	
	@Column(name="guest_id")
	public long guest_id;
	
	@Column(name="GUEST_NAME")
	public String guest_name;
	
	@Column(name="home_score")
	public int home_score;
	
	@Column(name="guest_score")
	public int guest_score;
	
	@Column(name="each_quarter")
	public String each_quarter;
	
	@Column(name="HAS_DETAIL")
	public int has_detail;
	
	@Column(name="PLAY_DATE")
	public Date play_date;
	
	@Column(name="USA_PLAY_DATE")
	public Date usa_play_date;	
	
	@Column(name="COST_TIME")
	public int cost_time;
	
	@Column(name="STATIC_URL")
	public String  static_url;	
	
	@Column(name="season")
	public int  season;	
	
	@Column(name="g_btb")
	public int  g_btb;	
	
	@Column(name="h_btb")
	public int  h_btb;		
	
	@Column(name="LEAD_CHA")
	public int  lead_cha;	
	
	@Column(name="EQUAL")
	public int  equal;	
	
	@Column(name="VIEWERS")
	public int  viewers;	
	
	@Column(name="has_chart")
	public int has_chart;	
	
	@Column(name="PLAY_TIME")
	public String play_time;
	
	@Column(name="ESPN_ID")
	public long espnId;
	
	@Column(name="has_recap")
	public int hasRecap;
	
	@Column(name="has_preview")
	public int hasPreview;
	
	@Column(name="status")
	public int status;
	
	@Column(name="REPORT_TITLE")
	public String reportTitle;	
	
	@Column(name="WEEK_DAY")
	public int week_day;
	
	@Column(name="HAS_PLAYBYPLAY")
	public int has_playbyplay;
	
	@Column(name="HAS_SYNC")
	public int has_sync;
	
	@Column(name="GAME_TYPE")
	public int game_type;
	
	
	@Transient
	public String chart = "";		
	
	public GameInfo(){};
	
	
	
	
	public GameInfo(int has_detail,long home_id,long away_id,
			String home_name,String away_name,Date gameDate){
		this.has_detail = has_detail;
		this.home_id = home_id;
		this.guest_id = away_id;
		this.home_name = home_name;
		this.guest_name = away_name;
		this.play_date = gameDate;
		this.season = Application.season;
		this.save();
	}
	
	public String getWeekDay(){
		if (this.week_day == 1){
			return "星期7";
		}else{
			return "星期" + (this.week_day - 1);	
		}
	}
	
	public Boolean getBtoB(String teamName){
		Boolean bTb = false;
		if (this.home_name.equals(teamName) && this.h_btb == 1){
			 bTb = true;
		}else if  (this.guest_name.equals(teamName) && this.g_btb == 1){
			bTb = true;
		}
		
		return bTb;
	}
	
	public String getWin(String teamName){
		int score = this.home_score - this.guest_score;
		String result = "";
		if (teamName.equals(this.home_name)){
			if (score>0){
				result = "主胜";
			}else{
				result = "主负";
			}
		}else if(teamName.equals(this.guest_name)){
			if (score<0){
				result = "客胜";
			}else{
				result = "客负";
			}			
		}else{
			result = "分析";
		}
		return result;
		
	}
	
	public int getWinScore(String teamName){
		int score = this.home_score - this.guest_score;
		
		if (teamName.equals(this.guest_name)){
			score = -score;
		}
		
		return score;
		
	}
	
	public static List<GameInfo>  getGameInfos(long teamId){
		return find("home_id = ? or guest_id = ?  order by play_date ", teamId,teamId).fetch();		
	}
	
	public static List<GameInfo>  getGameInfos(long teamId,Integer season){
		return find(" (home_id = ? or guest_id = ? ) and  season = ?  order by play_date ", teamId,teamId,season).fetch();		
	}	
	
	public static List<GameInfo>  getGameInfos(long teamId,Date gameDate,int games){
		return find("(home_id = ? or guest_id = ?)  and play_date < ?   order by play_date desc", teamId,teamId,gameDate).fetch(games);		
	}
	
	public static List<GameInfo>  getGameInfos(long teamId,Date beginDate,Date endDate,String homeType){
		String sql = "";
		if (homeType.equals("all")){
				sql = "  play_date >= ? and play_date <= ?  and (home_id =? or guest_id = ?)  and has_detail = 1 order by play_date desc";
				return find(sql, beginDate,endDate,teamId,teamId).fetch();		
		}
		else if (homeType.equals("home")){
				sql = "  play_date >= ? and play_date <= ?  and home_id =?   and has_detail = 1 order by play_date desc";
				return find(sql, beginDate,endDate,teamId).fetch();		
		}
		else{
				sql = "  play_date >= ? and play_date <= ?  and guest_id = ?  and has_detail = 1 order by play_date desc";
				return find(sql, beginDate,endDate,teamId).fetch();		
		}
	}
	
	public static List<GameInfo> getRecentGames(long teamId,Date playDate,int size,String homeType){
		if (homeType.equals("all")){
			return find( " (home_id =? or guest_id = ?)  and  play_date < ?  order by play_date desc " ,teamId,teamId,playDate).fetch(size);
		}else if (homeType.equals("home")){
			return find( " home_id =? and  play_date < ?  order by play_date desc " ,teamId,playDate).fetch(size);
		}else{
			return find( "  guest_id = ?  and  play_date < ?  order by play_date desc " ,teamId,playDate).fetch(size);
		}
	}
	
	
	public static List<GameInfo>  getHomeGameInfos(long teamId ,int beginScore,int endScore, int season){
		return find(" home_id = ? and home_score - guest_score >= ? and home_score - guest_score <=? and season = ?  and has_detail = 1",
				teamId,beginScore,endScore,season).fetch();		
	}
	
	public static List<GameInfo>  getHomeGameInfos(long teamId ,int beginScore, int season){
		if (beginScore > 0)
			return find("  home_id = ?  and home_score - guest_score >= ? and season = ?  and has_detail = 1", 
				teamId,beginScore,season).fetch();		
		else
			return find("  home_id = ?  and home_score - guest_score <= ? and season = ?  and has_detail = 1", 
					teamId,beginScore,season).fetch();		
	}
	
	
	public static List<GameInfo>  getAwayGameInfos(long teamId ,int beginScore,int endScore, int season){
		return find(" guest_id = ?  and  guest_score - home_score >= ? and  guest_score - home_score <=? and season = ?  and has_detail = 1 order by play_date desc",
				teamId,beginScore,endScore,season).fetch();		
	}
	
	public static List<GameInfo>  getAwayGameInfos(long teamId ,int beginScore, int season){
		if (beginScore < 0)
			return find("  guest_id = ?  and guest_score - home_score <= ?  and season = ? and has_detail = 1  order by play_date desc ", 
				teamId,beginScore,season).fetch();		
		else
			return find("  guest_id = ?  and guest_score - home_score >= ?  and season = ? and has_detail = 1  order by play_date desc ", 
					teamId,beginScore,season).fetch();		
	}
	
	public static GameInfo  getPreGameInfo(long teamId,Date PlayDate){
		return find(" play_date < ? and (home_id = ? or guest_id = ? ) order by play_date desc ", teamId,teamId,PlayDate).first();		
	}	
	
	public static GameInfo  getNextGameInfo(long teamId,Date PlayDate){
		return find(" play_date > ? and (home_id = ? or guest_id = ? ) order by play_date asc ", teamId,teamId,PlayDate).first();		
	}	

	public static GameInfo  getGameInfo(long homeId ,long awayId ,Date playDate){
		return find("home_id = ? and guest_id = ? and play_date = ?", homeId,awayId,playDate).first();		
	}
	
	public static List<GameInfo>  getGameInfos(long homeid ,long guestid){
		return find(" ((home_id = ? and guest_id = ?) or (home_id = ? and guest_id = ?))  " +
				" and has_detail = 1 order by play_date desc ", homeid,guestid,guestid,homeid).fetch();		
	}
	
	public static List<GameInfo>  getGameInfos(long homeid ,long guestid,Date gameDate){
		return find(" ((home_id = ? and guest_id = ?) or (home_id = ? and guest_id = ?))  " +
				" and has_detail = 1 and play_date <= ?  order by play_date desc ", homeid,guestid,guestid,homeid,gameDate).fetch();		
	}
	
	public static List<GameInfo>  getGameInfos(long homeid ,long guestid,Date beginDate,Date endDate){
		return find(" ((home_id = ? and guest_id = ?) or (home_id = ? and guest_id = ?))  " +
				" and has_detail = 1 and play_date >= ? and play_date < ?  order by play_date desc ", 
				homeid,guestid,guestid,homeid,beginDate,endDate).fetch();		
	}
	
	
	public static GameInfo  getGameInfo(String homeName ,String guestName ,Date playDate){
		return find("home_name = ? and guest_name = ? and play_date = ?", homeName,guestName,playDate).first();		
	}
	
	public static GameInfo  getGameInfoByEng(String homeName ,String guestName ,Date playDate){
		return find("home_name = ? and guest_name = ? and play_date = ?", homeName,guestName,playDate).first();		
	}
	
	public static List<GameInfo> getGameInfos(Date beginDate,Date endDate){
		return find(" play_date >= ? and play_date < ? order by play_date desc",beginDate,endDate).fetch();
	}
	
	public static List<GameInfo>  getGameInfos(Date playDate){
		return find("  play_date = ? order by usa_play_date,id", playDate).fetch();		
	}
	
	public static List<GameInfo>  getGameInfos(String playDate){
		List<GameInfo> games = null;
		try {
			games=  find("  play_date = ? order by id desc", DateUtil.parseDate(playDate)).fetch();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return games;
	}
	
	public static List<GameInfo>  getGameInfos(List<Long> ids){
		return find( " id in :ids order by PLAY_DATE desc").bind("ids", ids).fetch();
	}
	
	public static List<GameInfo>  getGameInfos(List<Long> ids,Long teamId,Integer season){
		return find( " (home_id in :homeIds and guest_id = :guest_id) or " +
				" (guest_id in :guestIds and guest_id = :home_id) and season =  " + season + 
				" order by play_date desc")
				.bind("homeIds", ids).bind("guestIds", ids).bind("guest_id", teamId).bind("home_id", teamId).fetch();
	}
	
	
}
