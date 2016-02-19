package com.mario.playermsg;


public class Player implements Comparable<Player> 
{
	int score;
	String name;
	
	public Player(String name,int score)
	{
		this.name = name;
		this.score = score;
	}

	@Override
	public int compareTo(Player p) 
	{
		return p.score - this.score;
	}
	
}
	
	