package org.openintents.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DistributionLibraryFragmentActivity extends FragmentActivity{

	static final int MENU_DISTRIBUTION_START = Menu.FIRST;
	static final int DIALOG_DISTRIBUTION_START = 1;
	 	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
