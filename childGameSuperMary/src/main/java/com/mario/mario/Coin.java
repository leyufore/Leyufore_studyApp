package com.mario.mario;

import com.mario.load.LoadResource;
import android.graphics.Bitmap;
import game.sprite.Sprite;

public class Coin extends Sprite
{
	private float startX, startY; 
	
	private int type;
	
	
	private int index,index2;
	

	private int move[] = {-10,-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9,10};
	
	private int changeTime;
	 

	
	public int getType() 
	{
		return type;
	}


	public Coin(float x, float y, Bitmap image, int type)
	{
		super(x, y, image);
		this.startX = x;
		this.startY = y;
		this.type = type;
		this.hp = 1;
	}
	
	
	
	public void ChangeImage()
	{
		this.changeTime ++;
		if(this.changeTime > 1)
		{
			this.changeTime = 0;
			this.image = LoadResource.coin.get(index2);
			index2++;
			if(index2==3) index2=0;
		}
	}
	
	
	
	
	
	public void Jump()
	{	
		if(this.type == 1)
		{
			this.y+=this.move[index];
			if(index < this.move.length - 1)
			{
				this.index++;
			}
			else
			{
				this.hp = 0;
			}
		}
	}
	
	
	public void Back()
	{
		if(this.type == 2)
		{
			this.hp = 1;
			this.type = 2;
			this.x = startX;
			this.y = startY;
			this.index2=0;
		}
	}

}
