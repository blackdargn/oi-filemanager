package com.dm;

import android.preference.PreferenceActivity;
import android.view.ViewGroup;
import android.view.Window;

public class DMPrefenceActivity extends PreferenceActivity {
    
    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        DMUtil.bindView(this, (ViewGroup)getListView().getRootView(), DMUtil.FlexibleInlinePPID1);
        getListView().addFooterView(DMUtil.createAdView(this, DMUtil.FlexibleInlinePPID2));
    }
}