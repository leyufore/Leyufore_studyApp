package com.mario.mario;

import com.mario.load.LoadResource;

import android.graphics.Bitmap;
import game.sprite.Sprite;

public class Flower extends Sprite
{

	private Tile t;
	

	private int count;
	

	private int tabIndex;
	
	
	
	
	
	public int getCount() 
	{
		return count;
	}



	public Flower(float x, float y, Bitmap image, Tile t)
	{
		super(x, y, image);
		this.t = t;
		this.hp = 1;
	}

	
	

	public void UpMove()
	{
		if(count < 16)
		{
			this.y-=2;
			count+=2;
		}
		this.x = t.x;
	}
	
	

	public void ChangeImage()
	{
		this.image = LoadResource.food.get(tabIndex); 
		tabIndex++;
		if(tabIndex == 3)
		{
			tabIndex=1;
		}
		
	}
	
	
	
}
