package com.mario.mario;

import com.mario.load.LoadActivity;

import android.graphics.Bitmap;
import game.sprite.Sprite;



public class Map extends Sprite
{
	private int id;

	public Map(float x, float y, Bitmap image,int id)
	{
		super(x, y, image);
		this.id = id;
	}



	public void Move(int dir,MarioView mv)
	{
		if(dir == 1)
		{
			xSpeed = 2;
		}
		else
		{
			xSpeed = -2;
		}

		if(this.x == -LoadActivity.ScreenWidth)
		{
			if(mv.getMario().state.equals("右跑"))
			{
				this.x = LoadActivity.ScreenWidth;
			}
		}
		else if(this.x == LoadActivity.ScreenWidth)
		{
			if(mv.getMario().state.equals("左跑"))
			{
				this.x = -LoadActivity.ScreenWidth;
			}
		}

		this.x+=xSpeed;
	}









	public void Back()
	{
		if(this.id == 1)
		{
			this.x = 0;
		}
		else if(this.id == 2)
		{
			this.x = LoadActivity.ScreenWidth;
		}
	}

}

