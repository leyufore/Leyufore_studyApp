package com.mario.menu;

import game.button.GameButton;
import game.music.GameMediaPlayers;
import game.view.GameView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.EditText;
import com.game.mario.R;
import com.mario.load.LoadActivity;
import com.mario.load.LoadResource;
import com.mario.load.LoadView;
import com.mario.mario.MarioActivity;
import com.mario.options.OptionsActivity;
import com.mario.playermsg.HeightScoreActivity;

public class MenuView extends GameView implements Runnable
{
	
	private int x1,x2 = LoadActivity.ScreenWidth;
	
	
	private GameButton StartGame,Options,Score,Exit;
	
	private int textSize = 20;
	
	public static final int UI = 1;
	public static final int BUTTON = 2;
	private int menuState = UI;
	
	

	private int move[] = {-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,7,6,5,4,3,2,1,0,-1,-2,-3,-4,-5,-6,-7};
	
	private int index;
	

	private GameMediaPlayers gm;
	
	public  String name ;
	
	
	public MenuView(Context context)
	{
		super(context);
		this.setFocusableInTouchMode(true);
		this.setKeepScreenOn(true);
	
		StartGame = new GameButton((LoadActivity.ScreenWidth - 100)/2 , LoadActivity.ScreenHeight/2 - 40, textSize,"start_game", context, R.raw.button);
		Options   = new GameButton(StartGame.x, StartGame.y  + 40, textSize, "options",  context, R.raw.button);
		Score     = new GameButton(StartGame.x, Options.y  + 40, textSize, "height_score",  context, R.raw.button);
		Exit      = new GameButton(StartGame.x, Score.y   + 40, textSize, "exit",     context, R.raw.button);
		
		
		  
        paint.setTypeface(LoadView.mFace);  
      
  		gm = new GameMediaPlayers();
  		gm.LoadMusic(context, R.raw.menu);

        
	}
	
	
	
	

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		
		gm.StartMusic(true);
		
		this.flag = true;
		this.t = new Thread(this);
		this.t.start();
	}




	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		
		gm.PuaseMusic(false);
		
		this.flag = false;
	}
	
	
	public void Draw()
	{
		this.canvas = sh.lockCanvas();
		
		if(canvas != null)
		{
			paint.setColor(Color.YELLOW);
		
			canvas.drawBitmap(LoadResource.map.get(0), x1-=2, 0, null);
			canvas.drawBitmap(LoadResource.map.get(0), x2-=2 ,0, null);
			if(x1 <= -LoadActivity.ScreenWidth)
			{
				x1 = LoadActivity.ScreenWidth;
			}
			if(x2 <= -LoadActivity.ScreenWidth)
			{
				x2 = LoadActivity.ScreenWidth;
			}
			
			switch(menuState)
			{
			case UI:
				
				canvas.drawBitmap(LoadResource.ui.get(1),
								 (LoadActivity.ScreenWidth - LoadResource.ui.get(1).getWidth())/2, 
								  LoadActivity.ScreenHeight/7 - move[index++], 
								  null);
				
				canvas.drawBitmap(LoadResource.ui.get(0), 
								 (LoadActivity.ScreenWidth - LoadResource.ui.get(0).getWidth())/2,
								  LoadActivity.ScreenHeight/2,
								  null);
				
				if(index > move.length - 1)
				{
					index = 0;
				}

			break;
			
			case BUTTON:
				this.StartGame.Draw(canvas, paint);
				this.Options.Draw(canvas, paint);
				this.Score.Draw(canvas, paint);
				this.Exit.Draw(canvas, paint);
			break;
			}

			this.sh.unlockCanvasAndPost(canvas);
		}
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch(menuState)
		{
		case UI:
			this.menuState = BUTTON;
		break;
		
		case BUTTON:
			if(this.StartGame.OnTouch(event.getX(), event.getY()))
			{
				LayoutInflater l = ((Activity) this.getContext()).getLayoutInflater();
				View v = l.inflate(R.layout.dialog,null);
				
				
				final EditText edit = (EditText) v.findViewById(R.id.edit); 
				
				
				Dialog d = new AlertDialog.Builder(this.getContext())
				.setTitle("input your name ?")		
				.setView(v)
				.setPositiveButton("yes", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						
						MenuView.this.name = edit.getText().toString();
						
					
						Intent intent = new Intent();   
						intent.setClass(MenuView.this.getContext(), MarioActivity.class);    
						Bundle bundle = new Bundle(); 
						bundle.putString("save_name", name);       
						intent.putExtras(bundle);                               
						MenuView.this.getContext().startActivity(intent);  
						Activity a = (Activity)MenuView.this.getContext();
						a.finish();
					}
				})
				.setNegativeButton("no", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
					
						MenuView.this.name = "null";
						
						Intent intent = new Intent();   
						intent.setClass(MenuView.this.getContext(), MarioActivity.class);    
						Bundle bundle = new Bundle(); 
						bundle.putString("save_name", name);       
						intent.putExtras(bundle);                               
						MenuView.this.getContext().startActivity(intent);  
						Activity a = (Activity)MenuView.this.getContext();
						a.finish();
					}
				})
				.create();
				d.show();
			}
			else if(this.Options.OnTouch(event.getX(), event.getY()))
			{
				Intent i = new Intent(this.getContext(),OptionsActivity.class);
				this.getContext().startActivity(i);
			}
			else if(this.Score.OnTouch(event.getX(), event.getY()))
			{
				Intent i = new Intent(this.getContext(),HeightScoreActivity.class);
				this.getContext().startActivity(i);
			}
			else if(this.Exit.OnTouch(event.getX(), event.getY()))
			{
				Activity a = (Activity)this.getContext();
				a.finish();
				this.flag = false;
				System.exit(0);
			}
		break;
		}
		return super.onTouchEvent(event);
	}
	
	




	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return true;
	}





	@Override
	public void run() 
	{
		while(flag)
		{
			Long start = System.currentTimeMillis();
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
