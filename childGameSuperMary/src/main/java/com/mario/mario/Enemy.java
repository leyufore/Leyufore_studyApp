package com.mario.mario;

import com.mario.load.LoadActivity;

import game.sprite.Sprite;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Enemy extends Sprite
{

	public float startX,startY;


	public String name;


	public int index,moveindex;


	public int changeTime;


	public int dir;


	public String state;

	public boolean onLand;

	public boolean hitbullet_or_tortoise;

	public int hit_bullet_or_tortois_dir;



	private int move[] = {1,2,3,4,5,6,7,8};

	private int updownMove[] = {-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8};

	public int udindex;







	public Enemy(float x, float y, Bitmap image)
	{
		super(x, y, image);
		this.startX = x;
		this.startY = y;
	}




	public void Draw(Canvas canvas)
	{
		if(!this.name.equals("食人花"))
		{
			canvas.save();
			canvas.scale(this.dir==1? -1:1 , this.hitbullet_or_tortoise? -1:1, this.x+this.image.getWidth()/2, this.y+this.image.getHeight()/2);
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
		if(dir == 1)
		{
			this.x-=this.xSpeed;
		}
		else
		{
			this.x+=this.xSpeed;
		}
	}

	public void ChangeImage(){}
	public void Dead(){}
	public void ChangeState(){}
	public void ChangeStateTime(){}
	public void CollisionWithEnemy(MarioView mv){}
	public void Back(){}
	public void Move(MarioView mv){}
	public void ThrowThornBlame(MarioView mv){}


	public void Dead2()
	{
		if(!this.hitbullet_or_tortoise) return;

		if(!this.name.equals("食人花"))
		{
			this.xSpeed = 0;

			if(this.hit_bullet_or_tortois_dir == 2)
			{
				this.x+=4;
			}
			else
			{
				this.x-=4;
			}

			if(this.udindex < this.updownMove.length)
			{
				this.y+=this.updownMove[udindex];
				udindex++;
			}
			else
			{
				this.y+=8;
				if(this.y > LoadActivity.ScreenHeight) this.hp = 0;
			}
		}
		else
		{
			this.hp = 0;
		}
	}



	public void IsTimeOver()
	{
		if(this.changeTime <= 0)
		{
			this.index++;
			this.changeTime = 4;
		}
	}



	public void Logic(MarioView mv)
	{
		if(this.name.equals("食人花") || this.name.equals("烟雾怪") ||  this.hitbullet_or_tortoise) return;

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
							this.moveindex = 0;
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

					if(this.x > t.x - 16 && this.x < t.x + 16 && (this.name.equals("乌龟")? this.y+8:this.y) < t.y)
					{
						this.y = t.y - this.image.getHeight();
						this.onLand = true;
						this.moveindex = 0;
					}


					if(this.y > t.y - this.image.getHeight() && this.x < t.x )
					{
						this.dir = 1;
						if(this.name.equals("乌龟") && this.state.equals("龟壳") && this.xSpeed > 0)
						{
							if(SpriteAtScreen(this, LoadActivity.ScreenWidth, LoadActivity.ScreenHeight)) MarioView.PlayMusic(7);
						}

					}


					else if(this.y > t.y - this.image.getHeight() && this.x > t.x )
					{
						this.dir = 2;
						if(this.name.equals("乌龟") && this.state.equals("龟壳") && this.xSpeed > 0)
						{
							if(SpriteAtScreen(this, LoadActivity.ScreenWidth, LoadActivity.ScreenHeight)) MarioView.PlayMusic(7);
						}
					}
				}
			}
		}


		if(!onLand)
		{
			this.y+=this.move[moveindex];
			if(moveindex < move.length -1) moveindex++;
		}
	}


}
