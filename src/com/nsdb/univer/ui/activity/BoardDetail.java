package com.nsdb.univer.ui.activity;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.R;
import com.nsdb.univer.data.BoardData;
import com.nsdb.univer.supporter.NetworkSupporter;
import com.nsdb.univer.supporter.adapter.BoardCommentDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.FontSetter;

public class BoardDetail extends Activity implements OnScrollListener, OnClickListener {
	
	// data
	BoardData lastdata;
	// linear layout
	TextView title,created,range;
	ImageView image;
	TextView description;
	// listview
	ListView lv;
	BoardCommentDataAdapter adapter;
	// comment
	EditText commenttxt;
	Button commentbtn;
	ProgressDialog pdl;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontSetter.setDefault(this);
        
        // combine layout
        setContentView(R.layout.boarddetail_part2);
        View part1=((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.boarddetail_part1,null);
        ((ListView)findViewById(R.id.commentlist)).addHeaderView(part1,null,false);

        // data
        lastdata=AppPref.getLastBoardData();
        // linear layout
		title=(TextView)findViewById(R.id.title);
		created=(TextView)findViewById(R.id.created);
		range=(TextView)findViewById(R.id.range);
		description=(TextView)findViewById(R.id.description);
		title.setText(lastdata.title);
		created.setText(lastdata.created);
		range.setText(lastdata.region+" / "+lastdata.university);
		description.setText(lastdata.description);
		// image
		image=(ImageView)findViewById(R.id.image);
		if(lastdata.image.compareTo("")!=0) {
			System.out.println("image : "+lastdata.image);
			// resizing
			ViewGroup.LayoutParams params=image.getLayoutParams();
			params.width=getIntent().getIntExtra("width",-1);
			params.height=getIntent().getIntExtra("height",-1);
			image.setLayoutParams(params);
			// load image
        	ImageLoader loader=new ImageLoader(this);
			loader.DisplayImage(
					getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.media_url)+'/'
					+lastdata.image,image);
			image.setVisibility(View.VISIBLE);
		}
        // ListView
    	lv=(ListView)findViewById(R.id.commentlist);
    	adapter=new BoardCommentDataAdapter(this,lv);
    	lv.setOnScrollListener(this);
    	adapter.updateData(lastdata.id,true);
    	// comment
    	commenttxt=(EditText)findViewById(R.id.commenttxt);
    	commentbtn=(Button)findViewById(R.id.commentbtn);
    	commentbtn.setOnClickListener(this);
		
        
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem+visibleItemCount==totalItemCount) {
	    	adapter.updateData(lastdata.id,false);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	public void onClick(View v) {
		if(commenttxt.getText().toString().compareTo("")!=0)
			new AddCommentHelper().execute();
	}

	private class AddCommentHelper extends AsyncTask<Void,Void,String> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdl=ProgressDialog.show(BoardDetail.this,"Loading","Loading...",true,false);
		}

		@Override
		protected String doInBackground(Void... params) {
			
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.boardcomment_url)+'/';

			try {
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("user_id",""+AppPref.getInt("user_id")));				
				postdata.add( new BasicNameValuePair("value",AppPref.getString("value")));
				postdata.add( new BasicNameValuePair("id",""+lastdata.id));
				postdata.add( new BasicNameValuePair("content",commenttxt.getText().toString()));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
				request.setEntity(ent);

				// get result
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				String result=NetworkSupporter.getStringFromStream(isr);
				isr.close();
				return result;
				
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			pdl.dismiss();

			if(result.compareTo("200")==0) {
				Toast.makeText(BoardDetail.this,"등록 성공",Toast.LENGTH_SHORT).show();
				commenttxt.setText("");
				adapter.updateData(lastdata.id,true);
			} else {
				Toast.makeText(BoardDetail.this,result,Toast.LENGTH_SHORT).show();				
			}
		}
		
		
		
	}

}
