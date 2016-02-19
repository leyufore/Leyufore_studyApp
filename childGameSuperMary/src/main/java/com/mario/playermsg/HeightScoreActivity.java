package com.mario.playermsg;

import android.os.Bundle;
import android.view.KeyEvent;
import game.activity.GameActivity;

public class HeightScoreActivity extends GameActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.SetScreenToFull();
		super.setContentView(new HeightScoreView(this));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			this.finish();
		}
		return true;
	}
	
	
	
	
	
}
