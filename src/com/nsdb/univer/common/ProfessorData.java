package com.nsdb.univer.common;

import org.jdom2.Element;

public class ProfessorData {
	public String title;
	public double quality;
	public double report;
	public double grade;
	public double attendance;
	public double personality;
	public double total;
	public int count;
	public String university;
	public String college;
	public String major;
	public String thumbnail;
	public String image;
	public int id;

	public ProfessorData(String title) {
		this.title=title;
		this.quality=0.0;
		this.report=0.0;
		this.grade=0.0;
		this.attendance=0.0;
		this.personality=0.0;
		this.total=0.0;
		this.count=-1;
		this.university="";
		this.college="";
		this.major="";
		this.thumbnail="";
		this.image="";
		id=-1;
	}
	public ProfessorData(Element item) {
		this.title=item.getChildText("title");
		this.quality=Double.parseDouble( item.getChildText("quality") );
		this.report=Double.parseDouble( item.getChildText("report") );
		this.grade=Double.parseDouble( item.getChildText("grade") );
		this.attendance=Double.parseDouble( item.getChildText("attendance") );
		this.personality=Double.parseDouble( item.getChildText("personality") );
		this.total=Double.parseDouble( item.getChildText("total") );
		this.count=Integer.parseInt( item.getChildText("count") );
		this.university=item.getChildText("university");
		this.college=item.getChildText("college");
		this.major=item.getChildText("major");
		this.thumbnail=item.getChildText("thumbnail");
		this.image=item.getChildText("image");
		this.id=Integer.parseInt( item.getChildText("id") );
	}
}
