package models.statements;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name="config_statements")
public class StatementsConfig extends GenericModel  {
	
	@Id
	@Column(name="id")
	public long id;
	
	@Column(name="name")
	public String name;
	
	@Column(name="columns")
	public String columns;
	
	@Column(name="columns_count")
	public Integer columns_count;
	
	@Column(name="ORDER_BY")
	public String order_by;
	
	@Column(name="TABLE_NAME")
	public String table_name;
	
	public static StatementsConfig getConfig(String name){
		return find(" name = ? ",name).first();
	}
}
