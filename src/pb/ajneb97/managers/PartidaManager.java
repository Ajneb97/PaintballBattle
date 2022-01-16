package pb.ajneb97.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import pb.ajneb97.PaintballBattle;
import pb.ajneb97.api.Hat;
import pb.ajneb97.api.PaintballAPI;
import pb.ajneb97.api.Perk;
import pb.ajneb97.database.JugadorDatos;
import pb.ajneb97.database.MySQL;
import pb.ajneb97.juego.Equipo;
import pb.ajneb97.juego.EstadoPartida;
import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.juego.Partida;
import pb.ajneb97.lib.titleapi.TitleAPI;
import pb.ajneb97.utils.UtilidadesItems;
import pb.ajneb97.utils.UtilidadesOtros;

public class PartidaManager {

	@SuppressWarnings("deprecation")
	public static void jugadorEntra(Partida partida,Player jugador,PaintballBattle plugin) {
		JugadorPaintball jugadorPaintball = new JugadorPaintball(jugador);
		FileConfiguration messages = plugin.getMessages();
		partida.agregarJugador(jugadorPaintball);
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		for(int i=0;i<jugadores.size();i++) {
			jugadores.get(i).getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("playerJoin").replace("%player%", jugador.getName())
					.replace("%current_players%", partida.getCantidadActualJugadores()+"").replace("%max_players%", partida.getCantidadMaximaJugadores()+"")));
		}
		
		jugador.getInventory().clear();
		jugador.getEquipment().clear();
		jugador.getEquipment().setArmorContents(null);
		jugador.updateInventory();
		
		jugador.setGameMode(GameMode.SURVIVAL);
		jugador.setExp(0);
		jugador.setLevel(0);
		jugador.setFoodLevel(20);
		jugador.setMaxHealth(20);
		jugador.setHealth(20);
		jugador.setFlying(false);
		jugador.setAllowFlight(false);
		for(PotionEffect p : jugador.getActivePotionEffects()) {
			jugador.removePotionEffect(p.getType());
		}
		
		jugador.teleport(partida.getLobby());
		
		FileConfiguration config = plugin.getConfig();
		if(config.getString("leave_item_enabled").equals("true")) {
			ItemStack item = UtilidadesItems.crearItem(config, "leave_item");
			jugador.getInventory().setItem(8, item);
		}
		if(config.getString("hats_item_enabled").equals("true")) {
			ItemStack item = UtilidadesItems.crearItem(config, "hats_item");
			jugador.getInventory().setItem(7, item);
		}
		if(config.getString("choose_team_system").equals("true")) {
			ItemStack item = UtilidadesItems.crearItem(config, "teams."+partida.getTeam1().getTipo());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', messages.getString("teamChoose").replace("%team%", config.getString("teams."+partida.getTeam1().getTipo()+".name"))));
			item.setItemMeta(meta);
			jugador.getInventory().setItem(0, item);
			item = UtilidadesItems.crearItem(config, "teams."+partida.getTeam2().getTipo());
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', messages.getString("teamChoose").replace("%team%", config.getString("teams."+partida.getTeam2().getTipo()+".name"))));
			item.setItemMeta(meta);
			jugador.getInventory().setItem(1, item);
		}
		
		if(partida.getCantidadActualJugadores() >= partida.getCantidadMinimaJugadores() 
				&& partida.getEstado().equals(EstadoPartida.ESPERANDO)) {
			cooldownIniciarPartida(partida,plugin);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void jugadorSale(Partida partida,Player jugador,boolean finalizaPartida,
			PaintballBattle plugin,boolean cerrandoServer) {
		JugadorPaintball jugadorPaintball = partida.getJugador(jugador.getName());
		FileConfiguration messages = plugin.getMessages();
		ItemStack[] inventarioGuardado = jugadorPaintball.getGuardados().getInventarioGuardado();
		ItemStack[] equipamientoGuardado = jugadorPaintball.getGuardados().getEquipamientoGuardado();
		GameMode gamemodeGuardado = jugadorPaintball.getGuardados().getGamemodeGuardado();	
		float xpGuardada = jugadorPaintball.getGuardados().getXPGuardada();
		int levelGuardado = jugadorPaintball.getGuardados().getLevelGuardado();
		int hambreGuardada = jugadorPaintball.getGuardados().getHambreGuardada();
		double vidaGuardada = jugadorPaintball.getGuardados().getVidaGuardada();
		double maxVidaGuardada = jugadorPaintball.getGuardados().getMaxVidaGuardada();
		boolean allowFligth = jugadorPaintball.getGuardados().isAllowFlight();
		boolean isFlying = jugadorPaintball.getGuardados().isFlying();

		partida.removerJugador(jugador.getName());
		
		if(!finalizaPartida) {
			ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
			for(int i=0;i<jugadores.size();i++) {
				jugadores.get(i).getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("playerLeave").replace("%player%", jugador.getName())
						.replace("%current_players%", partida.getCantidadActualJugadores()+"").replace("%max_players%", partida.getCantidadMaximaJugadores()+"")));
			}
		}
		
		FileConfiguration config = plugin.getConfig();
		double x = Double.valueOf(config.getString("MainLobby.x"));
		double y = Double.valueOf(config.getString("MainLobby.y"));
		double z = Double.valueOf(config.getString("MainLobby.z"));
		String world = config.getString("MainLobby.world");
		float yaw = Float.valueOf(config.getString("MainLobby.yaw"));
		float pitch = Float.valueOf(config.getString("MainLobby.pitch"));
		Location mainLobby = new Location(Bukkit.getWorld(world),x,y,z,yaw,pitch);
		jugador.teleport(mainLobby);
		
		jugador.getInventory().setContents(inventarioGuardado);
		jugador.getEquipment().setArmorContents(equipamientoGuardado);
		jugador.setGameMode(gamemodeGuardado);
		jugador.setLevel(levelGuardado);
		jugador.setExp(xpGuardada);
		jugador.setFoodLevel(hambreGuardada);
		jugador.setMaxHealth(maxVidaGuardada);
		jugador.setHealth(vidaGuardada);
		for(PotionEffect p : jugador.getActivePotionEffects()) {
			jugador.removePotionEffect(p.getType());
		}
		jugador.updateInventory();

		jugador.setAllowFlight(allowFligth);
		jugador.setFlying(isFlying);
		
		if(!cerrandoServer) {
			if(partida.getCantidadActualJugadores() < partida.getCantidadMinimaJugadores() 
					&& partida.getEstado().equals(EstadoPartida.COMENZANDO)){
				partida.setEstado(EstadoPartida.ESPERANDO);
			}else if(partida.getCantidadActualJugadores() <= 1 && (partida.getEstado().equals(EstadoPartida.JUGANDO))) {
				//fase finalizacion
				PartidaManager.iniciarFaseFinalizacion(partida, plugin);
			}else if((partida.getTeam1().getCantidadJugadores() == 0 || partida.getTeam2().getCantidadJugadores() == 0) && partida.getEstado().equals(EstadoPartida.JUGANDO)) {
				//fase finalizacion
				PartidaManager.iniciarFaseFinalizacion(partida, plugin);
			}
		}
	}
	
	public static void cooldownIniciarPartida(Partida partida,PaintballBattle plugin) {
		partida.setEstado(EstadoPartida.COMENZANDO);
		FileConfiguration config = plugin.getConfig();
		FileConfiguration messages = plugin.getMessages();
		int time = Integer.valueOf(config.getString("arena_starting_cooldown"));
		
		CooldownManager cooldown = new CooldownManager(plugin);
		cooldown.cooldownComenzarJuego(partida,time);
		
		String prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"))+" ";
		
		if(config.getString("broadcast_starting_arena.enabled").equals("true")) {
			List<String> worlds = config.getStringList("broadcast_starting_arena.worlds");
			for(String world : worlds) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.getWorld().getName().equals(world)) {
						player.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaStartingBroadcast")
								.replace("%arena%", partida.getNombre())));
					}
				}
			}
		}
	}
	
	public static void iniciarPartida(Partida partida,PaintballBattle plugin) {
		partida.setEstado(EstadoPartida.JUGANDO);
		FileConfiguration messages = plugin.getMessages();
		FileConfiguration config = plugin.getConfig();
		//String prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"))+" ";
		
		if(plugin.getConfig().getString("choose_team_system").equals("true")) {
			setTeams(partida);
		}else {
			setTeamsAleatorios(partida);
		}

		darItems(partida,plugin.getConfig(),plugin.getShop(),plugin.getMessages());
		teletransportarJugadores(partida);
		setVidas(partida,plugin.getShop());
		
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		String[] separados = config.getString("startGameSound").split(";");
		Sound sound = null;
		float volume = 0;
		float pitch = 0;
		try {
			sound = Sound.valueOf(separados[0]);
			volume = Float.valueOf(separados[1]);
			pitch = Float.valueOf(separados[2]);
		}catch(Exception ex) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PaintballBattle.prefix+"&7Sound Name: &c"+separados[0]+" &7is not valid."));
			sound = null;
		}
		for(int i=0;i<jugadores.size();i++) {
			String nombreTeam = partida.getEquipoJugador(jugadores.get(i).getJugador().getName()).getTipo();
			jugadores.get(i).getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("gameStarted")));
			jugadores.get(i).getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("teamInformation").replace("%team%", plugin.getConfig().getString("teams."+nombreTeam+".name"))));
			jugadores.get(i).getJugador().closeInventory();
			if(sound != null) {
				jugadores.get(i).getJugador().playSound(jugadores.get(i).getJugador().getLocation(), sound, volume, pitch);
			}
		}
		
		CooldownManager cooldown = new CooldownManager(plugin);
		cooldown.cooldownJuego(partida);
	}
	
	

	public static void setVidas(Partida partida,FileConfiguration shop) {
		partida.getTeam1().setVidas(partida.getVidasIniciales());
		partida.getTeam2().setVidas(partida.getVidasIniciales());
		
		ArrayList<JugadorPaintball> jugadoresTeam1 = partida.getTeam1().getJugadores();
		for(JugadorPaintball j : jugadoresTeam1) {
			//comprobar perk extralives
			int nivelExtraLives = PaintballAPI.getPerkLevel(j.getJugador(), "extra_lives");
			if(nivelExtraLives != 0) {
				String linea = shop.getStringList("perks_upgrades.extra_lives").get(nivelExtraLives-1);
				String[] sep = linea.split(";");
				int cantidad = Integer.valueOf(sep[0]);
				partida.getTeam1().aumentarVidas(cantidad);
			}
		}
		ArrayList<JugadorPaintball> jugadoresTeam2 = partida.getTeam2().getJugadores();
		for(JugadorPaintball j : jugadoresTeam2) {
			//comprobar perk extralives
			int nivelExtraLives = PaintballAPI.getPerkLevel(j.getJugador(), "extra_lives");
			if(nivelExtraLives != 0) {
				String linea = shop.getStringList("perks_upgrades.extra_lives").get(nivelExtraLives-1);
				String[] sep = linea.split(";");
				int cantidad = Integer.valueOf(sep[0]);
				partida.getTeam2().aumentarVidas(cantidad);
			}
		}
	}
	
	public static void killstreakInstantanea(String key,Player jugador,Partida partida,PaintballBattle plugin) {
		FileConfiguration config = plugin.getConfig();
		if(key.equalsIgnoreCase("3_lives")) {
			Equipo equipo = partida.getEquipoJugador(jugador.getName());
			equipo.aumentarVidas(3);
		}else if(key.equalsIgnoreCase("teleport")) {
			JugadorPaintball j = partida.getJugador(jugador.getName());
			if(j.getDeathLocation() != null) {
				j.getJugador().teleport(j.getDeathLocation());
			}else {
				Equipo equipo = partida.getEquipoJugador(jugador.getName());
				j.getJugador().teleport(equipo.getSpawn());
			}
		}else if(key.equalsIgnoreCase("more_snowballs")) {
			JugadorPaintball j = partida.getJugador(jugador.getName());
			int snowballs = Integer.valueOf(config.getString("killstreaks_items."+key+".snowballs"));
			ItemStack item = null;
			if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
					|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
				if(j.getSelectedHat().equals("chicken_hat")) {
					item = new ItemStack(Material.EGG,1);
				}else {
					item = new ItemStack(Material.SNOWBALL,1);
				}
				
			}else {
				if(j.getSelectedHat().equals("chicken_hat")) {
					item = new ItemStack(Material.EGG,1);
				}else {
					item = new ItemStack(Material.valueOf("SNOW_BALL"),1);
				}
				
			}
			for(int i=0;i<snowballs;i++) {
				jugador.getInventory().addItem(item);
			}
		}else if(key.equalsIgnoreCase("lightning")) {
			JugadorPaintball jugadorAtacante = partida.getJugador(jugador.getName());
			int radio = Integer.valueOf(config.getString("killstreaks_items."+key+".radius"));
			Collection<Entity> entidades = jugador.getWorld().getNearbyEntities(jugador.getLocation(), radio, radio, radio);
			for(Entity e : entidades) {
				if(e != null && e.getType().equals(EntityType.PLAYER)) {
					Player player = (Player) e;
					JugadorPaintball jugadorDañado = partida.getJugador(player.getName());
					if(jugadorDañado != null) {
						PartidaManager.muereJugador(partida, jugadorAtacante, jugadorDañado, plugin, true, false);
					}
				}
			}
		}else if(key.equalsIgnoreCase("nuke")) {
			partida.setEnNuke(true);
			JugadorPaintball jugadorAtacante = partida.getJugador(jugador.getName());
			CooldownKillstreaks c = new CooldownKillstreaks(plugin);
			String[] separados1 = config.getString("killstreaks_items."+key+".activateSound").split(";");
			String[] separados2 = config.getString("killstreaks_items."+key+".finalSound").split(";");
			c.cooldownNuke(jugadorAtacante, partida, separados1, separados2);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setTeamsAleatorios(Partida partida) {
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		ArrayList<JugadorPaintball> jugadoresCopia = (ArrayList<JugadorPaintball>) partida.getJugadores().clone();
		//Si son 4 se seleccionan 2, Si son 5 tambien 2, Si son 6, 3, Si son 7, tambien 3
		Random r = new Random();
		int num = jugadores.size()/2;
		for(int i=0;i<num;i++) {
			int pos = r.nextInt(jugadoresCopia.size());
			JugadorPaintball jugadorSelect = jugadoresCopia.get(pos);
			jugadoresCopia.remove(pos);
			
			partida.repartirJugadorTeam2(jugadorSelect);
		}
	}
	
	private static void setTeams(Partida partida) {
		//Falta comprobar lo siguiente:
		//Si 2 usuarios seleccionan team y uno se va, los 2 usuarios estaran en el mismo team al
		//iniciar la partida y seran solo ellos 2.
		
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		for(JugadorPaintball j : jugadores) {
			partida.getEquipoJugador(j.getJugador().getName()).removerJugador(j.getJugador().getName());
			String preferenciaTeam = j.getPreferenciaTeam();
			if(preferenciaTeam == null) {
				if(partida.puedeSeleccionarEquipo(partida.getTeam1().getTipo())) {
					j.setPreferenciaTeam(partida.getTeam1().getTipo());
				}else {
					j.setPreferenciaTeam(partida.getTeam2().getTipo());
				}
			}
			preferenciaTeam = j.getPreferenciaTeam();
			if(preferenciaTeam.equals(partida.getTeam2().getTipo())) {
				partida.getTeam2().agregarJugador(j);
			}else {
				partida.getTeam1().agregarJugador(j);
			}
		}
		
		//Balanceo final
		Equipo equipo1 = partida.getTeam1();
		Equipo equipo2 = partida.getTeam2();
		for(JugadorPaintball j : jugadores) {
			Equipo equipo = partida.getEquipoJugador(j.getJugador().getName());
			if(equipo1.getCantidadJugadores() > equipo2.getCantidadJugadores()+1) {
				if(equipo.getTipo().equals(equipo1.getTipo())) {
					//Mover al jugador del equipo1 al equipo2
					equipo1.removerJugador(j.getJugador().getName());
					equipo2.agregarJugador(j);
				}
			}else if(equipo2.getCantidadJugadores() > equipo1.getCantidadJugadores()+1) {
				if(equipo.getTipo().equals(equipo2.getTipo())) {
					//Mover al jugador del equipo2 al equipo1
					equipo2.removerJugador(j.getJugador().getName());
					equipo1.agregarJugador(j);
				}
			}
		}
	}
	
	public static void darItems(Partida partida,FileConfiguration config,FileConfiguration shop,FileConfiguration messages) {
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		for(JugadorPaintball j : jugadores) {
			Player p = j.getJugador();
			p.getInventory().setItem(8, null);
			
			Equipo equipo = partida.getEquipoJugador(p.getName());
			if(config.contains("teams."+equipo.getTipo())) {
				darEquipamientoJugador(p,Integer.valueOf(config.getString("teams."+equipo.getTipo()+".color")));
			}else {
				darEquipamientoJugador(p,0);
			}
			//comprobar perk initial killcoins
			int nivelInitialKillcoins = PaintballAPI.getPerkLevel(j.getJugador(), "initial_killcoins");
			if(nivelInitialKillcoins != 0) {
				String linea = shop.getStringList("perks_upgrades.initial_killcoins").get(nivelInitialKillcoins-1);
				String[] sep = linea.split(";");
				int cantidad = Integer.valueOf(sep[0]);
				j.agregarCoins(cantidad);
			}
			UtilidadesItems.crearItemKillstreaks(j,config);
			ponerHat(partida,j,config,messages);
			setBolasDeNieve(j,config);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void ponerHat(Partida partida,JugadorPaintball jugador,FileConfiguration config,FileConfiguration messages) {
		ArrayList<Hat> hats = PaintballAPI.getHats(jugador.getJugador());
		for(Hat h : hats) {
			if(h.isSelected()) {
				jugador.setSelectedHat(h.getName());
				ItemStack item = UtilidadesItems.crearItem(config, "hats_items."+h.getName());
				ItemMeta meta = item.getItemMeta();
				meta.setLore(null);
				item.setItemMeta(meta);
				if(config.contains("hats_items."+h.getName()+".skull_id")) {
					String id = config.getString("hats_items."+h.getName()+".skull_id");
					String textura = config.getString("hats_items."+h.getName()+".skull_texture");
					item = UtilidadesItems.getCabeza(item, id, textura);
				}
				jugador.getJugador().getEquipment().setHelmet(item);
				
				if(h.getName().equals("speed_hat")) {
					jugador.getJugador().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,9999999,0,false,false));
				}else if(h.getName().equals("present_hat")) {
					Equipo equipo = partida.getEquipoJugador(jugador.getJugador().getName());
					ArrayList<JugadorPaintball> jugadoresCopy = (ArrayList<JugadorPaintball>) equipo.getJugadores().clone();
					jugadoresCopy.remove(jugador);
					if(!jugadoresCopy.isEmpty()) {
						Random r = new Random();
						int pos = r.nextInt(jugadoresCopy.size());
						String jName = jugadoresCopy.get(pos).getJugador().getName();
						JugadorPaintball j = partida.getJugador(jName);
						j.agregarCoins(3);
						jugador.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("presentHatGive").replace("%player%", j.getJugador().getName())));
						j.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("presentHatReceive").replace("%player%", jugador.getJugador().getName())));
					}	
				}
				return;
			}
		}
	}
	
	public static void darEquipamientoJugador(Player jugador,int color) {
		ItemStack item = new ItemStack(Material.LEATHER_HELMET,1);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(color));
		item.setItemMeta(meta);
		jugador.getInventory().setHelmet(item);
		
		item = new ItemStack(Material.LEATHER_CHESTPLATE,1);
		meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(color));
		item.setItemMeta(meta);
		jugador.getInventory().setChestplate(item);
		
		item = new ItemStack(Material.LEATHER_LEGGINGS,1);
		meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(color));
		item.setItemMeta(meta);
		jugador.getInventory().setLeggings(item);
		
		item = new ItemStack(Material.LEATHER_BOOTS,1);
		meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(color));
		item.setItemMeta(meta);
		jugador.getInventory().setBoots(item);
	}
	
	public static void setBolasDeNieve(JugadorPaintball j,FileConfiguration config) {
		for(int i=0;i<=7;i++) {
			j.getJugador().getInventory().setItem(i, null);
		}
		for(int i=9;i<=35;i++) {
			j.getJugador().getInventory().setItem(i, null);
		}
		int amount = Integer.valueOf(config.getString("initial_snowballs"));
		ItemStack item = null;
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
				|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
			if(j.getSelectedHat().equals("chicken_hat")) {
				item = new ItemStack(Material.EGG,1);
			}else {
				item = new ItemStack(Material.SNOWBALL,1);
			}
		}else {
			if(j.getSelectedHat().equals("chicken_hat")) {
				item = new ItemStack(Material.EGG,1);
			}else {
				item = new ItemStack(Material.valueOf("SNOW_BALL"),1);
			}
		}

		for(int i=0;i<amount;i++) {
			j.getJugador().getInventory().addItem(item);
		}
	}
	
	public static void lanzarFuegos(ArrayList<JugadorPaintball> jugadores) {
		for(JugadorPaintball j : jugadores) {
			Firework fw = (Firework) j.getJugador().getWorld().spawnEntity(j.getJugador().getLocation(), EntityType.FIREWORK);
	        FireworkMeta fwm = fw.getFireworkMeta();
	        Type type = Type.BALL;
	        Color c1 = Color.RED;
	        Color c2 = Color.AQUA;
	        FireworkEffect efecto = FireworkEffect.builder().withColor(c1).withFade(c2).with(type).build();
	        fwm.addEffect(efecto);
	        fwm.setPower(2);
	        fw.setFireworkMeta(fwm);
		}	
	}
	
	public static void teletransportarJugadores(Partida partida) {
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		for(JugadorPaintball j : jugadores) {
			Player p = j.getJugador();
			Equipo equipo = partida.getEquipoJugador(p.getName());
			p.teleport(equipo.getSpawn());
		}
	}
	
	public static void iniciarFaseFinalizacion(Partida partida,PaintballBattle plugin) {
		partida.setEstado(EstadoPartida.TERMINANDO);
		Equipo ganador = partida.getGanador();
		FileConfiguration messages = plugin.getMessages();
		FileConfiguration config = plugin.getConfig();
		
		String nameTeam1 = config.getString("teams."+partida.getTeam1().getTipo()+".name");
		String nameTeam2 = config.getString("teams."+partida.getTeam2().getTipo()+".name");
		
		String status = "";
		if(ganador == null) {
			//empate
			status = messages.getString("gameFinishedTieStatus");
		}else {
			String ganadorTexto = plugin.getConfig().getString("teams."+ganador.getTipo()+".name");
			status = messages.getString("gameFinishedWinnerStatus").replace("%winner_team%", ganadorTexto);
		}	
				
		ArrayList<JugadorPaintball> jugadoresKillsOrd = partida.getJugadoresKills();
		String top1 = "";
		String top2 = "";
		String top3 = "";
		int top1Kills = 0;
		int top2Kills = 0;
		int top3Kills = 0;
		
		if(jugadoresKillsOrd.size() == 2) {
			top1 = jugadoresKillsOrd.get(0).getJugador().getName();
			top1Kills = jugadoresKillsOrd.get(0).getAsesinatos();
			top2 = jugadoresKillsOrd.get(1).getJugador().getName();
			top2Kills = jugadoresKillsOrd.get(1).getAsesinatos();
			top3 = messages.getString("topKillsNone");
		}else if(jugadoresKillsOrd.size() == 1) {
			top1 = jugadoresKillsOrd.get(0).getJugador().getName();
			top1Kills = jugadoresKillsOrd.get(0).getAsesinatos();
			top3 = messages.getString("topKillsNone");
			top2 = messages.getString("topKillsNone");
		}else {
			top1 = jugadoresKillsOrd.get(0).getJugador().getName();
			top1Kills = jugadoresKillsOrd.get(0).getAsesinatos();
			top2 = jugadoresKillsOrd.get(1).getJugador().getName();
			top3 = jugadoresKillsOrd.get(2).getJugador().getName();
			top2Kills = jugadoresKillsOrd.get(1).getAsesinatos();
			top3Kills = jugadoresKillsOrd.get(2).getAsesinatos();
		}
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		List<String> msg = messages.getStringList("gameFinished");
		for(JugadorPaintball j : jugadores) {
			for(int i=0;i<msg.size();i++) {
				j.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', msg.get(i).replace("%status_message%", status).replace("%team1%", nameTeam1)
						.replace("%team2%", nameTeam2).replace("%kills_team1%", partida.getTeam1().getAsesinatosTotales()+"")
						.replace("%kills_team2%", partida.getTeam2().getAsesinatosTotales()+"").replace("%player1%", top1).replace("%player2%", top2)
						.replace("%player3%", top3).replace("%kills_player1%", top1Kills+"").replace("%kills_player2%", top2Kills+"")
						.replace("%kills_player3%", top3Kills+"").replace("%kills_player%", j.getAsesinatos()+"")));
			}
			Equipo equipoJugador = partida.getEquipoJugador(j.getJugador().getName());
			if(MySQL.isEnabled(plugin.getConfig())) {
				int win = 0;
				int lose = 0;
				int tie = 0;
				if(equipoJugador.equals(ganador)) {
					win = 1;
					TitleAPI.sendTitle(j.getJugador(), 10, 40, 10, messages.getString("winnerTitleMessage"), "");
				}else if(ganador == null) {
					tie = 1;
					TitleAPI.sendTitle(j.getJugador(), 10, 40, 10, messages.getString("tieTitleMessage"), "");
				}else {
					lose = 1;
					TitleAPI.sendTitle(j.getJugador(), 10, 40, 10, messages.getString("loserTitleMessage"), "");
				}
				//Aqui se crea/modifica el registro global del jugador
				if(!MySQL.jugadorExiste(plugin, j.getJugador().getName())) {
					MySQL.crearJugadorPartidaAsync(plugin, j.getJugador().getUniqueId().toString(), j.getJugador().getName(), "", win, tie, lose, j.getAsesinatos(),0, 1);
				}else {
					JugadorDatos player = MySQL.getJugador(plugin, j.getJugador().getName());
					int kills = j.getAsesinatos()+player.getKills();
					int wins = player.getWins()+win;
					int loses = player.getLoses()+lose;
					int ties = player.getTies()+tie;
					MySQL.actualizarJugadorPartidaAsync(plugin, j.getJugador().getUniqueId().toString(), j.getJugador().getName(), wins, loses, ties, kills);
				}				
				//Este registro es el que se crea para datos mensuales y semanales
				MySQL.crearJugadorPartidaAsync(plugin, j.getJugador().getUniqueId().toString(), j.getJugador().getName(), partida.getNombre(), win, tie, lose, j.getAsesinatos(),0,0);	
			}else {
				plugin.registerPlayer(j.getJugador().getUniqueId().toString()+".yml");
				if(plugin.getJugador(j.getJugador().getName()) == null) {
					plugin.agregarJugadorDatos(new JugadorDatos(j.getJugador().getName(),j.getJugador().getUniqueId().toString(),0,0,0,0,0,new ArrayList<Perk>(),new ArrayList<Hat>()));
				}
				JugadorDatos jugador = plugin.getJugador(j.getJugador().getName());
				if(partida.getEquipoJugador(j.getJugador().getName()).equals(ganador)) {
					jugador.aumentarWins();
					TitleAPI.sendTitle(j.getJugador(), 10, 40, 10, messages.getString("winnerTitleMessage"), "");
				}else if(ganador == null) {
					jugador.aumentarTies();
					TitleAPI.sendTitle(j.getJugador(), 10, 40, 10, messages.getString("tieTitleMessage"), "");
				}else {
					jugador.aumentarLoses();
					TitleAPI.sendTitle(j.getJugador(), 10, 40, 10, messages.getString("loserTitleMessage"), "");
				}
				
				jugador.aumentarKills(j.getAsesinatos());
			}
			j.getJugador().closeInventory();
			j.getJugador().getInventory().clear();
			
			
			if(config.getString("leave_item_enabled").equals("true")) {
				ItemStack item = UtilidadesItems.crearItem(config, "leave_item");
				j.getJugador().getInventory().setItem(8, item);
			}
			if(config.getString("play_again_item_enabled").equals("true")) {
				ItemStack item = UtilidadesItems.crearItem(config, "play_again_item");
				j.getJugador().getInventory().setItem(7, item);
			}
			
			if(config.getString("rewards_executed_after_teleport").equals("false")) {
				if(ganador != null) {
					if(ganador.getTipo().equals(equipoJugador.getTipo())) {
						List<String> commands = config.getStringList("winners_command_rewards");
						ejecutarComandosRewards(commands,j);
					}else {
						List<String> commands = config.getStringList("losers_command_rewards");
						ejecutarComandosRewards(commands,j);
					}
				}else {
					List<String> commands = config.getStringList("tie_command_rewards");
					ejecutarComandosRewards(commands,j);
				}
			}
		}
		
		int time = Integer.valueOf(config.getString("arena_ending_phase_cooldown"));
		CooldownManager c = new CooldownManager(plugin);
		c.cooldownFaseFinalizacion(partida,time,ganador);
	}
	
	public static void ejecutarComandosRewards(List<String> commands,JugadorPaintball j) {
		CommandSender console = Bukkit.getServer().getConsoleSender();
		for(int i=0;i<commands.size();i++){	
			if(commands.get(i).startsWith("msg %player%")) {
				String mensaje = commands.get(i).replace("msg %player% ", "");
				j.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', mensaje));
			}else {
				String comandoAEnviar = commands.get(i).replaceAll("%player%", j.getJugador().getName()); 
				if(comandoAEnviar.contains("%random")) {
					int pos = comandoAEnviar.indexOf("%random");
					int nextPos = comandoAEnviar.indexOf("%", pos+1);
					String variableCompleta = comandoAEnviar.substring(pos,nextPos+1);
					String variable = variableCompleta.replace("%random_", "").replace("%", "");
					String[] sep = variable.split("-");
					int cantidadMinima = 0;
					int cantidadMaxima = 0;
					
				    try {
				    	cantidadMinima = (int) UtilidadesOtros.eval(sep[0].replace("kills", j.getAsesinatos()+""));
				    	cantidadMaxima = (int) UtilidadesOtros.eval(sep[1].replace("kills", j.getAsesinatos()+""));
					} catch (Exception e) {
						
					}
				    int num = UtilidadesOtros.getNumeroAleatorio(cantidadMinima, cantidadMaxima);
				    comandoAEnviar = comandoAEnviar.replace(variableCompleta, num+"");
				}
				Bukkit.dispatchCommand(console, comandoAEnviar);	
			}
		}
	}
	
	public static void finalizarPartida(Partida partida,PaintballBattle plugin,boolean cerrandoServer,Equipo ganadorEquipo) {
		FileConfiguration config = plugin.getConfig();
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		for(JugadorPaintball j : jugadores) {	
			String tipoFin = "";
			if(ganadorEquipo != null) {
				Equipo equipoJugador = partida.getEquipoJugador(j.getJugador().getName());
				if(ganadorEquipo.getTipo().equals(equipoJugador.getTipo())) {
					tipoFin = "ganador";
				}else {
					tipoFin = "perdedor";
				}
			}else {
				tipoFin = "empate";
			}
			jugadorSale(partida, j.getJugador(),true,plugin,cerrandoServer);
			if(config.getString("rewards_executed_after_teleport").equals("true") && !cerrandoServer) {
				if(tipoFin.equals("ganador")) {
					List<String> commands = config.getStringList("winners_command_rewards");
					ejecutarComandosRewards(commands,j);
				}else if(tipoFin.equals("perdedor")) {
					List<String> commands = config.getStringList("losers_command_rewards");
					ejecutarComandosRewards(commands,j);
				}else {
					List<String> commands = config.getStringList("tie_command_rewards");
					ejecutarComandosRewards(commands,j);
				}
			}
		}
		partida.getTeam1().setVidas(0);
		partida.getTeam2().setVidas(0);
		partida.setEnNuke(false);
		partida.modificarTeams(config);
		
		partida.setEstado(EstadoPartida.ESPERANDO);
	}
	
	public static void muereJugador(Partida partida,JugadorPaintball jugadorAtacante,final JugadorPaintball jugadorDañado,PaintballBattle plugin,boolean lightning,boolean nuke) {
		if(jugadorDañado.haSidoAsesinadoRecientemente()) {
			return;
		}
		if(jugadorDañado.getSelectedHat().equals("guardian_hat") && jugadorDañado.isEfectoHatActivado()) {
			return;
		}
		if(jugadorDañado.getSelectedHat().equals("protector_hat")) {
			Random r = new Random();
			int num = r.nextInt(100);
			if(num >= 80) {
				return;
			}
		}
		
		Equipo equipoDañado = partida.getEquipoJugador(jugadorDañado.getJugador().getName());
		Equipo equipoAtacante = partida.getEquipoJugador(jugadorAtacante.getJugador().getName());
		if(equipoDañado.equals(equipoAtacante)) {
			return;
		}
		
		if(lightning) {
			jugadorDañado.getJugador().getWorld().strikeLightningEffect(jugadorDañado.getJugador().getLocation());
		}
		FileConfiguration messages = plugin.getMessages();
		FileConfiguration config = plugin.getConfig();
		jugadorDañado.aumentarMuertes();
		jugadorDañado.setDeathLocation(jugadorDañado.getJugador().getLocation().clone());
		jugadorDañado.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("killedBy").replace("%player%", jugadorAtacante.getJugador().getName())));
		String[] separados = config.getString("killedBySound").split(";");
		try {
			Sound sound = Sound.valueOf(separados[0]);
			jugadorDañado.getJugador().playSound(jugadorDañado.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
		}catch(Exception ex) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PaintballBattle.prefix+"&7Sound Name: &c"+separados[0]+" &7is not valid."));
		}
		jugadorDañado.setAsesinadoRecientemente(true);
		jugadorDañado.setLastKilledBy(jugadorAtacante.getJugador().getName());
		equipoDañado.disminuirVidas(1);
		
		Equipo equipo = partida.getEquipoJugador(jugadorDañado.getJugador().getName());
		if(jugadorDañado.getSelectedHat().equals("explosive_hat")) {
			Random r = new Random();
			int num = r.nextInt(100);
			if(num >= 80) {
				if(Bukkit.getVersion().contains("1.8")) {
					jugadorDañado.getJugador().getWorld().playEffect(jugadorDañado.getJugador().getLocation(), Effect.valueOf("EXPLOSION_LARGE"), 2);
				}else {
					jugadorDañado.getJugador().getWorld().spawnParticle(Particle.EXPLOSION_LARGE,jugadorDañado.getJugador().getLocation(),2);
				}
				separados = config.getString("explosiveHatSound").split(";");
				try {
					Sound sound = Sound.valueOf(separados[0]);
					jugadorDañado.getJugador().getWorld().playSound(jugadorDañado.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
				}catch(Exception ex) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PaintballBattle.prefix+"&7Sound Name: &c"+separados[0]+" &7is not valid."));
				}
				Collection<Entity> entidades = jugadorDañado.getJugador().getWorld().getNearbyEntities(jugadorDañado.getJugador().getLocation(), 5, 5, 5);
				for(Entity e : entidades) {
					if(e != null && e.getType().equals(EntityType.PLAYER)) {
						Player player = (Player) e;
						JugadorPaintball jugadorDañado2 = partida.getJugador(player.getName());
						if(jugadorDañado2 != null) {
							PartidaManager.muereJugador(partida, jugadorDañado, jugadorDañado2, plugin, false, false);
						}
					}
				}
			}
		}
		jugadorDañado.getJugador().teleport(equipo.getSpawn());
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
				|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
			if(jugadorDañado.getSelectedHat().equals("chicken_hat")) {
				jugadorDañado.getJugador().getInventory().removeItem(new ItemStack(Material.EGG));
			}else {
				jugadorDañado.getJugador().getInventory().removeItem(new ItemStack(Material.SNOWBALL));
			}
		}else {
			if(jugadorDañado.getSelectedHat().equals("chicken_hat")) {
				jugadorDañado.getJugador().getInventory().removeItem(new ItemStack(Material.EGG));
			}else {
				jugadorDañado.getJugador().getInventory().removeItem(new ItemStack(Material.valueOf("SNOW_BALL")));
			}
		}
		PartidaManager.setBolasDeNieve(jugadorDañado,config);
			
		jugadorAtacante.aumentarAsesinatos();
		int cantidadCoinsGanados = UtilidadesOtros.coinsGanados(jugadorAtacante.getJugador(), config);
		int nivelExtraKillCoins = PaintballAPI.getPerkLevel(jugadorAtacante.getJugador(), "extra_killcoins");
		if(nivelExtraKillCoins != 0) {
			String linea = plugin.getShop().getStringList("perks_upgrades.extra_killcoins").get(nivelExtraKillCoins-1);
			String[] sep = linea.split(";");
			int cantidad = Integer.valueOf(sep[0]);
			cantidadCoinsGanados = cantidadCoinsGanados+cantidad;
		}
		String lastKilledBy = jugadorAtacante.getLastKilledBy();
		if(lastKilledBy != null && lastKilledBy.equals(jugadorDañado.getJugador().getName())) {
			cantidadCoinsGanados = cantidadCoinsGanados+1;
		}
		jugadorAtacante.agregarCoins(cantidadCoinsGanados);
		UtilidadesItems.crearItemKillstreaks(jugadorAtacante,config);
		
		if(nuke) {
			String equipoAtacanteName = config.getString("teams."+equipoAtacante.getTipo()+".name");
			String equipoDañadoName = config.getString("teams."+equipoDañado.getTipo()+".name");
			for(JugadorPaintball j : partida.getJugadores()) {
				if(!j.getJugador().getName().equals(jugadorAtacante.getJugador().getName())) {
					j.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("nukeKillMessage").replace("%team_player1%", equipoDañadoName)
							.replace("%player1%", jugadorDañado.getJugador().getName()).replace("%team_player2%", equipoAtacanteName)
							.replace("%player2%", jugadorAtacante.getJugador().getName())));
				}	
			}
		}
		jugadorAtacante.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("kill").replace("%player%", jugadorDañado.getJugador().getName())));
		if(!nuke) {
			separados = config.getString("killSound").split(";");
			try {
				Sound sound = Sound.valueOf(separados[0]);
				jugadorAtacante.getJugador().playSound(jugadorAtacante.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
			}catch(Exception ex) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PaintballBattle.prefix+"&7Sound Name: &c"+separados[0]+" &7is not valid."));
			}
		}
		
		
		int snowballs = Integer.valueOf(config.getString("snowballs_per_kill"));
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
				|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
			if(jugadorAtacante.getSelectedHat().equals("chicken_hat")) {
				jugadorAtacante.getJugador().getInventory().addItem(new ItemStack(Material.EGG,snowballs));
			}else {
				jugadorAtacante.getJugador().getInventory().addItem(new ItemStack(Material.SNOWBALL,snowballs));
			}
			
		}else {
			if(jugadorAtacante.getSelectedHat().equals("chicken_hat")) {
				jugadorAtacante.getJugador().getInventory().addItem(new ItemStack(Material.EGG,snowballs));
			}else {
				jugadorAtacante.getJugador().getInventory().addItem(new ItemStack(Material.valueOf("SNOW_BALL"),snowballs));
			}
			
		}
		
		if(equipoDañado.getVidas() <= 0) {
			//terminar partida
			PartidaManager.iniciarFaseFinalizacion(partida, plugin);
			return;
		}
			
		int invulnerability = Integer.valueOf(config.getString("respawn_invulnerability"));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				jugadorDañado.setAsesinadoRecientemente(false);
			}
		}, invulnerability*20L);
	}
	
	public static Partida getPartidaDisponible(PaintballBattle plugin) {
		ArrayList<Partida> partidas = plugin.getPartidas();
		ArrayList<Partida> disponibles = new ArrayList<Partida>();
		for(int i=0;i<partidas.size();i++) {
			if(partidas.get(i).getEstado().equals(EstadoPartida.ESPERANDO) || 
					partidas.get(i).getEstado().equals(EstadoPartida.COMENZANDO)) {
				if(!partidas.get(i).estaLlena()) {
					disponibles.add(partidas.get(i));
				}
			}
		}
		
		if(disponibles.isEmpty()) {
			return null;
		}
		
		//Ordenar
		for(int i=0;i<disponibles.size();i++) {
			for(int c=i+1;c<disponibles.size();c++) {
				if(disponibles.get(i).getCantidadActualJugadores() < disponibles.get(c).getCantidadActualJugadores()) {
					Partida p = disponibles.get(i);
					disponibles.set(i, disponibles.get(c));
					disponibles.set(c, p);
				}
			}
		}
		return disponibles.get(0);
	}
}
