package org.openintents.filemanager.search;

import java.io.File;

import org.openintents.filemanager.ThumbnailLoader;
import org.openintents.filemanager.util.MessageBus;
import org.openintents.filemanager.util.MessageBus.MMessage;
import org.openintents.intents.FileManagerIntents;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service that asynchronously executes file searches.
 * 
 * @author George Venios.
 * 
 */
public class SearchService extends Service implements Runnable {
    
    public static final String ACTION_START_SEARCH = "android.dm.start_search";
    public static final String ACTION_STOP_SEARCH  = "android.dm.stop_search";
    
	private SearchCore searcher;
	private Intent mIntent;
	
	@Override
	public void onCreate() {
		super.onCreate();
		searcher = new SearchCore(this);
	}

	protected void onHandleIntent(Intent intent) {
		// The search query
		searcher.setQuery(intent.getStringExtra(FileManagerIntents.EXTRA_SEARCH_QUERY));

		// Set initial path. To be searched first!
		String path = intent.getStringExtra(FileManagerIntents.EXTRA_SEARCH_INIT_PATH);
		File root = null;
		if (path != null)
			root = new File(path);
		else
			root = new File("/");

		// Search started, let Receivers know.
		MMessage msg = MessageBus.getBusFactory().createMessage(FileManagerIntents.MSG_SEARCH_STARTED);
		MessageBus.getBusFactory().send(msg);
		
		// Search in current path.
		searcher.dropPreviousResults();
		searcher.setRoot(root);
		searcher.search(root);

		// Search is over, let Receivers know.
		MMessage msg2 = MessageBus.getBusFactory().createMessage(FileManagerIntents.MSG_SEARCH_FINISHED);
        MessageBus.getBusFactory().send(msg2);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    String action = intent.getAction();
	    if(action.equals(ACTION_START_SEARCH)) {
	        searcher.cancel();
	        mIntent = intent;
	        ThumbnailLoader.submit(this);
	        Log.d("$$$", "-->start search");
	    }else
	    if(action.equals(ACTION_STOP_SEARCH)) {
	        searcher.cancel();
	        stopSelf();
	        Log.d("$$$", "-->stop search");
	    }
	    return super.onStartCommand(intent, flags, startId);
	}
	
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void run() {
        onHandleIntent(mIntent);
    }
    
    public static void startService(Context context, String path, String qurery) {
        Intent in = new Intent(ACTION_START_SEARCH);
        in.putExtra(FileManagerIntents.EXTRA_SEARCH_INIT_PATH, path);
        in.putExtra(FileManagerIntents.EXTRA_SEARCH_QUERY, qurery);
        context.startService(in);
    }
    
    public static void stopService(Context context) {
        Intent in = new Intent(ACTION_STOP_SEARCH);
        context.startService(in);
    }
}