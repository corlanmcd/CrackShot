package cm.crackshot.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import cm.crackshot.R;

/**
 * TODO: document your custom view class.
 */
public class CrosshairView extends View 
{
	private String mExampleString; // TODO: use a default from R.string...
	private int mExampleColor = Color.RED; // TODO: use a default from
											// R.color...
	private float mExampleDimension = 0; // TODO: use a default from R.dimen...
	private Drawable mExampleDrawable;

	private TextPaint mTextPaint;
	private float mTextWidth;
	private float mTextHeight;
	
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
		// Load attributes
		
		paint = new Paint();
		
		
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.CrosshairView, defStyle, 0);

		mExampleString = a.getString(R.styleable.CrosshairView_exampleString);
		mExampleColor = a.getColor(R.styleable.CrosshairView_exampleColor,
				mExampleColor);
		// Use getDimensionPixelSize or getDimensionPixelOffset when dealing
		// with
		// values that should fall on pixel boundaries.
		mExampleDimension = a.getDimension(
				R.styleable.CrosshairView_exampleDimension, mExampleDimension);

		if (a.hasValue(R.styleable.CrosshairView_exampleDrawable)) {
			mExampleDrawable = a
					.getDrawable(R.styleable.CrosshairView_exampleDrawable);
			mExampleDrawable.setCallback(this);
		}

		a.recycle();

		// Set up a default TextPaint object
		mTextPaint = new TextPaint();
		mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Paint.Align.LEFT);

		// Update TextPaint and text measurements from attributes
		//invalidateTextPaintAndMeasurements();
	}

	private void invalidateTextPaintAndMeasurements() 
	{
		mTextPaint.setTextSize(mExampleDimension);
		mTextPaint.setColor(mExampleColor);
		mTextWidth = mTextPaint.measureText(mExampleString);

		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		mTextHeight = fontMetrics.bottom;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		setupPaintBrush();
		
		drawHUD(canvas);
		
		/*// TODO: consider storing these as member variables to reduce
		// allocations per draw cycle.
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		int paddingRight = getPaddingRight();
		int paddingBottom = getPaddingBottom();

		int contentWidth = getWidth() - paddingLeft - paddingRight;
		int contentHeight = getHeight() - paddingTop - paddingBottom;

		// Draw the text.
		canvas.drawText(mExampleString, paddingLeft
				+ (contentWidth - mTextWidth) / 2, paddingTop
				+ (contentHeight + mTextHeight) / 2, mTextPaint);

		// Draw the example drawable on top of the text.
		if (mExampleDrawable != null) {
			mExampleDrawable.setBounds(paddingLeft, paddingTop, paddingLeft
					+ contentWidth, paddingTop + contentHeight);
			mExampleDrawable.draw(canvas);
		}*/
	}
	
	private void setupPaintBrush() 
	{
		paint.setStrokeWidth(6.0f);
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
		canvas.drawRect(centerPoint.x - 100, getHeight()/2 - 100,
				centerPoint.x + 100, getHeight()/2 + 100, paint);
	}

	private void drawVerticalLine(Canvas canvas) 
	{
		canvas.drawLine(centerPoint.x, 0, centerPoint.x, 2 * centerPoint.y, paint);
	}
	
	private void drawHorizontalLines(Canvas canvas) 
	{
		int count = 1;
		int verticalSpacing = (int) (getHeight() * .18);
		
		while (count <= 5)
		{
			if (count == 1)
			{
				canvas.drawLine((float)(centerPoint.x - (getWidth() * .2)), verticalSpacing * count,
						(float)(centerPoint.x + (getWidth() * .2)), verticalSpacing*count, paint);
			}
			else
			{
				canvas.drawLine((float)(centerPoint.x - (getWidth() * .1)), verticalSpacing * count,
						(float)(centerPoint.x + (getWidth() * .1)), verticalSpacing*count, paint);
			}
			
			count++;
		}
	}

	/**
	 * Gets the example string attribute value.
	 * 
	 * @return The example string attribute value.
	 */
	public String getExampleString() {
		return mExampleString;
	}

	/**
	 * Sets the view's example string attribute value. In the example view, this
	 * string is the text to draw.
	 * 
	 * @param exampleString
	 *            The example string attribute value to use.
	 */
	public void setExampleString(String exampleString) {
		mExampleString = exampleString;
		invalidateTextPaintAndMeasurements();
	}

	/**
	 * Gets the example color attribute value.
	 * 
	 * @return The example color attribute value.
	 */
	public int getExampleColor() {
		return mExampleColor;
	}

	/**
	 * Sets the view's example color attribute value. In the example view, this
	 * color is the font color.
	 * 
	 * @param exampleColor
	 *            The example color attribute value to use.
	 */
	public void setExampleColor(int exampleColor) {
		mExampleColor = exampleColor;
		invalidateTextPaintAndMeasurements();
	}

	/**
	 * Gets the example dimension attribute value.
	 * 
	 * @return The example dimension attribute value.
	 */
	public float getExampleDimension() {
		return mExampleDimension;
	}

	/**
	 * Sets the view's example dimension attribute value. In the example view,
	 * this dimension is the font size.
	 * 
	 * @param exampleDimension
	 *            The example dimension attribute value to use.
	 */
	public void setExampleDimension(float exampleDimension) {
		mExampleDimension = exampleDimension;
		invalidateTextPaintAndMeasurements();
	}

	/**
	 * Gets the example drawable attribute value.
	 * 
	 * @return The example drawable attribute value.
	 */
	public Drawable getExampleDrawable() {
		return mExampleDrawable;
	}

	/**
	 * Sets the view's example drawable attribute value. In the example view,
	 * this drawable is drawn above the text.
	 * 
	 * @param exampleDrawable
	 *            The example drawable attribute value to use.
	 */
	public void setExampleDrawable(Drawable exampleDrawable) {
		mExampleDrawable = exampleDrawable;
	}

	public void setCenterPoint(Point centerPoint)
	{
		this.centerPoint = centerPoint;		
	}
}
