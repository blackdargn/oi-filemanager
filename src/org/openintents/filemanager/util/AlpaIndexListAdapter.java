package org.openintents.filemanager.util;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.openintents.filemanager.view.MyLetterListView;
import org.openintents.filemanager.view.MyLetterListView.OnTouchingLetterChangedListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public abstract class AlpaIndexListAdapter<T> extends ArrayListAdapter<T> implements OnTouchingLetterChangedListener{

    // 存放存在的汉语拼音首字母和与之对应的列表位置
    protected HashMap<String, Integer> alphaIndexer;
    private Handler handler;
    private OverlayThread overlayThread;
    private TextView overlay;
    private MyLetterListView letterListView;
    
    public AlpaIndexListAdapter(Context context) {
        super(context);
    }
    
    public void initAlpaIndex(TextView overlay, ListView listView, MyLetterListView letterListView) {
        this.handler = new Handler();
        this.overlayThread = new OverlayThread();
        this.alphaIndexer = new HashMap<String, Integer>();
        this.overlay = overlay;
        this.mListView = listView;
        this.letterListView  = letterListView;
    }
    
    public synchronized void setList(List<T> files) {
        super.setList(files);
        initData();
    }
      
    /** 只支持 实现了 T implements PYRender 的类型，不然报错*/
    protected void initData() {
        if(alphaIndexer == null || mList == null || mList.size() == 0) {
            if(letterListView != null) {
                letterListView.setVisibility(View.GONE);
            }
            return;
        }
        if(letterListView != null) {
            letterListView.setVisibility(getCount() < 8 ? View.GONE : View.VISIBLE);
        }
        String currentStr, previewStr;
        try {
            for (int i = 0; i < mList.size(); i++) {
                  currentStr = getAlpha(((PYRender)mList.get(i)).getPYName());
                  previewStr = getAlpha((i - 1) >= 0 ? ((PYRender)mList.get(i - 1)).getPYName() : "");
                  if (!currentStr.equals(previewStr)) {
                      alphaIndexer.put(currentStr, i);
                  }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        alphaIndexer.put("#", 0);
    }
    
    @Override
    public void onTouchingLetterChanged(final String s) {
        Integer position = alphaIndexer.get(s);
        if (position != null) {
            mListView.requestFocusFromTouch();
            mListView.setSelection(position + mListView.getHeaderViewsCount());
        }
        overlay.setText(s);
        overlay.setVisibility(View.VISIBLE);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 1500);
    }
    
    public static  interface PYRender{
        public String getPYName();
    }

    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }
    
    @SuppressLint("DefaultLocale")
    public static String getAlpha(String str) {
        if (str == null) { return "#"; }

        if (str.trim().length() == 0) { return "#"; }

        char c = str.trim().substring(0, 1).charAt(0);

        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        Pattern patternC = Pattern.compile("^[\u4e00-\u9fa5]+$");
        // 字母
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else 
        // 中文
        if(patternC.matcher(c + "").matches())
        {
            return c + "";
        }else
        // 其它 #
        {
            return "#";
        }
    }
}