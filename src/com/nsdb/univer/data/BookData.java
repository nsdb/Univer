package com.nsdb.univer.data;

public class BookData {
	public String title;
	public int original_price;
	public int discount_price;
	public String thumbnail;
	public int id;

	public final static int SALEMODE_BUY=0;
	public final static int SALEMODE_SELL=1;
	public final static int SALEMODE_ALL=2;
	
	public final static int SALESTATE_ABLE=1;
	public final static int SALESTATE_BOOKED=2;
	public final static int SALESTATE_DONE=3;
	
	public BookData(String title,int original_price,int discount_price,
			String thumbnail,int id) {
		this.title=title;
		this.original_price=original_price;
		this.discount_price=discount_price;
		this.thumbnail=thumbnail;
		this.id=id;
	}
	public BookData(String title) {
		this.title=title;
		original_price=-1;
		discount_price=-1;
		thumbnail="";
		id=-1;
	}
}
