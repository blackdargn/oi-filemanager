package org.openintents.filemanager.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dm.oifilemgr.R;



public class MyLetterListView extends View {
	
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private String[] b = {"#","A","B","C","D","E","F","G","H","I","J","K","L"
			,"M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private int choose = -1;
	private Paint paint = new Paint();
	private boolean showBkg = false;
	private float textSize = 0;
	
	public MyLetterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLetterListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyLetterListView(Context context) {
		this(context, null);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(showBkg){
//		    canvas.drawColor(Color.parseColor("#40000000"));
		}
		
	    int height = getHeight();
	    int width  = getWidth();	    
	    int singleHeight = height / b.length;
	    adjustTextSize(singleHeight);
	    
	    for(int i=0;i<b.length;i++){
	       paint.setColor(Color.parseColor("#666666"));
	       paint.setTypeface(Typeface.DEFAULT_BOLD);
	       paint.setAntiAlias(true);
	       if(i == choose){
	    	   paint.setColor(Color.parseColor("#3399ff"));
	    	   paint.setFakeBoldText(true);
	       }
	       float xPos = width/2  - paint.measureText(b[i])/2;
	       float yPos = singleHeight * i + singleHeight;
	       canvas.drawText(b[i], xPos, yPos, paint);
	       paint.reset();
	    }
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
	    final float y = event.getY();
	    final int oldChoose = choose;
	    final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
	    final int c = (int) (y/getHeight()*b.length);
	    
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				showBkg = true;
				setBackgroundResource(R.drawable.bg_char);
				if(oldChoose != c && listener != null){
					if(c >= 0 && c< b.length){
						listener.onTouchingLetterChanged(b[c]);
						choose = c;
						invalidate();
					}
				}
				
				break;
			case MotionEvent.ACTION_MOVE:
				if(oldChoose != c && listener != null){
					if(c >= 0 && c< b.length){
						listener.onTouchingLetterChanged(b[c]);
						choose = c;
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				showBkg = false;
				setBackgroundResource(R.drawable.bg_transparent);
				choose = -1;
				invalidate();
				break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener{
		public void onTouchingLetterChanged(String s);
	}
		
	/** 自适应高度改变字体大小*/
	private void adjustTextSize(int singleHeight) {
	    if(textSize > 0) return;
	    int fontHeight = 0;
	    FontMetrics fm = null;
	    textSize = paint.getTextSize();
	    while(true) {
    	    paint.setTextSize(textSize);
    	    fm = paint.getFontMetrics();
    	    fontHeight = (int) Math.ceil(fm.descent - fm.top) + 2;
    	    if(fontHeight > singleHeight) {
    	        textSize -= 0.5f;
    	        break;
    	    }else {
    	        textSize += 0.5f;
    	    }
	    }
	}
}
