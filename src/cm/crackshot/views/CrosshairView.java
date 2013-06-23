package cm.crackshot.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import cm.crackshot.R;

/**
 * TODO: document your custom view class.
 */
public class CrosshairView extends View 
{
	private int scopeRadius;
	private int selectedRange;
	private boolean goodAngle;
	private String ammoType = "round";
	
	private Paint paint;
	private Point centerpoint;
	private Point targetingPoint;
	
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
		goodAngle 			= false;
		paint 				= new Paint();
		centerpoint 		= new Point();
	}
	
	public void setCenterPoint(Point centerpoint)
	{
		this.centerpoint = targetingPoint = centerpoint;	
	}
	
	public void setAmmoType(String ammoType)
	{
		this.ammoType = ammoType;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
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
		//Horizontal Line
		canvas.drawLine(getWidth()/2 - 20, getHeight()/2, getWidth()/2 + 20, getHeight()/2, paint);
		
		//Vertical Line
		canvas.drawLine(getWidth()/2, getHeight()/2 - 20, getWidth()/2, getHeight()/2 + 20, paint);
	}

	private void drawScopeCircle(Canvas canvas) 
	{
		scopeRadius = (getWidth()/2) + 15;
		
		Paint scopePaint = new Paint();
		
		//Outermost circle
		scopePaint.setStrokeWidth(10.0f);
		scopePaint.setStyle(Paint.Style.STROKE);
		scopePaint.setColor(android.graphics.Color.BLACK);
		canvas.drawCircle(getWidth()/2, getHeight()/2, scopeRadius + 8, scopePaint);
		
		//Middle Circle
		scopePaint.setColor(0xffff8800);
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
						  (float) (targetingPoint.x - (scaledTargetingBoxImage.getWidth()/2)),
						  (float) (getHeight()/2 + getPixelDistanceBasedonTargetingPoint()),
						  paint);
		
		paint.setColor(0xffff8800);
		canvas.drawLine((float) (getWidth()/2),
						(float) (getHeight()/2 + getPixelDistanceBasedonTargetingPoint()) + scaledTargetingBoxImage.getHeight(),
						(float) (getWidth()/2), 
						getHeight()/2 + scopeRadius, 
						paint);
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

	private void drawVerticalLine(Canvas canvas) 
	{
		Paint temporaryPaint = new Paint();
		
		temporaryPaint.setARGB(255, 0, 0,0);
		temporaryPaint.setStyle(Style.STROKE);
		temporaryPaint.setPathEffect(new DashPathEffect(new float[]{5,10,15,20}, 5));
		
		canvas.drawLine(centerpoint.x, getHeight()/2 - 75 , centerpoint.x, getHeight()/2 + 175, temporaryPaint);
	}
	
	private void drawHorizontalLines(Canvas canvas) 
	{
		int count 				= 1;
		int verticalSpacing 	= (int) ((getHeight() / 2)* .05);
		float lineLength 		= getWidth() * .1f;
		
		while (count <= 5)
		{
			if(count == selectedRange)
			{
				paint.setColor(android.graphics.Color.BLUE);
			}
			else
			{
				paint.setColor(android.graphics.Color.RED);
			}
			
			canvas.drawLine(centerpoint.x - lineLength + (count * 10),
							centerpoint.y + (verticalSpacing * (count - 1)),
							centerpoint.x + lineLength - (count * 10),
							centerpoint.y + (verticalSpacing * (count - 1)),
							paint);
			
			count++;
		}
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
}
