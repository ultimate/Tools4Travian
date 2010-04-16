package travian.parse;

import java.awt.Color;
import java.awt.Paint;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYSeries;

import travian.graphics.VillageToolTipGenerator;
import travian.graphics.XYRectangleRenderer;
import travian.model.Ally;
import travian.model.Player;
import travian.model.PlayerHighlight;
import travian.model.Village;
import travian.model.World;

public abstract class MapSqlParser
{
	public static JFrame load(String confName,
			int groupLimit,
			boolean remoteBackup, 
			boolean enablePlayerSelection, 
			String loadBackup, 
			int tooltipLevel,
			PlayerHighlight ph, 
			boolean playerPopulationColoring, 
			boolean allyPopulationColoring
			) throws Exception
	{
		boolean loadBackupInstead = false;
		
		if(groupLimit < 0)
			groupLimit = 0;
		String baseName = confName;
		if(baseName.endsWith(".properties"))
			baseName = baseName.substring(0, baseName.length() - ".properties".length());
		
		System.out.println("Name for configuration is: " + baseName);
		ResourceBundle rsBun = ResourceBundle.getBundle(baseName);
		
		InputStream is = null;
	
		System.out.println("map.sql = " + rsBun.getString("map.sql"));
		File f = new File(rsBun.getString("map.sql"));
		boolean localFile = f.exists();
		if(localFile)
		{
			System.out.println("map.sql is local file.");
			is = new FileInputStream(f);
		}
		else
		{
			System.out.println("map.sql is remote file.");
			System.out.print("Opening stream to map.sql...");

			try
			{
				URL url = new URL(rsBun.getString("map.sql"));
				is = url.openStream();
				System.out.println("OK!");
			}
			catch(Exception e)
			{
				System.out.println("Error!");
				if(loadBackup.equals("onAllFailures") || loadBackup.equals("onFailedConnection"))
				{
					System.out.println("Loading backup instead...");
					loadBackupInstead = true;
				}
				else
				{
					System.out.println("Exiting program!");
					System.exit(0);
				}
			}
		}
		String sql = null;
		if(!loadBackupInstead)
		{
			try
			{
				System.out.print("Reading map.sql to memory...");
				sql = readPage(is, !localFile && remoteBackup);
				if(sql.equals(""))
				{
					System.out.println("Empty File!");
					if(loadBackup.equals("onAllFailures") || loadBackup.equals("onEmptyFile"))
					{
						System.out.println("Loading backup instead...");
						loadBackupInstead = true;
					}
					else
					{
						System.out.println("Exiting program!");
						System.exit(0);
					}
				}
				else
					System.out.println("OK!");
			}
			catch(Exception e)
			{
				System.out.println("Error!");
				if(loadBackup.equals("onAllFailures") || loadBackup.equals("onFailedConnection"))
				{
					System.out.println("Loading backup instead...");
					loadBackupInstead = true;
				}
				else
				{
					System.out.println("Exiting program!");
					System.exit(0);
				}
			}
		}
		if(loadBackup.equals("always"))
			loadBackupInstead = true;
		if(loadBackupInstead)
		{
			File dir = new File("maps");
			if(!dir.isDirectory())
			{
				System.out.println("Backup directory does not exist!");
				System.out.println("Exiting program!");
				System.exit(0);
			}
			else if(dir.listFiles().length == 0)
			{
				System.out.println("Backup directory is empty!");
				System.out.println("Exiting program!");
				System.exit(0);
			}
			else
			{
				File[] files = dir.listFiles();
				if(files.length == 1 && files[0].getName().endsWith(".sql"))
				{
					System.out.print("Backup file found: " + files[0].getName() + "...\nReading map.sql to memory...");
					is = new FileInputStream(files[0]);
					sql = readPage(is, false);
					if(sql.equals(""))
					{
						System.out.println("Empty File!");
						System.out.println("File was only backup file!");
						System.out.println("Exiting program!");
						System.exit(0);
					}
					System.out.println("OK!");
				}
				else
				{
					boolean broken = false;
					for(int i = files.length-1; i >= 0; i--)
					{
						if(!files[i].getName().endsWith(".sql"))
							continue;
						System.out.print("Backup file found: " + files[i].getName() + "...\nReading map.sql to memory...");
						is = new FileInputStream(files[i]);
						sql = readPage(is, false);
						if(sql.equals(""))
						{
							System.out.println("Empty File!");
						}
						else
						{
							System.out.println("OK!");
							broken = true;
							break;
						}
					}
					if(!broken)
					{
						System.out.println("All files were invalid!");
						System.out.println("Exiting program!");
						System.exit(0);
					}						
				}
			}
		}
		System.out.print("Analysing content of map.sql...");
		World w = parseSQLFile(sql);
		System.out.println("OK!");
		
		if(allyPopulationColoring)
		{
			System.out.print("Analysing allies...");
			TreeMap<Float, Ally> allyRankList = new TreeMap<Float, Ally>();

			Iterator<Entry<Integer, Ally>> iter = w.getAllys().entrySet().iterator();
			Entry<Integer, Ally> e;
			Ally a;
			while(iter.hasNext())
			{
				e = iter.next();
				a = e.getValue();
				float pop = (float)(a.getTotalPopulation());
				while(allyRankList.containsKey(pop))
					pop += 0.01;
				allyRankList.put(pop, a);		
			}
			
			Iterator<Entry<Float, Ally>> iter2 = allyRankList.descendingMap().entrySet().iterator();
			Entry<Float, Ally> e2;
			int rank = 1;
			while(iter2.hasNext())
			{
				e2 = iter2.next();
				a = e2.getValue();
				a.setRank(rank++);		
			}			
			System.out.println("OK!");
		}

		System.out.println("Creating chart data...");
		DefaultXYZDataset dataset = new DefaultXYZDataset();
//		XYSeriesCollection dataset;
//		dataset = new XYSeriesCollection();	
		
		Paint paint;
		int r,g,b;
		int count = 0;
		Paint paint0 = null;
		String name0 = null;
		while(count <= groupLimit)
		{
			if(!rsBun.containsKey("group." + count + ".name") || !rsBun.containsKey("group." + count + ".color"))
				break;
			String color = rsBun.getString("group." + count + ".color");			
			try
			{
				r = Integer.parseInt(color.substring(0, color.indexOf(",")));
				g = Integer.parseInt(color.substring(color.indexOf(",")+1, color.lastIndexOf(",")));
				b = Integer.parseInt(color.substring(color.lastIndexOf(",")+1));
				paint = new Color(r,g,b);
			}
			catch(Exception e)
			{
				paint = Color.DARK_GRAY;
			}
			String name = rsBun.getString("group." + count + ".name");
			String allies, players;
			
			if(count != 0)
			{
				if(rsBun.containsKey("group." + count + ".allies"))
					allies = rsBun.getString("group." + count + ".allies");
				else
					allies = "";
				if(rsBun.containsKey("group." + count + ".players"))
					players = rsBun.getString("group." + count + ".players");
				else 
					players = "";
				
				StringTokenizer st;
				// allies
				st = new StringTokenizer(allies, ",");
				int aid;
				while(st.hasMoreTokens())
				{
					aid = Integer.parseInt(st.nextToken());
					Ally a = w.getAllys().get(aid);
					if(a == null)
						continue;
					for(Player p: a.getPlayers())
					{
						p.setGroup(name);
						for(Village v: p.getVillages())
						{
							v.setColor(paint);
						}
					}
				}		
				// players
				st = new StringTokenizer(players, ",");
				int pid;
				while(st.hasMoreTokens() && enablePlayerSelection)
				{
					pid = Integer.parseInt(st.nextToken());
					Player p = w.getPlayers().get(pid);
					if(p == null)
						continue;
					p.setGroup(name);
					for(Village v: p.getVillages())
					{
						v.setColor(paint);
					}
				}	
			}		
			else
			{
				paint0 = paint;
				name0 = name;
			}
			count++;
		}		
		
		if(ph != null)
		{
			Iterator<Entry<Integer, Player>> iter = w.getPlayers().entrySet().iterator();
			Entry<Integer, Player> e;
			Player p;
			while(iter.hasNext())
			{
				e = iter.next();
				p = e.getValue();
				ph.apply(p);
			}
		}
		
		Iterator<Entry<Integer, Village>> iter = w.getVillages().entrySet().iterator();
		Entry<Integer, Village> e;
		Village v;
		XYSeries s;
		while(iter.hasNext())
		{
			e = iter.next();
			v = e.getValue();
			if(v.getColor() == null)
			{
				v.setColor(paint0);
				v.getPlayer().setGroup(name0);
			}
			s = new XYSeries(v);
			s.add(v.getX(), v.getY());	
			dataset.addSeries(v, new double[][]{	new double[]{v.getX()},
													new double[]{v.getY()},
													new double[]{1}});
		}
		
		System.out.println("Chart data created");
		System.out.print("Creating chart...");		
		JFreeChart chart = ChartFactory.createScatterPlot(
									"Travian World Map",
									"x",
									"y",
									dataset,
									PlotOrientation.VERTICAL,
									false,
									(tooltipLevel > 0),
									false);
		chart.getXYPlot().getDomainAxis().setLowerBound(-400);
		chart.getXYPlot().getDomainAxis().setUpperBound(400);
		chart.getXYPlot().getRangeAxis().setLowerBound(-400);
		chart.getXYPlot().getRangeAxis().setUpperBound(400);		
		System.out.println("OK!");		
		
		System.out.print("Aplying design, colors and groups...");	
		XYRectangleRenderer renderer = new XYRectangleRenderer();
		renderer.setBaseToolTipGenerator(new VillageToolTipGenerator(tooltipLevel));
		chart.getXYPlot().setRenderer(renderer);
		for(int i = 0; i < chart.getXYPlot().getSeriesCount(); i++)
		{
			v = (Village)chart.getXYPlot().getDataset().getSeriesKey(i);
			if(playerPopulationColoring)
				renderer.setSeriesPaint(i, v.getColorByPopulation());
			else
				renderer.setSeriesPaint(i, v.getColor());
			if(allyPopulationColoring)
			{
				if(v.getPlayer().getAlly() != null && v.getPlayer().getAlly().getRank() <= 25)
					renderer.setSeriesOutlinePaint(i, Color.red);
				else if(v.getPlayer().getAlly() != null && v.getPlayer().getAlly().getRank() <= 25)
					renderer.setSeriesOutlinePaint(i, Color.red);
				else
					renderer.setSeriesOutlinePaint(i, new Color(1,1,1));
			}	
			else
				renderer.setSeriesOutlinePaint(i, new Color(1,1,1));
				
		}
		System.out.println("OK!");		

		System.out.print("Creating frame...");		
		ChartFrame frame = new ChartFrame("Travian World Map", chart, true);
		frame.getChartPanel().setDomainZoomable(true);
		frame.getChartPanel().setRangeZoomable(true);
		System.out.println("OK!");
		
		return frame;
	}
	
	public static World parseSQLFile(String sql) throws Exception
	{
		TreeMap<Integer, Ally> allys = new TreeMap<Integer, Ally>();
		TreeMap<Integer, Player> players = new TreeMap<Integer, Player>();
		TreeMap<Integer, Village> villages = new TreeMap<Integer, Village>();

		String line = null;
		
		Village v;
		Player p;
		Ally a;
		
		int fieldId, x, y, vid, tid, uid, aid, pop;
		String vname, uname, aname;

		int currentIndex = 0;
		int currentStart = 0;
		int currentEnd = 0;
		while(true)
		{
			currentIndex = sql.indexOf("(", currentIndex)+1;
			if (currentIndex <= 0)
				break;
			currentEnd = sql.indexOf(");\n", currentIndex); 
			
			if(currentEnd < currentIndex || currentEnd > sql.length())
				currentEnd = sql.length();
			line = sql.substring(currentIndex, currentEnd);
			
			currentStart = 0;
			currentEnd = line.indexOf(",", currentStart);
			fieldId = Integer.parseInt(line.substring(currentStart, currentEnd));

			currentStart = currentEnd+1;
			currentEnd = line.indexOf(",", currentStart);
			x = Integer.parseInt(line.substring(currentStart, currentEnd));

			currentStart = currentEnd+1;
			currentEnd = line.indexOf(",", currentStart);
			y = Integer.parseInt(line.substring(currentStart, currentEnd));

			currentStart = currentEnd+1;
			currentEnd = line.indexOf(",", currentStart);
			tid = Integer.parseInt(line.substring(currentStart, currentEnd));

			currentStart = currentEnd+1;
			currentEnd = line.indexOf(",", currentStart);
			vid = Integer.parseInt(line.substring(currentStart, currentEnd));
			
			currentStart = currentEnd+2;
			currentEnd = line.indexOf("',", currentStart);
			vname = line.substring(currentStart, currentEnd);
			
			currentStart = currentEnd+2;
			currentEnd = line.indexOf(",", currentStart);
			uid = Integer.parseInt(line.substring(currentStart, currentEnd));
			
			currentStart = currentEnd+2;
			currentEnd = line.indexOf("',", currentStart);
			uname = line.substring(currentStart, currentEnd);
			
			currentStart = currentEnd+2;
			currentEnd = line.indexOf(",", currentStart);
			try
			{
				aid = Integer.parseInt(line.substring(currentStart, currentEnd));
			}
			catch(NumberFormatException e)
			{
				aid = -1;
			}
			
			currentStart = currentEnd+2;
			currentEnd = line.indexOf("',", currentStart);
			aname = line.substring(currentStart, currentEnd);
			
			currentStart = currentEnd+2;
			currentEnd = line.length();
			pop = Integer.parseInt(line.substring(currentStart, currentEnd));

			// ally
			if(allys.containsKey(aid))
				a = allys.get(aid);
			else if (aid != -1 && aid != 0)
			{
				a = new Ally(aid, aname);
				allys.put(aid, a);
			}
			else
				a = null;
			
			// player
			if(players.containsKey(uid))
				p = players.get(uid);
			else
			{
				p = new Player(uname, uid, a, tid);
				players.put(uid, p);
			}
			
			// village
			v = new Village(fieldId, x, y, vid, vname, p, pop);
			villages.put(fieldId, v);
			
			currentIndex += currentEnd;
		}		
		
		World world = new World(allys, players, villages);
		
		return world;		
	}

	public static String readPage(InputStream is, boolean save) throws IOException {
		StringBuilder site = new StringBuilder();
				
		BufferedInputStream bis = new BufferedInputStream(is);
		InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		File out = new File("maps/map-" + df.format(new Date()) + ".sql");
		if(!out.getParentFile().exists())
			out.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(out);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		OutputStreamWriter osw = new OutputStreamWriter(bos);
		BufferedWriter bw = new BufferedWriter(osw);
		int curr = br.read();
		while(curr != -1) {
			site.append((char)curr);
			if(save)
				bw.write(curr);
			curr = br.read();				
		}
		bw.close();
		osw.close();		
		bos.close();
		fos.close();
		br.close();
		isr.close();
		bis.close();
		is.close();
		
		return site.toString();
	}
}
