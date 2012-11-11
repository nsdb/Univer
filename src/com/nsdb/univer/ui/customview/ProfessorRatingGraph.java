package com.nsdb.univer.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
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
		setMeasuredDimension(600,600);
	}	
	@Override
	protected void onDraw(Canvas c) {
		paint.reset();
		// circle
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.rgb(225,225,225));
		c.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2,paint);
		// 5 layered pentagon
		paint.setStyle(Paint.Style.FILL);
		for(int n=0;n<5;n++) {
			paint.setColor(Color.rgb(200-10*n,200-10*n,200-10*n));
			path.reset();
			path.moveTo(getWidth()/2,getHeight()/2);
			for(int i=0;i<6;i++) {
				path.lineTo(
						(float)(getWidth()/2*(1+Math.cos(Math.PI*(0.5+0.4*i))*(1.0-0.2*n))),
						(float)(getHeight()/2*(1+Math.sin(Math.PI*(0.5+0.4*i))*(1.0-0.2*n))) );
			}
			c.drawPath(path, paint);
		}
		// distorted pentagon
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.rgb(240,240,0));
		double[] percents={quality/10.0,report/10.0,grade/10.0,attendance/10.0,personality/10.0};
		path.reset();
		path.moveTo(getWidth()/2,getHeight()/2);
		for(int i=0;i<6;i++) {
			path.lineTo(
					(float)(getWidth()/2*(1+Math.cos(Math.PI*(0.5+0.4*i))*percents[i%5])),
					(float)(getHeight()/2*(1+Math.sin(Math.PI*(0.5+0.4*i))*percents[i%5])) );
		}
		c.drawPath(path, paint);
		// center line
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.rgb(225,225,225));
		for(int i=0;i<6;i++) {
			c.drawLine(getWidth()/2,getHeight()/2,
					(float)(getWidth()/2*(1+Math.cos(Math.PI*(0.5+0.4*i)))),
					(float)(getHeight()/2*(1+Math.sin(Math.PI*(0.5+0.4*i)))),paint );
		}
		// text
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(50);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.rgb(20,20,20));
		String[] textArgs={"Q","R","G","A","P"};
		for(int i=0;i<5;i++) {
			c.drawText(textArgs[i],
					(float)(getWidth()/2*(1+Math.cos(Math.PI*(0.5+0.4*i))*0.9)),
					(float)(getHeight()/2*(1+Math.sin(Math.PI*(0.5+0.4*i))*0.9)),paint );
		}
	}

}
