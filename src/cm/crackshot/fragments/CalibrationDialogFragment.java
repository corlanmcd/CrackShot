package cm.crackshot.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import cm.crackshot.R;

public class CalibrationDialogFragment extends DialogFragment
{
	OnOptionSelectedListener optionSelectedListener;
	
	public interface OnOptionSelectedListener
	{
		public void onDialogOptionSelected(int id);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		try
		{
			optionSelectedListener = (OnOptionSelectedListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + "must implement OnDefaultSelectedListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.CalibrationDialogFragment_header_text);
		builder.setMessage(R.string.CalibrationDialogFragment_message_text)
			.setPositiveButton(R.string.CalibrationDialogFragment_configure_text, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id) 
				{
					optionSelectedListener.onDialogOptionSelected(id);
				}
			})
			.setNegativeButton(R.string.CalibrationDialogFragment_default_text, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id) 
				{
					optionSelectedListener.onDialogOptionSelected(id);
				}
			});
		
		return builder.create();			
	}
}
