package travian.application;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import travian.model.PlayerHighlight;
import travian.parse.MapSqlParser;

public class MapGenerator implements WindowListener
{
	private static final long serialVersionUID = 1L;
	private static Object synco = new Object();
	
	public static void main(String[] args)
	{
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("");
		System.out.println("");
		System.out.println("(c) TravianWorldMapGenerator made by ultimate");
		String baseName = null;
		int groupLimit = 2;
		boolean remoteBackup = false;
		boolean enablePlayerSelection = false;
		boolean playerPopulationColoring = false;
		boolean allyPopulationColoring = false;
		String loadBackup = "never";
		int tooltipLevel = 1;
		PlayerHighlight ph = null;
		if(args != null && args.length > 0)
		{
			for(String arg: args)
			{
				if(arg.equals("-help"))
				{
					printHelp(false);
					System.exit(0);
				}
				else if(arg.equals("-helpS"))
				{
					printHelp(true);
					System.exit(0);
				}
				else if(arg.equals("-conf"))
				{
					printConf(false);
					System.exit(0);
				}
				else if(arg.equals("-confS"))
				{
					printConf(true);
					System.exit(0);
				}
			}
			for(String arg: args)
			{
				if(arg.equalsIgnoreCase("-help") || arg.equalsIgnoreCase("-secret"))
				{
					// ignore
				}
				else if(arg.startsWith("-CF="))
				{
					baseName = arg.substring(4);
				}
				else if(arg.startsWith("-GL="))
				{
					try
					{
						groupLimit = Integer.parseInt(arg.substring(4));
						if(groupLimit < 0)
						{
							System.out.println("Invalid groupLimit: " + arg + " - Increasing groupLimit to 0");
							groupLimit = 0;
						}
					}
					catch(NumberFormatException e)
					{
						System.out.println("Invalid groupLimit: " + arg + " - Using standard groupLimit");
					}
				}
				else if(arg.startsWith("-TC="))
				{
					try
					{
						tooltipLevel = Integer.parseInt(arg.substring(4));
						if(tooltipLevel < 0 || tooltipLevel > 5)
						{
							System.out.println("Invalid tooltipLevel: " + arg + " - Using default of 1");
							tooltipLevel = 1;
						}
					}
					catch(NumberFormatException e)
					{
						System.out.println("Invalid tooltipLevel: " + arg + " - Using default of 1");
					}
				}
				else if(arg.equals("-RB"))
				{
					remoteBackup = true;
				}
				else if(arg.equals("-PP"))
				{
					playerPopulationColoring = true;
				}
				else if(arg.equals("-PA"))
				{
					allyPopulationColoring = true;
				}
				else if(arg.startsWith("-HP="))
				{
					boolean xset = false;
					boolean yset = false;
					int xMin = -400;
					int xMax =  400;
					int yMin = -400;
					int yMax =  400;
					int pMin =  0;
					int pMax =  Integer.MAX_VALUE;
					for(String arg2: args)
					{
						if(arg2.startsWith("-HX="))
						{
							xset = true;
							try
							{
								xMin = Integer.parseInt(arg2.substring(4,arg2.indexOf(",")));
							}
							catch(NumberFormatException e)
							{
								System.out.println("Invalid x boundary: " + arg2 + " - Using standard instead");
							}
							try
							{
								xMax = Integer.parseInt(arg2.substring(arg2.indexOf(",")+1));
							}
							catch(NumberFormatException e)
							{
								System.out.println("Invalid x boundary: " + arg2 + " - Using standard instead");
							}
						}
						else if(arg2.startsWith("-HY="))
						{
							yset = true;
							try
							{
								yMin = Integer.parseInt(arg2.substring(4,arg2.indexOf(",")));
							}
							catch(NumberFormatException e)
							{
								System.out.println("Invalid y boundary: " + arg2 + " - Using standard instead");
							}
							try
							{
								yMax = Integer.parseInt(arg2.substring(arg2.indexOf(",")+1));
							}
							catch(NumberFormatException e)
							{
								System.out.println("Invalid y boundary: " + arg2 + " - Using standard instead");
							}
						}
					}
					if(yset && xset)
					{
						try
						{
							pMin = Integer.parseInt(arg.substring(4,arg.indexOf(",")));
						}
						catch(NumberFormatException e)
						{
							System.out.println("Invalid p boundary: " + arg + " - Using standard instead");
						}
						try
						{
							pMax = Integer.parseInt(arg.substring(arg.indexOf(",")+1));
						}
						catch(NumberFormatException e)
						{
							System.out.println("Invalid p boundary: " + arg + " - Using standard instead");
						}
						ph = new PlayerHighlight(xMin, xMax, yMin, yMax, pMin, pMax);
						for(String arg2: args)
						{
							int r,g,b;
							if(arg2.startsWith("-HC="))
							{
								r = Integer.parseInt(arg2.substring(4, arg2.indexOf(",")));
								g = Integer.parseInt(arg2.substring(arg2.indexOf(",")+1, arg2.lastIndexOf(",")));
								b = Integer.parseInt(arg2.substring(arg2.lastIndexOf(",")+1));
								ph.setColor(new Color(r,g,b));
							}
						}
					}
				}
				else if(arg.equals("-PS"))
				{
					enablePlayerSelection = true;
				}
				else if(arg.startsWith("-LB="))
				{
					loadBackup = arg.substring(4);
					if(loadBackup.equals("always"));
					else if(loadBackup.equals("never"));
					else if(loadBackup.equals("onFailedConnection"));
					else if(loadBackup.equals("onEmptyFile"));
					else if(loadBackup.equals("onAllFailures"));
					else
					{
						System.out.println("Invalid load backup mode: " + arg + " - Using standard instead.");
						loadBackup = "never";
					}					
				}
				else if(arg.startsWith("-HC="));
				else if(arg.startsWith("-HX="));
				else if(arg.startsWith("-HY="));
				else
				{
					System.out.println("Invalid argument: " + arg);
					System.out.println("Argument will be ignored");
					System.out.println("Please add -help for details.");
				}
			}			
		}
		System.out.println("Program initialized...");
		if(baseName == null)
		{
			System.out.println("Asking for configuration file...");
			baseName = JOptionPane.showInputDialog("Please enter the name of the configuration file:", "");
			if(baseName == null)
			{
				System.out.println("No configuration file specified.\nProgram will exit!");
				System.exit(0);
			}
		}
		else
		{
			System.out.println("Configuration file specified in command line.");
		}
		System.out.println("Start loading...");
		JFrame f = null;
		try
		{
			f = MapSqlParser.load(baseName, groupLimit, remoteBackup, enablePlayerSelection, loadBackup, tooltipLevel, ph, playerPopulationColoring, allyPopulationColoring);
		}
		catch(Exception e1)
		{
			System.out.println("An Error occured while loading or parsing the map.sql");
			System.out.println("Maybe your configuration file is not correct.");
			System.out.println("Exiting program after printing error!");
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("Error is:");
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException e)
			{
			}
			e1.printStackTrace();
			System.exit(0);
			
		}
		System.out.println("Finished Loading!");
		System.out.println("Initializing Frame...");
		f.setVisible(true);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.addWindowListener(new MapGenerator());
		System.out.println("Program successfully started!");
		
		synchronized(synco)
		{
			while(true)
			{
				try
				{
					synco.wait();
					break;
				}
				catch(InterruptedException e)
				{
				}
			}
		}
		
		System.out.println("Program shutdown requested...");
		System.out.println("Closing Frame...");
		f.setVisible(false);
		f.dispose();
		System.out.println("Exiting program!");
		System.exit(0);
	}
	
	private static void printHelp(boolean secrets)
	{		
		System.out.println("");		
		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("");		
		System.out.println("There are two possible usages of this program:");
		System.out.println("");		
		System.out.println("  1) TravianWorldMapGenerator.bat [args]");
		System.out.println("  2) java -cp .;bin;lib/jcommon-1.0.12.jar;lib/jfreechart-1.0.9.jar");
		System.out.println("          travian.MapGenerator [args]");
		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("");		
		System.out.println("The configuration file must be a *.properties file and must be placed in this");
		System.out.println("  folder or the folder /bin. For further information on how to configure this");
		System.out.println("  program use the argument -conf.");
		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("");		
		System.out.println("Possible command line arguments are:");
		System.out.println("");		
		System.out.println("  -help            print this help menu");
		System.out.println("  -conf            print the explanation for configuration files");
		System.out.println("  -CF=[...]        the name of the configuration file");
		System.out.println("  -RB              make a backup of remote map.sql files");
		System.out.println("  -LB=[...]        load the latest backup in the specified case");
		System.out.println("                   [always|onFailedConnection|onEmptyFile|onAllFailures|never]");
		if(secrets)
		{
			System.out.println("");		
			System.out.println("Possible secret command line arguments are:");
			System.out.println("");		
			System.out.println("  -helpS           print this secret help menu");
			System.out.println("  -confS           print the secret explanation for configuration files");
			System.out.println("  -GL=[...]        enter the limit of user defined groups (default is 2)");
			System.out.println("  -PS              enable player selection");
			System.out.println("  -TC=[0..5]       set the tooltip complexity level (default is 1)");
			System.out.println("  -PP              use population specific coloring for players");
			System.out.println("  -PA              use population specific coloring for alliances");
			System.out.println("  The folowing 3 arguments must be used in a group. HC is optional:");
			System.out.println("  -HP=[..,..]      set the population limit for player highlighting");
			System.out.println("  -HX=[..,..]      set the X-boundaries for player highlighting");
			System.out.println("  -HY=[..,..]      set the Y-boundaries for player highlighting");
			System.out.println("  -HC=[r,g,b]      set the color for player highlighting");
		}	
		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");	
		System.out.println("");		
		System.out.println("");		
	}
	
	private static void printConf(boolean secrets)
	{
		System.out.println("");		
		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("");		
		System.out.println("The configuration of this program is easily done with an *.properties file.");
		System.out.println("The folowing rules are important when configuring the *.properties file:");
		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("");		
		System.out.println("  - Comments in a *.properties can be made by starting a line with \"#\".");
		System.out.println("  - Configure the location of the *.properties file with the key \"map.sql\".");
		System.out.println("  - Location can be either local or remote.");
		System.out.println("  - All groups must be enumerated with increasing numbers");
		System.out.println("      if a number is missing, following numbers are ignored");
		System.out.println("  - All colors must be specified as \"r,g,b\"");
		System.out.println("  - All allies must be specified as aid1,aid2,...");
		if(secrets)
			System.out.println("  - All players must be specified as pid1,pid2,...");
		System.out.println("  - Group 0 is necessary");
		System.out.println("      Necessary keys are \"group.0.name\" and \"group.0.color\"");	
		System.out.println("  - All other groups are build by using the following 4 keys:");
		if(!secrets)
			System.out.println("      \"group.i.name\", \"group.i.color\" and \"group.i.allies\"");
		else
			System.out.println("      \"group.i.name\", \"group.i.color\", \"group.i.allies\" and \"group.i.players\"");
		System.out.println("      where i is the number of the group");	
		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");	
		System.out.println("");		
		System.out.println("### Complete Example 1 ###");	
		System.out.println("### --> Everything will work fine ###");
		System.out.println("map.sql=http://welt3.travian.de/map.sql");
		System.out.println("# Occupied fields");		
		System.out.println("group.0.name=others");		
		System.out.println("group.0.color=153,153,153");	
		System.out.println("# My Ally");				
		System.out.println("group.1.name=ABC");		
		System.out.println("group.1.color=0,0,255");		
		System.out.println("group.1.allies=435");		
		if(secrets)	
			System.out.println("group.1.players=");		
		System.out.println("# Allys we are in war with");				
		System.out.println("group.2.name=War");		
		System.out.println("group.2.color=255,0,0");		
		System.out.println("group.2.allies=123,6376");	
		if(secrets)	
		{
			System.out.println("group.2.players=14568,34361");
			System.out.println("# Highlight my villages");	
			System.out.println("group.3.name=My Villages");		
			System.out.println("group.3.color=0,255,255");		
			System.out.println("group.3.allies=");	
			System.out.println("group.3.players=35575");
		}

		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");	
		System.out.println("");		
		System.out.println("### Complete Example 2 ###");	
		System.out.println("### --> Group 2 will be ignored, because group 1 does not exist ###");
		System.out.println("map.sql=C:/TravianData/map.sql");
		System.out.println("# Occupied fields");		
		System.out.println("group.0.name=occupied");		
		System.out.println("group.0.color=153,153,153");
		System.out.println("# Friends");	
		System.out.println("group.2.name=BNDs");		
		System.out.println("group.2.color=0,255,0");		
		System.out.println("group.2.allies=367");	
		if(secrets)	
			System.out.println("group.2.players=12644");
		System.out.println("");		
		System.out.println("--------------------------------------------------------------------------------");	
		System.out.println("");		
		System.out.println("");		
	}
	
	public MapGenerator()
	{
	}	

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e)
	{
		synchronized(synco)
		{
			synco.notifyAll();
		}
	}
		
	@Override
	public void windowClosing(WindowEvent e)
	{
		synchronized(synco)
		{
			synco.notifyAll();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
}
