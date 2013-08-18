package models.player;

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
import javax.persistence.Transient;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import util.DateUtil;

@Entity
@Table(name="player_Info")
public class PlayerInfo extends GenericModel {
	
	@Id
	@SequenceGenerator(name="SEQ_INFO",sequenceName="SEQ_INFO",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_INFO")
	@Column(name="id")
	public long id;
	
	@Column(name="espnid")
	public long espnId;	
	
	@Column(name="name")
	public String name;
	
	@Column(name="name_chi")
	public String name_chi;
	
	@Column(name="position")
	public String position;
	
	@Column(name="position_chi")
	public String position_chi;
	
	@Column(name="hight")
	public double hight;
	
	@Column(name="weight")
	public double weight;
	
	@Column(name="EXPERIENCE")
	public int experience;
	
	@Column(name="comments")
	public String comments;
	
	@Column(name="team_id")
	public long team_id;
	
	@Column(name="depth")
	public int depth;
	
	@Column(name="DEL_MARKER")
	public long del_marker;
	
	@Column(name="PLAY_NUM")
	public int play_num;
	
	@Column(name="BIRTHYEAR")
	public String birthyear;
	
	@Column(name="UPDATE_DATE")
	public Date updateDate;	
	
	@Transient
	public String additional;
	
	@Transient
	public PlayerSeasonScore pss;
	


	
	public  static PlayerInfo  findPlayerEng(String name){
		return find(" name = ?  ",name).first();
	}
	
	public  static PlayerInfo  findPlayer(String name){
		return find(" name_chi = ?  and  del_marker =  0 ",name).first();
	}	
	
	public static List<PlayerInfo> getAllPlayers(){
		return find(" del_marker = 0 ").fetch();
	}

	public  static List<PlayerInfo>  findPlayers(long team_id){
		return find(" team_id = ? and del_marker = 0 order by position,depth",team_id).fetch();
	}	
	
	public  static List<PlayerInfo>  getPlayersInGame(long game_id){
		return find("Select b from PlayerInfo b ,GamePlayerInfo c where b.id = c.player_id " +
				"  and c.game_id = ? order by b.position,b.depth",game_id).fetch();
	}		
	
	public  static List<PlayerInfo>  getDepthPlayers(long team_id){
		return find(" team_id = ? and del_marker = 0 order by depth,position",team_id).fetch();
	}	
	
	public  static List<PlayerInfo>  getDepthPlayersInGame(long game_id,Long team_id){
		return find("Select b from PlayerInfo b ,GamePlayerInfo c where b.id = c.player_id " +
				"  and c.game_id = ? and c.team_id = ? order by b.depth,b.position",game_id,team_id).fetch();
	}
	
	public  static PlayerInfo  findPlayer(String name,Long team_id){
		return find(" name_chi = ? and team_id = ? and  del_marker =  0 ",name,team_id).first();
	}	
	
	public  static PlayerInfo  findPlayer(long espnId){
		return find(" espnId = ?  and  del_marker =  0 ",espnId).first();
	}
	
	public  static List<PlayerInfo>  findPlayers(List<Long> teamIds){
		return find(" team_id in :teamIds and del_marker = 0 ").bind("teamIds", teamIds).fetch();
	}
	
	public static List<PlayerInfo> getPlayers(List<Long> ids){		
		return find(" id in :ids").bind("ids", ids).fetch();
	}
	
	public  static List<PlayerInfo>  findPlayers(Date date){
		return find(" team_id in (select t.home_id from  GameInfo t where t.play_date = ?)  " +
				" or team_id in (select t1.guest_id from  GameInfo t1 where t1.play_date = ?)" ,date,date).fetch();
	}
	
	public static void delPlayer(long teamId,List<Long> ids){
		StringBuffer sql = new StringBuffer("update player_info t  set t.del_marker = 1 where t.id  not in (");
		for(int i = 0;i<ids.size();i++){
			if (i==0)
				sql.append("?");
			else
				sql.append(",?");
		}
		sql.append(" ) and t.team_id = ? ");
		
		Query query = JPA.em().createNativeQuery(sql.toString());
		for (int i = 1;i<=ids.size();i++){
			query.setParameter(i, ids.get(i-1));
		}
		query.setParameter(ids.size()+1, teamId);
		query.executeUpdate();		
		
	}
	
	
	

}
