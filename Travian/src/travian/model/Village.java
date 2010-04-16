package travian.model;

import java.awt.Color;
import java.awt.Paint;

public class Village implements Comparable<Village>
{
	private int fieldId;
	private int x;
	private int y;
	private String name;
	private Player player;
	private int population;
	private Paint color = null;
	private int vid;
	
	public Village(int fieldId, int x, int y, int vid, String name, Player player, int population)
	{
		super();
		this.fieldId = fieldId;
		this.x = x;
		this.y = y;
		this.name = name;
		this.player = player;
		this.player.addVillage(this);
		this.population = population;
		this.vid = vid;
	}

	public Village()
	{
		super();
	}

	public int getFieldId()
	{
		return fieldId;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public String getName()
	{
		return name;
	}

	public Player getPlayer()
	{
		return player;
	}

	public void setFieldId(int fieldId)
	{
		this.fieldId = fieldId;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
		this.player.addVillage(this);
	}

	public int getPopulation()
	{
		return population;
	}

	public void setPopulation(int population)
	{
		this.population = population;
	}

	public Paint getColor()
	{
		return color;
	}
	
	public Paint getColorByPopulation()
	{
		if(color instanceof Color)
		{
			Color tmp = ((Color)color);
			if(player.getTotalPopulation() < 250)
				return tmp.brighter();
			if(player.getTotalPopulation() < 500)
				return tmp;
			if(player.getTotalPopulation() < 1000)
				return tmp.darker();
			if(player.getTotalPopulation() < 2500)
				return tmp.darker().darker();
			if(player.getTotalPopulation() < 5000)
				return tmp.darker().darker().darker();
			if(player.getTotalPopulation() < 10000)
				return tmp.darker().darker().darker().darker();
			return tmp.darker().darker().darker().darker().darker();
		}
		else 
			return color;
	}

	public void setColor(Paint color)
	{
		this.color = color;
	}

	public int getVid()
	{
		return vid;
	}

	public void setVid(int vid)
	{
		this.vid = vid;
	}

	@Override
	public int compareTo(Village o)
	{
		return o.vid - this.vid;
	}	
	
	@Override
	public String toString()
	{
		return this.name + " - " +  this.getPlayer().getName() + " (" + this.getX() + "|" + this.getY() + ")";
	}
}
