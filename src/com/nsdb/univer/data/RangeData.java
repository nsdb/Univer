package com.nsdb.univer.data;

public class RangeData {
	public String title;
	public String nick;
	public int id;
	public RangeData(String title,String nick,int id) {
		this.title=title;
		this.nick=nick;
		this.id=id;
		if(nick.compareTo("")==0) this.nick=this.title;
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
	}
	public RangeData(String title) {
		this.title=title;
		this.nick=title;
		this.id=-1;
	}
}
