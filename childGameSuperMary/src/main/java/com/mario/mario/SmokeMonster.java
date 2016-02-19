package com.mario.mario;

import com.mario.load.LoadActivity;
import com.mario.load.LoadResource;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class SmokeMonster extends Enemy
{
	private int throwTime;



	public SmokeMonster(float x, float y, Bitmap image)
	{
		super(x, y, image);
		this.startX = x;
		this.startY = y;
		this.name = "烟雾怪";
		this.hp = 1;
		this.dir = 1;
		this.xSpeed = 4;
		this.state = "移动";
	}






	@Override
	public void Draw(Canvas canvas)
	{
		canvas.save();
		canvas.scale(1, this.state.equals("移动") && !this.hitbullet_or_tortoise ? 1 : -1, this.x+this.image.getWidth()/2, this.y+ this.image.getHeight()/2);
		canvas.drawBitmap(image, x, y, null);
		canvas.restore();
	}






	@Override
	public void Move(MarioView mv)
	{
		if(!this.state.equals("移动")) return;
		if(this.y > LoadActivity.ScreenHeight) this.hp = 0;

		if(!mv.getNowLevel().isWin)
		{
			if(this.x > mv.getMario().x + 50)
			{
				this.dir = 1;
			}
			if(this.x < mv.getMario().x - 50)
			{
				this.dir = 2;
			}

			if(dir == 2)
			{
				this.x+= this.xSpeed;
				if(this.xSpeed < 4) this.xSpeed++;
			}
			else if(dir == 1)
			{
				this.x+= this.xSpeed;
				if(this.xSpeed > -4) this.xSpeed--;
			}
		}
		else
		{
			this.x-=4;
		}
	}






	@Override
	public void ChangeImage()
	{
		if(this.throwTime >=80)
		{
			this.image = LoadResource.enemy.get(12);
		}
		else
		{
			this.image =  LoadResource.enemy.get(13);
		}
	}






	public void ThrowThornBlame(MarioView mv)
	{
		this.throwTime ++;
		if(this.xSpeed <= 0 || this.throwTime < 80 || mv.getNowLevel().isWin) return;
		mv.getNowLevel().getEnemy().add(new Thorn(this.x, this.y, LoadResource.enemy.get(3)));
		this.throwTime=0;
	}



	@Override
	public void Dead()
	{
		if(this.state.equals("移动")) return;
		this.xSpeed = 0;
		this.y+=8;
	}



	@Override
	public void Back()
	{
		this.x = startX;
		this.y = startY;
		this.xSpeed = 4;
		this.hp = 1;
		this.hitbullet_or_tortoise = false;
		this.dir = 1;
		this.hit_bullet_or_tortois_dir = 0;
		this.state ="移动";
		this.throwTime = 0;
	}

}
