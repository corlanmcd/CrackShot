package cm.crackshot.data;

import java.util.TimerTask;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class AngleManager 
{
	private boolean hasGyroscope;
	private boolean initState 		= true;
	
	private static final float a 					= 0.1f;						// value used in low pass filter
	private static final float EPSILON 				= 0.000000001f;				// offset used in low pass filter
	private static final float FILTER_COEFFICIENT = 0.98f;
	private static final float NS2S 				= 1.0f / 1000000000.0f;		//NS2S converts nanoseconds to seconds

	private float timestamp;
	
	//private float[] accelValuesFromHardware = new float[3];
	private float[] accelLowPassValues 		= new float[3];
	private float[] finalRotationMatrix    	= new float[9];
    private float[] fusedOrientation 		= new float[3];		// final orientation angles from sensor fusion
	private float[] gyroValuesFromHardware 	= new float[3];
    private float[] gyroOrientation 		= new float[3];		// orientation angles from gyro matrix
    private float[] gyroRotationMatrix 		= new float[9];		// rotation matrix from gyro data
	private float[] magFieldValues 			= new float[3];
	private float[] orientationValues 		= new float[3];
	private float[] temporaryRotationMatrix = new float[9];
		
	public AngleManager(boolean hasGyroscope)
	{
		this.hasGyroscope = hasGyroscope;
	}
	
	public void calculateAccelMagneticOrientation(SensorEvent event) 
	{
		accelLowPassValues = getLowPassValues(event.values.clone(), accelLowPassValues);
		
		if(SensorManager.getRotationMatrix(temporaryRotationMatrix, null, accelLowPassValues,
				magFieldValues)) 
		{
	    	SensorManager.remapCoordinateSystem(temporaryRotationMatrix, 
	    					SensorManager.AXIS_X, SensorManager.AXIS_Z, finalRotationMatrix);
	    	
	        SensorManager.getOrientation(finalRotationMatrix, orientationValues);
		}
	}
    
    public float getPitchAngle()
    {
    	if (hasGyroscope)
    	{
    		//Log.e("Pitch Angle: ", Float.toString((float)Math.toDegrees(fusedOrientation[1])));
    		return (float) Math.toDegrees(fusedOrientation[1]);
    	}
    	else
    	{
    		return (float)(orientationValues[1] * (180/Math.PI));
    	}
    }
    
    public float getRollAngle()
    {
    	//Log.e("Roll Angle: ", Float.toString((float)Math.toDegrees(orientationValues[2])));
    	return (float) Math.toDegrees(orientationValues[2]);
    }
	
	// simple low-pass filter
	private float[] getLowPassValues(float[] current, float[] last)
	{
		float[] newValues = new float[current.length];

		for(int x = 0; x < current.length; x++)	
		{
			newValues[x] = last[x] * (1.0f - a) + (current[x] * a);
		}

		return newValues;
	}
	
	//------------------------- Sensor Fusion Code from Thousand Thoughts Blog
	
	private void getRotationVectorFromGyro(float[] gyroValues, float[] deltaRotationVector,
            float timeFactor) 
	{
		float[] normValues = new float[3];
		
		// Calculate the angular speed of the sample
		float omegaMagnitude =
		(float)Math.sqrt(gyroValues[0] * gyroValues[0] +
		gyroValues[1] * gyroValues[1] +
		gyroValues[2] * gyroValues[2]);
		
		// Normalize the rotation vector if it's big enough to get the axis
		if(omegaMagnitude > EPSILON) 
		{
			normValues[0] = gyroValues[0] / omegaMagnitude;
			normValues[1] = gyroValues[1] / omegaMagnitude;
			normValues[2] = gyroValues[2] / omegaMagnitude;
		}
		
		// Integrate around this axis with the angular speed by the timestep
		// in order to get a delta rotation from this sample over the timestep
		// We will convert this axis-angle representation of the delta rotation
		// into a quaternion before turning it into the rotation matrix.
		float thetaOverTwo = omegaMagnitude * timeFactor;
		float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
		float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
		deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
		deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
		deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
		deltaRotationVector[3] = cosThetaOverTwo;
	}
	
	// This function performs the integration of the gyroscope data.
    // It writes the gyroscope based orientation into gyroOrientation.
    public void calculateGyroscopicOrientation(SensorEvent event) 
    {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (orientationValues == null)
            return;
     
        // initialization of the gyroscope based rotation matrix
        if(initState) 
        {
            float[] initMatrix = new float[9];
            initMatrix = getRotationMatrixFromOrientation(orientationValues);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroRotationMatrix = matrixMultiplication(gyroRotationMatrix, initMatrix);
            initState = false;
        }
     
        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        
        if(timestamp != 0) 
        {
            final float dT = (event.timestamp - timestamp) * NS2S;
            gyroValuesFromHardware = event.values.clone();
            getRotationVectorFromGyro(gyroValuesFromHardware, deltaVector, dT / 2.0f);
        }
     
        // measurement done, save current time for next interval
        timestamp = event.timestamp;
     
        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);
     
        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroRotationMatrix = matrixMultiplication(gyroRotationMatrix, deltaMatrix);
     
        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroRotationMatrix, gyroOrientation);
    }
	
    private float[] getRotationMatrixFromOrientation(float[] o) 
    {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];
     
        float sinX = (float)Math.sin(o[1]);
        float cosX = (float)Math.cos(o[1]);
        float sinY = (float)Math.sin(o[2]);
        float cosY = (float)Math.cos(o[2]);
        float sinZ = (float)Math.sin(o[0]);
        float cosZ = (float)Math.cos(o[0]);
     
        // rotation about x-axis (pitch)
        xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
        xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
        xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;
     
        // rotation about y-axis (roll)
        yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
        yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
        yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;
     
        // rotation about z-axis (azimuth)
        zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
        zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
        zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;
     
        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }
	
    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];
     
        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
     
        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
     
        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
     
        return result;
    }
	
    public class calculateFusedOrientationTask extends TimerTask 
    {
        public void run() 
        {
        	float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
            
            /*
             * Fix for 179° <--> -179° transition problem:
             * Check whether one of the two orientation angles (gyro or accMag) is negative while the other one is positive.
             * If so, add 360° (2 * math.PI) to the negative value, perform the sensor fusion, and remove the 360° from the result
             * if it is greater than 180°. This stabilizes the output in positive-to-negative-transition cases.
             */
            
            // azimuth
            if (gyroOrientation[0] < -0.5 * Math.PI && orientationValues[0] > 0.0) 
            {
            	fusedOrientation[0] = (float) (FILTER_COEFFICIENT * (gyroOrientation[0] + 2.0 * Math.PI)
            			+ oneMinusCoeff * orientationValues[0]);
        		fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (orientationValues[0] < -0.5 * Math.PI && gyroOrientation[0] > 0.0) 
            {
            	fusedOrientation[0] = (float) (FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff
            			* (orientationValues[0] + 2.0 * Math.PI));
            	fusedOrientation[0] -= (fusedOrientation[0] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else 
            {
            	fusedOrientation[0] = FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff
            			* orientationValues[0];
            }
            
            // pitch
            if (gyroOrientation[1] < -0.5 * Math.PI && orientationValues[1] > 0.0) 
            {
            	fusedOrientation[1] = (float) (FILTER_COEFFICIENT * (gyroOrientation[1] + 2.0 * Math.PI)
            			+ oneMinusCoeff * orientationValues[1]);
        		fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (orientationValues[1] < -0.5 * Math.PI && gyroOrientation[1] > 0.0) 
            {
            	fusedOrientation[1] = (float) (FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff
            			* (orientationValues[1] + 2.0 * Math.PI));
            	fusedOrientation[1] -= (fusedOrientation[1] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else 
            {
            	fusedOrientation[1] = FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * orientationValues[1];
            }
            
            // roll
            if (gyroOrientation[2] < -0.5 * Math.PI && orientationValues[2] > 0.0) 
            {
            	fusedOrientation[2] = (float) (FILTER_COEFFICIENT * (gyroOrientation[2] + 2.0 * Math.PI)
            			+ oneMinusCoeff * orientationValues[2]);
        		fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (orientationValues[2] < -0.5 * Math.PI && gyroOrientation[2] > 0.0) 
            {
            	fusedOrientation[2] = (float) (FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff
            			* (orientationValues[2] + 2.0 * Math.PI));
            	fusedOrientation[2] -= (fusedOrientation[2] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else 
            {
            	fusedOrientation[2] = FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff
            			* orientationValues[2];
            }
            
            // overwrite gyro matrix and orientation with fused orientation
            // to compensate gyro drift
            gyroRotationMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            fusedOrientation = gyroOrientation.clone();
        }
    }
    
	public void setMagneticField(SensorEvent event)
	{
		magFieldValues = event.values.clone();
	}
}
