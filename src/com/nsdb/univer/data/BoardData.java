package com.nsdb.univer.data;

import org.jdom2.Element;

public class BoardData {
	public String title;
	public String description;
	public String pubDate;
	public String created;
	public String region;
	public String university;
	public int like;
	public int comment;
	public String image;
	public int id;
	
	public BoardData() {
		title="";
		description="";
		pubDate="";
		created="";
		region="";
		university="";
		like=-1;
		comment=-1;
		image="";
		id=-1;
	}
	public BoardData(Element item) {
		this.title=item.getChildText("title");
		this.description=item.getChildText("description");
		this.pubDate=item.getChildText("pubDate");
		this.created=item.getChildText("created");
		this.region=item.getChildText("region");
		this.university=item.getChildText("university");
		this.like=Integer.parseInt( item.getChildText("like") );
		this.comment=Integer.parseInt( item.getChildText("comment") );
		this.image=item.getChildText("image");
		this.id=Integer.parseInt( item.getChildText("id") );
		
	}

}
