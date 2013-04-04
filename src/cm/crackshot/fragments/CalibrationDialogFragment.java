package cm.crackshot.fragments;

import cm.crackshot.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CalibrationDialogFragment extends DialogFragment 
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.CalibrationDialogFragment_Header)
			.setPositiveButton(R.string.CalibrationDialogFragment_OK, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					// Nothing
				}
			});
		
		return builder.create();			
	}
}
