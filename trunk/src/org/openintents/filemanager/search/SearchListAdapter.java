package org.openintents.filemanager.search;

import java.io.File;

import org.openintents.filemanager.ThumbnailLoader;
import org.openintents.filemanager.files.FileHolder;
import org.openintents.filemanager.util.ArrayListAdapter;
import org.openintents.filemanager.view.ViewHolder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dm.oifilemgr.R;

/**
 * Simple adapter for displaying search results.
 * @author George Venios
 *
 */
public class SearchListAdapter extends ArrayListAdapter<FileHolder> {
	private ThumbnailLoader mThumbnailLoader;
	private Drawable folderIcon, genericFileIcon;
	private Context mContext;
	
	public SearchListAdapter(Context context) {
		super(context);
		mThumbnailLoader = new ThumbnailLoader(context);
		folderIcon = context.getResources().getDrawable(R.drawable.ic_launcher_folder);
        genericFileIcon = context.getResources().getDrawable(R.drawable.ic_launcher_file);
        mContext = context;
	}
	
	public void destory() {
	    mThumbnailLoader.cancel();
	}
	
	public void addItem(String path) {
	    File file = new File(path);
	    FileHolder fHolder = new FileHolder(file, file.isDirectory() ? folderIcon : genericFileIcon, mContext); 
        addItem(fHolder);
	}

	public void bindView(View view, FileHolder fHolder) {		
		ViewHolder h = (ViewHolder) view.getTag();
		h.primaryInfo.setText(fHolder.getName());
		h.secondaryInfo.setText(fHolder.getFile().getAbsolutePath());
		h.icon.setImageDrawable(fHolder.getIcon());
		
		 if(shouldLoadIcon(fHolder)){
	            if(mThumbnailLoader != null) {
	                mThumbnailLoader.loadImage(fHolder, h.icon);
	            }
	    }
	}

	public View newView(Context context) {
		ViewGroup v = (ViewGroup) ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.item_filelist, null);
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) v.findViewById(R.id.icon);
		holder.primaryInfo = (TextView) v.findViewById(R.id.primary_info);
		holder.secondaryInfo = (TextView) v.findViewById(R.id.secondary_info);
		v.findViewById(R.id.tertiary_info).setVisibility(View.GONE);
		
		v.setTag(holder);
		
		return v;
	}

	private boolean shouldLoadIcon(FileHolder item){
        return item.getFile().isFile() && !item.getMimeType().equals("video/mpeg");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = newView(mContext);
        }
        bindView(convertView, getItem(position));
        return convertView;
    }
}