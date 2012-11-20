package com.nsdb.univer.ui.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.data.AppPref;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class ChatRoomDetail extends Activity implements OnClickListener {

	WebView view;
	EditText text;
	Button send;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroomdetail);
        
        view=(WebView)findViewById(R.id.view);
        text=(EditText)findViewById(R.id.text);
        send=(Button)findViewById(R.id.send);
        view.getSettings().setJavaScriptEnabled(true);
        send.setOnClickListener(this);
        
        try {
			ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
			postdata.add( new BasicNameValuePair("user_id",""+AppPref.getInt("user_id")));				
			postdata.add( new BasicNameValuePair("value",AppPref.getString("value")));				
			postdata.add( new BasicNameValuePair("roomName",""+3.2) );
			postdata.add( new BasicNameValuePair("is_chatRoom",""+1) );
			postdata.add( new BasicNameValuePair("chatRoom_id",""+1) );
			postdata.add( new BasicNameValuePair("to_id",""+3) );
			postdata.add( new BasicNameValuePair("my_id",""+2) );
			postdata.add( new BasicNameValuePair("my_seller",""+1) );
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
			InputStream is=ent.getContent();
			InputStreamReader isr=new InputStreamReader(is);
			BufferedReader br=new BufferedReader(isr);
			String data="";
			String temp=br.readLine();
			while(temp!=null) {
				data=data+temp;
				temp=br.readLine();
			}
	        view.postUrl("http://54.248.233.34:3000/join/",EncodingUtils.getBytes(data,"BASE64"));
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
    }

	public void onClick(View v) {
		
		if(text.getText().toString().compareTo("")!=0) {
			view.loadUrl("javascript:callJavascriptFromObjectiveC('"+text.getText().toString()+"')");
			text.setText("");
		}
	}
	
}
