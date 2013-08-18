package gsonmoudle;

import javax.persistence.Column;

public class HupuMsg {
	
	private long id;	
	
	private String type;
	
	private String title;
	
	private String text;
	
	private String publish_date;
	
	private long timeline;

	private String category;		

	private String source;	
	
	private String source_url;	
	
	private String publisher_name;	
	
	private String publisher_url;			
	
	private String publisher_description;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPublish_date() {
		return publish_date;
	}

	public void setPublish_date(String publish_date) {
		this.publish_date = publish_date;
	}

	public long getTimeline() {
		return timeline;
	}

	public void setTimeline(long timeline) {
		this.timeline = timeline;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource_url() {
		return source_url;
	}

	public void setSource_url(String source_url) {
		this.source_url = source_url;
	}

	public String getPublisher_name() {
		return publisher_name;
	}

	public void setPublisher_name(String publisher_name) {
		this.publisher_name = publisher_name;
	}

	public String getPublisher_url() {
		return publisher_url;
	}

	public void setPublisher_url(String publisher_url) {
		this.publisher_url = publisher_url;
	}

	public String getPublisher_description() {
		return publisher_description;
	}

	public void setPublisher_description(String publisher_description) {
		this.publisher_description = publisher_description;
	}	
	
	
	
	
}
