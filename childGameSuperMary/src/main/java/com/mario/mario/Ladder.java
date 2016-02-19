package com.mario.mario;

import com.mario.load.LoadActivity;
import android.graphics.Bitmap;
import game.sprite.Sprite;

public class Ladder extends Sprite
{

	private int movedir;


	public Ladder(float x, float y, Bitmap image, int movedir) 
	{
		super(x, y, image);
		this.startX = x;
		this.startY = y;
		this.movedir = movedir;
	}

	
	public void Move()
	{
		
		if(this.movedir == 2)
		{
			this.y-=2;
			if(this.y + this.image.getHeight() < 0) this.y = (float) (LoadActivity.ScreenHeight*1.5);
		}
		else
		{
			this.y+=2;
			if(this.y > LoadActivity.ScreenHeight) this.y = (float) (-LoadActivity.ScreenHeight/2);
		}
			
	}
	
	
	public void Back()
	{
		this.x = this.startX;
		this.y = this.startY;
	}
	

}
