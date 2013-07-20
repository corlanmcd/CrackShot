package cm.crackshot.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import cm.crackshot.R;

/**
 * TODO: document your custom view class.
 */
public class CrosshairView extends View 
{
	private int 	reticleColor;
	private float	rotationValue;
	private int 	scopeColor;
	private int 	scopeOffset;
	private int 	scopeRadius;
	private int 	selectedRange;
	private boolean goodAngle;
	private String 	ammoType;
	
	private Paint 	paint;
	private Point 	targetingPoint;
	
	public CrosshairView(Context context)
	{
		super(context);
		initialization(null, 0);
	}

	public CrosshairView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initialization(attrs, 0);
	}

	public CrosshairView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		initialization(attrs, defStyle);
	}

	private void initialization(AttributeSet attrs, int defStyle) 
	{
		ammoType			= "round";
		goodAngle 			= false;
		paint 				= new Paint();
		reticleColor 		= android.graphics.Color.RED;
		rotationValue		= 0;
		scopeColor 			= 0xffff8800;
		scopeOffset			= 0;
		scopeRadius 		= (getWidth()/2) + 15;
	}
	
	public void setCenterPoint(Point centerpoint)
	{
		this.targetingPoint = centerpoint;
	}

	public void setAmmoType(String ammoType)
	{
		this.ammoType = ammoType;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		//Rotate Canvas through canvas.rotate() (necessary for API 10)
		canvas.rotate(rotationValue, getWidth()/2, getHeight()/2);
		
		super.onDraw(canvas);
		
		setupPaintBrush();
		drawHUD(canvas);
	}
	
	private void setupPaintBrush() 
	{
		paint.setStrokeWidth(4.0f);
		paint.setStyle(Paint.Style.STROKE); 
		paint.setColor(android.graphics.Color.RED);
	}

	private void drawHUD(Canvas canvas)
	{
		drawScopeCircle(canvas);
		drawReticle(canvas);
		//drawVerticalLine(canvas);
		//drawHorizontalLines(canvas);
		drawTargetingBox(canvas);
	}
	
	private void drawReticle(Canvas canvas) 
	{
		paint.setColor(reticleColor);
		
		//Horizontal Line
		canvas.drawLine(getWidth()/2 - 20 + scopeOffset,
				getHeight()/2,
				getWidth()/2 + 20 + scopeOffset,
				getHeight()/2,
				paint);
		
		//Vertical Line
		canvas.drawLine(getWidth()/2 + scopeOffset,
				getHeight()/2 - 20,
				getWidth()/2 + scopeOffset,
				getHeight()/2 + 20,
				paint);
	}

	private void drawScopeCircle(Canvas canvas) 
	{
		scopeRadius = (getWidth()/2) - 20;
		
		Paint scopePaint = new Paint();
		
		//Outermost circle
		scopePaint.setStrokeWidth(10.0f);
		scopePaint.setStyle(Paint.Style.STROKE);
		scopePaint.setColor(android.graphics.Color.BLACK);
		canvas.drawCircle(getWidth()/2, getHeight()/2, scopeRadius + 8, scopePaint);
		
		//Middle Circle
		scopePaint.setColor(scopeColor);
		scopePaint.setStrokeWidth(8.0f);
		canvas.drawCircle(getWidth()/2, getHeight()/2, scopeRadius, scopePaint);	
		
		//Innermost circle
		scopePaint.setColor(android.graphics.Color.LTGRAY);
		scopePaint.setStrokeWidth(6.0f);
		canvas.drawCircle(getWidth()/2, getHeight()/2, scopeRadius - 6, scopePaint);
		
		
	}

	public Point getTargetingBoxPosition()
	{
		return targetingPoint;
	}
	
	private void drawTargetingBox(Canvas canvas) 
	{
		Bitmap targetingBoxImage;
		Bitmap scaledTargetingBoxImage;
		
		// Determine Box Color
		if(!goodAngle)
		{
			targetingBoxImage = BitmapFactory.decodeResource(getResources(), R.drawable.targetbox);
		}
		else
		{
			targetingBoxImage = BitmapFactory.decodeResource(getResources(), R.drawable.targetboxred);
		}
		
		//Determine Box Size
		if(selectedRange == 1)
		{
			scaledTargetingBoxImage = Bitmap.createScaledBitmap(targetingBoxImage,
													  	 (int)(targetingBoxImage.getWidth() * .45),
													     (int)(targetingBoxImage.getHeight() * .45),
													      false);
		}
		else if(selectedRange == 2)
		{
			scaledTargetingBoxImage = Bitmap.createScaledBitmap(targetingBoxImage,
													  	 (int)(targetingBoxImage.getWidth() * .25),
													     (int)(targetingBoxImage.getHeight() * .25),
													      false);
		}
		else if(selectedRange == 3)
		{
			scaledTargetingBoxImage = Bitmap.createScaledBitmap(targetingBoxImage,
													  	 (int)(targetingBoxImage.getWidth() * .15),
													     (int)(targetingBoxImage.getHeight() * .15),
													      false);
		}
		else if(selectedRange == 4)
		{
			scaledTargetingBoxImage = Bitmap.createScaledBitmap(targetingBoxImage,
													  	 (int)(targetingBoxImage.getWidth() * .10),
													     (int)(targetingBoxImage.getHeight() * .10),
													      false);
		}
		else if(selectedRange == 5)
		{
			scaledTargetingBoxImage = Bitmap.createScaledBitmap(targetingBoxImage,
													  	 (int)(targetingBoxImage.getWidth() * .07),
													     (int)(targetingBoxImage.getHeight() * .07),
													      false);
		}
		else
		{
			scaledTargetingBoxImage = targetingBoxImage;
		}
		
		//getPixelDistanceBasedonTargetingPoint()
		
		
		canvas.drawBitmap(scaledTargetingBoxImage,
						  (float) (targetingPoint.x - (scaledTargetingBoxImage.getWidth()/2) + scopeOffset),
						  (float) (getHeight()/2 + getPixelDistanceBasedonTargetingPoint()),
						  paint);
		
		paint.setColor(scopeColor);
		
		if(scopeOffset < 0) // Left
		{
			canvas.drawLine((float) (getWidth()/2 + scopeOffset),
							(float) (getHeight()/2 + getPixelDistanceBasedonTargetingPoint()) + scaledTargetingBoxImage.getHeight(),
							(float) (getWidth()/2 + scopeOffset), 
							getHeight()/2 + scopeRadius + (scopeOffset * 0.26f), 
							paint);
		}
		else // Right
		{
			canvas.drawLine((float) (getWidth()/2 + scopeOffset),
					(float) (getHeight()/2 + getPixelDistanceBasedonTargetingPoint()) + scaledTargetingBoxImage.getHeight(),
					(float) (getWidth()/2 + scopeOffset), 
					getHeight()/2 + scopeRadius - (scopeOffset * 0.26f), 
					paint);
		}
	}

	private int getPixelDistanceBasedonTargetingPoint() 
	{
		int numPixels = 0;
		
		if(ammoType.equals("shaped"))
		{
			switch(selectedRange)
			{
				case 1:
					numPixels = 85;
					break;
				case 2:
					numPixels = 120;
					break;
				case 3:
					numPixels = 160;
					break;
				case 4:
					numPixels = 213;
					break;
				case 5:
					numPixels = 277;
					break;
				default:
					numPixels = 90;
					break;
			}
		}
		else
		{
			switch(selectedRange)
			{
				case 1:
					numPixels = 95;
					break;
				case 2:
					numPixels = 155;
					break;
				case 3:
					numPixels = 345;
					break;
				default:
					numPixels = 100;
					break;
			}
		}/*
		
		
		if(selectedRange == 1)
		{
			numPixels = 90;
		}
		else if(selectedRange == 2)
		{
			numPixels = 130;
		}
		else if(selectedRange == 3)
		{
			numPixels = 170;
		}
		else if(selectedRange == 4)
		{
			numPixels = 210;
		}
		else if(selectedRange == 5)
		{
			numPixels = 260;
		}*/
		
		return numPixels;
	}

	public void setSelectedRange(int range) 
	{
		switch (range)
		{
			case 25:
				selectedRange = 1;
				break;
			case 50:
				selectedRange = 2;
				break;
			case 75:
				selectedRange = 3;
				break;
			case 100:
				selectedRange = 4;
				break;
			case 125:
				selectedRange = 5;
				break;
			default:
				selectedRange = 1;	
				break;
		}
	}

	public void setGoodAngle(boolean bool) 
	{
		goodAngle = bool;
	}

	public void resetScopeOffset()
	{
		scopeOffset = 0;
	}

	public void subtractPixelFromRadius(int offset) 
	{
		if(((getWidth()/2) + scopeOffset + offset) >= (getWidth()/4) 
				&& ((getWidth()/2) + scopeOffset + offset) <= (getWidth() * 0.75f))
		{
			scopeOffset += offset;	
		}
	}
	
	public void setReticleColor(int color)
	{
		reticleColor = color;
	}

	public void setScopeColor(int color) 
	{
		scopeColor = color;
	}
	
	public void setRotationValue(float value)
	{
		rotationValue = value;
	}
}
