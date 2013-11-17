package org.openintents.filemanager.search;

import org.openintents.filemanager.compatibility.HomeIconHelper;
import org.openintents.filemanager.lists.SearchFileListFragment;
import org.openintents.filemanager.util.UIUtils;
import org.openintents.intents.FileManagerIntents;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;

import com.dm.DMUtil;
import com.dm.oifilemgr.R;

/**
 * The activity that handles queries and shows search results. 
 * Also handles search-suggestion triggered intents.
 * 
 * @author George Venios
 * 
 */
public class SearchableActivity extends FragmentActivity {
    
    private static final String FRAGMENT_TAG = "SearchListFragment";  
    private SearchFileListFragment mFragment;   
	private LocalBroadcastManager lbm;
	private String mPath, mQuery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		UIUtils.setThemeFor(this);
		
		// Presentation settings
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			HomeIconHelper.activity_actionbar_setDisplayHomeAsUpEnabled(this);
		}		
		lbm = LocalBroadcastManager.getInstance(getApplicationContext());
		setContentView(R.layout.activity_template);
		DMUtil.bindView(this, (ViewGroup)findViewById(R.id.btmBarLay),DMUtil.FlexibleInlinePPID2);
		
		// Add fragment only if it hasn't already been added.
        mFragment = (SearchFileListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if(mFragment == null){
            mFragment = new SearchFileListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.containerLay, mFragment, FRAGMENT_TAG).commit();
        }
		// Handle the search request.
		handleIntent();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			HomeIconHelper.showHome(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent();
	}

	private void handleIntent() {
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// Get the query.
		    mQuery = intent.getStringExtra(SearchManager.QUERY);
			setTitle(mQuery);
			// Get the current path, which allows us to refine the search.
			if (intent.getBundleExtra(SearchManager.APP_DATA) != null)
			    mPath = intent.getBundleExtra(SearchManager.APP_DATA).getString(
						FileManagerIntents.EXTRA_SEARCH_INIT_PATH);			
	        // Add query to recents.
	        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
	                this, RecentsSuggestionsProvider.AUTHORITY,
	                RecentsSuggestionsProvider.MODE);
	        suggestions.saveRecentQuery(mQuery, null);
			// Register broadcast receivers
	        lbm.registerReceiver(new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context context, Intent intent) {
	                setProgressBarIndeterminateVisibility(false);
	            }
	        }, new IntentFilter(FileManagerIntents.ACTION_SEARCH_FINISHED));	        
	        lbm.registerReceiver(new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context context, Intent intent) {
	                setProgressBarIndeterminateVisibility(true);
	            }
	        }, new IntentFilter(FileManagerIntents.ACTION_SEARCH_STARTED));
	        
	        mFragment.handIntent(mQuery, this);	        
	        refresh();
	        
		} // We're here because of a clicked suggestion
		else if(Intent.ACTION_VIEW.equals(intent.getAction())){
		    mFragment.browse(intent.getData().getPath());
		}
		else
			// Intent contents error.
			setTitle(R.string.query_error);
	}

	@Override
	protected void onDestroy() {		
	    SearchService.stopService(this);
		super.onDestroy();
	}
	
	public void refresh() {
	    // Start the search service.
	    SearchService.startService(this, mPath, mQuery);
	}
	
	/**
	 * Clear the recents' history.
	 */
	public static void clearSearchRecents(Context c) {
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(c,
				RecentsSuggestionsProvider.AUTHORITY,
				RecentsSuggestionsProvider.MODE);
		suggestions.clearHistory();
	}
}