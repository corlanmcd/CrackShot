package cm.crackshot.data;

import android.util.Log;

public class Ballistics 
{
	public float getAngleByDistance(String ammoType, int distance)
	{
		if(ammoType.equals("shaped"))
		{
			switch (distance) 
			{
				case 25:
					return 0.8925f;
				case 50:
					return 2.125f;
				case 75:
					return 3.8308f;
				case 100:
					return 6.25f;
				case 125:
					return 9.8375f;
				case 150:
					return 15.6667f;
				default:
					Log.e("GetAngleFromDistance", "Error, could not retrieve distance!");
					return 0.0f;
			}
		}
		else
		{
			switch (distance) 
			{
				case 25:
					return 1.177486589f;
				case 50:
					return 4.099570276f;
				case 75:
					return 13.65173229f;
				default:
					Log.e("GetAngleFromDistance", "Error, could not retrieve distance!");
					return 0.0f;
			}
		}
	}
}
