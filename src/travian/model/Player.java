package travian.model;

import java.util.ArrayList;
import java.util.List;

public class Player
{
	private String name;
	private int id;
	
	private List<Village> villages;
	
	private Ally ally;
	private Nation nation;
	
	private String group;

	public Player(String name, int id, Ally ally, Nation nation)
	{
		super();
		this.name = name;
		this.id = id;
		this.villages = new ArrayList<Village>();
		this.ally = ally;
		this.ally.addPlayer(this);
		this.nation = nation;
	}
	
	public Player(String name, int id, Ally ally, int nation)
	{
		super();
		this.name = name;
		this.id = id;
		this.villages = new ArrayList<Village>();
		this.ally = ally;
		if(ally != null)
			this.ally.addPlayer(this);
		switch (nation)
		{
			case 1: this.nation = Nation.Roemer;
				break;
			case 2: this.nation = Nation.Germanen;
				break;
			case 3: this.nation = Nation.Gallier;
				break;
			case 4: this.nation = Nation.Natur;
				break;
			case 5: this.nation = Nation.Nataren;
				break;
			default:
				throw new IllegalArgumentException("Illegal Nation");
		}
	}

	public String getName()
	{
		return name;
	}

	public int getId()
	{
		return id;
	}

	public List<Village> getVillages()
	{
		return villages;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public void addVillage(Village village)
	{
		this.villages.add(village);
	}

	public Ally getAlly()
	{
		return ally;
	}

	public void setAlly(Ally ally)
	{
		this.ally = ally;
		this.ally.addPlayer(this);
	}

	public Nation getNation()
	{
		return nation;
	}

	public void setNation(Nation nation)
	{
		this.nation = nation;
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group = group;
	}
	
	public int getTotalPopulation()
	{
		int pop = 0;
		for(Village v: villages)
		{
			pop += v.getPopulation();
		}
		return pop;
	}
	
	
}
