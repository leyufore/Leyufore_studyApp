package com.mario.load;

import game.image.Image;
import game.music.GameSoundPool;
import java.util.ArrayList;
import com.game.mario.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class  LoadResource 
{
	
	public static ArrayList <Bitmap>mario = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>enemy = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>coin = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>blast = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>food = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>map = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>tile = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>weapon = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>ui = new ArrayList<Bitmap>();
	
	public static ArrayList <Bitmap>button = new ArrayList<Bitmap>();
	
	public  static Bitmap ladder ;
	
	public static int temp;
	
	public static GameSoundPool gs = new GameSoundPool(16);

	public static int musicID[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	
	
	
	
	public static void LoadImage(Context context)
	{
		try
		{
			for(int i=1; i<=14; i++)
			{
				mario.add(BitmapFactory.decodeStream(context.getAssets().open("mario/mario" + i + ".png")));
				temp++;
			}
			
			for(int i=1; i<=14; i++)
			{
				enemy.add(BitmapFactory.decodeStream(context.getAssets().open("enemy/enemy" + i + ".png")));
				temp++;
			}
			
			for(int i=1; i<=4; i++)
			{
				 coin.add(BitmapFactory.decodeStream(context.getAssets().open("coin/coin" + i + ".png")));
				 temp++;
			}
			
			for(int i=1; i<=3; i++)
			{
				 blast.add(BitmapFactory.decodeStream(context.getAssets().open("blast/blast" + i + ".png")));
				 temp++;
			}
			
			for(int i=1; i<=3; i++)
			{
				 food.add(BitmapFactory.decodeStream(context.getAssets().open("food/food" + i + ".png")));
				 temp++;
			}
			
			for(int i=1; i<=4; i++)
			{
				Bitmap m = BitmapFactory.decodeStream(context.getAssets().open("map/map" + i +".jpg"));
				m = Image.FitTheScreenSizeImage(m, LoadActivity.ScreenWidth, LoadActivity.ScreenHeight);
				map.add(m);
				temp++;
			}
			
			for(int i=1; i<=35; i++)
			{
				tile.add(BitmapFactory.decodeStream(context.getAssets().open("tile/tile" + i + ".png")));
				temp++;
			}
			
			for(int i=1; i<=2; i++)
			{
				weapon.add(BitmapFactory.decodeStream(context.getAssets().open("weapon/weapon" + i + ".png")));
				temp++;
			}
			

			for(int i=1; i<=2; i++)
			{
				 ui.add(BitmapFactory.decodeStream(context.getAssets().open("ui/ui" + i + ".png")));
				 temp++;
			}
			
			for(int i=1; i<=5; i++)
			{
				 Bitmap image = BitmapFactory.decodeStream(context.getAssets().open("button/button" + i + ".png"));
				 float rate = SetButtonSize(LoadActivity.ScreenWidth, LoadActivity.ScreenHeight);
				 image = Image.FitTheImage(image, rate, rate);
				 button.add(image);
				 temp++;
			}
			
			ladder = BitmapFactory.decodeStream(context.getAssets().open("tool/tool.jpg"));
			temp++;
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static float SetButtonSize(int w, int h)
	{
		if     (w==320 && h==240 || w==400 && h==240 || w==432 && h==240) return 0.3f;
		else if(w==480 && h==320) return 0.35f;
		else if(w==800 && h==480 || w==854 && h==480) return 0.5f;
		else if(w==960 && h==540 || w==960 && h==640) return 0.6f;
		else if(w>1000 && h >=600 ||w>=600 && h>1000) return 0.8f;
		return 0.4f;
	}
	
	
	
	
	public static void LoadMusic(Context context)
	{
		musicID[0] = gs.LoadGameMusic(context, musicID[0], R.raw.bullet_2);
		
		musicID[1] = gs.LoadGameMusic(context, musicID[1], R.raw.bullet_1);
		
		musicID[2] = gs.LoadGameMusic(context, musicID[2], R.raw.bullet_or_tortoise_hit);
		
		musicID[3] = gs.LoadGameMusic(context, musicID[3], R.raw.coin);
		
		musicID[4] = gs.LoadGameMusic(context, musicID[4], R.raw.dead);
		
		musicID[5] = gs.LoadGameMusic(context, musicID[5], R.raw.eatfood);
		
		musicID[6] = gs.LoadGameMusic(context, musicID[6], R.raw.gameover);
		
		musicID[7] = gs.LoadGameMusic(context, musicID[7], R.raw.hit);
		
		musicID[8] = gs.LoadGameMusic(context, musicID[8], R.raw.jump);
		
		musicID[9] = gs.LoadGameMusic(context, musicID[9], R.raw.level_1);
		
		musicID[10] = gs.LoadGameMusic(context, musicID[10], R.raw.level_2);
		
		musicID[11] = gs.LoadGameMusic(context, musicID[11], R.raw.level_win);
		
		musicID[12] = gs.LoadGameMusic(context, musicID[12], R.raw.no_time);
		
		musicID[13] = gs.LoadGameMusic(context, musicID[13], R.raw.pluslife);
		
		musicID[14] = gs.LoadGameMusic(context, musicID[14], R.raw.step_on_enemy);
		
		musicID[15] = gs.LoadGameMusic(context, musicID[14], R.raw.top_of_food);
		
		temp+=16;
	}
	
	
	
	
	
	
		
	
}
