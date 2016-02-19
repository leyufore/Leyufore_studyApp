package com.mario.mario;

import com.mario.load.LoadActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Panel 
{

	private int tabTime;
	
	public void Draw(MarioView mv, Canvas canvas,Paint paint)
	{
	
		canvas.drawText("score : " + mv.getMario().score, 0, 20, paint);
		
	
		canvas.drawText("coin : " + mv.getMario().coin_value, LoadActivity.ScreenWidth/4, 20, paint);
		
	
		canvas.drawText("level : " + mv.getNowLevel().getLevel_name(), LoadActivity.ScreenWidth/2, 20, paint);
		if(mv.getNowLevel().getTime() <= 100 && mv.getGameState() == MarioView.GAME_ING) paint.setColor(Color.RED);
		
		canvas.drawText("time : " + mv.getNowLevel().time, LoadActivity.ScreenWidth - 60, 20, paint);
	}
	
	
	
	public void Logic(MarioView mv)
	{
		this.tabTime++;
		
		if(this.tabTime > 20)
		{
			if(mv.getNowLevel().time > 0 && !mv.getNowLevel().isWin) mv.getNowLevel().time--;
			this.tabTime = 0;
			if(mv.getNowLevel().time == 100 && !mv.getNowLevel().isWin) MarioView.PlayMusic(12);
			if(mv.getNowLevel().getTime() == 0 && mv.getMario().hp > 0) mv.getMario().Dead3(mv); 
		}

	}
	
	
}
