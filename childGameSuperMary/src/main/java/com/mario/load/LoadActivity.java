package com.mario.load;

import com.mario.menu.MenuActivity;

import android.content.Intent;
import android.os.Bundle;
import game.activity.GameActivity;

public class LoadActivity extends GameActivity  implements Runnable
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		new Thread(this).start();
		
		this.SetScreenToFull();
		
		this.GetScreenSize();
		
		super.onCreate(savedInstanceState);
		super.setContentView(new LoadView(this));
	}

	@Override
	public void run() 
	{
		LoadResource.LoadImage(this);
		
		LoadResource.LoadMusic(this);
		
		Intent i = new Intent(this,MenuActivity.class);
		this.startActivity(i);
		this.finish();
	}
	
}
