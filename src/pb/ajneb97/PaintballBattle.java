package pb.ajneb97;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import pb.ajneb97.api.ExpansionPaintballBattle;
import pb.ajneb97.api.Hat;
import pb.ajneb97.api.PaintballAPI;
import pb.ajneb97.api.Perk;
import pb.ajneb97.database.ConexionDatabase;
import pb.ajneb97.database.JugadorDatos;
import pb.ajneb97.database.MySQL;
import pb.ajneb97.juego.EstadoPartida;
import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.juego.Partida;
import pb.ajneb97.juego.PartidaEditando;
import pb.ajneb97.managers.Actualizacion;
import pb.ajneb97.managers.CartelesAdmin;
import pb.ajneb97.managers.CartelesListener;
import pb.ajneb97.managers.Checks;
import pb.ajneb97.managers.CooldownKillstreaksActionbar;
import pb.ajneb97.managers.InventarioAdmin;
import pb.ajneb97.managers.InventarioHats;
import pb.ajneb97.managers.InventarioShop;
import pb.ajneb97.managers.PartidaListener;
import pb.ajneb97.managers.PartidaListenerNew;
import pb.ajneb97.managers.PartidaManager;
import pb.ajneb97.managers.ScoreboardAdmin;
import pb.ajneb97.managers.TopHologram;
import pb.ajneb97.managers.TopHologramAdmin;


public class PaintballBattle extends JavaPlugin {
  
	PluginDescriptionFile pdfFile = getDescription();
	public String version = pdfFile.getVersion();
	public static String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&b&lPaintball Battle&8] ");
	private ArrayList<Partida> partidas;
	private FileConfiguration arenas = null;
	private File arenasFile = null;
	private FileConfiguration messages = null;
	private File messagesFile = null;
	private FileConfiguration shop = null;
	private File shopFile = null;
	private PartidaEditando partidaEditando;
	private ArrayList<PlayerConfig> configPlayers;
	private ArrayList<JugadorDatos> jugadoresDatos;
	private ArrayList<TopHologram> topHologramas;
	private FileConfiguration holograms = null;
	private File hologramsFile = null;
	private static Economy econ = null;	
	public boolean primeraVez = false;
	public String latestversion;
	
	public String rutaMessages;
	public String rutaConfig;
	
	private ScoreboardAdmin scoreboardTask;
	private CartelesAdmin cartelesTask;
	private TopHologramAdmin hologramasTask;
	
	private ConexionDatabase conexionDatabase;
	
	
	@SuppressWarnings("unused")
	public void onEnable(){
	   configPlayers = new ArrayList<PlayerConfig>();
	   jugadoresDatos = new ArrayList<JugadorDatos>();
	   topHologramas = new ArrayList<TopHologram>();
	   registerEvents();
	   registerArenas();
	   registerConfig();
	   registerHolograms();
	   registerMessages();
	   createPlayersFolder();
	   registerPlayers();
	   registerShop();
	   cargarPartidas();
	   registerCommands();
	   checkMessagesUpdate();
	   
	   cargarJugadores();
	   setupEconomy();
	   
	   if(MySQL.isEnabled(getConfig())){
		   conexionDatabase = new ConexionDatabase(getConfig());
	   }
	   
	   scoreboardTask = new ScoreboardAdmin(this);
	   scoreboardTask.crearScoreboards();
	   cartelesTask = new CartelesAdmin(this);
	   cartelesTask.actualizarCarteles();
	   CooldownKillstreaksActionbar c = new CooldownKillstreaksActionbar(this);
	   c.crearActionbars();
	   
	   cargarTopHologramas();
	   hologramasTask = new TopHologramAdmin(this);
	   hologramasTask.actualizarHologramas();
	   
	   PaintballAPI api = new PaintballAPI(this);
	   if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
		   new ExpansionPaintballBattle(this).register();
	   }
	   
	   Checks.checkearYModificar(this, primeraVez);
	   Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.YELLOW + "Has been enabled! " + ChatColor.WHITE + "Version: " + version);
	   Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.YELLOW + "Thanks for using my plugin!  " + ChatColor.WHITE + "~Ajneb97");
	   updateChecker();
	}

	public void onDisable(){
		if(partidas != null) {
			for(int i=0;i<partidas.size();i++) {
				if(!partidas.get(i).getEstado().equals(EstadoPartida.DESACTIVADA)) {
					PartidaManager.finalizarPartida(partidas.get(i),this,true,null);
				}
			}
		}
		guardarPartidas();
		guardarJugadores();
		guardarTopHologramas();
		
		Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.YELLOW + "Has been disabled! " + ChatColor.WHITE + "Version: " + version);
	}
	
	public void recargarScoreboard() {
		int taskID = scoreboardTask.getTaskID();
		Bukkit.getScheduler().cancelTask(taskID);
		scoreboardTask = new ScoreboardAdmin(this);
		scoreboardTask.crearScoreboards();
	}
	
	public void recargarCarteles() {
		int taskID = cartelesTask.getTaskID();
		Bukkit.getScheduler().cancelTask(taskID);
		cartelesTask = new CartelesAdmin(this);
		cartelesTask.actualizarCarteles();
	}
	
	public void recargarHologramas() {
		int taskID = hologramasTask.getTaskID();
		Bukkit.getScheduler().cancelTask(taskID);
		hologramasTask = new TopHologramAdmin(this);
		hologramasTask.actualizarHologramas();
	}
	
	public void setPartidaEditando(PartidaEditando p) {
		this.partidaEditando = p;
	}
	
	public void removerPartidaEditando() {
		this.partidaEditando = null;
	}
	
	public PartidaEditando getPartidaEditando() {
		return this.partidaEditando;
	}
	
	public ConexionDatabase getConexionDatabase() {
		return this.conexionDatabase;
	}	
	
	private boolean setupEconomy() {
		  if (getServer().getPluginManager().getPlugin("Vault") == null) {
	          return false;
	      }
	      RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	      if (rsp == null) {
	          return false;
	      }
	      econ = rsp.getProvider();
	      return econ != null;
	  }
	  
	public Economy getEconomy(){	
		return econ;
	}
	
	public void registerEvents(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PartidaListener(this), this);
		if(!Bukkit.getVersion().contains("1.8")) {
			pm.registerEvents(new PartidaListenerNew(this), this);
		}
		pm.registerEvents(new CartelesListener(this), this);
		pm.registerEvents(new InventarioAdmin(this), this);
		pm.registerEvents(new InventarioShop(this), this);
		pm.registerEvents(new InventarioHats(this), this);
		pm.registerEvents(new Actualizacion(this), this);
	}
	
	public void registerCommands(){
		this.getCommand("paintball").setExecutor(new Comando(this));
	}
	
	public Partida getPartidaJugador(String jugador) {
		for(int i=0;i<partidas.size();i++) {
			ArrayList<JugadorPaintball> jugadores = partidas.get(i).getJugadores();
			for(int c=0;c<jugadores.size();c++) {
				if(jugadores.get(c).getJugador().getName().equals(jugador)) {
					return partidas.get(i);
				}
			}
		}
		return null;
	}
	
	public ArrayList<Partida> getPartidas() {
		return this.partidas;
	}
	
	public Partida getPartida(String nombre) {
		for(int i=0;i<partidas.size();i++) {
			if(partidas.get(i).getNombre().equals(nombre)) {
				return partidas.get(i);
			}
		}
		return null;
	}
	
	public void agregarPartida(Partida partida) {
		this.partidas.add(partida);
	}
	
	public void removerPartida(String nombre) {
		for(int i=0;i<partidas.size();i++) {
			if(partidas.get(i).getNombre().equals(nombre)) {
				partidas.remove(i);
			}
		}
	}
	
	public void cargarPartidas() {
		  this.partidas = new ArrayList<Partida>();
		  FileConfiguration arenas = getArenas();
		  if(arenas.contains("Arenas")) {
			  for(String key : arenas.getConfigurationSection("Arenas").getKeys(false)) {
				  int min_players = Integer.valueOf(arenas.getString("Arenas."+key+".min_players"));
				  int max_players = Integer.valueOf(arenas.getString("Arenas."+key+".max_players"));
				  int time = Integer.valueOf(arenas.getString("Arenas."+key+".time"));
				  int vidas = Integer.valueOf(arenas.getString("Arenas."+key+".lives"));
				  
				  Location lLobby = null;
				  if(arenas.contains("Arenas."+key+".Lobby")) {
					  double xLobby = Double.valueOf(arenas.getString("Arenas."+key+".Lobby.x"));
					  double yLobby = Double.valueOf(arenas.getString("Arenas."+key+".Lobby.y"));
					  double zLobby = Double.valueOf(arenas.getString("Arenas."+key+".Lobby.z"));
					  String worldLobby = arenas.getString("Arenas."+key+".Lobby.world");
					  float pitchLobby = Float.valueOf(arenas.getString("Arenas."+key+".Lobby.pitch"));
					  float yawLobby = Float.valueOf(arenas.getString("Arenas."+key+".Lobby.yaw"));
					  lLobby = new Location(Bukkit.getWorld(worldLobby),xLobby,yLobby,zLobby,yawLobby,pitchLobby);
				  }
				  
				  
				  String nombreTeam1 = arenas.getString("Arenas."+key+".Team1.name");
				  
				  Location lSpawnTeam1 = null;
				  if(arenas.contains("Arenas."+key+".Team1.Spawn")) {
					  double xSpawnTeam1 = Double.valueOf(arenas.getString("Arenas."+key+".Team1.Spawn.x"));
					  double ySpawnTeam1 = Double.valueOf(arenas.getString("Arenas."+key+".Team1.Spawn.y"));
					  double zSpawnTeam1 = Double.valueOf(arenas.getString("Arenas."+key+".Team1.Spawn.z"));
					  String worldSpawnTeam1 = arenas.getString("Arenas."+key+".Team1.Spawn.world");
					  float pitchSpawnTeam1 = Float.valueOf(arenas.getString("Arenas."+key+".Team1.Spawn.pitch"));
					  float yawSpawnTeam1 = Float.valueOf(arenas.getString("Arenas."+key+".Team1.Spawn.yaw"));
					  lSpawnTeam1 = new Location(Bukkit.getWorld(worldSpawnTeam1),xSpawnTeam1,ySpawnTeam1,zSpawnTeam1,yawSpawnTeam1,pitchSpawnTeam1);
				  }
				  
				  
				  String nombreTeam2 = arenas.getString("Arenas."+key+".Team2.name");
				  Location lSpawnTeam2 = null;
				  if(arenas.contains("Arenas."+key+".Team2.Spawn")) {
					  double xSpawnTeam2 = Double.valueOf(arenas.getString("Arenas."+key+".Team2.Spawn.x"));
					  double ySpawnTeam2 = Double.valueOf(arenas.getString("Arenas."+key+".Team2.Spawn.y"));
					  double zSpawnTeam2 = Double.valueOf(arenas.getString("Arenas."+key+".Team2.Spawn.z"));
					  String worldSpawnTeam2 = arenas.getString("Arenas."+key+".Team2.Spawn.world");
					  float pitchSpawnTeam2 = Float.valueOf(arenas.getString("Arenas."+key+".Team2.Spawn.pitch"));
					  float yawSpawnTeam2 = Float.valueOf(arenas.getString("Arenas."+key+".Team2.Spawn.yaw"));
					  lSpawnTeam2 = new Location(Bukkit.getWorld(worldSpawnTeam2),xSpawnTeam2,ySpawnTeam2,zSpawnTeam2,yawSpawnTeam2,pitchSpawnTeam2);
				  }
				  
				  Partida partida = new Partida(key,time,nombreTeam1,nombreTeam2,vidas);
				  if(nombreTeam1.equalsIgnoreCase("random")) {
					  partida.getTeam1().setRandom(true);
				  }
				  if(nombreTeam2.equalsIgnoreCase("random")) {
					  partida.getTeam2().setRandom(true);
				  }
				  partida.modificarTeams(getConfig());
				  partida.setCantidadMaximaJugadores(max_players);
				  partida.setCantidadMinimaJugadores(min_players);
				  partida.setLobby(lLobby);
				  partida.getTeam1().setSpawn(lSpawnTeam1);
				  partida.getTeam2().setSpawn(lSpawnTeam2);
				  String enabled = arenas.getString("Arenas."+key+".enabled");
				  if(enabled.equals("true")) {
					  partida.setEstado(EstadoPartida.ESPERANDO);
				  }else {
					  partida.setEstado(EstadoPartida.DESACTIVADA);
				  }
				  
				  this.partidas.add(partida);
			  }
		  }
		  
	  }
	
	public void guardarPartidas() {
		  FileConfiguration arenas = getArenas();
		  arenas.set("Arenas", null);
		  for(Partida p : this.partidas) {
			  String nombre = p.getNombre();
			  arenas.set("Arenas."+nombre+".min_players", p.getCantidadMinimaJugadores()+"");
			  arenas.set("Arenas."+nombre+".max_players", p.getCantidadMaximaJugadores()+"");
			  arenas.set("Arenas."+nombre+".time", p.getTiempoMaximo()+"");
			  arenas.set("Arenas."+nombre+".lives", p.getVidasIniciales()+"");
			  Location lLobby = p.getLobby();
			  if(lLobby != null) {
				  arenas.set("Arenas."+nombre+".Lobby.x", lLobby.getX()+"");
				  arenas.set("Arenas."+nombre+".Lobby.y", lLobby.getY()+"");
				  arenas.set("Arenas."+nombre+".Lobby.z", lLobby.getZ()+"");
				  arenas.set("Arenas."+nombre+".Lobby.world", lLobby.getWorld().getName());
				  arenas.set("Arenas."+nombre+".Lobby.pitch", lLobby.getPitch());
				  arenas.set("Arenas."+nombre+".Lobby.yaw", lLobby.getYaw());
			  }
			  
			  Location lSpawnTeam1 = p.getTeam1().getSpawn();
			  if(lSpawnTeam1 != null) {
				  arenas.set("Arenas."+nombre+".Team1.Spawn.x", lSpawnTeam1.getX()+"");
				  arenas.set("Arenas."+nombre+".Team1.Spawn.y", lSpawnTeam1.getY()+"");
				  arenas.set("Arenas."+nombre+".Team1.Spawn.z", lSpawnTeam1.getZ()+"");
				  arenas.set("Arenas."+nombre+".Team1.Spawn.world", lSpawnTeam1.getWorld().getName());
				  arenas.set("Arenas."+nombre+".Team1.Spawn.pitch", lSpawnTeam1.getPitch());
				  arenas.set("Arenas."+nombre+".Team1.Spawn.yaw", lSpawnTeam1.getYaw());
			  }
			  if(p.getTeam1().esRandom()) {
				  arenas.set("Arenas."+nombre+".Team1.name", "random");
			  }else {
				  arenas.set("Arenas."+nombre+".Team1.name", p.getTeam1().getTipo()); 
			  }
			  
			  
			  Location lSpawnTeam2 = p.getTeam2().getSpawn();
			  if(lSpawnTeam2 != null) {
				  arenas.set("Arenas."+nombre+".Team2.Spawn.x", lSpawnTeam2.getX()+"");
				  arenas.set("Arenas."+nombre+".Team2.Spawn.y", lSpawnTeam2.getY()+"");
				  arenas.set("Arenas."+nombre+".Team2.Spawn.z", lSpawnTeam2.getZ()+"");
				  arenas.set("Arenas."+nombre+".Team2.Spawn.world", lSpawnTeam2.getWorld().getName());
				  arenas.set("Arenas."+nombre+".Team2.Spawn.pitch", lSpawnTeam2.getPitch());
				  arenas.set("Arenas."+nombre+".Team2.Spawn.yaw", lSpawnTeam2.getYaw());
			  }
			  if(p.getTeam2().esRandom()) {
				  arenas.set("Arenas."+nombre+".Team2.name", "random");
			  }else {
				  arenas.set("Arenas."+nombre+".Team2.name", p.getTeam2().getTipo()); 
			  }
			  
			  if(p.getEstado().equals(EstadoPartida.DESACTIVADA)) {
				  arenas.set("Arenas."+nombre+".enabled", "false");
			  }else {
				  arenas.set("Arenas."+nombre+".enabled", "true");
			  }
		  }
		  this.saveArenas();
	  }
	
	 public void registerArenas(){
		  arenasFile = new File(this.getDataFolder(), "arenas.yml");
		  if(!arenasFile.exists()){
		    	this.getArenas().options().copyDefaults(true);
				saveArenas();
		    }
	  }
	  public void saveArenas() {
		 try {
			 arenas.save(arenasFile);
		 } catch (IOException e) {
			 e.printStackTrace();
	 	}
	 }
	  
	  public FileConfiguration getArenas() {
		    if (arenas == null) {
		        reloadArenas();
		    }
		    return arenas;
		}
	  
	  public void reloadArenas() {
		    if (arenas == null) {
		    	arenasFile = new File(getDataFolder(), "arenas.yml");
		    }
		    arenas = YamlConfiguration.loadConfiguration(arenasFile);

		    Reader defConfigStream;
			try {
				defConfigStream = new InputStreamReader(this.getResource("arenas.yml"), "UTF8");
				if (defConfigStream != null) {
			        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			        arenas.setDefaults(defConfig);
			    }
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	    
		}
	  
	  public void registerConfig(){	
			File config = new File(this.getDataFolder(), "config.yml");
			rutaConfig = config.getPath();
		    if(!config.exists()){
		    	this.primeraVez = true;
		    	this.getConfig().options().copyDefaults(true);
				saveConfig();  
		    }
	  }
	  
	  public void registerShop(){
		  shopFile = new File(this.getDataFolder(), "shop.yml");
			if(!shopFile.exists()){
				this.getShop().options().copyDefaults(true);
				saveShop();
			}
		}
		
		public void saveShop() {
			try {
				shop.save(shopFile);
			}catch (IOException e) {
				 e.printStackTrace();
		 	}
		}
		  
		public FileConfiguration getShop() {
			if (shop == null) {
			   reloadShop();
			}
			return shop;
		}
		  
		public void reloadShop() {
			if (shop == null) {
				shopFile = new File(getDataFolder(), "shop.yml");
			}
			shop = YamlConfiguration.loadConfiguration(shopFile);
			Reader defConfigStream;
			try {
				defConfigStream = new InputStreamReader(this.getResource("shop.yml"), "UTF8");
				if (defConfigStream != null) {
				     YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				     shop.setDefaults(defConfig);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	    
		}
	  
	  public void registerMessages(){
		  messagesFile = new File(this.getDataFolder(), "messages.yml");
		  rutaMessages = messagesFile.getPath();
			if(!messagesFile.exists()){
				this.getMessages().options().copyDefaults(true);
				saveMessages();
			}
		}
		
		public void saveMessages() {
			try {
				messages.save(messagesFile);
			}catch (IOException e) {
				 e.printStackTrace();
		 	}
		}
		  
		public FileConfiguration getMessages() {
			if (messages == null) {
			   reloadMessages();
			}
			return messages;
		}
		  
		public void reloadMessages() {
			if (messages == null) {
			    messagesFile = new File(getDataFolder(), "messages.yml");
			}
			messages = YamlConfiguration.loadConfiguration(messagesFile);
			Reader defConfigStream;
			try {
				defConfigStream = new InputStreamReader(this.getResource("messages.yml"), "UTF8");
				if (defConfigStream != null) {
				     YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				     messages.setDefaults(defConfig);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	    
		}
	
		public void createPlayersFolder(){
			File folder;
	        try {
	            folder = new File(this.getDataFolder() + File.separator + "players");
	            if(!folder.exists()){
	                folder.mkdirs();
	            }
	        } catch(SecurityException e) {
	            folder = null;
	        }
		}
		
		public void savePlayers() {
			for(int i=0;i<configPlayers.size();i++) {
				configPlayers.get(i).savePlayerConfig();
			}
		}
		
		public void registerPlayers(){
			String path = this.getDataFolder() + File.separator + "players";
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			for (int i=0;i<listOfFiles.length;i++) {
				if(listOfFiles[i].isFile()) {
			        String pathName = listOfFiles[i].getName();
			        PlayerConfig config = new PlayerConfig(pathName,this);
			        config.registerPlayerConfig();
			        configPlayers.add(config);
			    }
			}
		}
		
		public ArrayList<PlayerConfig> getConfigPlayers(){
			return this.configPlayers;
		}
		
		public boolean archivoYaRegistrado(String pathName) {
			for(int i=0;i<configPlayers.size();i++) {
				if(configPlayers.get(i).getPath().equals(pathName)) {
					return true;
				}
			}
			return false;
		}
		
		public PlayerConfig getPlayerConfig(String pathName) {
			for(int i=0;i<configPlayers.size();i++) {
				if(configPlayers.get(i).getPath().equals(pathName)) {
					return configPlayers.get(i);
				}
			}
			return null;
		}
		public ArrayList<PlayerConfig> getPlayerConfigs() {
			return this.configPlayers;
		}
		
		public boolean registerPlayer(String pathName) {
			if(!archivoYaRegistrado(pathName)) {
				PlayerConfig config = new PlayerConfig(pathName,this);
		        config.registerPlayerConfig();
		        configPlayers.add(config);
		        return true;
			}else {
				return false;
			}
		}
		
		public void removerConfigPlayer(String path) {
			for(int i=0;i<configPlayers.size();i++) {
				if(configPlayers.get(i).getPath().equals(path)) {
					configPlayers.remove(i);
				}
			}
		}
	
		public void cargarJugadores() {
			if(!MySQL.isEnabled(getConfig())) {
				for(PlayerConfig playerConfig : configPlayers) {
					FileConfiguration players = playerConfig.getConfig();
					String jugador = players.getString("name");
					int kills = 0;
					int wins = 0;
					int loses = 0;
					int ties = 0;
					int coins = 0;
					
					if(players.contains("kills")) {
						kills = Integer.valueOf(players.getString("kills"));
					}
					if(players.contains("wins")) {
						wins = Integer.valueOf(players.getString("wins"));
					}
					if(players.contains("loses")) {
						loses = Integer.valueOf(players.getString("loses"));
					}
					if(players.contains("ties")) {
						ties = Integer.valueOf(players.getString("ties"));
					}
					if(players.contains("coins")) {
						coins = Integer.valueOf(players.getString("coins"));
					}
					ArrayList<Perk> perks = new ArrayList<Perk>();
					if(players.contains("perks")) {
						List<String> listaPerks = players.getStringList("perks");
						for(int i=0;i<listaPerks.size();i++) {
							String[] separados = listaPerks.get(i).split(";");
							Perk p = new Perk(separados[0],Integer.valueOf(separados[1]));
							perks.add(p);
						}	
					}
					ArrayList<Hat> hats = new ArrayList<Hat>();
					if(players.contains("hats")) {
						List<String> listaHats = players.getStringList("hats");
						for(int i=0;i<listaHats.size();i++) {
							String[] separados = listaHats.get(i).split(";");
							Hat h = new Hat(separados[0],Boolean.valueOf(separados[1]));
							hats.add(h);
						}
					}
					
						
					this.agregarJugadorDatos(new JugadorDatos(jugador,playerConfig.getPath().replace(".yml", ""),wins,loses,ties,kills,coins,perks,hats));
				}
			}
		}
		
		public void guardarJugadores() {
			if(!MySQL.isEnabled(getConfig())) {
				for(JugadorDatos j : jugadoresDatos) {
					String jugador = j.getName();
					PlayerConfig playerConfig = getPlayerConfig(j.getUUID()+".yml");
					FileConfiguration players = playerConfig.getConfig();
					players.set("name", jugador);
					players.set("kills", j.getKills());
					players.set("wins", j.getWins());
					players.set("ties", j.getTies());
					players.set("loses", j.getLoses());
					players.set("coins", j.getCoins());
					
					List<String> listaPerks = new ArrayList<String>();
					ArrayList<Perk> perks = j.getPerks();
					for(Perk p : perks) {
						listaPerks.add(p.getName()+";"+p.getNivel());
					}
					players.set("perks", listaPerks);
					
					List<String> listaHats = new ArrayList<String>();
					ArrayList<Hat> hats = j.getHats();
					for(Hat h : hats) {
						listaHats.add(h.getName()+";"+h.isSelected());
					}
					players.set("hats", listaHats);
				}
				savePlayers();
			}
		}
		
		public void agregarJugadorDatos(JugadorDatos jugador) {
			jugadoresDatos.add(jugador);
		}
		
		public JugadorDatos getJugador(String jugador) {
			for(JugadorDatos j : jugadoresDatos) {
				if(j != null && j.getName() != null && j.getName().equals(jugador)) {
					return j;
				}
			}
			return null;
		}
		
		public ArrayList<JugadorDatos> getJugadores(){
			return this.jugadoresDatos;
		}
		
		public void registerHolograms(){
			  hologramsFile = new File(this.getDataFolder(), "holograms.yml");
			  if(!hologramsFile.exists()){
			    	this.getHolograms().options().copyDefaults(true);
					saveHolograms();
			    }
		  }
		  public void saveHolograms() {
			 try {
				 holograms.save(hologramsFile);
			 } catch (IOException e) {
				 e.printStackTrace();
		 	}
		 }
		  
		  public FileConfiguration getHolograms() {
			    if (holograms == null) {
			        reloadHolograms();
			    }
			    return holograms;
			}
		  
		  public void reloadHolograms() {
			    if (holograms == null) {
			    	hologramsFile = new File(getDataFolder(), "holograms.yml");
			    }
			    holograms = YamlConfiguration.loadConfiguration(hologramsFile);

			    Reader defConfigStream;
				try {
					defConfigStream = new InputStreamReader(this.getResource("holograms.yml"), "UTF8");
					if (defConfigStream != null) {
				        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				        holograms.setDefaults(defConfig);
				    }
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}	    
			}
		  
		  public void agregarTopHolograma(TopHologram topHologram) {
				this.topHologramas.add(topHologram);
			}
			
			public boolean eliminarTopHologama(String nombre) {
				for(int i=0;i<topHologramas.size();i++) {
					if(topHologramas.get(i).getName().equals(nombre)) {
						topHologramas.get(i).removeHologram();
						topHologramas.remove(i);
						return true;
					}
				}
				return false;
			}
			
			public TopHologram getTopHologram(String nombre) {
				for(int i=0;i<topHologramas.size();i++) {
					if(topHologramas.get(i).getName().equals(nombre)) {
						return topHologramas.get(i);
					}
				}
				return null;
			}
			
			public void guardarTopHologramas() {
				FileConfiguration holograms = getHolograms();
				holograms.set("Holograms", null);
				for(int i=0;i<topHologramas.size();i++) {
					Location l = topHologramas.get(i).getHologram().getLocation();
					String name = topHologramas.get(i).getName();
					String type = topHologramas.get(i).getType();
					String period = topHologramas.get(i).getPeriod();
					holograms.set("Holograms."+name+".type", type);
					holograms.set("Holograms."+name+".period", period);
					holograms.set("Holograms."+name+".x", l.getX()+"");
					holograms.set("Holograms."+name+".y", topHologramas.get(i).getyOriginal()+"");
					holograms.set("Holograms."+name+".z", l.getZ()+"");
					holograms.set("Holograms."+name+".world", l.getWorld().getName());
				}
				saveHolograms();
			}
			
			public void cargarTopHologramas() {
				FileConfiguration holograms = getHolograms();
				if(holograms.contains("Holograms")) {
					for(String name : holograms.getConfigurationSection("Holograms").getKeys(false)) {
						String type = holograms.getString("Holograms."+name+".type");
						double x = Double.valueOf(holograms.getString("Holograms."+name+".x"));
						double y = Double.valueOf(holograms.getString("Holograms."+name+".y"));
						double z = Double.valueOf(holograms.getString("Holograms."+name+".z"));
						World world = Bukkit.getWorld(holograms.getString("Holograms."+name+".world"));
						Location location = new Location(world,x,y,z);
						String period = "global";
						if(holograms.contains("Holograms."+name+".period")) {
							period = holograms.getString("Holograms."+name+".period");
						}
						TopHologram topHologram = new TopHologram(name,type,location,this,period);
						topHologram.spawnHologram(this);
						this.agregarTopHolograma(topHologram);
					}
				}
			}
			
			public ArrayList<TopHologram> getTopHologramas(){
				return this.topHologramas;
			}
			
			public void updateChecker(){
				  
				  try {
					  HttpURLConnection con = (HttpURLConnection) new URL(
			                  "https://api.spigotmc.org/legacy/update.php?resource=76676").openConnection();
			          int timed_out = 1250;
			          con.setConnectTimeout(timed_out);
			          con.setReadTimeout(timed_out);
			          latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			          if (latestversion.length() <= 7) {
			        	  if(!version.equals(latestversion)){
			        		  Bukkit.getConsoleSender().sendMessage(ChatColor.RED +"There is a new version available. "+ChatColor.YELLOW+
			        				  "("+ChatColor.GRAY+latestversion+ChatColor.YELLOW+")");
			        		  Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You can download it at: "+ChatColor.WHITE+"https://www.spigotmc.org/resources/76676/");  
			        	  }      	  
			          }
			      } catch (Exception ex) {
			    	  Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED +"Error while checking update.");
			      }
			  }
			
			public void checkMessagesUpdate(){
				  Path archivo = Paths.get(rutaMessages);
				  Path archivoConfig = Paths.get(rutaConfig);
				  try{
					  String texto = new String(Files.readAllBytes(archivo));
					  String textoConfig = new String(Files.readAllBytes(archivoConfig));
					  if(!textoConfig.contains("broadcast_starting_arena:")){
						  getConfig().set("broadcast_starting_arena.enabled", true);
						  List<String> lista = new ArrayList<String>();
						  lista.add("paintball");lista.add("lobby");
						  getConfig().set("broadcast_starting_arena.worlds", lista);
						  getConfig().set("rewards_executed_after_teleport", false);
						  getMessages().set("arenaStartingBroadcast", "&aArena &6&l%arena% &ais about to start! Use &b/pb join %arena% &ato join the game!");
						  saveConfig();	
						  saveMessages();
					  }
					  if(!textoConfig.contains("startCooldownSound:")){
						  getConfig().set("startCooldownSound", "BLOCK_NOTE_BLOCK_PLING;10;1");
						  getConfig().set("startGameSound", "BLOCK_NOTE_BLOCK_PLING;10;2");
						  getConfig().set("arena_chat_enabled", true);
						  saveConfig();	
					  }
					  if(!texto.contains("errorClearInventory:")){
							getMessages().set("errorClearInventory", "&c&lERROR! &7To join an arena clear your inventory first.");
							getConfig().set("empty_inventory_to_join", false);
							saveConfig();
							saveMessages();
					  }
					  if(!textoConfig.contains("snowball_particle:")){
						  getConfig().set("snowball_particle", "SNOW_SHOVEL");
						  saveConfig();	
					  }
					  if(!texto.contains("receiveCoinsMessage:")){
						  getMessages().set("receiveCoinsMessage", "&aYou received &e%amount% &acoins.");
						  saveMessages();	
					  }
					  if(!textoConfig.contains("losers_command_rewards:")) {
						  List<String> lista = new ArrayList<String>();
						  lista.add("msg %player% &aYou've lost! Here, take this compensation reward.");
						  lista.add("paintball givecoins %player% %random_2*kills-6*kills%");
						  getConfig().set("losers_command_rewards", lista);
						  lista = new ArrayList<String>();
						  lista.add("msg %player% &aIt's a tie! Here, take this reward.");
						  lista.add("paintball givecoins %player% %random_2*kills-6*kills%");
						  getConfig().set("tie_command_rewards", lista);
						  saveConfig();
					  }
				  }catch(IOException e){
					  e.printStackTrace();
				  }
			  }
}
