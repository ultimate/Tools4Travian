package travian.model;

import java.awt.Color;
import java.awt.Paint;


public class PlayerHighlight
{
	private int xMin;
	private int xMax;
	private int yMin;
	private int yMax;
	private int pMin;
	private int pMax;
	
	private Paint color;
	
	public PlayerHighlight(int xMin, int xMax, int yMin, int yMax, int pMin, int pMax)
	{
		super();
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.pMin = pMin;
		this.pMax = pMax;
		color = Color.black;
		
		int tmp;
		if(xMin > xMax)
		{
			tmp = xMax;
			xMax = xMin;
			xMin = tmp;
		}
		if(yMin > yMax)
		{
			tmp = yMax;
			yMax = yMin;
			yMin = tmp;
		}
		if(pMin > pMax)
		{
			tmp = pMax;
			pMax = pMin;
			pMin = tmp;
		}
	}
	
	public int getXMin()
	{
		return xMin;
	}
	public int getXMax()
	{
		return xMax;
	}
	public int getYMin()
	{
		return yMin;
	}
	public int getYMax()
	{
		return yMax;
	}
	public int getPMin()
	{
		return pMin;
	}
	public int getPMax()
	{
		return pMax;
	}

	public Paint getColor()
	{
		return color;
	}

	public void setColor(Paint color)
	{
		this.color = color;
	}
	
	public boolean matches(Player p)
	{
		int pop = 0;
		boolean vInArea = false;
		boolean noColor = true;;
		for(Village v: p.getVillages())
		{
			pop += v.getPopulation();
			if(v.getX() >= xMin && v.getX() <= xMax 
					&& v.getY() >= yMin && v.getY() <= yMax)
			{
				vInArea = true;
			}
			noColor = noColor && v.getColor() == null;
		}
		return (pop >= pMin && pop <= pMax && vInArea && noColor);
	}
	
	public void apply(Player p)
	{
		if(matches(p))
		{
			p.setGroup("Highlight");
			for(Village v: p.getVillages())
				v.setColor(color);
		}
	}
}
