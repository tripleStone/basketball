package models.team;

import gsonmoudle.HupuMsg;

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

import play.db.jpa.GenericModel;
import util.DateUtil;

@Entity
@Table(name="team_message")
public class TeamMsg extends GenericModel {
	
	@Id
	@SequenceGenerator(name="SEQ_TEAM_MSG",sequenceName="SEQ_TEAM_MSG",allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_TEAM_MSG")
	@Column(name="id")
	public long id;
	
	@Column(name="team_id")
	public long team_id;
	
	@Column(name="TEAM_NAME")
	public String team_name;	
	
	@Column(name="MSG_TITLE")
	public String msg_title;		
	
	@Column(name="MSG_CONTENT")
	public String msg_content;			
	
	@Column(name="season")
	public int season;	
	
	@Column(name="msg_date")
	public Date msg_date;	
	
	@Column(name="msg_type")
	public int msg_type;	
	
	@Column(name="HUPU_MSG_TYPE")
	public int hupu_msg_type;
	
	@Column(name="DEL_MARKER")
	public int del_marker;		
	
	@Column(name="LINK_URL")
	public String link_url;		
	
	@Column(name="MSG_CATEGORY")
	public String msgCategory;		

	@Column(name="SOURCE")
	public String source;	
	
	@Column(name="SOURCE_URL")
	public String souceURL;	
	
	@Column(name="PUBLISHER_NAME")
	public String publisherName;	
	
	@Column(name="PUBLISHER_URL")
	public String publisherURL;		
	
	@Column(name="SOURCE_ID")
	public long sourceId;		
	
	@Column(name="PUBLISHER_DESC")
	public String publisherDesc;	
	
	@Column(name="TIMELINE")
	public long timeLine;
	
	public TeamMsg(){}
	
	public TeamMsg(HupuMsg msg,TeamInfo team){
		this.team_id = team.id;
		this.team_name = team.team_name;
		this.msg_title = msg.getTitle();
		this.msg_content = msg.getText().trim();
		this.season = 2013;
		try {
			this.msg_date = DateUtil.parseDate24(msg.getPublish_date());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.hupu_msg_type = Integer.parseInt(msg.getType());
		this.del_marker = 0;
		this.msgCategory = msg.getCategory();
		this.source = msg.getSource();
		this.souceURL = msg.getSource_url();
		this.sourceId = msg.getId();
		this.publisherDesc = msg.getPublisher_description();
		this.publisherName = msg.getPublisher_name();
		this.publisherURL = msg.getPublisher_url();
		this.timeLine = msg.getTimeline();
		
	}
	
	public static TeamMsg getNewestMsg(long team_id){
		return find( " team_id = ?  and del_marker = 0 order by msg_date desc",team_id).first();
	}
	
	public static List<TeamMsg> getMsgs(long team_id,Date gameDate,Integer page,Integer pageSize){
		return find( " team_id = ?  and msg_date <= ? order by msg_date desc",team_id,gameDate).fetch(page,pageSize);
	}
	
}
