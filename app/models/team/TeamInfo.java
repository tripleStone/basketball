package models.team;

import java.util.Date;
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
@Table(name="team_info")
public class TeamInfo extends GenericModel{

	@Id
	@SequenceGenerator(name="SEQ_INFO",sequenceName="SEQ_INFO",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_INFO")
	@Column(name="id")
	public long id;
	
	@Column(name="team_name")
	public String team_name;
	
	@Column(name="full_name")
	public String full_name;
	
	@Column(name="bigarea")
	public String bigarea;
	
	@Column(name="smallarea")
	public String smallarea;
	
	@Column(name="COMMENTS")
	public String comments;	
	
	@Column(name="HOST_PAGE")
	public String host_page;
	
	@Column(name="ENG_NAME")
	public String eng_name;
	
	@Column(name="city_name")
	public String city_name;
	
	@Column(name="eng_simple_name")
	public String eng_simple_name;	
		
	@Column(name="year")
	public int year;
	
	@Column(name="espn_id")
	public Long espnId;	
	
	@Column(name="hupu_id")
	public Long hupuId;	
			
	public static TeamInfo getTeam(String team_name){
		return find(" team_name  = ?" ,team_name).first();
	}
	
	public static TeamInfo getTeamByEngName(String team_name){
		return find(" eng_name  = ?",team_name).first();
	}
	
	public static TeamInfo getTeamByEspnId(Long espnId){
		return find(" espnId  = ?",espnId).first();
	}	
	
	public static TeamInfo getTeamByFullName(String full_name){
		return find(" full_name  = ?" ,full_name).first();
	}
	
	public static List<TeamInfo> getTeams(){
		return find(" order by bigarea,smallarea").fetch();
	}	
	
	public static List<TeamInfo> getTeams(int year){
		return find(" year != ? order by bigarea,smallarea",year).fetch();
	}
	
	public static List<TeamInfo> getTeamsNoRgP(Date gameDate){
		return find(" id in (select distinct(gpi.team_id) from GamePlayerInfo gpi where gpi.player_id = -1 and gpi.game_date = ? )",gameDate).fetch();
	}

	
	
	public static List<TeamInfo> getTeamsWithNoGame(String date){
		return find(" select t from TeamInfo  t  where t.id not in " +
				" (select gi1.home_id  from GameInfo gi1 where gi1.play_date = to_date(?,'yyyy-MM-dd') and gi1.status = 2 ) " +
				" and t.id not in (select gi2.guest_id  from GameInfo gi2 where gi2.play_date = to_date(?,'yyyy-MM-dd') " +
				" and gi2.status = 2)",date,date).fetch();
	}
	
	public static List<TeamInfo> getTeams(Date gameDate){
		return find(" id in (select home_id from GameInfo  where play_date = ?  ) or id in (select guest_id from GameInfo  where play_date = ?  )  ",gameDate,gameDate).fetch();
	}
	
}
