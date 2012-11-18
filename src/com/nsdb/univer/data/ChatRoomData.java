package com.nsdb.univer.data;

import org.jdom2.Element;

public class ChatRoomData {
	public String title;
	public String description;
	public String edited;
	public int chatRoom_id;
	public int seller;
	public int to_id;
	public int count;
	public ChatRoomData() {
		this.title="";
		this.description="";
		this.edited="";
		this.chatRoom_id=-1;
		this.seller=-1;
		this.to_id=-1;
		this.count=-1;
	}
	public ChatRoomData(Element item) {
		this.title=item.getChildText("title");
		this.description=item.getChildText("description");
		this.edited=item.getChildText("edited");
		this.chatRoom_id=Integer.parseInt( item.getChildText("chatRoom_id") );
		this.seller=Integer.parseInt( item.getChildText("seller") );
		this.to_id=Integer.parseInt( item.getChildText("to_id") );
		this.count=Integer.parseInt( item.getChildText("count") );
		
	}

}
