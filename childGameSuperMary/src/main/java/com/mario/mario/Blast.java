package com.mario.mario;

import com.mario.load.LoadResource;
import game.sprite.Sprite;
import android.graphics.Bitmap;

public class Blast extends Sprite
{
	private int index;


	public Blast(float x, float y, Bitmap image)
	{
		super(x, y, image);
		this.hp = 1;
	}	

	public void ChangeImage()
	{
		this.image = LoadResource.blast.get(index);
		index++;
		if(index > 2)
		{
			this.hp = 0;
		}
	}
	

}
