package com.mario.mario;

import com.mario.load.LoadActivity;
import com.mario.load.LoadResource;
import game.sprite.Sprite;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet extends Sprite
{

	private int degrees;


	boolean onLand;


	int jumpTime;


	String state="";



	public Bullet(float x, float y, Bitmap image,int xSpeed)
	{
		super(x, y, image);
		this.xSpeed = xSpeed;
		this.hp = 1;
	}





	public void Draw(Canvas canvas)
	{

		canvas.save();
		canvas.rotate(degrees, this.x+this.image.getWidth()/2, this.y+this.image.getHeight()/2);
		canvas.drawBitmap(image, x, y, null);
		canvas.restore();
	}





	public void DegreesPlus()
	{
		this.degrees+=60;
		if(this.degrees>=360)
		{
			this.degrees=0;
		}
	}





	public void Jump()
	{
		if(this.state.indexOf("跳")==-1 && onLand)
		{
			this.jumpTime=6;
			this.ySpeed=6;
			this.state="跳";
		}
	}




	public void Move()
	{
		this.x+=this.xSpeed;


		if(this.x + this.image.getWidth() < 0 || this.x > LoadActivity.ScreenWidth || this.y > LoadActivity.ScreenHeight)
		{
			this.hp = 0;
		}
	}



	public void UpDownMove()
	{
		if(this.jumpTime>0)
		{
			this.y-=ySpeed;
			if(ySpeed>0)ySpeed--;
			this.state="跳";
			this.jumpTime--;
		}
		else if(this.jumpTime<=0 && !onLand)
		{
			this.y+=ySpeed;
			if(ySpeed<5) ySpeed++;
			this.state="跳";
		}

	}





	public void Logic(MarioView mv)
	{
		if(mv.getMario().getBullet().size() < 0) return;

		this.onLand = false;


		for(int i=0; i<mv.getNowLevel().getB_tile().size(); i++)
		{
			Tile t = mv.getNowLevel().getB_tile().get(i);

			if(t.x > this.x - this.image.getWidth()*2 && t.x < this.x + this.image.getWidth()*2)
			{
				if(t.getType() == 133 || t.getType() == 134 || t.getType() == 135)
				{
					if(this.Rectangle_CollisionWithSprite(t))
					{
						if(this.y < t.y)
						{
							this.onLand = true;
							this.ySpeed=0;
							this.state="";
						}
					}
				}
			}
		}


		for(int i=0; i<mv.getNowLevel().getQ_tile().size(); i++)
		{
			Tile t = mv.getNowLevel().getQ_tile().get(i);

			if(t.x > this.x - this.image.getWidth()*2  && t.x < this.x + this.image.getWidth()*2 )
			{
				if(this.Rectangle_CollisionWithSprite(t))
				{
					if(this.y < t.y)
					{
						this.onLand = true;
						this.ySpeed=0;
						this.state="";
					}
					else
					{
						this.hp=0;
						Level.blast.add(new Blast(this.x,this.y,LoadResource.blast.get(0)));
						MarioView.PlayMusic(7);
					}
				}
			}
		}
	}

}
