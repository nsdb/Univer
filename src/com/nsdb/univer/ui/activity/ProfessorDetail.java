package com.nsdb.univer.ui.activity;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.R;
import com.nsdb.univer.data.ProfessorData;
import com.nsdb.univer.supporter.adapter.ProfessorCommentDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.OnClickMover;
import com.nsdb.univer.ui.customview.ProfessorRatingGraph;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProfessorDetail extends IntentPreservingActivity implements OnScrollListener {

    // data
	ProfessorData lastdata;
	
	// evaluation
	Button evaluate;
	private static int REQUESTCODE_EVALUATE=1;
	
    // first linear
	ImageView image;
	TextView title;
	RatingBar quality,report,grade,attendance,personality,total;
	TextView qualitytxt,reporttxt,gradetxt,attendancetxt,personalitytxt,totaltxt;
	
	// graph
	ProfessorRatingGraph graph;
	
    // ListView
	ListView lv;
	ProfessorCommentDataAdapter adapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // combine layout
        setContentView(R.layout.professordetail_part2);
        View part1=((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.professordetail_part1,null);
        ((ListView)findViewById(R.id.commentlist)).addHeaderView(part1,null,false);
        
        // data
        lastdata=AppPref.getLastProfessorData();
        
        // evaluate
        evaluate=(Button)findViewById(R.id.evaluate);
        evaluate.setOnClickListener(new OnClickMover(this,new Intent("EvaluateProfessor"),REQUESTCODE_EVALUATE));
        
        // first linear
        image=(ImageView)findViewById(R.id.image);
        title=(TextView)findViewById(R.id.title);
        quality=(RatingBar)findViewById(R.id.quality);
        report=(RatingBar)findViewById(R.id.report);
        grade=(RatingBar)findViewById(R.id.grade);
        attendance=(RatingBar)findViewById(R.id.attendance);
        personality=(RatingBar)findViewById(R.id.personality);
        total=(RatingBar)findViewById(R.id.total);
        qualitytxt=(TextView)findViewById(R.id.qualitytxt);
        reporttxt=(TextView)findViewById(R.id.reporttxt);
        gradetxt=(TextView)findViewById(R.id.gradetxt);
        attendancetxt=(TextView)findViewById(R.id.attendancetxt);
        personalitytxt=(TextView)findViewById(R.id.personalitytxt);
        totaltxt=(TextView)findViewById(R.id.totaltxt); 
        if(lastdata.image.compareTo("")!=0) {
        	ImageLoader loader=new ImageLoader(this);
        	loader.DisplayImage(getResources().getString(R.string.base_url)+lastdata.image,image);
        }
        title.setText(lastdata.title);
		if(lastdata.count > 0) {
			double d;
			d=lastdata.quality/lastdata.count;
			d=Math.round(d*100)/100;
			qualitytxt.setText(""+d);
			quality.setRating( (float)d/2 );
			d=lastdata.report/lastdata.count;
			d=Math.round(d*100)/100;
			reporttxt.setText(""+d);
			report.setRating( (float)d/2 );
			d=lastdata.grade/lastdata.count;
			d=Math.round(d*100)/100;
			gradetxt.setText(""+d);
			grade.setRating( (float)d/2 );
			d=lastdata.attendance/lastdata.count;
			d=Math.round(d*100)/100;
			attendancetxt.setText(""+d);
			attendance.setRating( (float)d/2 );
			d=lastdata.personality/lastdata.count;
			d=Math.round(d*100)/100;
			personalitytxt.setText(""+d);
			personality.setRating( (float)d/2 );
			d=lastdata.total/(lastdata.count*5);
			d=Math.round(d*100)/100;
			totaltxt.setText(""+d);
			total.setRating( (float)d/2 );
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

}
