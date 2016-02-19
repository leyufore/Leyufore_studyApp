package com.mario.mario;

import java.util.ArrayList;
import java.util.Random;

import com.mario.load.LoadResource;

import android.graphics.Bitmap;
import game.sprite.Sprite;

public class Tile extends Sprite
{
	private float startX,startY;


	private int type;


	private int index,index2;


	private int changeTime = 4;

	static int count;


	private int jumpTime;


	static int move[] = {-4,-3,-2,-1,0,1,2,3,4};


	int value;

	private int frilTime;

	static ArrayList <Shell>shell = new ArrayList<Shell>();





	public int getType()
	{
		return type;
	}

	public void setJumpTime(int jumpTime)
	{
		this.jumpTime = jumpTime;
	}



	public Tile(float x, float y, Bitmap image, int type)
	{
		super(x, y, image);
		this.startX = x;
		this.startY = y;
		this.type = type;

		switch(type)
		{
			case 21:
				this.index = 8;
				this.value = 1;
				break;

			case 17:
				this.index = 31;
				break;

			case 37:
				this.value = new Random().nextInt(6) + 1;
				break;

			case 77:
				this.index =15;
				break;

			case 93:
				this.index =17;
				break;
		}

	}




	public static void Move(MarioView mv)
	{
		if(mv.getMario().hp == 0) return;

		if(mv.getMario().getState().equals("右跑"))
		{

			for(int i=0; i<mv.getNowLevel().getB_tile().size(); i++)
			{
				Tile t = mv.getNowLevel().getB_tile().get(i);
				t.x -= mv.getMario().getxSpeed();
			}

			for(int i=0; i<mv.getNowLevel().getQ_tile().size(); i++)
			{
				Tile t = mv.getNowLevel().getQ_tile().get(i);
				t.x -= mv.getMario().getxSpeed();
			}

			for(int i=0; i<mv.getNowLevel().getEnemy().size(); i++)
			{
				Enemy e = mv.getNowLevel().getEnemy().get(i);
				e.x-=mv.getMario().getxSpeed();
			}


			for(int i=0; i<mv.getNowLevel().getCoin().size(); i++)
			{
				Coin c = mv.getNowLevel().getCoin().get(i);
				c.x-=mv.getMario().getxSpeed();;
			}


			for(int i=0; i<Level.food.size(); i++)
			{
				Sprite s = Level.food.get(i);
				s.x-=mv.getMario().getxSpeed();;
			}


			for(int i=0; i<mv.getMario().getBullet().size(); i++)
			{
				Bullet b = mv.getMario().getBullet().get(i);
				b.x-=mv.getMario().getxSpeed();;
			}

			for(int i=0; i<shell.size(); i++)
			{
				Shell s  = shell.get(i);
				s.x-=mv.getMario().getxSpeed();;
			}

			for(int i=0; i<mv.getNowLevel().ladder.size(); i++)
			{
				Ladder l = mv.getNowLevel().ladder.get(i);
				l.x-=mv.getMario().getxSpeed();
			}
			count+= mv.getMario().getxSpeed();


		}
		else if(mv.getMario().getState().equals("左跑"))
		{

			for(int i=0; i<mv.getNowLevel().getB_tile().size(); i++)
			{
				Tile t = mv.getNowLevel().getB_tile().get(i);
				t.x += mv.getMario().getxSpeed();
			}

			for(int i=0; i<mv.getNowLevel().getQ_tile().size(); i++)
			{
				Tile t = mv.getNowLevel().getQ_tile().get(i);
				t.x += mv.getMario().getxSpeed();
			}

			for(int i=0; i<mv.getNowLevel().getEnemy().size(); i++)
			{
				Enemy e = mv.getNowLevel().getEnemy().get(i);
				e.x+=mv.getMario().getxSpeed();
			}


			for(int i=0; i<mv.getNowLevel().getCoin().size(); i++)
			{
				Coin c = mv.getNowLevel().getCoin().get(i);
				c.x+=mv.getMario().getxSpeed();;
			}


			for(int i=0; i<Level.food.size(); i++)
			{
				Sprite s = Level.food.get(i);
				s.x+=mv.getMario().getxSpeed();;
			}

			for(int i=0; i<mv.getMario().getBullet().size(); i++)
			{
				Bullet b = mv.getMario().getBullet().get(i);
				b.x+=mv.getMario().getxSpeed();;
			}


			for(int i=0; i<shell.size(); i++)
			{
				Shell s  = shell.get(i);
				s.x+=mv.getMario().getxSpeed();;
			}


			for(int i=0; i<mv.getNowLevel().ladder.size(); i++)
			{
				Ladder l = mv.getNowLevel().ladder.get(i);
				l.x+=mv.getMario().getxSpeed();
			}
			count-= mv.getMario().getxSpeed();


		}
	}



	public void Jump()
	{
		if(this.jumpTime > 0)
		{
			this.y += move[index2];
			if(index2 < move.length - 1)
			{
				index2++;
			}
			else
			{
				this.jumpTime = 0;
				index2 = 0;
			}
		}
	}



	public void ChangeImage()
	{
		this.changeTime -- ;
		switch(type)
		{
			case 21:
				if(this.value > 0)
				{
					this.image = LoadResource.tile.get(index);
					this.IsTimeOver();
					if(index > 11 ) index = 8;
				}
				else
				{
					this.image = LoadResource.tile.get(7);
				}
				break;

			case 17:
				this.image = LoadResource.tile.get(index);
				this.IsTimeOver();
				if(index > 34 ) index = 31;
				break;

			case 37:
				if(this.value > 0) this.image = LoadResource.tile.get(6);
				else this.image = LoadResource.tile.get(7);
				break;

			case 77:
				this.image = LoadResource.tile.get(index);
				index++;
				if(index > 16 ) index = 15;
				break;

			case 93:
				this.image = LoadResource.tile.get(index);
				index++;
				if(index > 18 ) index = 17;
				break;
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



	public void Back()
	{
		this.x = this.startX;
		this.y = this.startY;

		if(this.type == 21) this.value = 1;
		if(this.type == 37) this.value = new Random().nextInt(6) + 1 ;

	}



	public void Fril(Mario mario)
	{
		this.frilTime ++;
		if(this.type == 15 && this.frilTime > 50)
		{
			shell.add(new Shell(this.x, this.y+1, LoadResource.weapon.get(1),mario.x > this.x ? 4 : -4));
			this.frilTime = 0;
			MarioView.PlayMusic(7);
		}
	}





}
