package com.mario.playermsg;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import com.mario.load.LoadActivity;
import com.mario.load.LoadResource;
import com.mario.load.LoadView;

public class HeightScoreView extends View
{
	private Paint paint;
	private int textSize = 20;
	private String name,score,msg = "HeightScore";
	private Player p[] ;
	
	public HeightScoreView(Context context) 
	{
		super(context);
		
		this.setFocusableInTouchMode(true);
		this.setKeepScreenOn(true);
		
	
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setColor(Color.MAGENTA);
        this.paint.setTypeface(LoadView.mFace); 
        this.paint.setTextSize(textSize);
    


		byte[] b = new byte[1024];
		int length = 0;
		try 
		{
			FileInputStream fis = this.getContext().openFileInput("save.txt");
			BufferedInputStream bis = new BufferedInputStream(fis);
			while((length = bis.read(b)) != -1)
			{
				
				name = new String(b, 0, length);
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		String newname[] = null;
		if(name != null)
		{
			newname = name.split(":");
		}
		
		

		byte[] b2 = new byte[1024];
		int length2 = 0;
		try 
		{
			FileInputStream fis = this.getContext().openFileInput("score.txt");
			BufferedInputStream bis = new BufferedInputStream(fis);
			while((length2 = bis.read(b2)) != -1)
			{
				
				score = new String(b2, 0, length2);
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		 String newscore[] = null;
		 if(score != null)
		 {
			 newscore = score.split(":");
		 }
 
		 
 
 		if(newname != null && newscore!= null )
 		{
 			
 			p = new Player[newname.length];
 			
 			for(int i=0; i<newname.length; i++)
 			{
 				p[i] = new Player(newname[i],Integer.parseInt(newscore[i]));
 			}
 			
 		
 			Arrays.sort(p);
 		}
 	}
	
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		canvas.drawBitmap(LoadResource.map.get(0), 0, 0, paint);
		canvas.drawText(msg, (LoadActivity.ScreenWidth - msg.length()*textSize/2)/2, textSize, paint);
		canvas.drawText("name", LoadActivity.ScreenWidth/8, textSize*3, paint);
		canvas.drawText("score", LoadActivity.ScreenWidth/2+LoadActivity.ScreenWidth/8, textSize*3, paint);
		
		if(p != null)
		{
			for(int i=0; i<p.length; i++)
			{
				canvas.drawText(p[i].name, LoadActivity.ScreenWidth/8, i*20+textSize*5, paint);
			}
		
			for(int i=0; i<p.length; i++)
			{
				canvas.drawText(""+p[i].score, LoadActivity.ScreenWidth/2+LoadActivity.ScreenWidth/8, i*20+textSize*5, paint);
			}
		}
		super.invalidate();
		super.onDraw(canvas);
		
	}
	
	
}
		 
		
		