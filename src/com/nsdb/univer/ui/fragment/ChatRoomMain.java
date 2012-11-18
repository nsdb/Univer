package com.nsdb.univer.ui.fragment;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.adapter.ChatRoomDataAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChatRoomMain extends ActiveFragment implements OnItemClickListener {

	ListView lv;
	ChatRoomDataAdapter adapter;
	
	public ChatRoomMain(Activity activity) {
		super(activity, R.layout.chatroommain);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=super.onCreateView(inflater, container, savedInstanceState);
		
		lv=(ListView)v.findViewById(R.id.chatlist);
		adapter=new ChatRoomDataAdapter(THIS,lv);
		lv.setOnItemClickListener(this);
		adapter.updateData();
		
		return v;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		startActivity( new Intent("ChatRoomDetail") );
		
	}
}
