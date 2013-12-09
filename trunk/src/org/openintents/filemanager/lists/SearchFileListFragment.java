package org.openintents.filemanager.lists;

import java.io.File;

import org.openintents.filemanager.FileManagerActivity;
import org.openintents.filemanager.files.FileHolder;
import org.openintents.filemanager.search.SearchListAdapter;
import org.openintents.filemanager.search.SearchableActivity;
import org.openintents.filemanager.util.FileUtils;
import org.openintents.filemanager.util.MenuUtils;
import org.openintents.filemanager.view.MenuPopWin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.dm.oifilemgr.R;

public class SearchFileListFragment extends RefreshListFragment  implements MenuPopWin.OnOptionsItemSelectedListener{

    private int mSingleSelectionMenu = R.menu.context;
    private SearchListAdapter mAdapter;
    
    private MenuPopWin menuPop;
    
    public MenuPopWin createMenuPopWin() {
        menuPop = new MenuPopWin(getActivity(), new MenuPopWin.OnOptionsItemSelectedListener[] {this});
        return menuPop;
    }
    
    public void showMenuPopWin() {
        if(!menuPop.isShowing()) {
            menuPop.showAtLocation(getActivity().getWindow().getDecorView(),Gravity.BOTTOM, 0, 0);
            menuPop.update();
        }else {
            menuPop.dismiss();
        }
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);     
        registerForContextMenu(getListView());
    }
    
    @Override
    public void onDestroy() {
        if(mAdapter != null) mAdapter.destory();
        super.onDestroy();
    }
    
    public void handIntent(String query, Activity activity) {
        // Set the list adapter.
        mAdapter = new SearchListAdapter(activity);
        setListAdapter(mAdapter);
    }
    
    public void addResult(String path) {
        mAdapter.addItem(path);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(getActivity());

        // Obtain context menu info
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        }
        
        FileHolder item = (FileHolder) mAdapter.getItem(info.position);
        MenuUtils.fillContextMenu(item, menu, mSingleSelectionMenu, inflater, getActivity());
        
        createMenuPopWin().builder(menu, item.getName(), item.getIcon());
        showMenuPopWin();
        
        menu.clear();
        menu.clearHeader();
        menu.close();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getMenuInfo() != null) {
            onContextItemSelected(item);
            return true;
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        return MenuUtils.handleSingleSelectionAction(this, item, mAdapter.getItem(position), getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {       
        String path = mAdapter.getItem(position).getFile().getAbsolutePath();       
        browse(path);
    }
    
    public void browse(String path) {
        File file = new File(path);
        if(file.isDirectory()) {
            Uri pathUri = Uri.parse(path);
            Intent intent = new Intent(getActivity(), FileManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setData(pathUri);
            startActivity(intent);
            getActivity().finish();
        }else {
            FileUtils.openFile(new FileHolder(file, getActivity()), getActivity());
        }
    }

    @Override
    public void openInformingPathBar(FileHolder fItem) {
        browse(fItem.getFile().getAbsolutePath());
    }
    
    @Override
    public void refresh() {
        ((SearchableActivity)getActivity()).refresh();
    }
}