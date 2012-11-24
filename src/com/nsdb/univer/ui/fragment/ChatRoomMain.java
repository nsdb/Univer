package com.nsdb.univer.ui.fragment;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.adapter.ChatRoomDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.FontSetter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ChatRoomMain extends ActiveFragment implements OnItemClickListener {

	ListView lv;
	ChatRoomDataAdapter adapter;
	private final static int REQUESTCODE_CHATROOMDETAIL=3;
	
	TextView badge;
	
	public ChatRoomMain() {
		super();
	}
	public ChatRoomMain(Activity activity) {
		super(activity, R.layout.chatroommain);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=super.onCreateView(inflater, container, savedInstanceState);
        FontSetter.setDefault(THIS,v);
		
		lv=(ListView)v.findViewById(R.id.chatlist);
		adapter=new ChatRoomDataAdapter(THIS,lv);
		lv.setOnItemClickListener(this);
		
		badge=(TextView)THIS.findViewById(R.id.tabbadge);
		
		adapter.updateData(badge);
		
		return v;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		AppPref.setLastChatRoomData(adapter.getItem(position));
		startActivity( new Intent("ChatRoomDetail").putExtra("from","ChatRoomMain") );
		
	}
	
	// Every Fragment's requestCode are linked! (REQUESTCODE_REGISTERBOARD==REQUESTCODE_REGISTERPROFESSOR...)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case REQUESTCODE_CHATROOMDETAIL:
			if(data.getBooleanExtra("edited",false)==true) {
				adapter.updateData(badge);
			} break;			
		}
	}
	
}
