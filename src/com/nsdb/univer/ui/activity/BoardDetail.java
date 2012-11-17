package com.nsdb.univer.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.R;
import com.nsdb.univer.data.BoardData;
import com.nsdb.univer.supporter.adapter.BoardCommentDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;

public class BoardDetail extends Activity implements OnScrollListener {
	
	// data
	BoardData lastdata;
	// linear layout
	TextView title,pubdate,description;
	ImageView image;
	// listview
	ListView lv;
	BoardCommentDataAdapter adapter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // combine layout
        setContentView(R.layout.boarddetail_part2);
        View part1=((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.boarddetail_part1,null);
        ((ListView)findViewById(R.id.commentlist)).addHeaderView(part1);

        // data
        lastdata=AppPref.getLastBoardData();
        // linear layout
		title=(TextView)findViewById(R.id.title);
		pubdate=(TextView)findViewById(R.id.pubdate);
		description=(TextView)findViewById(R.id.description);
		title.setText(lastdata.title);
		pubdate.setText(lastdata.pubDate);
		description.setText(lastdata.description);
		// image
		image=(ImageView)findViewById(R.id.image);
		if(lastdata.image.compareTo("")!=0) {
			System.out.println("image : "+lastdata.image);
        	ImageLoader loader=new ImageLoader(this);
			loader.DisplayImage(getResources().getString(R.string.base_url)+"/"+lastdata.image,image);
			image.setVisibility(View.VISIBLE);
		}
        // ListView
    	lv=(ListView)findViewById(R.id.commentlist);
    	adapter=new BoardCommentDataAdapter(this,lv);
    	lv.setOnScrollListener(this);
    	adapter.updateData(lastdata.id,true);
		
        
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem+visibleItemCount==totalItemCount) {
	    	adapter.updateData(lastdata.id,false);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {}
}
