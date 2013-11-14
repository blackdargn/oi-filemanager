package org.openintents.filemanager.search;

import java.io.File;
import java.util.HashMap;

import org.openintents.filemanager.ThumbnailLoader;
import org.openintents.filemanager.files.FileHolder;
import org.openintents.filemanager.view.ViewHolder;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dm.oifilemgr.R;

/**
 * Simple adapter for displaying search results.
 * @author George Venios
 *
 */
public class SearchListAdapter extends CursorAdapter {
	private HashMap<String, FileHolder> itemCache = new HashMap<String, FileHolder>();
	private ThumbnailLoader mThumbnailLoader;
	private Drawable folderIcon, genericFileIcon;
	private Context mContext;
	
	public SearchListAdapter(Context context, Cursor c) {
		super(context, c, true);
		mThumbnailLoader = new ThumbnailLoader(context);
		folderIcon = context.getResources().getDrawable(R.drawable.ic_launcher_folder);
        genericFileIcon = context.getResources().getDrawable(R.drawable.ic_launcher_file);
        mContext = context;
	}
	
	public void destory() {
	    mThumbnailLoader.cancel();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String path = cursor.getString(cursor.getColumnIndex(SearchResultsProvider.COLUMN_PATH));
		FileHolder fHolder;
		if((fHolder = itemCache.get(path)) == null){
		    File file = new File(path);
			fHolder = new FileHolder(file, file.isDirectory() ? folderIcon : genericFileIcon, context);
			itemCache.put(path, fHolder);
		}

		ViewHolder h = (ViewHolder) view.getTag();
		h.primaryInfo.setText(fHolder.getName());
		h.secondaryInfo.setText(path);
		h.icon.setImageDrawable(fHolder.getIcon());
		
		 if(shouldLoadIcon(fHolder)){
	            if(mThumbnailLoader != null) {
	                mThumbnailLoader.loadImage(fHolder, h.icon);
	            }
	    }
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// Inflate the view
		ViewGroup v = (ViewGroup) ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.item_filelist, null);

		// Set the viewholder optimization.
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) v.findViewById(R.id.icon);
		holder.primaryInfo = (TextView) v.findViewById(R.id.primary_info);
		holder.secondaryInfo = (TextView) v.findViewById(R.id.secondary_info);
		v.findViewById(R.id.tertiary_info).setVisibility(View.GONE);
		
		v.setTag(holder);
		
		return v;
	}
	
	public FileHolder getItem(int position) {
	    Cursor c = new CursorWrapper(getCursor());
        c.moveToPosition(position);
        String path = c.getString(c.getColumnIndex(SearchResultsProvider.COLUMN_PATH));
        return new FileHolder(new File(path), mContext);
	}

	private boolean shouldLoadIcon(FileHolder item){
        return item.getFile().isFile() && !item.getMimeType().equals("video/mpeg");
    }
}