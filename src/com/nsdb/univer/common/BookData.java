package com.nsdb.univer.common;

import org.jdom2.Element;

public class BookData {
	public String title;
	public String description;
	public String author;
	public String pubdate;
	public int seller_id;
	public String seller_nick;
	public int original_price;
	public int discount_price;
	public String published;
	public int edition;
	public String publisher;
	public String book_author;
	public String thumbnail;
	public String image;
	public int parcel;
	public int sale;  // salestate
	public int meet;
	public int sell;  // salemode
	public String university;
	public String college;
	public String major;
	public int id;
	public String created;
	public String edited;

	public final static int SALEMODE_BUY=0;
	public final static int SALEMODE_SELL=1;
	public final static int SALEMODE_ALL=2;
	
	public final static int SALESTATE_ABLE=1;
	public final static int SALESTATE_BOOKED=2;
	public final static int SALESTATE_DONE=3;
	
	public final static int RANGEMODE_ALL=0;
	public final static int RANGEMODE_REGION=1;
	public final static int RANGEMODE_UNIV=2;
	public final static int RANGEMODE_COLLEGE=3;
	public final static int RANGEMODE_MAJOR=4;
	public final static int RANGEMODE_MINE=5;
	public final static int RANGEMODE_OTHER=6;

	public BookData(String title) {
		this.title=title;
		this.description="";
		this.author="";
		this.pubdate="";
		this.seller_id=-1;
		this.seller_nick="";
		this.original_price=-1;
		this.discount_price=-1;
		this.published="";
		this.edition=-1;
		this.publisher="";
		this.book_author="";
		this.thumbnail="";
		this.image="";
		this.parcel=-1;
		this.sale=-1;  // salestate
		this.meet=-1;
		this.sell=-1;  // salemode
		this.university="";
		this.college="";
		this.major="";
		this.id=-1;
		this.created="";
		this.edited="";
	}
	public BookData(Element item) {
		this.title=item.getChildText("title");
		this.description=item.getChildText("description");
		this.author=item.getChildText("author");
		this.pubdate=item.getChildText("pubdate");
		this.seller_id=Integer.parseInt( item.getChildText("seller_id") );
		this.seller_nick=item.getChildText("seller_nick");
		this.original_price=Integer.parseInt( item.getChildText("original_price") );
		this.discount_price=Integer.parseInt( item.getChildText("discount_price") );
		this.published=item.getChildText("published");
		this.edition=Integer.parseInt( item.getChildText("edition") );
		this.publisher=item.getChildText("publisher");
		this.book_author=item.getChildText("book_author");
		this.thumbnail=item.getChildText("thumbnail");
		this.image=item.getChildText("image");
		this.parcel=Integer.parseInt( item.getChildText("parcel") );
		this.sale=Integer.parseInt( item.getChildText("sale") );  // salestate
		this.meet=Integer.parseInt( item.getChildText("meet") );
		this.sell=Integer.parseInt( item.getChildText("sell") );  // salemode
		this.university=item.getChildText("university");
		this.college=item.getChildText("college");
		this.major=item.getChildText("major");
		this.id=Integer.parseInt( item.getChildText("id") );
		this.created=item.getChildText("created");
		this.edited=item.getChildText("edited");
		
	}
}
