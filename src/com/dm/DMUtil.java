package com.dm;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;
import cn.domob.android.ads.DomobAdEventListener;
import cn.domob.android.ads.DomobAdView;
import cn.domob.android.ads.DomobAdManager.ErrorCode;

public class DMUtil {
    // attention this  
    public static final String PUBLISHER_ID = "56OJzVlYuNOOvu5mDp";
    public static final String FlexibleInlinePPID1 = "16TLm7ZaAp0tWNU-Ux1inwXs";
    public static final String FlexibleInlinePPID2 = "16TLm7ZaAp0tWNU-UsjJ5S-i";
    public static final int BUFFED = 6;
    public static long count = 0;
    public static final boolean isRealease = false;  
    public static boolean isShowAd = false;
    
    public static void checkShowAd(Context context) {
        if(isRealease) {
            long firstTime = PreferenceManager.getDefaultSharedPreferences(context).getLong("firstTime", 0);
            if(firstTime == 0) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("firstTime", firstTime = System.currentTimeMillis()).commit();
            }
            isShowAd = System.currentTimeMillis() - firstTime > 5*24*60*60*1000l;
        }else {
            isShowAd = true;
        }
    }
    
    public static boolean isBuffed() {
        if(isRealease) {
            return false;
        }else {
            count++;
            return count%BUFFED != 0;
        }
    }
    
    public static DomobAdView bindView(final Activity activity, ViewGroup container, String placeId) {
        if(!isShowAd) return null;
        DomobAdView mAdviewFlexibleAdView = createAdView(activity, placeId);
        container.addView(mAdviewFlexibleAdView);
        return mAdviewFlexibleAdView;
    }
    
    public static DomobAdView createAdView( final Activity activity, String placeId) {
        check();
        DomobAdView mAdviewFlexibleAdView = new DomobAdView(activity, DMUtil.PUBLISHER_ID, placeId, DomobAdView.INLINE_SIZE_FLEXIBLE);
        mAdviewFlexibleAdView.setKeyword("game");
        mAdviewFlexibleAdView.setUserGender("male");
        mAdviewFlexibleAdView.setUserBirthdayStr("1984-05-08");
        mAdviewFlexibleAdView.setUserPostcode("518000");
        mAdviewFlexibleAdView.setAdEventListener(new DomobAdEventListener() {
            @Override
            public void onDomobLeaveApplication(DomobAdView arg0) {
                
            }
            
            @Override
            public void onDomobAdReturned(DomobAdView arg0) {
                
            }
            
            @Override
            public Context onDomobAdRequiresCurrentContext() {
                return activity;
            }
            
            @Override
            public void onDomobAdOverlayPresented(DomobAdView arg0) {
                
            }
            
            @Override
            public void onDomobAdOverlayDismissed(DomobAdView arg0) {
                
            }
            
            @Override
            public void onDomobAdFailed(DomobAdView view, ErrorCode arg1) {
                if(isNetAvaliable(view.getContext())) {
                    view.requestRefreshAd();
                    view.setRefreshable(true);
                }
                Log.d("##", "Failed!");
            }
            
            @Override
            public void onDomobAdClicked(DomobAdView arg0) {
                
            }
        });
        return mAdviewFlexibleAdView;
    }
    
    public static void requestRefresh(DomobAdView view) {
        if(view == null) return;
        if(isNetAvaliable(view.getContext())) {
            view.requestRefreshAd();
            view.setRefreshable(true);
        }else {
            Log.d("##", "network Failed!");
        }
    }
    
    public static boolean isNetAvaliable(Context context)
    {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr == null) return false;
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ( (wifi == null || !wifi.isAvailable()) && ( mobile == null || !mobile.isAvailable()))
        {
            return false;
        }
        return true;
    }
    
    private static boolean isClear = false;
    public static void check() {
        if(!isClear) {
            isClear = true;
            if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        delDir(getExternDir("/DomobAppDownload/"));
                    }
                }).start();
           }
        }
    }
    
    /** 删除目录下的文件 或者 该文件， 不删除目录 */
    public static boolean delDir(File fd) {
        if (fd == null)
            return false;
        if (!fd.exists())
            return true;
        if (!fd.isDirectory()) {
            return fd.delete();
        } else {
            File[] files = fd.listFiles();
            // 目录下有文件
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        delDir(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            return true;
        }
    }

    public static File getExternDir(String dir) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += dir;
        return new File(path);
    }
}