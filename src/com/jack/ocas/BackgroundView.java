package com.jack.ocas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class BackgroundView extends View {
	private int width, height, fretSize, fretCount;
	Paint fretColor=new Paint();

	public BackgroundView(Context context, int frets) {
		super(context);
		Log.d("BACKGROUNDVIEW","derp");

		fretColor.setARGB(255,255,255,0);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		Point size=new Point();
		display.getSize(size);
		height=size.y;
		fretCount=frets;

		//the size of individual frets
		fretSize=height/frets;
		Log.d("BGINIT","width="+width);
		Log.d("BGINIT","height"+height);
		Log.d("BGINIT","fretCount"+fretCount);
	}

	protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
//        canvas.drawPaint(fretColor);
        float width = canvas.getWidth();
        for (int y = fretCount; y>0; y--){
            canvas.drawLine(0f, (y)*fretSize, width, (y)*fretSize, fretColor);
//            canvas.drawLine(0f, (y+1)*fretSize, width, (y+1)*fretSize, fretColor);
        }
    }
}
