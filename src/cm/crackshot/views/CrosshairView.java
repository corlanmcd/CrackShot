package cm.crackshot.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import cm.crackshot.R;

/**
 * TODO: document your custom view class.
 */
public class CrosshairView extends View 
{
	private ImageView targetingBox;
	
	private int selectedRange;
	private Paint paint;
	private Point centerPoint;
	
	public CrosshairView(Context context)
	{
		super(context);
		init(null, 0);
	}

	public CrosshairView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public CrosshairView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) 
	{
		paint 			= new Paint();
		centerPoint 	= new Point();
		targetingBox 	= (ImageView)findViewById(R.id.targetingBoxView);
	}
	
	public void setCenterPoint(Point centerPoint)
	{
		this.centerPoint = centerPoint;		
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
		drawVerticalLine(canvas);
		drawHorizontalLines(canvas);
		drawTargetingBox(canvas);
	}

	private void drawTargetingBox(Canvas canvas) 
	{
		Bitmap targetingBox = BitmapFactory.decodeResource(getResources(), R.drawable.targetbox);
		canvas.drawBitmap(targetingBox,
						  (float) (centerPoint.x - (targetingBox.getWidth()/2)),
						  (float) (centerPoint.y - (targetingBox.getHeight()/2)),
						  paint);
	}

	private void drawVerticalLine(Canvas canvas) 
	{
		canvas.drawLine(centerPoint.x, 0, centerPoint.x, getHeight(), paint);
	}
	
	private void drawHorizontalLines(Canvas canvas) 
	{
		int count 				= 1;
		int verticalSpacing 	= (int) ((getHeight() / 2)* .1);
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
			
			canvas.drawLine(centerPoint.x - lineLength + (count * 10),
							centerPoint.y + (verticalSpacing * (count - 1)),
							centerPoint.x + lineLength - (count * 10),
							centerPoint.y + (verticalSpacing * (count - 1)),
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
}
