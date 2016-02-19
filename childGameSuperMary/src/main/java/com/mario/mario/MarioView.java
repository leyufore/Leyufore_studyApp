package com.mario.mario;

import game.button.GameButton;
import game.sprite.Sprite;
import game.view.GameView;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.game.mario.R;
import com.mario.load.LoadActivity;
import com.mario.load.LoadResource;
import com.mario.load.LoadView;
import com.mario.menu.MenuActivity;
import com.mario.options.OptionsActivity;

public class MarioView extends GameView implements Runnable
{
	ArrayList <Level>level = new ArrayList<Level>();

	Level nowLevel;

	Mario mario;

	private GameButton backToGame,options,backToMenu,left,down,right,A,B;

	public static final int GAME_ING = 0;
	public static final int GAME_PAUSE = 1;
	public static final int GAME_WIN = 2;
	public static final int GAME_OVER = 3;
	public static final int GAME_PANNEL = 4;

	int gameState = GAME_PANNEL;

	private Panel panel;

	boolean isallreadyingame;

	private String gameover = "GAME OVER", gamewin = "MISSION ARE OVER ! YOU WIN" ,msg = "click screen to back";

	private int gameoverTime = 170;




	public Mario getMario()
	{
		return mario;
	}

	public Level getNowLevel()
	{
		return nowLevel;
	}
	public int getGameState()
	{
		return gameState;
	}

	public MarioView(Context context)
	{
		super(context);
		this.setKeepScreenOn(true);
		this.setFocusableInTouchMode(true);

		paint.setTypeface(LoadView.mFace);


		for(int i=1; i<=3; i++)
		{
			level.add(new Level(i,context));
		}

		this.nowLevel = level.get(0);

		mario = new Mario(80,0,LoadResource.mario.get(0),context);

		this.panel = new Panel();


		this.backToGame = new GameButton(LoadActivity.ScreenWidth/2 - 60,LoadActivity.ScreenHeight / 3 ,20,"back to game",context,R.raw.button);
		this.options    = new GameButton(backToGame.x, backToGame.y + 60, 20 , "options", context, R.raw.button);
		this.backToMenu = new GameButton(options.x, options.y + 60, 20 , "backToMenu", context, R.raw.button);

		this.left       = new GameButton(0 ,
				LoadActivity.ScreenHeight - LoadResource.button.get(0).getHeight()*2,
				LoadResource.button.get(0));

		this.right      = new GameButton(left.image.getWidth()*2 ,
				LoadActivity.ScreenHeight - LoadResource.button.get(0).getHeight()*2,
				LoadResource.button.get(1));

		this.down       = new GameButton(left.image.getWidth(),
				LoadActivity.ScreenHeight - LoadResource.button.get(0).getHeight(),
				LoadResource.button.get(2));

		this.A          = new GameButton(LoadActivity.ScreenWidth  - LoadResource.button.get(3).getWidth()*2,
				LoadActivity.ScreenHeight - LoadResource.button.get(3).getHeight(),
				LoadResource.button.get(3));



		this.B          = new GameButton(LoadActivity.ScreenWidth  - LoadResource.button.get(4).getWidth(),
				LoadActivity.ScreenHeight - LoadResource.button.get(4).getHeight()*2,
				LoadResource.button.get(4));

	}





	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		this.flag = true;
		this.t = new Thread(this);
		this.t.start();
	}



	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		this.flag = false;
		this.nowLevel.getGm().PuaseMusic(false);
	}




	public void Draw()
	{
		this.canvas = sh.lockCanvas();
		if(canvas != null)
		{
			canvas.drawColor(Color.BLACK);

			if(this.mario.y > LoadActivity.ScreenHeight*2.5 && this.mario.lifevalue > 0)
			{
				this.gameState = GAME_PANNEL;
			}

			switch(gameState)
			{

				case GAME_PAUSE:
					paint.setColor(Color.BLUE);

					canvas.drawBitmap(LoadResource.map.get(3), 0, 0,null);
					this.backToGame.Draw(canvas, paint);
					this.options.Draw(canvas, paint);
					this.backToMenu.Draw(canvas, paint);
					break;


				case GAME_PANNEL:

					if(this.isallreadyingame)
					{

						this.RemoveAllSprite();


						this.nowLevel.time = 160;


						mario.Back();


						this.nowLevel.getEnemy().addAll(this.nowLevel.getSaveenemy());
						for(int i=0; i<this.nowLevel.getEnemy().size(); i++)
						{
							Enemy e = this.nowLevel.getEnemy().get(i);
							e.Back();
						}


						for(int i=0; i<this.nowLevel.getB_tile().size(); i++)
						{
							Tile t = this.nowLevel.getB_tile().get(i);
							t.Back();
						}
						for(int i=0; i<this.nowLevel.getQ_tile().size(); i++)
						{
							Tile t = this.nowLevel.getQ_tile().get(i);
							t.Back();
						}
						Tile.count = 0;


						this.nowLevel.getCoin().addAll(this.nowLevel.getSavecoin());
						for(int i=0; i<this.nowLevel.getCoin().size(); i++)
						{
							Coin c = this.nowLevel.getCoin().get(i);
							c.Back();
						}


						for(int i=0; i<this.nowLevel.ladder.size(); i++)
						{
							Ladder l = this.nowLevel.ladder.get(i);
							l.Back();
						}


						if(this.nowLevel.getMap().size() > 0)
						{
							for(int i=0; i<this.nowLevel.getMap().size(); i++)
							{
								Map map = this.nowLevel.getMap().get(i);
								map.Back();
							}
						}

						this.nowLevel.SetTileY();

						this.isallreadyingame = false;
					}
					paint.setColor(Color.YELLOW);
					paint.setTextSize(13);


					panel.Draw(this, canvas, paint);


					canvas.drawBitmap(LoadResource.mario.get(0),
							LoadActivity.ScreenWidth /2 - LoadResource.mario.get(0).getWidth(),
							LoadActivity.ScreenHeight/2 - LoadResource.mario.get(0).getHeight(),
							paint);
					canvas.drawText("x" + mario.lifevalue, LoadActivity.ScreenWidth/2, LoadActivity.ScreenHeight/2, paint);
					break;


				case GAME_ING:

					if(!this.nowLevel.getGm().isPlaying() && this.mario.hp > 0 && !this.nowLevel.isWin)
					{
						this.nowLevel.getGm().StartMusic(true);
					}
					else
					{
						this.nowLevel.getGm().PuaseMusic(true);
					}

					this.isallreadyingame = true;

					paint.setColor(Color.YELLOW);
					paint.setTextSize(13);


					for(int i=0; i<this.nowLevel.getMap().size(); i++)
					{
						Map map = this.nowLevel.getMap().get(i);
						map.Draw(canvas);
					}

					for(int i=0; i<this.nowLevel.getB_tile().size(); i++)
					{
						Tile tile = this.nowLevel.getB_tile().get(i);


						if(tile.x >= -16 && tile.x <= LoadActivity.ScreenWidth)
						{
							tile.Draw(canvas);
							tile.ChangeImage();
						}
					}


					for(int i=0; i<Level.food.size(); i++)
					{
						Sprite s = Level.food.get(i);
						if(s.hp > 0)
						{
							if(s instanceof Flower)
							{
								Flower f = (Flower) s;
								f.UpMove();
								f.Draw(canvas);
								f.ChangeImage();
							}
							else if(s instanceof MushRoom)
							{
								MushRoom mr = (MushRoom) s;
								mr.Move();
								mr.Draw(canvas);
								mr.Logic(this);
							}
						}
						else
						{
							Level.food.remove(i);
						}

					}


					for(int i=0; i<this.nowLevel.getCoin().size(); i++)
					{
						Coin c = this.nowLevel.getCoin().get(i);
						if(c.hp > 0)
						{
							c.Draw(canvas);
							c.Jump();
							c.ChangeImage();
						}
						else
						{
							this.nowLevel.getCoin().remove(i);
						}
					}

					for(int i=0; i<this.nowLevel.getEnemy().size(); i++)
					{
						Enemy e = this.nowLevel.getEnemy().get(i);
						if(e.name.equals("食人花"))
						{
							if(e.hp > 0)
							{
								e.Draw(canvas);
								e.Move();
								e.ChangeImage();
								e.Dead2();
							}
							else
							{
								this.nowLevel.getEnemy().remove(i);
							}
						}
					}


					for(int i=0; i<this.nowLevel.getQ_tile().size(); i++)
					{
						Tile tile = this.nowLevel.getQ_tile().get(i);

						if(tile.getType() != 15)
						{

							if(tile.x >= -16 && tile.x <= LoadActivity.ScreenWidth)
							{
								tile.Draw(canvas);
								tile.ChangeImage();
								tile.Jump();
							}
						}
					}


					for(int i=0; i<Tile.shell.size(); i++)
					{
						Shell shell = Tile.shell.get(i);
						if(shell.hp > 0 )
						{
							shell.Draw(canvas);
							shell.Move();
							if(shell.y > LoadActivity.ScreenHeight)
							{
								Tile.shell.remove(i);
							}
						}
						else
						{
							Tile.shell.remove(i);
						}
					}


					for(int i=0; i<this.nowLevel.getQ_tile().size(); i++)
					{
						Tile tile = this.nowLevel.getQ_tile().get(i);

						if(tile.getType() == 15)
						{

							if(tile.x >= -16 && tile.x <= LoadActivity.ScreenWidth)
							{
								tile.Draw(canvas);
								tile.Fril(mario);
							}
						}
					}


					for(int i=0; i<this.nowLevel.getEnemy().size(); i++)
					{
						Enemy e = this.nowLevel.getEnemy().get(i);
						if(!e.name.equals("食人花"))
						{
							if(e.hp > 0)
							{
								e.Logic(this);
								e.Draw(canvas);
								if(!e.name.equals("烟雾怪"))
								{
									e.Move();
								}
								else
								{
									e.ThrowThornBlame(this);
									e.Move(this);
									e.Dead();
								}
								e.ChangeImage();
								e.ChangeStateTime();
								e.Dead2();
							}
							else
							{
								this.nowLevel.getEnemy().remove(i);
							}
						}
					}


					panel.Draw(this, canvas, paint);
					panel.Logic(this);


					for(int i=0; i<this.mario.getBullet().size(); i++)
					{
						Bullet b = this.mario.getBullet().get(i);
						if(b.hp > 0)
						{
							b.Logic(this);
							b.DegreesPlus();
							b.Draw(canvas);
							b.Jump();
							b.Move();
							b.UpDownMove();
						}
						else
						{
							this.mario.getBullet().remove(i);
						}
					}


					mario.Draw(canvas,paint);
					mario.Move(this);
					mario.ChangeImage(this);
					if(this.mario.coin_value >= 100)
					{
						this.mario.lifevalue++;
						this.mario.coin_value = 0;
						PlayMusic(13);
					}


					for(int i=0; i<Level.blast.size(); i++)
					{
						Blast b = Level.blast.get(i);
						if(b.hp > 0)
						{
							b.Draw(canvas);
							b.ChangeImage();
						}
						else
						{
							Level.blast.remove(i);
						}
					}


					for(int i=0; i<this.nowLevel.ladder.size(); i++)
					{
						Ladder l = this.nowLevel.ladder.get(i);
						l.Draw(canvas);
						l.Move();
					}

					left.Draw(canvas, paint);
					right.Draw(canvas, paint);
					down.Draw(canvas, paint);
					A.Draw(canvas, paint);
					B.Draw(canvas, paint);

					if(this.mario.lifevalue == 0 && this.mario.hp == 0 && this.mario.y > LoadActivity.ScreenHeight*2.5) this.gameState = GAME_OVER;
					this.nowLevel.GotoNextLevel(this);
					break;

				case GAME_OVER:
					if(this.nowLevel.getTime() > 0)
					{
						PlayMusic(6);
					}
					paint.setColor(Color.YELLOW);
					panel.Draw(this, canvas, paint);
					canvas.drawText(gameover, (LoadActivity.ScreenWidth - gameover.length()*13/2)/2, LoadActivity.ScreenHeight/2, paint);
					this.nowLevel.setTime(0);
					this.BackToMenu();
					break;

				case GAME_WIN:
					paint.setColor(Color.YELLOW);
					canvas.drawText(gamewin, (LoadActivity.ScreenWidth - gamewin.length()*13/2)/2, LoadActivity.ScreenHeight/2, paint);
					canvas.drawText(msg, (LoadActivity.ScreenWidth - msg.length()*13/2)/2, LoadActivity.ScreenHeight - 26, paint);
					break;

			}
			this.sh.unlockCanvasAndPost(canvas);
		}

	}



	public void Logic()
	{
		if(this.gameState != GAME_ING) return;

		mario.ChangeRectSize();

		mario.Logic(this);

		for(int i=0; i<this.nowLevel.getEnemy().size(); i++)
		{
			Enemy e = this.nowLevel.getEnemy().get(i);
			if(e.name.equals("乌龟") && e.state.equals("龟壳") && e.xSpeed >0)
			{
				if(e.SpriteAtScreen(e, LoadActivity.ScreenWidth, LoadActivity.ScreenHeight))
				{
					e.CollisionWithEnemy(this);
				}
			}
		}

		if(this.mario.hp > 0 && this.mario.getNoCheckCoiiisionTime() <= 0 && !this.nowLevel.isWin)
		{
			for(int i=0; i<this.nowLevel.getEnemy().size(); i++)
			{
				Enemy e = this.nowLevel.getEnemy().get(i);

				if(e.SpriteAtScreen(e, LoadActivity.ScreenWidth, LoadActivity.ScreenHeight))
				{
					if(!e.hitbullet_or_tortoise)
					{
						if(e.MoreRectangle_CollisionWithSprite(mario, mario.getRect()))
						{
							if(e.name.equals("三角"))
							{
								if(this.mario.level == 1)
								{
									if(mario.y + mario.getRect().top < e.y && !mario.isOnLand())
									{
										mario.score+=10;
										mario.setJumpTime(mario.getySpeed());
										e.Dead();
										MarioView.PlayMusic(14);
									}
									else
									{
										mario.Dead(this);
									}
								}
								else
								{
									if((mario.getState().indexOf("蹲") != -1? mario.y + mario.getRect().top : mario.y) < e.y && !mario.isOnLand())
									{
										mario.score+=10;
										mario.setJumpTime(mario.getySpeed());
										e.Dead();
										MarioView.PlayMusic(14);
									}
									else
									{
										mario.Dead(this);
									}
								}
							}
							else if(e.name.equals("乌龟"))
							{
								if(e.xSpeed != 0)
								{
									if(mario.y < e.y && !mario.isOnLand())
									{
										mario.setJumpTime(mario.getySpeed());
										e.ChangeState();
									}
									else
									{
										mario.Dead(this);
									}
								}
								else
								{
									e.xSpeed = 8;
									if(mario.x > e.x)
									{
										e.dir = 1;
									}
									else
									{
										e.dir = 2;
									}
								}
							}
							else if(e.name.equals("烟雾怪"))
							{
								if(mario.y < e.y && !mario.isOnLand() && e.state.equals("移动"))
								{
									mario.setJumpTime(mario.getySpeed());
									mario.score+=10;
									e.state = "";
									MarioView.PlayMusic(14);
								}
								else
								{
									mario.Dead(this);
								}
							}
							else
							{
								mario.Dead(this);
							}
						}
					}
				}
			}
		}


		for(int i=0; i<mario.getBullet().size(); i++)
		{
			Bullet b = mario.getBullet().get(i);

			for(int j=0; j<this.nowLevel.getEnemy().size(); j++)
			{
				Enemy e = this.nowLevel.getEnemy().get(j);
				if(!e.hitbullet_or_tortoise && b.Rectangle_CollisionWithSprite(e))
				{
					mario.score+=10;
					b.hp = 0;
					e.hitbullet_or_tortoise = true;
					if(b.xSpeed > 0)
					{
						e.hit_bullet_or_tortois_dir = 2;
					}
					else
					{
						e.hit_bullet_or_tortois_dir = 1;
					}
					MarioView.PlayMusic(2);
				}
			}
		}

		if(this.mario.hp >0 && this.mario.getNoCheckCoiiisionTime() <=0 && !this.nowLevel.isWin)
		{
			for(int i=0; i<Tile.shell.size(); i++)
			{
				Shell s = Tile.shell.get(i);
				if(s.getMovedir() >= 1)
				{
					if(s.MoreRectangle_CollisionWithSprite(mario, mario.getRect()))
					{
						if(this.mario.level == 1)
						{
							if(mario.y + mario.getRect().top < s.y && !mario.isOnLand())
							{
								MarioView.PlayMusic(14);
								mario.setJumpTime(mario.getySpeed());
								mario.score+=10;
								s.setMovedir(0);
							}
							else
							{
								mario.Dead(this);
								s.hp = 0;
							}
						}
						else
						{
							if((mario.getState().indexOf("蹲") != -1? mario.y + mario.getRect().top : mario.y) < s.y && !mario.isOnLand())
							{
								MarioView.PlayMusic(14);
								mario.setJumpTime(mario.getySpeed());
								mario.score+=10;
								s.setMovedir(0);

							}
							else
							{
								mario.Dead(this);
								s.hp = 0;
							}
						}
					}
				}
			}
		}



		if(this.mario.hp > 0)
		{
			for(int i=0; i<Level.food.size(); i++)
			{
				Sprite s = Level.food.get(i);
				if(s instanceof MushRoom)
				{
					MushRoom mr = (MushRoom) s;

					if(mr.getCount() == 18)
					{
						if(mr.MoreRectangle_CollisionWithSprite(mario, mario.getRect()))
						{
							mario.score+=10;
							mr.hp = 0;
							if(this.mario.level < 3)
							{
								this.mario.level+=1;
							}

							MarioView.PlayMusic(5);
						}
					}
				}
				else if(s instanceof Flower)
				{
					Flower f = (Flower) s;
					if(f.getCount() == 16)
					{
						if(f.MoreRectangle_CollisionWithSprite(mario, mario.getRect()))
						{
							mario.score+=10;
							f.hp = 0;
							if(this.mario.level < 3)
							{
								this.mario.level+=1;
							}
							MarioView.PlayMusic(5);
						}
					}
				}
			}
		}


		for(int i=0; i<this.nowLevel.getCoin().size(); i++)
		{
			Coin coin = this.nowLevel.getCoin().get(i);
			if(coin.getType() == 2)
			{
				if(coin.MoreRectangle_CollisionWithSprite(mario, mario.getRect()) && coin.hp > 0)
				{
					mario.score+=10;
					coin.hp = 0;
					MarioView.PlayMusic(3);
					mario.coin_value++;
				}
			}
		}

		for(int i=0; i<this.nowLevel.ladder.size(); i++)
		{
			Ladder l = this.nowLevel.ladder.get(i);
			if(mario.hp > 0)
			{
				if(l.MoreRectangle_CollisionWithSprite(mario, mario.rect3))
				{
					this.mario.y = l.y - this.mario.image.getHeight();
					this.mario.onLand = true;
					this.mario.jumping = "";
					this.mario.ySpeed = 0;
				}
			}
		}


		for(int i=0; i<this.mario.getBullet().size(); i++)
		{
			Bullet b = this.mario.getBullet().get(i);
			for(int j=0; j<this.nowLevel.ladder.size(); j++)
			{
				Ladder l = this.nowLevel.ladder.get(j);
				if(b.Rectangle_CollisionWithSprite(l))
				{
					if(b.y < l.y)
					{
						b.jumpTime=6;
						b.ySpeed=6;
						b.state="跳";
					}
				}
			}
		}

	}


	public static void PlayMusic(int id)
	{
		LoadResource.gs.Play(LoadResource.musicID[id]);
	}


	public void RemoveStaticSprite()
	{
		Level.food.removeAll(Level.food);
		Level.blast.removeAll(Level.blast);
		Tile.shell.removeAll(Tile.shell);
	}

	public void RemoveAllSprite()
	{
		Level.food.removeAll(Level.food);
		Level.blast.removeAll(Level.blast);
		Tile.shell.removeAll(Tile.shell);
		this.mario.getBullet().removeAll(mario.getBullet());
		this.nowLevel.getEnemy().removeAll(this.nowLevel.getEnemy());
		this.nowLevel.getCoin().removeAll(this.nowLevel.getCoin());
	}


	public void SaveScore()
	{
		try
		{
			FileOutputStream fos = this.getContext().openFileOutput("score.txt", Context.MODE_APPEND);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			String score = Integer.toString(mario.score)+":";
			bos.write(score.getBytes());
			bos.flush();
			bos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public void SaveName()
	{
		try
		{
			FileOutputStream fos = this.getContext().openFileOutput("save.txt", Context.MODE_APPEND);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(mario.name.getBytes());
			bos.flush();
			bos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	public void BackToMenu()
	{
		this.gameoverTime --;
		if(this.gameoverTime == 0)
		{
			BackToMenuReady();
		}
	}


	public void BackToMenuReady()
	{
		Tile.count = 0;
		SaveName();
		SaveScore();
		RemoveStaticSprite();
		Intent i = new Intent(this.getContext(),MenuActivity.class);
		this.getContext().startActivity(i);
		Activity a = (Activity) this.getContext();
		a.finish();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch(gameState)
		{
			case GAME_PANNEL:
				if(!this.isallreadyingame) this.gameState = GAME_ING;
				break;

			case GAME_PAUSE:
				if(this.backToGame.OnTouch(event.getX(), event.getY()))
				{
					this.gameState = GAME_ING;
				}
				else if(this.options.OnTouch(event.getX(), event.getY()))
				{
					Intent i = new Intent(this.getContext(),OptionsActivity.class);
					this.getContext().startActivity(i);
				}
				else if(this.backToMenu.OnTouch(event.getX(), event.getY()))
				{
					BackToMenuReady();
				}
				break;

			case GAME_ING:
				switch (event.getAction() & MotionEvent.ACTION_MASK)
				{
					case MotionEvent.ACTION_DOWN:
						this.JudgmentButtonState(event.getX(),event.getY(),"down");
						break;

					case MotionEvent.ACTION_UP:
						this.JudgmentButtonState(event.getX(),event.getY(),"up");
						break;

					case MotionEvent.ACTION_POINTER_DOWN:
						this.JudgmentButtonState(event.getX(1),event.getY(1),"down");
						break;
				}
				break;

			case GAME_WIN:
				BackToMenuReady();
				break;

		}
		if(this.gameState == GAME_ING)
		{
			return true;
		}
		else
		{
			return super.onTouchEvent(event);
		}
	}


	public void JudgmentButtonState(float x, float y, String dir)
	{
		if(left.OnTouch(x, y))
		{
			if(dir.equals("down"))
			{
				this.mario.state = "左跑";
			}
			else
			{
				this.mario.state = "左停";
			}
		}
		else if(right.OnTouch(x, y))
		{
			if(dir.equals("down"))
			{
				this.mario.state = "右跑";
			}
			else
			{
				this.mario.state = "右停";
			}
		}
		else if(down.OnTouch(x, y))
		{
			if(this.mario.state.indexOf("左")!= -1)
			{
				this.mario.state = "左蹲";
			}
			else
			{
				this.mario.state = "右蹲";
			}
		}
		else if(A.OnTouch(x, y))
		{
			if(!this.nowLevel.isWin) this.mario.Fril();
		}
		else if(B.OnTouch(x, y))
		{
			if(!this.nowLevel.isWin) this.mario.Jump();
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch(gameState)
		{
			case GAME_ING:
				if(this.mario.hp > 0) this.mario.onKeyDown(keyCode, event,this);
				if(keyCode == KeyEvent.KEYCODE_BACK)
				{
					this.nowLevel.getGm().PuaseMusic(false);
					this.gameState = GAME_PAUSE;
				}
				break;
		}
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch(gameState)
		{
			case GAME_ING:
				this.mario.onKeyUp(keyCode, event);
				break;
		}
		return true;
	}




	@Override
	public void run()
	{
		while(flag)
		{
			Long start = System.currentTimeMillis();
			this.Logic();
			this.Draw();
			Long end   = System.currentTimeMillis();
			try
			{
				if(end - start < 50)
				{
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


}
