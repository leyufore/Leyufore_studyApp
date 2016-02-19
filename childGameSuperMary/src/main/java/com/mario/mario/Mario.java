package com.mario.mario;

import game.sprite.Sprite;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.KeyEvent;

import com.mario.load.LoadActivity;
import com.mario.load.LoadResource;



public class Mario extends Sprite
{

	String name;

	float startX,startY;

	int level;


	int xSpeed,ySpeed;


	int lifevalue;


	String state;


	private int index,index2=4,index3=8;


	private int changeTime;


	Rect rect,rect2,rect3;

	boolean onLand,right,left;

	String jumping;

	int jumpTime;

	int score;

	int coin_value;

	private int noCheckCoiiisionTime;

	private ArrayList <Bullet>bullet = new ArrayList<Bullet>();





	public int getySpeed()
	{
		return ySpeed;
	}
	public void setJumpTime(int jumpTime)
	{
		this.jumpTime = jumpTime;
	}
	public ArrayList<Bullet> getBullet()
	{
		return bullet;
	}
	public String getState()
	{
		return state;
	}
	public int getxSpeed()
	{
		return xSpeed;
	}
	public boolean isRight()
	{
		return right;
	}
	public boolean isLeft()
	{
		return left;
	}
	public Rect getRect()
	{
		return rect;
	}
	public boolean isOnLand()
	{
		return onLand;
	}
	public int getNoCheckCoiiisionTime()
	{
		return noCheckCoiiisionTime;
	}




	public Mario(float x, float y, Bitmap image,Context context)
	{
		super(x, y, image);
		this.startX = x;
		this.startY = y;
		this.level = 1;
		this.hp = 1;
		this.lifevalue = 3;
		this.xSpeed = 4;
		this.changeTime = 2;
		this.state = "右停";
		this.jumping = "";
		rect  = new Rect(0,11,16,31);
		rect2 = new Rect(4,34,12,35);
		rect3 = new Rect(0,29,16,33);

		Intent intent = ((Activity) context).getIntent();
		Bundle bundle = intent.getExtras();
		this.name = bundle.getString("save_name")+":";
		if(this.name.equals(":"))
		{
			this.name = "null:";
		}
	}



	public void Draw(Canvas canvas,Paint paint)
	{
		if(this.noCheckCoiiisionTime > 0) this.noCheckCoiiisionTime --;

		if(this.noCheckCoiiisionTime % 2 == 0)
		{
			if(this.state.indexOf("左") != -1)
			{
				canvas.save();
				canvas.scale(-1, 1, this.x+this.image.getWidth()/2, this.y+this.image.getHeight()/2);
				canvas.drawBitmap(image, x, y, null);
				canvas.restore();
			}
			else
			{
				canvas.drawBitmap(image, x, y, null);
			}
		}
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
	}




	public void Move(MarioView mv)
	{
		if(this.hp <= 0 || mv.getNowLevel().isWin) return;

		if(this.state.equals("右跑"))
		{
			if(right)
			{
				if(this.x < LoadActivity.ScreenWidth/2)
				{
					this.x +=this.xSpeed;
				}
				else
				{
					if(Tile.count != 300*16-LoadActivity.ScreenWidth)
					{
						Tile.Move(mv);
						if(mv.getNowLevel().getMap().size() > 0)
						{
							mv.getNowLevel().getMap().get(0).Move(2,mv);
							mv.getNowLevel().getMap().get(1).Move(2,mv);
						}
					}
					else
					{
						if(this.x < LoadActivity.ScreenWidth - this.image.getWidth())
						{
							this.x +=this.xSpeed;
						}
					}
				}
			}
		}

		else if(this.state.equals("左跑"))
		{
			if(left)
			{
				if(this.x > LoadActivity.ScreenWidth/2)
				{
					this.x -=this.xSpeed;
				}
				else
				{
					if(Tile.count != 0)
					{
						Tile.Move(mv);
						if(mv.getNowLevel().getMap().size() > 0)
						{
							mv.getNowLevel().getMap().get(0).Move(1,mv);
							mv.getNowLevel().getMap().get(1).Move(1,mv);
						}
					}
					else
					{
						if(this.x > 0)
						{
							this.x -=this.xSpeed;
						}
					}
				}
			}
		}

	}


	public void Jump()
	{
		if(this.hp < 0) return;

		if(this.jumping.indexOf("跳") == -1)
		{
			this.jumpTime = 11;
			this.ySpeed = 11;
			this.jumping = "跳";
			MarioView.PlayMusic(8);
		}

	}



	public void ChangeImage(MarioView mv)
	{
		if(this.hp < 0) return;

		this.changeTime --;

		if(this.hp > 0)
		{
			if(!mv.getNowLevel().isWin)
			{

				if(this.state.equals("右跑") && !this.jumping.equals("跳")  || this.state.equals("左跑") && !this.jumping.equals("跳"))
				{
					if(this.level == 1)
					{
						this.image = LoadResource.mario.get(index);
						if(this.changeTime <= 0)
						{
							index++;
							this.changeTime = 2;
						}
						if(index == 2) index=0;
					}

					else if(this.level == 2)
					{
						this.image = LoadResource.mario.get(index2);
						if(this.changeTime <= 0)
						{
							index2++;
							this.changeTime = 2;
						}
						if(index2 == 6) index2=4;
					}

					else if(this.level == 3)
					{
						this.image = LoadResource.mario.get(index3);
						if(this.changeTime <= 0)
						{
							index3++;
							this.changeTime = 2;
						}
						if(index3 == 10) index3=8;
					}
				}


				else if(this.state.equals("右停") && !this.jumping.equals("跳")  || this.state.equals("左停") && !this.jumping.equals("跳")   )
				{
					if(this.level == 1)
					{
						this.image = LoadResource.mario.get(0);
					}
					else if(this.level == 2)
					{
						this.image = LoadResource.mario.get(4);
					}
					else if(this.level == 3)
					{
						this.image = LoadResource.mario.get(8);
					}
				}


				else if(this.state.equals("左蹲") || this.state.equals("右蹲") )
				{
					if(this.level == 1)
					{
						this.image = LoadResource.mario.get(3);
					}
					else if(this.level == 2)
					{
						this.image = LoadResource.mario.get(7);
					}
					else if(this.level == 3)
					{
						this.image = LoadResource.mario.get(11);
					}
				}


				else if(this.jumping.equals("跳"))
				{
					if(this.level == 1)
					{
						this.image = LoadResource.mario.get(2);
					}
					else if(this.level == 2)
					{
						this.image = LoadResource.mario.get(6);
					}
					else if(this.level == 3)
					{
						this.image = LoadResource.mario.get(10);
					}
				}
			}
			else
			{
				this.image = LoadResource.mario.get(13);
			}
		}
		else
		{
			this.image = LoadResource.mario.get(12);
		}
	}



	public void Fril()
	{
		if(this.level >= 3 && this.bullet.size() < 3 && this.state.indexOf("蹲")== -1)
		{
			this.bullet.add(new Bullet(this.x + this.image.getWidth()/2,
					this.y + this.image.getHeight()/2,
					LoadResource.weapon.get(0),
					this.state.indexOf("右")!= -1? 6 : -6 ));
			MarioView.PlayMusic(0);
		}
	}



	public void Dead(MarioView mv)
	{
		if(this.level > 1)
		{
			this.level --;
			this.noCheckCoiiisionTime = 100;
		}
		else
		{
			this.hp = 0;
			this.jumpTime = 11;
			this.ySpeed = 11;
			MarioView.PlayMusic(4);
			mv.getNowLevel().getGm().PuaseMusic(true);
		}
	}


	public void Dead2(MarioView mv)
	{
		if(this.hp > 0)
		{
			this.hp = 0;
			MarioView.PlayMusic(4);
			mv.getNowLevel().getGm().PuaseMusic(true);
		}
	}

	public void Dead3(MarioView mv)
	{
		this.hp = 0;
		this.jumpTime = 11;
		this.ySpeed = 11;
		MarioView.PlayMusic(4);
		mv.getNowLevel().getGm().PuaseMusic(true);
	}


	public void Back()
	{
		if(this.hp == 0 && this.y > LoadActivity.ScreenHeight*2)
		{
			this.noCheckCoiiisionTime = 0;
			this.level = 1;
			this.lifevalue--;
			this.x = startX;
			this.y = startY;
			this.hp = 1;
			this.state = "右停";
		}
	}



	public void ChangeRectSize()
	{
		if(this.level == 1)
		{
			if(this.state.indexOf("蹲")!= -1)
			{
				this.rect.left = 0 ;
				this.rect.top = 17;
				this.rect.right = 16;
				this.rect.bottom = 31;
			}
			else
			{
				this.rect.left = 0 ;
				this.rect.top = 11;
				this.rect.right = 16;
				this.rect.bottom = 31;
			}
		}
		else if(this.level == 2 || this.level == 3)
		{
			if(this.state.indexOf("蹲")!= -1)
			{
				this.rect.left = 0 ;
				this.rect.top = 16;
				this.rect.right = 16;
				this.rect.bottom = 31;
			}
			else
			{
				this.rect.left = 0 ;
				this.rect.top = 3;
				this.rect.right = 16;
				this.rect.bottom = 31;
			}
		}

	}



	public void onKeyDown(int keyCode, KeyEvent event,MarioView mv)
	{
		if(this.hp < 0) return ;

		switch(keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_UP :
				if(!mv.getNowLevel().isWin) this.Fril();
				break;

			case KeyEvent.KEYCODE_DPAD_LEFT :
				this.state = "左跑";
				break;

			case KeyEvent.KEYCODE_DPAD_RIGHT :
				this.state = "右跑";
				break;

			case KeyEvent.KEYCODE_DPAD_CENTER:
				if(!mv.getNowLevel().isWin) this.Jump();
				break;

			case KeyEvent.KEYCODE_DPAD_DOWN:
				if(this.state.indexOf("左")!= -1)
				{
					this.state = "左蹲";
				}
				else
				{
					this.state = "右蹲";
				}
				break;
		}

	}



	public void onKeyUp(int keyCode, KeyEvent event)
	{
		if(this.hp < 0) return ;

		switch(keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_LEFT :
				this.state = "左停";
				break;

			case KeyEvent.KEYCODE_DPAD_RIGHT :
				this.state = "右停";
				break;
		}
	}




	public void Logic(MarioView mv)
	{
		onLand = false;
		this.left = true;
		this.right = true;

		if(this.hp > 0)
		{

			for(int i=0; i<mv.getNowLevel().getB_tile().size(); i++)
			{
				Tile t = mv.getNowLevel().getB_tile().get(i);

				if(t.x > this.x - this.image.getWidth()*2 && t.x < this.x + this.image.getWidth()*2)
				{
					if(t.getType() == 133 || t.getType() == 134 || t.getType() == 135 || t.getType() == 77 || t.getType() == 93)
					{
						if(t.MoreRectangle_CollisionWithSprite(this, t.getType() == 77 || t.getType() == 93 ? rect : rect2))
						{
							if(t.getType() == 133 || t.getType() == 134 || t.getType() == 135 )
							{
								this.onLand = true;
								this.jumping = "";
								if(this.jumpTime <= 0)
								{
									this.ySpeed = 0;
									this.y = t.y - this.image.getHeight();
								}
							}
							else
							{
								if(this.x + this.image.getWidth()/2 > t.x)
								{
									if(mv.getNowLevel().time > 0 )mv.getNowLevel().isWin = true;
								}
							}
						}
					}
				}
			}

			for(int i=0; i<mv.getNowLevel().getQ_tile().size(); i++)
			{
				Tile t = mv.getNowLevel().getQ_tile().get(i);

				if(t.x > this.x - this.image.getWidth()*2 && t.x < this.x + this.image.getWidth()*2)
				{
					if(t.MoreRectangle_CollisionWithSprite(this, rect))
					{
						if(this.x > t.x - 16 && this.x < t.x + 16 && this.y + this.rect.top < t.y)
						{
							this.y = t.y - this.image.getHeight();
							this.onLand = true;
							this.jumping = "";
							if(this.jumpTime <= 0)
							{
								this.ySpeed = 0;
							}
						}


						if(this.x > t.x - 16 && this.x < t.x + 16 && this.y + (this.image.getHeight() - this.rect.top) > t.y)
						{
							this.jumpTime = 0;

							switch(t.getType())
							{
								case 21:
									if(t.value > 0)
									{
										if(this.level == 1)
										{
											Level.food.add(new MushRoom(t.x,t.y,LoadResource.food.get(0),t));
										}
										else
										{
											Level.food.add(new Flower(t.x,t.y,LoadResource.food.get(1),t));
										}
										t.value --;
										t.setJumpTime(1);
										MarioView.PlayMusic(15);
									}
									break;

								case 37:
									if(t.value > 0)
									{
										mv.getNowLevel().getCoin().add(new Coin(t.x,t.y,LoadResource.coin.get(0),1));
										t.value --;
										t.setJumpTime(1);
										MarioView.PlayMusic(3);
										score+=10;
										coin_value++;
									}

									break;
							}

							MarioView.PlayMusic(7);

						}


						if(this.y > t.y - this.image.getHeight() && this.x < t.x )
						{
							this.right = false;
						}


						else if(this.y > t.y - this.image.getHeight() && this.x > t.x )
						{
							this.left = false;
						}
					}
				}

			}

		}


		if(this.jumpTime > 0)
		{
			this.jumping = "跳";
			this.y-= ySpeed;
			if(this.ySpeed > 0) this.ySpeed--;
			jumpTime --;
		}
		else if(!onLand && jumpTime <= 0)
		{
			this.y+=ySpeed;
			if(this.ySpeed < 11) this.ySpeed++;
			this.jumping = "跳";
		}


		if(this.y > LoadActivity.ScreenHeight)  this.Dead2(mv);

	}

}
