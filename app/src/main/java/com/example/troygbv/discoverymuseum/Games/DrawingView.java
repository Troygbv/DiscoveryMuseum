package com.example.troygbv.discoverymuseum.Games;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

import com.example.troygbv.discoverymuseum.R;


public class DrawingView extends View {

    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private float mX, mY, brushSize, lastBrushSize;
    private static final float TOLERANCE = 5;
    private boolean erase=false;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPaint.setStrokeWidth(brushSize);

    }

    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0,0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);

    }

    private void startTouch(float x, float y) {
        drawPath.moveTo(x,y);
        mX = x;
        mY = y;

    }

    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if(dx >= TOLERANCE || dy >= TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearCanvas() {
        drawPath.reset();
        invalidate();
    }

    private void upTouch() {
        drawPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath,drawPaint);
                drawPath.reset();
                break;
            default:
                return false;

        }
        invalidate();
        return true;

    }

    public void setColor(String newColor){
        //set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setErase(boolean isErase){
        erase=isErase;
        if(erase) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            drawPaint.setColor(Color.WHITE);
        }
        else drawPaint.setXfermode(null);
            drawPaint.setColor(paintColor);


    }
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }


}
