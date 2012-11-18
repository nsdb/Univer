package com.nsdb.univer.ui.customview;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;
import android.view.View.OnClickListener;

public class ImageArrayRadioGroup extends LinearLayout implements OnClickListener {

	int count;
	ArrayList<ImageView> list;
	ArrayList<Integer> uncheckedId;
	ArrayList<Integer> checkedId;
	int checked;
	
	public ImageArrayRadioGroup(Context context) {
		super(context);
		count=0;
		list=new ArrayList<ImageView>();
		uncheckedId=new ArrayList<Integer>();
		checkedId=new ArrayList<Integer>();
		checked=0;
	}
	public ImageArrayRadioGroup(Context context, AttributeSet attrs) {
		super(context,attrs);
		count=0;
		list=new ArrayList<ImageView>();
		uncheckedId=new ArrayList<Integer>();
		checkedId=new ArrayList<Integer>();
		checked=0;
	}
	
	public void addImage(int uncheckedId,int checkedId) {
		ImageView btn=new ImageView(getContext());
		if(count==0)
			btn.setImageResource(checkedId);
		else
			btn.setImageResource(uncheckedId);
		btn.setOnClickListener(this);
		addView(btn,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	}
	
	public void onClick(View v) {
		for(int i=0;i<list.size();i++) {
			if(v.hashCode()==list.get(i).hashCode()) {
				list.get(checked).setImageResource(uncheckedId.get(checked));
				list.get(i).setImageResource(checkedId.get(i));
				checked=i;
				break;
			}
		}
	}
}
