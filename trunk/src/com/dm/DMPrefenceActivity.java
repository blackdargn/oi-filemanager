package com.dm;

import android.preference.PreferenceActivity;
import android.view.ViewGroup;
import android.view.Window;

public class DMPrefenceActivity extends PreferenceActivity {
    
    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        MLinelayout line1 = new MLinelayout(this);
        line1.addView(DMUtil.createAdView(this, DMUtil.FlexibleInlinePPID1));
        ((ViewGroup)getListView().getRootView()).addView(line1);
        MLinelayout line2 = new MLinelayout(this);
        line2.addView(DMUtil.createAdView(this, DMUtil.FlexibleInlinePPID2));
        getListView().addFooterView(line2);
    }
}