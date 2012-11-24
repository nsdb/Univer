package com.nsdb.univer.ui.activity;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.R;
import com.nsdb.univer.data.ProfessorData;
import com.nsdb.univer.supporter.NetworkSupporter;
import com.nsdb.univer.supporter.adapter.ProfessorCommentDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.FontSetter;
import com.nsdb.univer.supporter.ui.NullIntentPreventer;
import com.nsdb.univer.supporter.ui.OnClickMover;
import com.nsdb.univer.ui.customview.ProfessorRatingGraph;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProfessorDetail extends Activity implements OnScrollListener, OnClickListener {

    // data
	ProfessorData lastdata;
	
	// evaluation
	ImageButton evaluate;
	private static int REQUESTCODE_EVALUATE=1;
	
    // first linear
	ImageView image;
	TextView title;
	TextView count;
	TextView like,dislike,comment_count;
	
	RatingBar quality,report,grade,attendance,personality;
	
	// graph
	ProfessorRatingGraph graph;
	
    // ListView
	ListView lv;
	ProfessorCommentDataAdapter adapter;
	
	// comment
	EditText commenttxt;
	Button commentbtn;
	ProgressDialog pdl;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NullIntentPreventer.prevent(this);
        setContentView(R.layout.professordetail_part2);
        View part1=((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.professordetail_part1,null);
        ((ListView)findViewById(R.id.commentlist)).addHeaderView(part1,null,false);
        FontSetter.setDefault(this,part1);
        FontSetter.setDefault(this);
        
        // data
        lastdata=AppPref.getLastProfessorData();
        
        // evaluate
        evaluate=(ImageButton)findViewById(R.id.evaluate);
        evaluate.setOnClickListener(new OnClickMover(this,new Intent("EvaluateProfessor"),REQUESTCODE_EVALUATE));
        
        // first linear
        image=(ImageView)findViewById(R.id.image);
        title=(TextView)findViewById(R.id.title);
        count=(TextView)findViewById(R.id.count);
        like=(TextView)findViewById(R.id.like);
        dislike=(TextView)findViewById(R.id.dislike);
        comment_count=(TextView)findViewById(R.id.comment_count);
        if(lastdata.image.compareTo("")!=0) {
        	ImageLoader loader=new ImageLoader(this);
        	loader.DisplayImage(getResources().getString(R.string.base_url)+lastdata.image,image);
        }
        title.setText(lastdata.title);
        count.setText(""+lastdata.count);
        like.setText(""+lastdata.like);
        dislike.setText(""+lastdata.dislike);
        comment_count.setText(""+lastdata.comment_count);
        
        
        
        quality=(RatingBar)findViewById(R.id.quality);
        report=(RatingBar)findViewById(R.id.report);
        grade=(RatingBar)findViewById(R.id.grade);
        attendance=(RatingBar)findViewById(R.id.attendance);
        personality=(RatingBar)findViewById(R.id.personality);
		if(lastdata.count > 0) {
			double d;
			d=lastdata.quality/lastdata.count;
			d=Math.round(d*100)/100;
			quality.setRating( (float)d/2 );
			d=lastdata.report/lastdata.count;
			d=Math.round(d*100)/100;
			report.setRating( (float)d/2 );
			d=lastdata.grade/lastdata.count;
			d=Math.round(d*100)/100;
			grade.setRating( (float)d/2 );
			d=lastdata.attendance/lastdata.count;
			d=Math.round(d*100)/100;
			attendance.setRating( (float)d/2 );
			d=lastdata.personality/lastdata.count;
			d=Math.round(d*100)/100;
			personality.setRating( (float)d/2 );
		}
		
		// graph
		graph=(ProfessorRatingGraph)findViewById(R.id.graph);
		if(graph != null) {
			graph.setRating(quality.getRating()*2,
					report.getRating()*2,
					grade.getRating()*2,
					attendance.getRating()*2,
					personality.getRating()*2);
		}
		
        // ListView
    	lv=(ListView)findViewById(R.id.commentlist);
    	adapter=new ProfessorCommentDataAdapter(this,lv);
    	lv.setOnScrollListener(this);
    	adapter.updateData(lastdata.id,true);
    	
    	// comment
    	commenttxt=(EditText)findViewById(R.id.commenttxt);
    	commentbtn=(Button)findViewById(R.id.commentbtn);
    	commentbtn.setOnClickListener(this);
    }

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem+visibleItemCount==totalItemCount) {
	    	adapter.updateData(lastdata.id,false);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==REQUESTCODE_EVALUATE && resultCode==RESULT_OK) {
			getIntent().putExtra("edited",true);
			finish();
		}
	}

	public void onClick(View v) {
		if(commenttxt.getText().toString().compareTo("")!=0)
			new AddCommentHelper().execute();
	}
	
	private class AddCommentHelper extends AsyncTask<Void,Void,String> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdl=ProgressDialog.show(ProfessorDetail.this,"Loading","Loading...",true,false);
		}

		@Override
		protected String doInBackground(Void... params) {
			
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.professorcomment_url)+'/';

			try {
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("user_id",""+AppPref.getInt("user_id")));				
				postdata.add( new BasicNameValuePair("value",AppPref.getString("value")));
				postdata.add( new BasicNameValuePair("professor",""+lastdata.id));
				postdata.add( new BasicNameValuePair("comment",commenttxt.getText().toString()));
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
				Toast.makeText(ProfessorDetail.this,"등록 성공",Toast.LENGTH_SHORT).show();
				commenttxt.setText("");
				adapter.updateData(lastdata.id,true);
			} else {
				Toast.makeText(ProfessorDetail.this,result,Toast.LENGTH_SHORT).show();				
			}
		}
		
		
		
	}

}
