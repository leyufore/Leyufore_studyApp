package com.mario.menu;

import android.os.Bundle;
import game.activity.GameActivity;

public class MenuActivity extends GameActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.SetScreenToFull();
		super.setContentView(new MenuView(this));
	}
	
}
