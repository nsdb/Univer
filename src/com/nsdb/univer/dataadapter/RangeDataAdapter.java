package com.nsdb.univer.dataadapter;

import org.jdom2.Element;

import com.nsdb.univer.R;
import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.RangeData;
import com.woozzu.android.util.StringMatcher;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class RangeDataAdapter extends DataLoadingArrayAdapter<RangeData> implements SectionIndexer {
	
	private String filterTitle;
	private String sectionArr;
	
	public RangeDataAdapter(Context context, ListView view) {
		super(context, R.layout.stringdata, view);
		filterTitle="";
		sectionArr="ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
		
		// because adapter is set before sectionArr is initialized
		// and it is also same result : private String sectionArr="ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
		view.setAdapter(null);
		view.setAdapter(this);
	}
	
	public void updateData(String range) {
		// region : 1.234.23.142/~ypunval/feeds/region/
		// univ : 1.234.23.142/~ypunval/feeds/univ/(region_id)/
		// college:1.234.23.142/~ypunval/feeds/collage/(univ_id)/
		// major  :1.234.23.142/~ypunval/feeds/major/(college_id)/
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+range+"/";
		if(range.compareTo("univ")==0)
			url=url+AppPref.getRangeSet().get("region").id+"/";
		else if(range.compareTo("college")==0)
			url=url+AppPref.getRangeSet().get("univ").id+"/";
		else if(range.compareTo("major")==0)
			url=url+AppPref.getRangeSet().get("college").id+"/";
		System.out.println("XML URL : "+url);

		super.updateData(url);
	}
	@Override
	protected void applyUpdate() {
		
		// add default data
		getOriginalData().add(new RangeData());
		
		// sort data
		RangeData[] data=new RangeData[getOriginalData().size()];
		data=getOriginalData().toArray(data);
		int c=0;
		RangeData temp;
		while(c<data.length-1) {
			if(data[c].title.compareTo( data[c+1].title )>0) {
				temp=data[c];
				data[c]=data[c+1];
				data[c+1]=temp;
				if(c!=0) c--;
				else c++;
			} else c++;
		}
		getOriginalData().clear();
		for(int i=0;i<data.length;i++)
			getOriginalData().add(data[i]);
		
		// show data (is similiar to filter(String))
		// super.applyUpdate();
		
		// filter data
		filter(filterTitle);
	}
	@Override
	protected RangeData convertElementToObject(Element item) {
		return new RangeData( item.getChild("title").getText(),
				item.getChild("nick").getText(),
				Integer.parseInt(item.getChild("id").getText()) );
	}
	
	
	public void filter(String title) {
		filterTitle=title;
		clear();
		for(int i=0;i<getOriginalData().size();i++) {
			if(title.compareTo("")==0 || StringMatcher.match( getOriginalData().get(i).title, title )==true) {
				add(getOriginalData().get(i));
			}
		}
		//notifyDataSetChanged();
	}


	@Override
	protected void setView(int position, View v) {
		TextView t=(TextView)v.findViewById(R.id.text);
		if(getItem(position).title.compareTo("")!=0)
			t.setText( getItem(position).title );
		else
			t.setText( "전체설정" );
	}

	public int getPositionForSection(int section) {
		// If there is no item for current section, previous section will be selected
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				if (StringMatcher.match(
						String.valueOf(getItem(j).title.charAt(0)),
						String.valueOf(sectionArr.charAt(i)) ) ) {
					return j;
				}
			}
		}
		return 0;
	}

	public int getSectionForPosition(int position) {
		return 0;
	}

	public Object[] getSections() {
		// sectionArr is not initialized when view.setAdapter(this) of BaseArrayAdapter
		if(sectionArr==null) return null;
		String[] temp = new String[sectionArr.length()];
		for (int i = 0; i < sectionArr.length(); i++)
			temp[i] = String.valueOf(sectionArr.charAt(i));
		System.out.println("getSections : "+temp);
		return temp;
	}

}
