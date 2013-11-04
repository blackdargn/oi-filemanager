/* 
 * Copyright (C) 2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.filemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.openintents.filemanager.bookmarks.BookmarkListActivity;
import org.openintents.filemanager.compatibility.HomeIconHelper;
import org.openintents.filemanager.files.DirectoryContents;
import org.openintents.filemanager.files.DirectoryScanner;
import org.openintents.filemanager.files.FileHolder;
import org.openintents.filemanager.lists.MultiselectListFragment;
import org.openintents.filemanager.util.FileUtils;
import org.openintents.filemanager.util.MenuUtils;
import org.openintents.filemanager.util.MimeTypes;
import org.openintents.filemanager.util.UIUtils;
import org.openintents.filemanager.view.LegacyActionContainer;
import org.openintents.filemanager.view.PathBar;
import org.openintents.filemanager.view.PathBar.Mode;
import org.openintents.filemanager.view.PathBar.OnDirectoryChangedListener;
import org.openintents.intents.FileManagerIntents;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.dm.oifilemgr.R;

public class FileManagerActivity2 extends Activity implements OnItemClickListener,OnItemLongClickListener {  
   
    //////////////////////////////////////////////////////////////////////////////////////////////
    // FileManager
    protected static final int REQUEST_CODE_BOOKMARKS = 1;
    protected MenuDrawer mMenuDrawer;
    private View menu_main, menu_fileop;
            
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        UIUtils.setThemeFor(this);
        
        super.onCreate(icicle);
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.TOP);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        
        // Enable home button.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            HomeIconHelper.activity_actionbar_setHomeButtonEnabled(this);
        
        // Search when the user types.
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        
        // If not called by name, open on the requested location.
        File data = resolveIntentData();        
        mMenuDrawer.setContentView(R.layout.activity_main);

        // Add fragment only if it hasn't already been added.
        if(data == null)
                getIntent().putExtra(FileManagerIntents.EXTRA_DIR_PATH, Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/");
            else
                getIntent().putExtra(FileManagerIntents.EXTRA_DIR_PATH, data.toString());
        onViewCreated1(mMenuDrawer, icicle);
        onViewCreated2(mMenuDrawer, icicle);
        onViewCreated3(mMenuDrawer, icicle);
        // If we didn't rotate and data wasn't null.
        if(icicle == null && data!=null)
            openInformingPathBar(new FileHolder(new File(data.toString()), this));
        // menu click
        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showMenuMain();
            }
        });
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.getData() != null)
            openInformingPathBar(new FileHolder(FileUtils.getFile(intent.getData()), this));
    }

    /**
     * Either open the file and finish, or navigate to the designated directory. This gives FileManagerActivity the flexibility to actually handle file scheme data of any type.
     * @return The folder to navigate to, if applicable. Null otherwise.
     */
    private File resolveIntentData(){
        File data = FileUtils.getFile(getIntent().getData());
        if(data == null)
            return null;
        
        if(data.isFile()){
            FileUtils.openFile(new FileHolder(data, this), this);
            finish();
            return null;
        }
        else
            return FileUtils.getFile(getIntent().getData());
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
    
    // The following methods should properly handle back button presses on every API Level.
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (VERSION.SDK_INT > VERSION_CODES.DONUT) {
            if (keyCode == KeyEvent.KEYCODE_BACK && pressBack())
                return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (VERSION.SDK_INT <= VERSION_CODES.DONUT) {
            if (keyCode == KeyEvent.KEYCODE_BACK && pressBack())
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * This is called after the file manager finished.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
        case REQUEST_CODE_BOOKMARKS:
            if (resultCode == RESULT_OK && data != null) {
                openInformingPathBar(new FileHolder(new File(data.getStringExtra(BookmarkListActivity.KEY_RESULT_PATH)), this));
            }
            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
        }        
    }
    
    /**
     * We override this, so that we get informed about the opening of the search dialog and start scanning silently.
     */
    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putString(FileManagerIntents.EXTRA_SEARCH_INIT_PATH, getPath());
        startSearch(null, false, appData, false);
        
        return true;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    // FileList
    private static final String INSTANCE_STATE_PATH = "path";
    private static final String INSTANCE_STATE_FILES = "files";
    File mPreviousDirectory = null;
    private OnSharedPreferenceChangeListener preferenceListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
            if (getActivity() != null)
                refresh();
        }
    };

    protected FileHolderListAdapter mAdapter;
    protected DirectoryScanner mScanner;
    protected ArrayList<FileHolder> mFiles = new ArrayList<FileHolder>();
    private String mPath;
    private String mFilename;

    private ViewFlipper mFlipper;
    private File mCurrentDirectory;
    private ListView mListView;
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(INSTANCE_STATE_PATH, mPath);
        outState.putParcelableArrayList(INSTANCE_STATE_FILES, mFiles);
        outState.putBoolean(INSTANCE_STATE_PATHBAR_MODE, mPathBar.getMode() == Mode.MANUAL_INPUT);
    }
    
    private ListView getListView() {
        if(mListView == null) {
            mListView = (ListView)findViewById(android.R.id.list);
        }
        return mListView;
    }
    
    private Activity getActivity() {
        return this;
    }

    public void onViewCreated1(View view, Bundle savedInstanceState) {

        // Set auto refresh on preference change.
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(preferenceListener);

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

        // Get arguments
        if (savedInstanceState == null) {
            mPath = getIntent().getStringExtra(FileManagerIntents.EXTRA_DIR_PATH);
            mFilename = getIntent().getStringExtra(FileManagerIntents.EXTRA_FILENAME);
        } else {
            mPath = savedInstanceState.getString(INSTANCE_STATE_PATH);
            mFiles = savedInstanceState
                    .getParcelableArrayList(INSTANCE_STATE_FILES);
        }
        pathCheckAndFix();
        renewScanner();
        mAdapter = new FileHolderListAdapter(mFiles, getActivity());

        getListView().setAdapter(mAdapter);
        mScanner.start();

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onDestroy() {
        mScanner.cancel();
        super.onDestroy();
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
        mScanner = null;

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
        Intent it = getIntent();
        String filetypeFilter = it.getStringExtra(
                FileManagerIntents.EXTRA_FILTER_FILETYPE);
        String mimetypeFilter =  it.getStringExtra(
                FileManagerIntents.EXTRA_FILTER_MIMETYPE);
        boolean writeableOnly = it.getBooleanExtra(
                FileManagerIntents.EXTRA_WRITEABLE_ONLY, false);
        boolean directoriesOnly = it.getBooleanExtra(
                FileManagerIntents.EXTRA_DIRECTORIES_ONLY, false);

        mScanner = new DirectoryScanner(new File(mPath), getActivity(),
                new FileListMessageHandler(),
                MimeTypes.newInstance(getActivity()),
                filetypeFilter == null ? "" : filetypeFilter,
                mimetypeFilter == null ? "" : mimetypeFilter, writeableOnly,
                directoriesOnly);
        return mScanner;
    }

    private class FileListMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
            case DirectoryScanner.MESSAGE_SHOW_DIRECTORY_CONTENTS:
                DirectoryContents c = (DirectoryContents) msg.obj;
                mFiles.clear();
                mFiles.addAll(c.listSdCard);
                mFiles.addAll(c.listDir);
                mFiles.addAll(c.listFile);

                mAdapter.notifyDataSetChanged();

                
                if (mPreviousDirectory != null){
                    selectInList(mPreviousDirectory);
                } else {
                    // Reset list position.
                    if (mFiles.size() > 0)
                        getListView().setSelection(0);                  
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
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    // SimpleFileList       
    private PathBar mPathBar;
    private boolean mActionsEnabled = true;
    private static final String INSTANCE_STATE_PATHBAR_MODE = "pathbar_mode";
    
    public void onViewCreated2(View view, Bundle savedInstanceState) {        
        // Pathbar init.
        mPathBar = (PathBar) view.findViewById(R.id.pathbar);
        // Handle mPath differently if we restore state or just initially create the view.
        if(savedInstanceState == null)
            mPathBar.setInitialDirectory(getPath());
        else
            mPathBar.cd(getPath());
            mPathBar.setOnDirectoryChangedListener(new OnDirectoryChangedListener() {
            @Override
            public void directoryChanged(File newCurrentDir) {
                open(new FileHolder(newCurrentDir, getActivity()));
            }
        });
        if(savedInstanceState != null && savedInstanceState.getBoolean(INSTANCE_STATE_PATHBAR_MODE))
            mPathBar.switchToManualInput();
    }

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        FileHolder item = (FileHolder) mAdapter.getItem(position);
        openInformingPathBar(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
        FileHolder item = (FileHolder) mAdapter.getItem(position);
        showMenuFileOp();
        return true;
    }
    
    /**
     * Use this to open files and folders using this fragment. Appropriately handles pathbar updates.
     * @param item The dir/file to open.
     */
    public void openInformingPathBar(FileHolder item) {
        if(mPathBar == null)
            open(item);
        else
            mPathBar.cd(item.getFile());
    }

    /**
     * Point this Fragment to show the contents of the passed file.
     * 
     * @param f If same as current, does nothing.
     */
    private void open(FileHolder f) {
        if (!f.getFile().exists())
            return;

        if (f.getFile().isDirectory()) {
            openDir(f);
        } else if (f.getFile().isFile()) {
            openFile(f);
        }   
    }
    
    private void openFile(FileHolder fileholder){
        FileUtils.openFile(fileholder, getActivity());
    }
    
    /**
     * Attempts to open a directory for browsing. 
     * Override this to handle folder click behavior.
     * 
     * @param fileholder The holder of the directory to open.
     */
    protected void openDir(FileHolder fileholder){
        // Avoid unnecessary attempts to load.
        if(fileholder.getFile().getAbsolutePath().equals(getPath()))
            return;
        
        setPath(fileholder.getFile());
        refresh();
    }

    private void includeInMediaScan() {
        // Delete the .nomedia file.
        File file = FileUtils.getFile(mPathBar.getCurrentDirectory(),
                FileUtils.NOMEDIA_FILE_NAME);
        if (file.delete()) {
            Toast.makeText(getActivity(),
                    getString(R.string.media_scan_included), Toast.LENGTH_LONG)
                    .show();
        } else {
            // That didn't work.
            Toast.makeText(getActivity(), getString(R.string.error_generic),
                    Toast.LENGTH_LONG).show();
        }
        refresh();
    }

    private void excludeFromMediaScan() {
        // Create the .nomedia file.
        File file = FileUtils.getFile(mPathBar.getCurrentDirectory(),
                FileUtils.NOMEDIA_FILE_NAME);
        try {
            if (file.createNewFile()) {
                Toast.makeText(getActivity(),
                        getString(R.string.media_scan_excluded),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(),
                        getString(R.string.error_media_scan), Toast.LENGTH_LONG)
                        .show();
            }
        } catch (IOException e) {
            // That didn't work.
            Toast.makeText(getActivity(),
                    getString(R.string.error_generic) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        refresh();
    }

    public void browseToHome() {
        mPathBar.cd(mPathBar.getInitialDirectory());
    }

    public boolean pressBack() {
        return mPathBar.pressBack();
    }
    
    /**
     * Set whether to show menu and selection actions. Must be set before OnViewCreated is called.
     * @param enabled
     */
    public void setActionsEnabled(boolean enabled){
        mActionsEnabled = enabled;
    }   
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // mutilfilelist
    private LegacyActionContainer mLegacyActionContainer;
    
    /** 多选模式的装换*/
    public void changeMutilView() {
        if(mAdapter.changeMutilMode()) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            mLegacyActionContainer.setVisibility(View.VISIBLE);
        }else {
            getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
            mLegacyActionContainer.setVisibility(View.GONE);
        }
    }
    
    public void onViewCreated3(View view, Bundle savedInstanceState) {
        // Init members
        mLegacyActionContainer =  (LegacyActionContainer) view.findViewById(R.id.action_container);
        mLegacyActionContainer.setMenuResource(R.menu.multiselect);
        mLegacyActionContainer.setOnActionSelectedListener(new LegacyActionContainer.OnActionSelectedListener() {
            @Override
            public void actionSelected(MenuItem item) {
                if(getListView().getCheckItemIds().length == 0){
                    Toast.makeText(getActivity(), R.string.no_selection, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                ArrayList<FileHolder> fItems = new ArrayList<FileHolder>();
                
                for(long i : getListView().getCheckItemIds()){
                    fItems.add((FileHolder) mAdapter.getItem((int) i));
                }
                // TODO ...
            }
        });
    }
}