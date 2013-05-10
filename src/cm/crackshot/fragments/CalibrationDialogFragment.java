package cm.crackshot.fragments;

import cm.crackshot.R;
import cm.crackshot.activites.StartActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

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
		builder.setTitle(R.string.CalibrationDialogFragment_Header);
		builder.setMessage(R.string.CalibrationDialogFragment_Message)
			.setPositiveButton(R.string.CalibrationDialogFragment_Configure, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id) 
				{
					optionSelectedListener.onDialogOptionSelected(id);
				}
			})
			.setNegativeButton(R.string.CalibrationDialogFragment_Default, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id) 
				{
					optionSelectedListener.onDialogOptionSelected(id);
					
					/*// TODO Should send back center coordinates of device	
					StartActivity startActivity = (StartActivity) getActivity();
					startActivity.askUserToSelectCenterPointFromScreen();*/
				}
			});
		
		return builder.create();			
	}
}
