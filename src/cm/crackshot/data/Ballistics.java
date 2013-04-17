package cm.crackshot.data;

import android.util.Log;

public class Ballistics 
{
	public float getAngleByDistance(int distance)
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
}
