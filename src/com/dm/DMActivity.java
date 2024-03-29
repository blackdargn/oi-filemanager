package com.dm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import cn.domob.android.ads.DomobAdView;

import com.dm.oifilemgr.R;

public class DMActivity extends Activity {
    
    private DomobAdView adView1;
    private DomobAdView adView2;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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
        super.setContentView(R.layout.activity_template);
        LinearLayout topBarLay = (LinearLayout)findViewById(R.id.topBarLay);
        LinearLayout btmBarLay = (LinearLayout)findViewById(R.id.btmBarLay);
        LinearLayout containerLay = (LinearLayout)findViewById(R.id.containerLay);
        
        View view = getLayoutInflater().inflate(layoutResID, null);     
        containerLay.addView(view,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    
        if(!DMUtil.isRealease) adView1 = DMUtil.bindView(this, topBarLay, DMUtil.FlexibleInlinePPID1);
        adView2 = DMUtil.bindView(this, btmBarLay, DMUtil.FlexibleInlinePPID2);
    }
}