package com.nsdb.univer.ui.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

import com.nsdb.univer.R;
import com.nsdb.univer.data.ChatRoomData;
import com.nsdb.univer.supporter.NetworkSupporter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.FontSetter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChatRoomDetail extends Activity implements OnClickListener, OnFocusChangeListener {

	
	WebView view;
	EditText text;
	Button send;
	ProgressDialog pdl;

	// saved data
	int is_chatRoom,chatRoom_id,seller,seller_id;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroomdetail);
        FontSetter.setDefault(this);
        
        view=(WebView)findViewById(R.id.view);
        text=(EditText)findViewById(R.id.text);
        send=(Button)findViewById(R.id.send);
        view.getSettings().setJavaScriptEnabled(true);
        view.addJavascriptInterface(new AndroidBridge(),"android2");
        send.setOnClickListener(this);
        text.setOnFocusChangeListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        is_chatRoom=-1;
        chatRoom_id=-1;
        seller=-1;
        seller_id=-1;
        
		// if you come from ChatRoomMain
        if(getIntent().getStringExtra("from").compareTo("ChatRoomMain")==0) {
        	ChatRoomData lastdata=AppPref.getLastChatRoomData();
        	is_chatRoom=1;
        	chatRoom_id=lastdata.chatRoom_id;
        	seller=lastdata.seller;
        	seller_id=lastdata.to_id;
        	requestConnect();
        }
        // else if you come from BookDetail	
        else if(getIntent().getStringExtra("from").compareTo("BookDetail")==0) {
        	new ChatRoomChecker().execute();
        }	
        else {
        	finish();
        }
    }

    // send message
	public void onClick(View v) {
		
		if(text.getText().toString().compareTo("")!=0) {
			view.loadUrl("javascript:callJavascriptFromObjectiveC('"+text.getText().toString()+"')");
			text.setText("");
		}
	}
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus)
			view.loadUrl("javascript:callJavascriptTextViewResponse()");
	}
	
	
	// chect chat room
	private class ChatRoomChecker extends AsyncTask<Void,Void,Integer> {
		
		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(ChatRoomDetail.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			// create url
			// login : 1.234.23.142/~ypunval/chatRoomCheck/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.chatroomcheck_url)+'/';
			System.out.println("XML URL : "+url);
					
			try {				
				// create http post for sending
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("user_id",""+AppPref.getInt("user_id")));				
				postdata.add( new BasicNameValuePair("value",AppPref.getString("value")));				
				postdata.add( new BasicNameValuePair("other_user_id",""+getIntent().getIntExtra("seller_id",-1)));				
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
				request.setEntity(ent);

				// get result
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				String result=NetworkSupporter.getStringFromStream(isr);
				isr.close();
				return Integer.parseInt(result);
				
			} catch(Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			
			pdl.dismiss();
			if(result==-1) {
				Toast.makeText(ChatRoomDetail.this,"채팅창 활성화 실패, 다시 시도하십시요",Toast.LENGTH_SHORT).show();
				text.setHint("연결 실패");
				return;
			}
			is_chatRoom=(result==0)? 0:1;
			chatRoom_id=result;
			seller=0;
			seller_id=getIntent().getIntExtra("seller_id",-1);
			requestConnect();
		}
	}
	
	
	// update view
	public void requestConnect() {
		
        try {
			ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
			postdata.add( new BasicNameValuePair("user_id",""+AppPref.getInt("user_id")));				
			postdata.add( new BasicNameValuePair("value",AppPref.getString("value")));				
			postdata.add( new BasicNameValuePair("device_type",""+2));				
			postdata.add( new BasicNameValuePair("roomName",""+
					Math.max(AppPref.getInt("user_id"),seller_id)+"."
					+Math.min(AppPref.getInt("user_id"),seller_id) ) );
			postdata.add( new BasicNameValuePair("is_chatRoom",""+is_chatRoom) );
			postdata.add( new BasicNameValuePair("chatRoom_id",""+chatRoom_id) );
			postdata.add( new BasicNameValuePair("to_id",""+seller_id) );
			postdata.add( new BasicNameValuePair("my_id",""+AppPref.getInt("user_id")) );
			postdata.add( new BasicNameValuePair("my_seller",""+seller) );
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
	        view.postUrl(getResources().getString(R.string.base_url)+":3000/join/",
	        		EncodingUtils.getBytes(data,"BASE64"));
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	
	// javascript->android
	private class AndroidBridge {
		public void callAndroid(final String arg) {
			System.out.println("arg : "+arg);
			JsReceiver r=new JsReceiver(arg);
			ChatRoomDetail.this.runOnUiThread(r);
		}
	}
	
	private class JsReceiver implements Runnable {

		String arg;
		public JsReceiver(String arg) { this.arg=arg; }
		public void run() {
			
			if(arg.compareTo("1")==0) {
				
				System.out.println("Chat Connected");
				text.setEnabled(true);
				text.setHint("");
				send.setEnabled(true);
				
			} else if(arg.compareTo("0")==0) {
				
				System.out.println("Chat Disconnected");
				text.setEnabled(false);
				text.setHint("연결중입니다");
				send.setEnabled(false);
				requestConnect();
			}
			
		}
		
	}

}
