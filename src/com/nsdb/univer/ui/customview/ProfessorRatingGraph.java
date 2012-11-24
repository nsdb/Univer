package com.nsdb.univer.ui.customview;

import com.nsdb.univer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class ProfessorRatingGraph extends View {
	
	private Paint paint;
	private Path path;
	double quality,report,grade,attendance,personality;
	
	public ProfessorRatingGraph(Context context) {
		super(context);
		init();
	}
	public ProfessorRatingGraph(Context context, AttributeSet attrs) {
		super(context,attrs);
		init();
	}
	
	private void init() {
		this.quality=0.0;
		this.report=0.0;
		this.grade=0.0;
		this.attendance=0.0;
		this.personality=0.0;
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		path=new Path();		
	}
	
	
	public void setRating(double q,double r,double g,double a,double p) {
		quality=q;
		report=r;
		grade=g;
		attendance=a;
		personality=p;
		requestLayout();
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float density=getContext().getResources().getDisplayMetrics().density;
		System.out.println("density : "+density);
		setMeasuredDimension(Math.round(420*density*0.66625f),Math.round(380*density*0.66625f));
	}	
	@Override
	protected void onDraw(Canvas c) {
		float density=getContext().getResources().getDisplayMetrics().density;
		int cx=getWidth()/2;
		int cy=getHeight()/2+Math.round(10*density*0.66625f);
		int r=Math.round(160*density*0.66625f);
		paint.reset();
		// circle
		BitmapDrawable cDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.pf_circle);
		Bitmap cBitmap=cDrawable.getBitmap();
		c.drawBitmap(cBitmap,cx-r,cy-r,null);
		// 5 layered pentagon
		/*
		paint.setStyle(Paint.Style.FILL);
		for(int n=0;n<5;n++) {
			paint.setColor(Color.rgb(225-5*n,225-5*n,225-5*n));
			path.reset();
			path.moveTo(cx,cy);
			for(int i=0;i<6;i++) {
				path.lineTo(
						(float)(cx+r*Math.cos(Math.PI*(0.5+0.4*i))*(1.0-0.2*n)),
						(float)(cy+r*-Math.sin(Math.PI*(0.5+0.4*i))*(1.0-0.2*n)) );
			}
			c.drawPath(path, paint);
		}
		*/
		// distorted pentagon
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.rgb(100,170,200));
		double[] percents={quality/10.0,grade/10.0,report/10.0,attendance/10.0,personality/10.0};
		path.reset();
		path.moveTo(cx,cy);
		for(int i=0;i<6;i++) {
			path.lineTo(
					(float)(cx+r*Math.cos(Math.PI*(0.5+0.4*i))*Math.max(percents[i%5],0.05) ),
					(float)(cy+r*-Math.sin(Math.PI*(0.5+0.4*i))*Math.max(percents[i%5],0.05) ) );
		}
		c.drawPath(path, paint);
		// center line
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1*density*0.66625f);
		paint.setColor(Color.rgb(255,255,255));
		for(int i=0;i<6;i++) {
			c.drawLine(cx,cy,
					(float)(cx+r*Math.cos(Math.PI*(0.5+0.4*i))),
					(float)(cy-r*Math.sin(Math.PI*(0.5+0.4*i))),paint );
		}
		// text
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(20*density*0.66625f);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.rgb(20,20,20));
		String[] textArgs={"강의 질","학점","과제","출석","인성"};
		double[] rr={1.1,1.2,1.2,1.2,1.2};
		for(int i=0;i<5;i++) {
			c.drawText(textArgs[i],
					(float)(cx+r*Math.cos(Math.PI*(0.5+0.4*i))*rr[i]),
					(float)(cy+r*-Math.sin(Math.PI*(0.5+0.4*i))*rr[i]),paint );
		}
	}

}
