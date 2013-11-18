package org.openintents.filemanager.view;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dm.oifilemgr.R;

public class MenuPopWin extends PopupWindow implements OnClickListener{
    
    private FlowLayout flowView;
    private ConcurrentHashMap<Integer, View> viewMaps = new ConcurrentHashMap<Integer, View>();
    private OnOptionsItemSelectedListener[] mListeners;
    private Context mContext;
    
    public MenuPopWin(Context context, OnOptionsItemSelectedListener[] listeners) {
        super(context);
        mContext = context;
        mListeners = listeners;
        flowView = new FlowLayout(context);
        flowView.setBackgroundResource(R.drawable.bg_menu);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(flowView);
        setWidth(LayoutParams.FILL_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        // SetFocusable to true,If not set set true,the item in the TabMenu will not handle the touch event.
        setFocusable(true);
    }
    
    public void builder(Menu menu) {
        flowView.removeAllViews();
        
        int size = menu.size();
        MenuItem item = null;
        for(int i = 0; i < size; i++) {
            item = menu.getItem(i);
            flowView.addView(makeMenuBody(item));
        }
    }
    
    private View makeMenuBody(MenuItem item) {
        if(viewMaps.containsKey(item.getItemId())) {
            return viewMaps.get(item.getItemId());
        }                
        LinearLayout result = new LinearLayout(mContext);
        result.setId(item.getItemId());
        result.setOrientation(LinearLayout.VERTICAL);
        result.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        result.setPadding(2, 2, 2, 2);

        TextView text = new TextView(mContext);
        text.setSingleLine(true);
        text.setText(item.getTitle());
        text.setTextSize(16);
        if (item.isEnabled()) {
            text.setTextColor(Color.BLACK);
        } else {
            text.setTextColor(Color.GRAY);
        }
        text.setGravity(Gravity.CENTER);
        text.setPadding(1, 1, 1, 1);
        
        ImageView img = new ImageView(this.mContext);
        img.setBackgroundDrawable(item.getIcon());
        result.addView(img, new LinearLayout.LayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
        result.addView(text, new LinearLayout.LayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));

        result.setVisibility(item.isVisible() ? View.VISIBLE : View.GONE);

        result.setTag(item);
        result.setOnClickListener(this);
        result.setBackgroundResource(R.drawable.btn_menu);
               
        viewMaps.put(result.getId(), result);
        return result;
    }
    
    @Override
    public void onClick(View v) {
        MenuItem item = (MenuItem) v.getTag();
        for( OnOptionsItemSelectedListener mListener : mListeners) {
            mListener.onOptionsItemSelected(item);
        }
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 500);
    }
    
    public static interface OnOptionsItemSelectedListener{
        public boolean onOptionsItemSelected(MenuItem item);
    }
}
