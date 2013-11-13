package org.openintents.filemanager.util;

import android.text.Editable;
import android.text.TextWatcher;

public class EditTextWatcher implements TextWatcher
{
	public interface OnTextChanged
	{
		public void onExecute(CharSequence s, int start, int before, int count);
	}
	private OnTextChanged onTextChanged;
	public void setOnTextChanaged(OnTextChanged onTextChanged)
	{
		this.onTextChanged = onTextChanged; 
	}
	
	@Override
	public void afterTextChanged(Editable s)
	{
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		onTextChanged.onExecute( s,  start,  before,  count);
	}
}
