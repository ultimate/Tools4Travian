package travian.model;

import java.util.ArrayList;
import java.util.List;

public class Ally
{
	private int id;
	private String name;
	
	private List<Player> players;
	
	private int rank;

	public Ally(int id, String name)
	{
		super();
		this.id = id;
		this.name = name;
		this.players = new ArrayList<Player>();
		this.rank = Integer.MAX_VALUE;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public List<Player> getPlayers()
	{
		return players;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	
	public void addPlayer(Player player)
	{
		this.players.add(player);
	}

	public int getTotalPopulation()
	{
		int pop = 0;
		for(Player p: players)
		{
			pop += p.getTotalPopulation();
		}
		return pop;
	}
	
	public float getAveragePopulation()
	{
		return (float)getTotalPopulation()/(float)players.size();
	}

	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}	
}
