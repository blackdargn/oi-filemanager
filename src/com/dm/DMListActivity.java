package com.dm;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import cn.domob.android.ads.DomobAdView;

import com.dm.oifilemgr.R;

public class DMListActivity extends ListActivity {
    
    private DomobAdView adView1;
    private DomobAdView adView2;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isNoTitle()) getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }
    
    public boolean isNoTitle() {
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        DMUtil.requestRefresh(adView1);
        DMUtil.requestRefresh(adView2);
    }
        
    @Override
    public void setContentView(int layoutResID)
    {
        ViewGroup template = (ViewGroup)LayoutInflater.from(this).inflate(R.layout.activity_template, null);
        
        LinearLayout topBarLay = (LinearLayout)template.findViewById(R.id.topBarLay);
        LinearLayout btmBarLay = (LinearLayout)template.findViewById(R.id.btmBarLay);
        LinearLayout containerLay = (LinearLayout)template.findViewById(R.id.containerLay);
        
        View view = getLayoutInflater().inflate(layoutResID, null);     
        containerLay.addView(view,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    
        if(!DMUtil.isRealease) adView1 = DMUtil.bindView(this, topBarLay, DMUtil.FlexibleInlinePPID1);
        adView2 = DMUtil.bindView(this, btmBarLay, DMUtil.FlexibleInlinePPID2);
        
        setContentView(template);
        if(!DMUtil.isRealease) {
            MLinelayout line = new MLinelayout(this);
            line.addView(DMUtil.createAdView(this, DMUtil.FlexibleInlinePPID2));
            getListView().addFooterView(line);
        }
    }
}