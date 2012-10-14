package com.nsdb.univer.data;

public class CommentData {
	public String title;
	public String description;
	public String pubdate;
	public CommentData(String title,String description,String pubdate) {
		this.title=title;
		this.description=description;
		this.pubdate=pubdate;
	}
	public CommentData() {
		title="";
		description="";
		pubdate="";
	}
	public CommentData(String title) {
		this.title=title;
		description="";
		pubdate="";
	}
}
