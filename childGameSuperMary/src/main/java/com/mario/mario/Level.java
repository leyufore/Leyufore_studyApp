package com.mario.mario;

import game.music.GameMediaPlayers;
import game.sprite.Sprite;
import game.tool.Tool;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import com.game.mario.R;
import com.mario.load.LoadActivity;
import com.mario.load.LoadResource;

public class Level
{
	int Level;

	private String level_name;

	int time;

	private GameMediaPlayers gm;


	private ArrayList <Tile>b_tile = new ArrayList<Tile>();


	private ArrayList <Tile>q_tile = new ArrayList<Tile>();

	private ArrayList <Map>map = new ArrayList<Map>();

	private ArrayList <Enemy>enemy = new ArrayList<Enemy>();

	private ArrayList <Enemy>saveenemy = new ArrayList<Enemy>();

	private ArrayList <Coin>coin = new ArrayList <Coin>();

	private ArrayList <Coin>savecoin = new ArrayList <Coin>();

	ArrayList <Ladder>ladder = new ArrayList <Ladder>();

	static ArrayList <Sprite>food = new ArrayList<Sprite>();

	static ArrayList <Blast>blast = new ArrayList<Blast>();

	boolean isWin;

	private int goToNextLevelTime = 120;






	public ArrayList<Coin> getCoin()
	{
		return coin;
	}
	public ArrayList<Coin> getSavecoin()
	{
		return savecoin;
	}
	public void setTime(int time)
	{
		this.time = time;
	}
	public GameMediaPlayers getGm()
	{
		return gm;
	}
	public ArrayList<Enemy> getSaveenemy()
	{
		return saveenemy;
	}
	public ArrayList<Tile> getB_tile()
	{
		return b_tile;
	}
	public ArrayList<Tile> getQ_tile()
	{
		return q_tile;
	}
	public String getLevel_name()
	{
		return level_name;
	}
	public int getTime()
	{
		return time;
	}
	public ArrayList<Map> getMap()
	{
		return map;
	}
	public ArrayList<Enemy> getEnemy()
	{
		return enemy;
	}





	public Level(int level,Context context)
	{
		Level = level;

		switch(level)
		{
			case 1:

				this.CreatTile(Tool.ReadMap(context, "mapdat/map_b_1.dat"),0);
				this.CreatTile(Tool.ReadMap(context, "mapdat/map_q_1.dat"),1);


				this.map.add(new Map(0,0,LoadResource.map.get(1),1));
				this.map.add(new Map(LoadActivity.ScreenWidth,0,LoadResource.map.get(1),2));

				this.coin.add(new Coin(29*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(30*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(31*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(32*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(62*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(63*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(64*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(65*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(101*16, 3*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(113*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(114*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(115*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(116*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(158*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(159*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(160*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(249*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(250*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(251*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(252*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(253*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(261*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(262*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(263*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(264*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(265*16, 9*16, LoadResource.coin.get(0),2));


				savecoin.addAll(coin);

				this.enemy.add(new Triangle(29*16 ,  13*16, LoadResource.enemy.get(0)));
				this.enemy.add(new Tortoise(46*16 ,  13*16, LoadResource.enemy.get(7)));
				this.enemy.add(new Piranha (59*16+8, 13*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (104*16+8,11*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Thorn   (96*16 ,  8*16,  LoadResource.enemy.get(3)));
				this.enemy.add(new Triangle(115*16 , 11*16, LoadResource.enemy.get(0)));
				this.enemy.add(new Triangle(147*16 , 13*16, LoadResource.enemy.get(0)));
				this.enemy.add(new Tortoise(158*16 , 13*16, LoadResource.enemy.get(7)));
				this.enemy.add(new Thorn   (167*16 ,  8*16, LoadResource.enemy.get(3)));
				this.enemy.add(new Tortoise(221*16 , 13*16, LoadResource.enemy.get(7)));
				this.enemy.add(new Piranha (240*16+8,12*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (272*16+8,13*16, LoadResource.enemy.get(10)));


				this.saveenemy.addAll(enemy);

				this.level_name = "1-1";
				this.time = 160;

				this.gm = new GameMediaPlayers();
				this.gm.LoadMusic(context, R.raw.level_1);


				this.SetTileY();

				break;

			case 2:

				this.CreatTile(Tool.ReadMap(context, "mapdat/map_b_2.dat"),0);
				this.CreatTile(Tool.ReadMap(context, "mapdat/map_q_2.dat"),1);




				this.map.add(new Map(0,0,LoadResource.map.get(2),1));
				this.map.add(new Map(LoadActivity.ScreenWidth,0,LoadResource.map.get(2),2));


				this.coin.add(new Coin(52*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(53*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(54*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(55*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(56*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(57*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(58*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(59*16, 9*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(60*16, 9*16, LoadResource.coin.get(0),2));

				this.coin.add(new Coin(102*16, 2*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(103*16, 2*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(104*16, 2*16, LoadResource.coin.get(0),2));

				this.coin.add(new Coin(114*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(115*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(116*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(117*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(118*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(119*16, 7*16, LoadResource.coin.get(0),2));

				this.coin.add(new Coin(123*16, 2*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(124*16, 2*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(125*16, 2*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(126*16, 2*16, LoadResource.coin.get(0),2));

				this.coin.add(new Coin(214*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(215*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(216*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(217*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(218*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(219*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(220*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(221*16, 10*16, LoadResource.coin.get(0),2));

				this.coin.add(new Coin(257*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(258*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(259*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(260*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(261*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(262*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(263*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(264*16, 10*16, LoadResource.coin.get(0),2));


				savecoin.addAll(coin);


				this.enemy.add(new Triangle(25*16 ,  13*16, LoadResource.enemy.get(0)));
				this.enemy.add(new Tortoise(60*16 ,  13*16, LoadResource.enemy.get(7)));
				this.enemy.add(new Piranha (70*16+8, 13*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (116*16+8,12*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Thorn   (130*16 , 13*16,  LoadResource.enemy.get(3)));
				this.enemy.add(new Triangle(146*16 , 13*16, LoadResource.enemy.get(0)));
				this.enemy.add(new Triangle(146*16 , 10*16, LoadResource.enemy.get(7)));
				this.enemy.add(new Thorn   (160*16 ,  13*16, LoadResource.enemy.get(3)));
				this.enemy.add(new Tortoise(170*16 , 13*16, LoadResource.enemy.get(7)));
				this.enemy.add(new Piranha (175*16+8, 12*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (185*16+8, 12*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (195*16+8, 12*16, LoadResource.enemy.get(10)));



				this.saveenemy.addAll(enemy);


				this.level_name = "1-2";
				this.time = 160;

				this.gm = new GameMediaPlayers();
				this.gm.LoadMusic(context, R.raw.level_2);

				this.ladder.add(new Ladder(84*16,0, LoadResource.ladder,2));
				this.ladder.add(new Ladder(84*16,LoadActivity.ScreenHeight/2 + 5, LoadResource.ladder,2));
				this.ladder.add(new Ladder(84*16,LoadActivity.ScreenHeight-10,  LoadResource.ladder,2));

				this.ladder.add(new Ladder(93*16,0,  LoadResource.ladder,1));
				this.ladder.add(new Ladder(93*16,LoadActivity.ScreenHeight/2 + 5,  LoadResource.ladder,1));
				this.ladder.add(new Ladder(93*16,LoadActivity.ScreenHeight-10, LoadResource.ladder,1));

				this.ladder.add(new Ladder(242*16,0,  LoadResource.ladder,2));
				this.ladder.add(new Ladder(242*16,LoadActivity.ScreenHeight/2 + 5,  LoadResource.ladder,2));
				this.ladder.add(new Ladder(242*16,LoadActivity.ScreenHeight-10, LoadResource.ladder,2));


				this.SetTileY();
				break;

			case 3:

				this.CreatTile(Tool.ReadMap(context, "mapdat/map_b_3.dat"),0);
				this.CreatTile(Tool.ReadMap(context, "mapdat/map_q_3.dat"),1);

				this.level_name = "1-3";
				this.time = 160;

				this.gm = new GameMediaPlayers();
				this.gm.LoadMusic(context, R.raw.level_4);

				this.coin.add(new Coin(26*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(27*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(28*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(88*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(89*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(90*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(139*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(140*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(141*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(142*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(143*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(144*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(145*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(146*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(147*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(163*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(164*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(165*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(166*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(167*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(168*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(169*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(170*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(171*16, 10*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(206*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(207*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(208*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(209*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(210*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(211*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(212*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(213*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(235*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(236*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(237*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(238*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(239*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(240*16, 8*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(286*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(288*16, 7*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(292*16, 5*16, LoadResource.coin.get(0),2));
				this.coin.add(new Coin(293*16, 5*16, LoadResource.coin.get(0),2));


				savecoin.addAll(coin);

				this.enemy.add(new SmokeMonster(LoadActivity.ScreenWidth ,  4*16, LoadResource.enemy.get(13)));
				this.enemy.add(new Piranha (85*16+8, 13*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (174*16+8,12*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (203*16+8,13*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (286*16+8,12*16, LoadResource.enemy.get(10)));
				this.enemy.add(new Piranha (292*16+8, 10*16,LoadResource.enemy.get(10)));

				this.saveenemy.addAll(enemy);


				this.SetTileY();

				break;
		}
	}












	public void GotoNextLevel(MarioView mv)
	{
		if(!this.isWin) return;

		mv.getMario().xSpeed  = 0 ;

		if(this.goToNextLevelTime == 120)
		{
			MarioView.PlayMusic(11);
		}
		this.goToNextLevelTime --;

		if(this.goToNextLevelTime <= 0)
		{

			if(mv.getNowLevel().Level != 3)
			{
				Tile.count = 0;
				mv.nowLevel = mv.level.get(mv.nowLevel.Level);
				mv.isallreadyingame = false;
				mv.gameState = MarioView.GAME_PANNEL;
				mv.getMario().x = mv.getMario().startX;
				mv.getMario().y = mv.getMario().startY;
				mv.getMario().xSpeed  = 4 ;
				mv.getMario().state = "右停";
			}
			else
			{
				mv.gameState = MarioView.GAME_WIN;
			}
		}

	}



























































	public void SetTileY()
	{
		if(LoadActivity.ScreenHeight == 240) return;

		int k = (LoadActivity.ScreenHeight - 240)/16*16;
		for(int i=0; i<this.q_tile.size(); i++)
		{
			Tile t = this.q_tile.get(i);
			t.y +=k;
		}
		for(int i=0; i<this.b_tile.size(); i++)
		{
			Tile t = this.b_tile.get(i);
			t.y +=k;
		}
		for(int i=0; i<this.coin.size(); i++)
		{
			Coin c = this.coin.get(i);
			c.y +=k;
		}
		for(int i=0; i<this.enemy.size(); i++)
		{
			Enemy e = this.enemy.get(i);
			e.y +=k;
		}
		for(int i=0; i<this.ladder.size(); i++)
		{
			Ladder l = this.ladder.get(i);
			l.y +=k;
		}

	}



	public Bitmap SelectImage(int index)
	{
		if(index == 130)  return LoadResource.tile.get(0);
		if(index == 146)  return LoadResource.tile.get(1);
		if(index == 11 )  return LoadResource.tile.get(2);
		if(index == 12 )  return LoadResource.tile.get(3);
		if(index == 27 )  return LoadResource.tile.get(4);
		if(index == 28 )  return LoadResource.tile.get(5);
		if(index == 37 )  return LoadResource.tile.get(6);
		if(index == 21 )  return LoadResource.tile.get(8);
		if(index == 15 )  return LoadResource.tile.get(12);
		if(index == 31 )  return LoadResource.tile.get(13);
		if(index == 47 )  return LoadResource.tile.get(14);
		if(index == 77 )  return LoadResource.tile.get(15);
		if(index == 93 )  return LoadResource.tile.get(17);
		if(index == 10 )  return LoadResource.tile.get(19);
		if(index == 131 ) return LoadResource.tile.get(20);
		if(index == 145 ) return LoadResource.tile.get(21);
		if(index == 129 ) return LoadResource.tile.get(22);
		if(index == 133 ) return LoadResource.tile.get(23);
		if(index == 134 ) return LoadResource.tile.get(24);
		if(index == 135 ) return LoadResource.tile.get(25);
		if(index == 149 ) return LoadResource.tile.get(26);
		if(index == 150 ) return LoadResource.tile.get(27);
		if(index == 151 ) return LoadResource.tile.get(28);
		if(index == 147 ) return LoadResource.tile.get(29);
		if(index == 152 ) return LoadResource.tile.get(30);
		if(index == 17 )  return LoadResource.tile.get(31);
		if(index == 18 )  return LoadResource.tile.get(32);
		if(index == 19 )  return LoadResource.tile.get(33);
		if(index == 20 )  return LoadResource.tile.get(34);
		return null;
	}


	public void CreatTile(int map[][], int k)
	{
		for(int i=0; i<map.length; i++)
		{
			for(int j=0; j<map[i].length; j++)
			{
				if(map[i][j] > 0)
				{
					Tile t = new Tile(j*16, i*16,SelectImage(map[i][j]),map[i][j]);
					if(k > 0)
					{
						this.q_tile.add(t);
					}
					else
					{
						this.b_tile.add(t);
					}
				}
			}
		}
	}


}
