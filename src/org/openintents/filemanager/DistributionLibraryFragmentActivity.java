package org.openintents.filemanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dm.oifilemgr.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class DistributionLibraryFragmentActivity extends SlidingFragmentActivity{

	static final int MENU_DISTRIBUTION_START = Menu.FIRST;
	
	static final int DIALOG_DISTRIBUTION_START = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // configure the SlidingMenu
        setBehindContentView(R.layout.menu_main);
        SlidingMenu menu = getSlidingMenu();
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);       
        menu.setFadeDegree(0.35f);
        menu.setBehindWidth(40);
    }

 	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
 		super.onCreateOptionsMenu(menu);
 		return true;
 	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
