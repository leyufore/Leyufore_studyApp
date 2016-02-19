package com.mario.load;

import java.io.IOException;

import game.image.Image;
import game.view.GameView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.SurfaceHolder;


public class LoadView extends GameView implements Runnable
{
	private Bitmap red,yellow;

	private int x,y;

	private int loadImageValue = 87 + 16;

	private float width;

	public static Typeface mFace;

	private int textSize = 16;


	private int alpha[] = {255,255,255,255,255,255,255,255,255,255,230,210,190,170,150,130,
			110,90,70,50,30,10,0,10,30,50,70,90,110,130,150,170,190,210,230};


	private int index;


	public LoadView(Context context)
	{
		super(context);
		this.setFocusableInTouchMode(true);
		this.setKeepScreenOn(true);


		try
		{
			red    = BitmapFactory.decodeStream(context.getAssets().open("progressbar/progressbar1.png"));
			red    = Image.FitTheImage(red,(float)LoadActivity.ScreenWidth/(red.getWidth()*2), 1.0f);
			yellow = BitmapFactory.decodeStream(context.getAssets().open("progressbar/progressbar2.png"));
			yellow = Image.FitTheImage(yellow,(float)LoadActivity.ScreenWidth/(yellow.getWidth()*2), 1.0f);

		} catch (IOException e) {
			e.printStackTrace();
		}

		this.x = (LoadActivity.ScreenWidth - red.getWidth())/2;
		this.y =  LoadActivity.ScreenHeight - 30;


		this.width = (float)yellow.getWidth()/loadImageValue;


		mFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/font.ttf");


		paint.setTypeface(mFace);

	}




	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		this.flag = true;
		this.t = new Thread(this);
		this.t.start();
	}




	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		this.flag = false;
	}



	public void Draw()
	{
		this.canvas = sh.lockCanvas();
		if(canvas != null)
		{
			canvas.drawColor(Color.BLACK);


			canvas.drawBitmap(red, x, y, null);


			canvas.save();
			canvas.clipRect(x, y, x + LoadResource.temp * width, y + yellow.getHeight());
			canvas.drawBitmap(yellow, x, y, null);
			canvas.restore();

			paint.setColor(Color.YELLOW);


			paint.setAlpha(alpha[index++]);
			if(index > alpha.length - 1)
			{
				index = 0;
			}
			textSize = 16;
			paint.setTextSize(textSize);

			canvas.drawText("loading......", x, y-10, paint);

			textSize = 20;
			paint.setTextSize(textSize);
			paint.setAlpha(255);
			canvas.drawText("作者 : 编程小白", 0,textSize, paint);
			canvas.drawText("QQ  : 2934308047",0,textSize*2, paint);

			this.sh.unlockCanvasAndPost(canvas);
		}
	}




	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return true;
	}




	@Override
	public void run()
	{
		while(flag)
		{
			Long start = System.currentTimeMillis();
			this.Draw();
			Long end   = System.currentTimeMillis();
			try
			{
				if(end - start < 50)
				{
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
