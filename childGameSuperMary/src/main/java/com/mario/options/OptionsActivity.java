package com.mario.options;

import game.activity.GameActivity;
import game.music.GameMediaPlayers;
import game.music.GameSoundPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.game.mario.R;

public class OptionsActivity extends GameActivity implements OnSeekBarChangeListener
{
	
	private SeekBar musicSeekBar,soundSeekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{  
		this.SetScreenToFull();
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.optionsmain);  
		
	
		this.musicSeekBar = (SeekBar) super.findViewById(R.id.mySeekBar1);
		this.soundSeekBar = (SeekBar) super.findViewById(R.id.mySeekBar2);  
		
		
		this.musicSeekBar.setOnSeekBarChangeListener(this);
		this.soundSeekBar.setOnSeekBarChangeListener(this);
		
		
		try 
		{
			FileInputStream fis = this.openFileInput("musicSet.txt");
			DataInputStream dis = new DataInputStream(fis);
			this.musicSeekBar.setProgress(dis.readInt());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try 
		{
			FileInputStream fis = this.openFileInput("soundSet.txt");
			DataInputStream dis = new DataInputStream(fis);
			this.soundSeekBar.setProgress(dis.readInt());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean arg2) 
	{
		if(seekBar == musicSeekBar)
		{
			
			GameMediaPlayers.SetVolumeSize(this,progress/7);
			
			
			try {
				FileOutputStream fos = this.openFileOutput("musicSet.txt", Context.MODE_PRIVATE);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeInt(progress);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(seekBar == soundSeekBar)
		{
		
			new GameSoundPool().setVolumes((float)(progress/10*0.1));
			
		
			try {
				FileOutputStream fos = this.openFileOutput("soundSet.txt", Context.MODE_PRIVATE);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeInt(progress);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0){}
	@Override
	public void onStopTrackingTouch(SeekBar arg0) {}
	
	
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
