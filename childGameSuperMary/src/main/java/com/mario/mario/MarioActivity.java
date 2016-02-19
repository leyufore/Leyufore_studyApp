package com.mario.mario;

import android.os.Bundle;
import game.activity.GameActivity;

public class MarioActivity extends GameActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		this.SetScreenToFull();
		super.onCreate(savedInstanceState);
		super.setContentView(new MarioView(this));
	}

}
