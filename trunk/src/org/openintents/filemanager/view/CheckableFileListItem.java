package org.openintents.filemanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dm.oifilemgr.R;

/**
 * An extension to the item_filelist layout that implements the checkable interface and displays a {@link CheckBox} to the right of the standard layout.
 * @author George Venios
 *
 */
public class CheckableFileListItem extends RelativeLayout implements Checkable{
    
    public CheckBox mCheckbox;
	public ImageView icon;
    public TextView primaryInfo;
    public TextView secondaryInfo;
    public TextView tertiaryInfo;
    
	public CheckableFileListItem(Context context) {
		super(context);
		init();
	}
	
	public CheckableFileListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init(){
		mCheckbox = new CheckBox(getContext());
		mCheckbox.setId(10);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(ALIGN_PARENT_RIGHT);
		params.addRule(CENTER_VERTICAL);
		mCheckbox.setChecked(false);
		mCheckbox.setClickable(false);
		mCheckbox.setFocusable(false);
		mCheckbox.setLayoutParams(params);
		
		View item = inflate(getContext(), R.layout.item_filelist, null);
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		p.addRule(LEFT_OF, 10);
		p.addRule(ALIGN_PARENT_LEFT);
		item.setLayoutParams(p);
		
		addView(mCheckbox);
		addView(item);
		
		icon = (ImageView) item.findViewById(R.id.icon);
        primaryInfo = (TextView) item.findViewById(R.id.primary_info);
        secondaryInfo = (TextView) item.findViewById(R.id.secondary_info);
        tertiaryInfo = (TextView) item.findViewById(R.id.tertiary_info);
	}

	@Override
	public boolean isChecked() {
		return mCheckbox.isChecked();
	}

	@Override
	public void setChecked(boolean checked) {
		mCheckbox.setChecked(checked);
	}

	@Override
	public void toggle() {
		mCheckbox.toggle();
	}
}