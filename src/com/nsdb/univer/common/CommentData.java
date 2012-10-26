package com.nsdb.univer.common;

public class CommentData {
	public String title;
	public String description;
	public String pubDate;
	public CommentData(String title,String description,String pubdate) {
		this.title=title;
		this.description=description;
		this.pubDate=pubdate;
	}
	public CommentData() {
		title="";
		description="";
		pubDate="";
	}
	public CommentData(String title) {
		this.title=title;
		description="";
		pubDate="";
	}
}
