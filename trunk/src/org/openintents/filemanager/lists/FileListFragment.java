package org.openintents.filemanager.lists;

import java.io.File;
import java.util.ArrayList;

import org.openintents.filemanager.FileHolderListAdapter;
import org.openintents.filemanager.dialogs.CreateDirectoryDialog;
import org.openintents.filemanager.files.DirectoryContents;
import org.openintents.filemanager.files.DirectoryScanner;
import org.openintents.filemanager.files.FileHolder;
import org.openintents.filemanager.util.EditTextWatcher;
import org.openintents.filemanager.util.MenuUtils;
import org.openintents.filemanager.util.EditTextWatcher.OnTextChanged;
import org.openintents.filemanager.util.MimeTypes;
import org.openintents.intents.FileManagerIntents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.dm.oifilemgr.R;

/**
 * A {@link ListFragment} that displays the contents of a directory.
 * <p>
 * Clicks do nothing.
 * </p>
 * <p>
 * Refreshes OnSharedPreferenceChange
 * </p>
 * 
 * @author George Venios
 */
public abstract class FileListFragment extends ListFragment implements OnClickListener {
	private static final String INSTANCE_STATE_PATH = "path";
	private static final String INSTANCE_STATE_FILES = "files";
	File mPreviousDirectory = null;

	// Not an anonymous inner class because of:
	// http://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently
	private OnSharedPreferenceChangeListener preferenceListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// We only care for list-altering preferences. This could be
			// dangerous though,
			// as later contributors might not see this, and have their settings
			// not work in realtime.
			// Therefore this is commented out, since it's not likely the
			// refresh is THAT heavy.
			// *****************
			// if (PreferenceActivity.PREFS_DISPLAYHIDDENFILES.equals(key)
			// || PreferenceActivity.PREFS_SORTBY.equals(key)
			// || PreferenceActivity.PREFS_ASCENDING.equals(key))

			// Prevent NullPointerException caused from this getting called
			// after we have finish()ed the activity.
			if (getActivity() != null)
				refresh();
		}
	};

	protected FileHolderListAdapter mAdapter;
	protected DirectoryScanner mScanner;
	protected ArrayList<FileHolder> mFiles = new ArrayList<FileHolder>();
	protected ArrayList<FileHolder> mFilterFiles = new ArrayList<FileHolder>();
	private String mPath;
	private String mFilename;

	private ViewFlipper mFlipper;
	private File mCurrentDirectory;
	
	private EditText et_file_filter;
	private EditTextWatcher watcher;
	private View toolbar;
	private View btn_add_book, btn_select, btn_search, btn_new_folder;
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(INSTANCE_STATE_PATH, mPath);
		outState.putParcelableArrayList(INSTANCE_STATE_FILES, mFiles);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.filelist, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		// Set auto refresh on preference change.
		PreferenceManager.getDefaultSharedPreferences(getActivity())
				.registerOnSharedPreferenceChangeListener(preferenceListener);

		// Set list properties
		getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					mAdapter.setScrolling(false);
				} else
					mAdapter.setScrolling(true);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		getListView().requestFocus();
		getListView().requestFocusFromTouch();

		// Init flipper
		mFlipper = (ViewFlipper) view.findViewById(R.id.flipper);
		et_file_filter = (EditText) view.findViewById(R.id.et_file_filter);
		btn_add_book = view.findViewById(R.id.btn_add_book);
		btn_select = view.findViewById(R.id.btn_select);
		btn_search = view.findViewById(R.id.btn_search);
		btn_new_folder = view.findViewById(R.id.btn_new_folder);
		toolbar = view.findViewById(R.id.toolbar);
		
		btn_add_book.setOnClickListener(this);
		btn_select.setOnClickListener(this);
		btn_search.setOnClickListener(this);
		btn_new_folder.setOnClickListener(this);
		
		watcher = new EditTextWatcher();
	    watcher.setOnTextChanaged(new OnTextChanged() {
	            @Override
	            public void onExecute(CharSequence s, int start, int before,
	                    int count) {
	                if (s == null || s.length() == 0) {
	                    filterPath(null);
	                    return;
	                }
	                filterPath(s.toString());
	            }
	    });	    
		
		// Get arguments
		if (savedInstanceState == null) {
			mPath = getArguments().getString(FileManagerIntents.EXTRA_DIR_PATH);
			mFilename = getArguments().getString(
					FileManagerIntents.EXTRA_FILENAME);
		} else {
			mPath = savedInstanceState.getString(INSTANCE_STATE_PATH);
			mFiles = savedInstanceState
					.getParcelableArrayList(INSTANCE_STATE_FILES);
		}
		pathCheckAndFix();
		renewScanner();
		mAdapter = new FileHolderListAdapter(mFiles, getActivity());

		setListAdapter(mAdapter);
		mScanner.start();

	}

	@Override
	public void onDestroy() {
		mScanner.cancel();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
	    switch(v.getId()) {
    	    case R.id.btn_add_book:{
    	        MenuUtils.addBookMarker(getActivity(), new FileHolder(new File(getPath()), getActivity()));
    	        break;
    	    }
    	    case R.id.btn_select:{
    	        onMutilSelect();
                break;
            }
    	    case R.id.btn_search:{
    	        onSearchRequested();
                break;
            }
    	    case R.id.btn_new_folder:{
    	        onNewFloder();
                break;
            }
	    }
	}
	
	protected void hideToolbar() {
	    toolbar.setVisibility(View.GONE);
	}
	
	public void onNewFloder() {
	    CreateDirectoryDialog dialog = new CreateDirectoryDialog();
        dialog.setTargetFragment(this, 0);
        Bundle args = new Bundle();
        args.putString(FileManagerIntents.EXTRA_DIR_PATH, getPath());
        dialog.setArguments(args);
        dialog.show(getActivity().getSupportFragmentManager(), CreateDirectoryDialog.class.getName());
	}
	
	public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putString(FileManagerIntents.EXTRA_SEARCH_INIT_PATH, getPath());
        getActivity().startSearch(null, false, appData, false);
        
        return true;
    }
	
	public void onMutilSelect() {
	    
	}
	
	/**
	 * Reloads {@link #mPath}'s contents.
	 */
	public void refresh() {
		// Cancel and GC previous scanner so that it doesn't load on top of the
		// new list.
		// Race condition seen if a long list is requested, and a short list is
		// requested before the long one loads.
		mScanner.cancel();
//		mScanner = null;

		// Indicate loading and start scanning.
		setLoading(true);
		renewScanner().start();
	}

	/**
	 * Make the UI indicate loading.
	 */
	private void setLoading(boolean show) {
		mFlipper.setDisplayedChild(show ? 0 : 1);
		onLoadingChanged(show);
	}

	protected void selectInList(File selectFile) {
		String filename = selectFile.getName();

		int count = mAdapter.getCount();
		for (int i = 0; i < count; i++) {
			FileHolder it = (FileHolder) mAdapter.getItem(i);
			if (it.getName().equals(filename)) {
				getListView().setSelection(i);
				break;
			}
		}
	}

	/**
	 * Recreates the {@link #mScanner} using the previously set arguments and
	 * {@link #mPath}.
	 * 
	 * @return {@link #mScanner} for convenience.
	 */
	protected DirectoryScanner renewScanner() {
		String filetypeFilter = getArguments().getString(
				FileManagerIntents.EXTRA_FILTER_FILETYPE);
		String mimetypeFilter = getArguments().getString(
				FileManagerIntents.EXTRA_FILTER_MIMETYPE);
		boolean writeableOnly = getArguments().getBoolean(
				FileManagerIntents.EXTRA_WRITEABLE_ONLY);
		boolean directoriesOnly = getArguments().getBoolean(
				FileManagerIntents.EXTRA_DIRECTORIES_ONLY);

		if(mScanner == null) {
    		mScanner = new DirectoryScanner(new File(mPath), getActivity(),
    				new FileListMessageHandler(),
    				MimeTypes.newInstance(getActivity()),
    				filetypeFilter == null ? "" : filetypeFilter,
    				mimetypeFilter == null ? "" : mimetypeFilter, writeableOnly,
    				directoriesOnly);
		}else {
		    mScanner.setup(new File(mPath), getActivity(),
                    new FileListMessageHandler(),
                    MimeTypes.newInstance(getActivity()),
                    filetypeFilter == null ? "" : filetypeFilter,
                    mimetypeFilter == null ? "" : mimetypeFilter, writeableOnly,
                    directoriesOnly);
		}
		return mScanner;
	}

	private class FileListMessageHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case DirectoryScanner.MESSAGE_SHOW_DIRECTORY_CONTENTS:
				DirectoryContents c = (DirectoryContents) msg.obj;
				
				et_file_filter.removeTextChangedListener(watcher);
				
				mFiles.clear();
				mFiles.addAll(c.listSdCard);
				mFiles.addAll(c.listDir);
				mFiles.addAll(c.listFile);

				mAdapter.setList(mFiles);
				
				et_file_filter.setText("");
				et_file_filter.addTextChangedListener(watcher);
				
				if (mPreviousDirectory != null){
					selectInList(mPreviousDirectory);
				} else {
					// Reset list position.
//					if (mFiles.size() > 0) getListView().setSelection(0);					
				}
				setLoading(false);
				break;
			case DirectoryScanner.MESSAGE_SET_PROGRESS:
				// Irrelevant.
				break;
			}
		}
	}

	/**
	 * Used to inform subclasses about loading state changing. Can be used to
	 * make the ui indicate the loading state of the fragment.
	 * 
	 * @param loading
	 *            If the list started or stopped loading.
	 */
	protected void onLoadingChanged(boolean loading) {
	}

	/**
	 * @return The currently displayed directory's absolute path.
	 */
	public final String getPath() {
		return mPath;
	}

	/**
	 * This will be ignored if path doesn't pass check as valid.
	 * 
	 * @param dir
	 *            The path to set.
	 */
	public final void setPath(File dir) {
		
		if (dir.exists() && dir.isDirectory()){
			mPreviousDirectory = mCurrentDirectory;
			mCurrentDirectory = dir;
			mPath = dir.getAbsolutePath();
			
		}
	}
	
	/** 过滤文件*/
	public void filterPath(String str) {
	    mFilterFiles.clear();
	    if(!TextUtils.isEmpty(str)) {
    	    for(FileHolder file : mFiles) {
    	        if(file.getName().indexOf(str) != -1) {
    	            mFilterFiles.add(file);
    	        }
    	    }
    	    mAdapter.setList(mFilterFiles);
	    }else {
	        mAdapter.setList(mFiles);
	    }
	}

	private void pathCheckAndFix() {
		File dir = new File(mPath);
		// Sanity check that the path (coming from extras_dir_path) is indeed a
		// directory
		if (!dir.isDirectory() && dir.getParentFile() != null) {
			// remember the filename for picking.
			mFilename = dir.getName();
			dir = dir.getParentFile();
			mPath = dir.getAbsolutePath();
		}
	}

	public String getFilename() {
		return mFilename;
	}
}
