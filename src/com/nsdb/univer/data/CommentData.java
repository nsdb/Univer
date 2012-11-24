package com.nsdb.univer.data;

public class CommentData {
	public String title;
	public String description;
	public String created;
	public CommentData(String title,String description,String pubdate) {
		this.title=title;
		this.description=description;
		this.created=pubdate;
	}
	public CommentData() {
		title="";
		description="";
		created="";
	}
	public CommentData(String title) {
		this.title=title;
		description="";
		created="";
	}
}
