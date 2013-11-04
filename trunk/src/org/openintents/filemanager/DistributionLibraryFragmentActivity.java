package org.openintents.filemanager;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.dm.oifilemgr.R;

public class DistributionLibraryFragmentActivity extends FragmentActivity{

	static final int MENU_DISTRIBUTION_START = Menu.FIRST;
	
	static final int DIALOG_DISTRIBUTION_START = 1;
	
	 protected MenuDrawer mMenuDrawer;
	 private View menu_main, menu_fileop;
	 	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.TOP);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
    }
    
    protected View getMenuMain() {
        if(menu_main == null) {
            menu_main = LayoutInflater.from(this).inflate(R.layout.menu_main, null, false);
        }
        return menu_main;
    }
    
    protected void showMenuMain() {
        if( mMenuDrawer.getPosition() != Position.LEFT) {
            mMenuDrawer.setMenuView(getMenuMain(), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            mMenuDrawer.setPosition(Position.LEFT);
            mMenuDrawer.setMenuSize(50);
        }
        mMenuDrawer.toggleMenu(true);
    }
    
    protected View getMenuFileOp() {
        if(menu_fileop == null) {
            menu_fileop = LayoutInflater.from(this).inflate(R.layout.menu_fileop, null, false);
        }
        return menu_fileop;
    }
    
    protected void showMenuFileOp() {
        if( mMenuDrawer.getPosition() != Position.RIGHT) {
            mMenuDrawer.setMenuView(getMenuFileOp(), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            mMenuDrawer.setPosition(Position.RIGHT);
            mMenuDrawer.setMenuSize(80);
        }
        mMenuDrawer.toggleMenu(true);
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
