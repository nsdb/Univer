package com.nsdb.univer.supporter.adapter;

import java.util.Calendar;

import org.jdom2.Element;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.nsdb.univer.R;
import com.nsdb.univer.data.ChatRoomData;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.FontSetter;

public class ChatRoomDataAdapter extends DataLoadingArrayAdapter<ChatRoomData> {
	
	private TextView badge;

	public ChatRoomDataAdapter(Context context, ListView view) {
		super(context, R.layout.chatroomdata, view);
	}
	
	public void updateData(TextView badge) {
		init(true);
		this.badge=badge;
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+getContext().getResources().getString(R.string.chatroom_url)+'/'
				+"user_id="+AppPref.getInt("user_id")+'/';
		System.out.println("XML url : "+url);
		super.updateData(url);
	}

	@Override
	protected ChatRoomData convertElementToObject(Element item) {
		return new ChatRoomData(item);
	}
	
	@Override
	protected void applyUpdate() {
		super.applyUpdate();
		
		int count=0;
		for(int i=0;i<getCount();i++) {
			count+=getItem(i).count;
		}
		if(count==0) {
			badge.setVisibility(View.GONE);
		} else {
			badge.setVisibility(View.VISIBLE);
			badge.setText(""+count);
		}
	}

	@Override
	protected void setView(int position, View v, boolean initial) {
        if(initial) FontSetter.setDefault(getContext(),v);

        TextView b=(TextView)v.findViewById(R.id.badge);
        TextView t=(TextView)v.findViewById(R.id.title);
        TextView s=(TextView)v.findViewById(R.id.seller);
        TextView e=(TextView)v.findViewById(R.id.edited);
        TextView d=(TextView)v.findViewById(R.id.description);
        
        if(getItem(position).count==0)	{
        	b.setVisibility(View.INVISIBLE);
        }
        else {
        	b.setVisibility(View.VISIBLE);
            b.setText(""+getItem(position).count);
        }

        t.setText( getItem(position).title );

		if(getItem(position).seller==0)
			s.setText("판매중");
		else
			s.setText("구매중");
		
		Calendar cal=Calendar.getInstance();
		String[] splited=getItem(position).edited.split(" ");
		String[] splitedDay=splited[0].split("\\-");
		String[] splitedTime=splited[1].split("\\:");
		if(Integer.parseInt(splitedDay[0])==cal.get(Calendar.YEAR)) {
			e.setText(splitedDay[1]+"월 "+splitedDay[2]+"일  "+splitedTime[0]+"시 "+splitedTime[1]+"분");
		} else {
			e.setText(splitedDay[0]+"년 "+splitedDay[1]+"월  "+splitedDay[2]+"일");
		}
		
		d.setText( getItem(position).description );
	}
}
