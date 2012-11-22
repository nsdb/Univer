package com.nsdb.univer.supporter.adapter;

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

	public ChatRoomDataAdapter(Context context, ListView view) {
		super(context, R.layout.stringdata, view);
	}
	
	public void updateData() {
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+getContext().getResources().getString(R.string.chatroom_url)+'/'
				+"user_id="+AppPref.getInt("user_id")+'/';
		super.updateData(url);
	}

	@Override
	protected ChatRoomData convertElementToObject(Element item) {
		return new ChatRoomData(item);
	}

	@Override
	protected void setView(int position, View v, boolean initial) {
        if(initial) FontSetter.setDefault(getContext(),v);

        TextView t=(TextView)v.findViewById(R.id.text);
		t.setText( getItem(position).title );
	}
}
