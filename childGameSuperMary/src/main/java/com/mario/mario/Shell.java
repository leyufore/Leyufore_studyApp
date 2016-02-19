package com.mario.mario;

import com.mario.load.LoadActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import game.sprite.Sprite;

public class Shell extends Sprite
{
	private int movedir;

	
	
	public void setMovedir(int movedir) 
	{
		this.movedir = movedir;
	}
	public int getMovedir()
	{
		return movedir;
	}




	public Shell(float x, float y, Bitmap image, int xSpeed) 
	{
		super(x, y, image);
		this.xSpeed = xSpeed;
		this.hp = 1;
		this.movedir = 1;
	}

	

	public void Draw(Canvas canvas)
	{
		if(this.xSpeed > 0)
		{
			canvas.save();
			canvas.scale(-1, 1, this.x + this.image.getWidth()/2, this.y + this.image.getHeight()/2);
			canvas.drawBitmap(image, x, y, null);
			canvas.restore();
		}
		else
		{
			canvas.drawBitmap(image, x, y, null);
		}
	}
	
	
	public void Move()
	{
		if(this.movedir >= 1)
		{
			this.x +=this.xSpeed;
			if(this.x + this.image.getWidth() < 0 || this.x > LoadActivity.ScreenWidth)
			{
				this.hp = 0;
			}
		}
		else
		{
			this.y+=4;	
		}
	}

	
}
