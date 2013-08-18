package models.game;

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
@Table(name="GAME_INFO_REPORT")
public class GameInfoReport extends GenericModel{
	
	@Id
	@SequenceGenerator(name="SEQ_GAME_REPORT",sequenceName="SEQ_GAME_REPORT",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_GAME_REPORT")
	@Column(name="id")
	public long id;
	
	@Column(name="GAME_ID")
	public long gameId;
	
	@Column(name="PIECE")
	public int piece;
	
	@Column(name="content")
	public String content;
	
	@Column(name="RTYPE")
	public int rtype;
	
	public GameInfoReport(){}
	
	public GameInfoReport(long gameId,int piece,String content){
		this.gameId = gameId;
		this.piece = piece;
		this.content = content;
	}
	
	public static List<GameInfoReport> getReports(Long gameId ,int rtype){
		return find("gameId = ? and rtype = ? order by piece asc",gameId,rtype).fetch();
	}
	
	public static void delReports(Long gameId,int rtype){
		 delete("gameId = ? and rtype = ?" ,gameId,rtype);
	}
}
