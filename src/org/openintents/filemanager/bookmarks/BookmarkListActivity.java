package org.openintents.filemanager.bookmarks;

import org.openintents.filemanager.compatibility.HomeIconHelper;
import org.openintents.filemanager.util.UIUtils;

import com.dm.DMUtil;
import com.dm.oifilemgr.R;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.ViewGroup;

public class BookmarkListActivity extends FragmentActivity {
	private static final String FRAGMENT_TAG = "Fragment";
	
	public static String KEY_RESULT_PATH = "path";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		UIUtils.setThemeFor(this);
		super.onCreate(savedInstanceState);				
			 
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
			HomeIconHelper.activity_actionbar_setDisplayHomeAsUpEnabled(this);
		}
		
		setContentView(R.layout.activity_template);
        DMUtil.bindView(this, (ViewGroup)findViewById(R.id.btmBarLay), DMUtil.FlexibleInlinePPID2);
		
		if(getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null)
			getSupportFragmentManager().beginTransaction().add(R.id.containerLay, new BookmarkListFragment(), FRAGMENT_TAG).commit();
	}

	public void onListItemClick(String path) {
		setResult(RESULT_OK, new Intent().putExtra(KEY_RESULT_PATH, path));
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			HomeIconHelper.showHome(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}