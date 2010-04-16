package travian.model;

import java.util.TreeMap;

public class World
{
	private TreeMap<Integer, Ally> allys;
	private TreeMap<Integer, Player> players;
	private TreeMap<Integer, Village> villages;
	
	public TreeMap<Integer, Ally> getAllys()
	{
		return allys;
	}

	public TreeMap<Integer, Player> getPlayers()
	{
		return players;
	}

	public TreeMap<Integer, Village> getVillages()
	{
		return villages;
	}

	public World(TreeMap<Integer, Ally> allys, TreeMap<Integer, Player> players, TreeMap<Integer, Village> villages)
	{
		super();
		this.allys = allys;
		this.players = players;
		this.villages = villages;
	}

	public World()
	{
		super();
		this.allys = new TreeMap<Integer, Ally>();
		this.players = new TreeMap<Integer, Player>();
		this.villages = new TreeMap<Integer, Village>();
	}
	
	
}
