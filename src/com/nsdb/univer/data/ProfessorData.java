package com.nsdb.univer.data;

public class ProfessorData {
	public String title;
	public int id;
	public ProfessorData(String title,int id) {
		this.title=title;
		this.id=id;
	}
	public ProfessorData() {
		title="";
		id=-1;
	}
	public ProfessorData(ProfessorData data) {
		title=data.title;
		id=data.id;
	}
	public ProfessorData(String title) {
		this.title=title;
		id=-1;
	}
}
