package com.mario.mario;

import com.mario.load.LoadResource;

import android.graphics.Bitmap;

public class Piranha extends Enemy
{

	private int updownmove[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ,0};



	private int count;

	public Piranha(float x, float y, Bitmap image)
	{
		super(x, y, image);
		this.index = 10;
		this.ySpeed = 2;
		this.hp = 1;
		this.name = "食人花";
	}


	public void Move()
	{
		this.y+=this.updownmove[count];
		count++;
		if(count > this.updownmove.length - 1)
		{
			count = 0;
		}
	}


	public void ChangeImage()
	{
		this.changeTime --;

		this.image = LoadResource.enemy.get(index);
		this.IsTimeOver();
		if(this.index == 12) this.index = 10;
	}



	@Override
	public void Back()
	{
		this.x = startX;
		this.y = startY;
		this.index = 10;
		this.ySpeed = 2;
		this.hp = 1;
		this.count = 0;
		this.hit_bullet_or_tortois_dir = 0;
		this.hitbullet_or_tortoise = false;
	}

}
