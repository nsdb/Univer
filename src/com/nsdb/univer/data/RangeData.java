package com.nsdb.univer.data;

public class RangeData {
	public String title;
	public String nick;
	public int id;
	public RangeData(String title,String nick,int id) {
		this.title=title;
		this.nick=nick;
		this.id=id;
	}
	public RangeData() {
		title="";
		nick="";
		id=-1;
	}
	public RangeData(RangeData data) {
		title=data.title;
		nick=data.nick;
		id=data.id;
		if(nick.compareTo("")==0) nick=title;
	}
	public RangeData(String title) {
		this.title=title;
		nick="";
		id=-1;
		if(nick.compareTo("")==0) nick=title;
	}
}
