package com.mario.mario;

import android.graphics.Bitmap;
import game.sprite.Sprite;

public class MushRoom extends Sprite
{

	private Tile t;


	private int count;


	private int dir;


	private boolean onLand;


	private int move[] = {1,2,3,4,5,6,7,8};


	private int index;







	public int getCount()
	{
		return count;
	}




	public MushRoom(float x, float y, Bitmap image,Tile t)
	{
		super(x, y, image);
		this.t = t;
		this.dir = 2;
		this.hp = 1;
	}





	public void Move()
	{
		if(count < 16)
		{
			this.y-=2;
			count+=2;
			this.x = t.x;
		}

		else
		{
			if(count<18) count++;

			//左走
			if(dir == 1)
			{
				this.x-=2;
			}
			//右走
			else
			{
				this.x+=2;
			}
		}

	}





	public void Logic(MarioView mv)
	{
		if(this.count < 18) return;

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

						if(this.x > t.x - 16 && this.x < t.x + 16 && this.y < t.y)
						{
							this.y = t.y - this.image.getHeight();
							this.onLand = true;
							this.index = 0;
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

					if(this.x > t.x - 16 && this.x < t.x + 16 && this.y < t.y)
					{
						this.y = t.y - this.image.getHeight();
						this.onLand = true;
						this.index = 0;
					}


					if(this.y > t.y - this.image.getHeight() && this.x < t.x )
					{
						this.dir = 1;
					}


					else if(this.y > t.y - this.image.getHeight() && this.x > t.x )
					{
						this.dir = 2;
					}
				}
			}
		}


		if(!onLand)
		{
			this.y+=this.move[index];
			if(index < move.length -1) index++;
		}
	}




}
