package com.nsdb.univer.ui;

import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.ProfessorData;
import com.nsdb.univer.dataadapter.CommentDataAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProfessorDetail extends Activity implements OnScrollListener {

	ProfessorData lastdata;
	
	WebView image;
	TextView title;
	RatingBar quality,report,grade,attendance,personality,total;
	TextView qualitytxt,reporttxt,gradetxt,attendancetxt,personalitytxt,totaltxt;
	
	ListView lv;
	CommentDataAdapter adapter;
	int pageNum;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professordetail);
        
        // data
        lastdata=AppPref.getLastProfessorData();
        
        // first linear
        image=(WebView)findViewById(R.id.image);
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
        if(lastdata.image.compareTo("")!=0)
        	image.loadUrl( getResources().getString(R.string.base_url)+lastdata.image );
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
        
        // ListView
    	lv=(ListView)findViewById(R.id.commentlist);
    	adapter=new CommentDataAdapter(this,lv,true);
    	pageNum=1;
    	lv.setOnScrollListener(this);
    	adapter.updateData(lastdata.id,pageNum);
    }

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem+visibleItemCount==totalItemCount && adapter.isLoadable()) {
			pageNum++;
	    	adapter.updateData(lastdata.id,pageNum);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {}
}
