package travian.graphics;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

import travian.model.Village;

public class VillageToolTipGenerator implements XYToolTipGenerator
{
	private int tooltipLevel;
	
	public VillageToolTipGenerator()
	{
		this.tooltipLevel = 1;
	}
	
	public VillageToolTipGenerator(int tooltipLevel)
	{
		if(tooltipLevel < 0 || tooltipLevel > 5)
			throw new IllegalArgumentException("TooltipLevel must be in [0..5].");
		this.tooltipLevel = tooltipLevel;
	}
	
	public int getTooltipLevel()
	{
		return this.tooltipLevel;
	}
	
	@Override
	public String generateToolTip(XYDataset dataset, int series, int item)
	{
		Village v = (Village)dataset.getSeriesKey(series);
		String allyName = "";
		if(v.getPlayer().getAlly() != null)
			allyName = v.getPlayer().getAlly().getName();
		switch(tooltipLevel)
		{
			case 0:
				return "";
			case 1:
				return v.getPlayer().getName();
			case 2:
				return v.getPlayer().getName() + " - " + v.getName();
			case 3:
				return v.getPlayer().getName() + " - " + v.getName() + " (" + v.getX() + "|" + v.getY() + ")";
			case 4:
				return v.getPlayer().getName() + " - " + v.getName() + " (" + v.getX() + "|" + v.getY() + ") [" + v.getPopulation() + "]";
			case 5:
				return v.getPlayer().getName() + " (" + v.getPlayer().getNation().toString() + ")" +
						" [" + allyName + "|" + v.getPlayer().getGroup() + "] - " +
						v.getName() + " (" + v.getX() + "|" + v.getY() + ") [" + v.getPopulation() + "]";			
		}
		return null;
	}	
}
