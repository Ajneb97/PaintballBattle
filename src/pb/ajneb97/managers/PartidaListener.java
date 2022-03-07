package pb.ajneb97.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import me.clip.placeholderapi.PlaceholderAPI;
import pb.ajneb97.PaintballBattle;
import pb.ajneb97.juego.Equipo;
import pb.ajneb97.juego.EstadoPartida;
import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.juego.Killstreak;
import pb.ajneb97.juego.Partida;
import pb.ajneb97.utils.UtilidadesItems;

public class PartidaListener implements Listener{

	PaintballBattle plugin;
	public PartidaListener(PaintballBattle plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void alSalir(PlayerQuitEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			PartidaManager.jugadorSale(partida, jugador,false,plugin,false);
		}
	}
	
	@EventHandler
	public void clickearItemSalir(PlayerInteractEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(event.getItem() != null) {
					FileConfiguration config = plugin.getConfig();
					ItemStack item = UtilidadesItems.crearItem(config, "leave_item");
					if(event.getItem().isSimilar(item)) {
						event.setCancelled(true);
						PartidaManager.jugadorSale(partida, jugador,false,plugin,false);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void clickearItemHats(PlayerInteractEvent event) {
		final Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(event.getItem() != null) {
					FileConfiguration config = plugin.getConfig();
					ItemStack item = UtilidadesItems.crearItem(config, "hats_item");
					if(event.getItem().isSimilar(item)) {
						event.setCancelled(true);
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								jugador.updateInventory();
								jugador.getEquipment().setHelmet(null);
								InventarioHats.crearInventario(jugador, plugin);
							}
						},2L);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void clickearItemPlayAgain(PlayerInteractEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(event.getItem() != null) {
					FileConfiguration config = plugin.getConfig();
					ItemStack item = UtilidadesItems.crearItem(config, "play_again_item");
					if(event.getItem().isSimilar(item)) {
						event.setCancelled(true);
						Partida partidaNueva = PartidaManager.getPartidaDisponible(plugin);
						if(partidaNueva == null) {
							FileConfiguration messages = plugin.getMessages();
							jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("noArenasAvailable")));
						}else {
							PartidaManager.jugadorSale(partida, jugador, true, plugin, false);
							PartidaManager.jugadorEntra(partidaNueva, jugador, plugin);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void clickearItemSelectorEquipo(PlayerInteractEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(event.getItem() != null) {
					FileConfiguration config = plugin.getConfig();
					FileConfiguration messages = plugin.getMessages();
					if(config.getString("choose_team_system").equals("true")) {
						ItemStack team1 = UtilidadesItems.crearItem(config, "teams."+partida.getTeam1().getTipo());
						ItemMeta meta = team1.getItemMeta();
						meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', messages.getString("teamChoose").replace("%team%", config.getString("teams."+partida.getTeam1().getTipo()+".name"))));
						team1.setItemMeta(meta);
						ItemStack team2 = UtilidadesItems.crearItem(config, "teams."+partida.getTeam2().getTipo());
						meta = team2.getItemMeta();
						meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', messages.getString("teamChoose").replace("%team%", config.getString("teams."+partida.getTeam2().getTipo()+".name"))));
						team2.setItemMeta(meta);
						//String prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"))+" ";
						if(event.getItem().isSimilar(team1)) {
							event.setCancelled(true);
							jugador.updateInventory();
							JugadorPaintball j = partida.getJugador(jugador.getName());
							if(j.getPreferenciaTeam() != null && j.getPreferenciaTeam().equals(partida.getTeam1().getTipo())) {
								jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("errorTeamAlreadySelected")));
								return;
							}
							if(partida.puedeSeleccionarEquipo(partida.getTeam1().getTipo())) {
								j.setPreferenciaTeam(partida.getTeam1().getTipo());
								jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("teamSelected").replace("%team%", config.getString("teams."+partida.getTeam1().getTipo()+".name"))));
							}else {
								jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("errorTeamSelected")));
							}
							
						}else if(event.getItem().isSimilar(team2)) {
							event.setCancelled(true);
							jugador.updateInventory();
							JugadorPaintball j = partida.getJugador(jugador.getName());
							if(j.getPreferenciaTeam() != null && j.getPreferenciaTeam().equals(partida.getTeam2().getTipo())) {
								jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("errorTeamAlreadySelected")));
								return;
							}
							if(partida.puedeSeleccionarEquipo(partida.getTeam2().getTipo())) {
								j.setPreferenciaTeam(partida.getTeam2().getTipo());
								jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("teamSelected").replace("%team%", config.getString("teams."+partida.getTeam2().getTipo()+".name"))));
							}else {
								jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("errorTeamSelected")));
							}
							
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void clickearItemKillstreak(PlayerInteractEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(event.getItem() != null && event.getItem().hasItemMeta()) {
					FileConfiguration config = plugin.getConfig();
					if(jugador.getInventory().getHeldItemSlot() == 8 && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
						if(config.getString("killstreaks_item_enabled").equals("true")) {
							if(partida.getEstado().equals(EstadoPartida.JUGANDO)) {
								event.setCancelled(true);
								Inventory inv = Bukkit.createInventory(null, 18, ChatColor.translateAlternateColorCodes('&', config.getString("killstreaks_inventory_title")));
								jugador.openInventory(inv);
								InventarioKillstreaks i = new InventarioKillstreaks(plugin);
								i.actualizarInventario(jugador, partida);
							}
						}
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void alShiftear(PlayerToggleSneakEvent event) {
		if(event.isSneaking()) {
			Player jugador = event.getPlayer();
			Partida partida = plugin.getPartidaJugador(jugador.getName());
			if(partida != null && partida.getEstado().equals(EstadoPartida.JUGANDO)) {
				FileConfiguration messages = plugin.getMessages();
				JugadorPaintball j = partida.getJugador(jugador.getName());
				String hat = j.getSelectedHat();
				if(hat.equals("guardian_hat") || hat.equals("jump_hat")) {
					if(!j.isEfectoHatEnCooldown()) {
						FileConfiguration config = plugin.getConfig();
						int duration = Integer.valueOf(config.getString("hats_items."+hat+".duration"));
						int cooldown = Integer.valueOf(config.getString("hats_items."+hat+".cooldown"));
						if(hat.equals("jump_hat")) {
							jugador.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,20*duration,1,false,false));
						}else if(hat.equals("guardian_hat")) {
							jugador.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20*duration,2,false,false));
							CooldownHats c = new CooldownHats(plugin);
							c.durationHat(j, partida, duration);
						}
						j.setEfectoHatActivado(true);
						j.setEfectoHatEnCooldown(true);
						j.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("hatAbilityActivated")));
						String[] separados = config.getString("hatAbilityActivatedSound").split(";");
						try {
							Sound sound = Sound.valueOf(separados[0]);
							j.getJugador().playSound(j.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
						}catch(Exception ex) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PaintballBattle.prefix+"&7Sound Name: &c"+separados[0]+" &7is not valid."));
						}
						CooldownHats c = new CooldownHats(plugin);
						c.cooldownHat(j, partida, cooldown);
					}else {
						jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("hatCooldownError").replace("%time%", j.getTiempoEfectoHat()+"")));
					}
				}
				
			}
		}
	}
	
	@EventHandler
	public void alUsarComando(PlayerCommandPreprocessEvent event) {
		String comando = event.getMessage().toLowerCase();
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null && !jugador.isOp() && !jugador.hasPermission("paintball.admin")) {
			FileConfiguration config = plugin.getConfig();
			List<String> comandos = config.getStringList("commands_whitelist");
			for(int i=0;i<comandos.size();i++) {
				if(comando.toLowerCase().startsWith(comandos.get(i))) {
					return;
				}
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void romperBloques(BlockBreakEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void dropearItem(PlayerDropItemEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void interactuarInventario(InventoryClickEvent event){
		Player jugador = (Player) event.getWhoClicked();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if((event.getInventory().getType().equals(InventoryType.PLAYER) || 
					event.getInventory().getType().equals(InventoryType.CRAFTING)) 
					&& event.getSlotType() != null && event.getCurrentItem() != null){
				if(!event.getCurrentItem().getType().equals(Material.AIR) && 
						!event.getCurrentItem().getType().name().contains("SNOW")) {
					event.setCancelled(true);
				}	
			}
		}
	}
	
	@EventHandler
	public void ponerBloques(BlockPlaceEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void caerVacio(EntityDamageEvent event) {
		Entity entidad = event.getEntity();
		if(entidad instanceof Player) {
			Player jugador = (Player) entidad;
			Partida partida = plugin.getPartidaJugador(jugador.getName());
			if(partida != null  && partida.estaIniciada()) {
				if(event.getCause().equals(DamageCause.VOID)) {
					Equipo equipo = partida.getEquipoJugador(jugador.getName());
					if(equipo != null) {
						jugador.teleport(equipo.getSpawn());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void craftear(InventoryClickEvent event) {
		Player jugador = (Player) event.getWhoClicked();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if(event.getClickedInventory() != null) {
				if(event.getClickedInventory().getType().equals(InventoryType.CRAFTING)
						&& event.getSlot() == 0 && event.getSlotType() != null && event.getSlotType().equals(SlotType.RESULT)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void romperGranjas(PlayerInteractEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			if(event.getClickedBlock() != null) {
				String name = event.getClickedBlock().getType().name();
			    if(event.getAction() == Action.PHYSICAL && (name.equals("SOIL") || name.equals("FARMLAND"))) {
			    	event.setCancelled(true);
			    }
			}
		}
	}
	
	@EventHandler
	public void alDañar(EntityDamageEvent event) {
		Entity entidad = event.getEntity();
		if(entidad instanceof Player) {
			Player jugador = (Player) entidad;
			Partida partida = plugin.getPartidaJugador(jugador.getName());
			if(partida != null) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void nivelDeComida(FoodLevelChangeEvent event) {
		Player jugador = (Player) event.getEntity();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void alChatear(AsyncPlayerChatEvent event) {
		Player jugador = event.getPlayer();
		if(!event.isCancelled()) {
			Partida partida = plugin.getPartidaJugador(jugador.getName());
			FileConfiguration config = plugin.getConfig();
			if(config.getString("arena_chat_enabled").equals("false")) {
				return;
			}
			if(partida != null) {
				FileConfiguration messages = plugin.getMessages();
				String message = event.getMessage();
				event.setCancelled(true);
				ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
				if(partida.estaIniciada()) {
					String teamName = config.getString("teams."+partida.getEquipoJugador(jugador.getName()).getTipo()+".name");
					for(JugadorPaintball j : jugadores) {
						String msg = ChatColor.translateAlternateColorCodes('&', config.getString("arena_chat_format").replace("%player%", jugador.getName()).replace("%team%", teamName));
						if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
							msg = PlaceholderAPI.setPlaceholders(j.getJugador(), msg.replace("%message%", message));
						}else {
							msg = msg.replace("%message%", message);
						}
						j.getJugador().sendMessage(msg);
					}
				}else {
					for(JugadorPaintball j : jugadores) {
						String msg = ChatColor.translateAlternateColorCodes('&', config.getString("arena_chat_format").replace("%player%", jugador.getName()).replace("%team%", messages.getString("teamInformationNone")));
						if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
							msg = PlaceholderAPI.setPlaceholders(j.getJugador(), msg.replace("%message%", message));
						}else {
							msg = msg.replace("%message%", message);
						}
						j.getJugador().sendMessage(msg);
					}
				}
			}else {
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(plugin.getPartidaJugador(p.getName()) != null) {
						event.getRecipients().remove(p);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void impactoBolaDeNieve(EntityDamageByEntityEvent event) {
		Entity e = event.getDamager();
		if(e instanceof Projectile && (e.getType().equals(EntityType.SNOWBALL) || e.getType().equals(EntityType.EGG))) {
			Projectile proyectil = (Projectile) e;
			ProjectileSource shooter = proyectil.getShooter();
			Entity dañado = event.getEntity();
			if(dañado instanceof Player && shooter instanceof Player) {
				Player jugadorDañado = (Player) dañado;
				Player jugadorAtacante = (Player) shooter;
				Partida partida = plugin.getPartidaJugador(jugadorAtacante.getName());
				if(partida != null) {
					if(partida.getEstado().equals(EstadoPartida.JUGANDO)) {
						
						event.setCancelled(true);
						
						JugadorPaintball j = partida.getJugador(jugadorAtacante.getName());
						JugadorPaintball j2 = partida.getJugador(jugadorDañado.getName());
						
						if(j2 == null || j2.getKillstreak("fury") != null) {
							return;
						}
						
						
						PartidaManager.muereJugador(partida, j, j2, plugin, false, false);
											
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void clickInventarioKillstreak(InventoryClickEvent event){
		FileConfiguration config = plugin.getConfig();
		String pathInventory = ChatColor.translateAlternateColorCodes('&', config.getString("killstreaks_inventory_title"));
		String pathInventoryM = ChatColor.stripColor(pathInventory);
		FileConfiguration messages = plugin.getMessages();
		//String prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"))+" ";
		if(ChatColor.stripColor(event.getView().getTitle()).contains(pathInventoryM)){
			if(event.getCurrentItem() == null){
				event.setCancelled(true);
				return;
			}
			if((event.getSlotType() == null)){
				event.setCancelled(true);
				return;
			}else{
				Player jugador = (Player) event.getWhoClicked();
				event.setCancelled(true);
				if(event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
					Partida partida = plugin.getPartidaJugador(jugador.getName());
					JugadorPaintball j = partida.getJugador(jugador.getName());
					if(partida != null) {
						int slot = event.getSlot();
						for(String key : config.getConfigurationSection("killstreaks_items").getKeys(false)) {
							if(slot == Integer.valueOf(config.getString("killstreaks_items."+key+".slot"))) {
								if(j.getKillstreak(key) != null) {
									jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("killstreakAlreadyActivated")));
									return;
								}
								if(config.contains("killstreaks_items."+key+".permission")) {
									if(!jugador.hasPermission(config.getString("killstreaks_items."+key+".permission"))) {
										jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("killstreaks_items."+key+".permissionError")));
										return;
									}
								}
								
								int cost = Integer.valueOf(config.getString("killstreaks_items."+key+".cost"));
								if(j.getCoins() >= cost) {
									if(key.equalsIgnoreCase("nuke") && partida.isEnNuke()) {
										jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("nukeError")));
										return;
									}
									
									j.disminuirCoins(cost);
									String name = ChatColor.translateAlternateColorCodes('&', config.getString("killstreaks_items."+key+".name"));
									String teamName = config.getString("teams."+partida.getEquipoJugador(jugador.getName()).getTipo()+".name");
									
									for(JugadorPaintball player : partida.getJugadores()) {
										if(!player.getJugador().getName().equals(jugador.getName())) {
											String msg = ChatColor.translateAlternateColorCodes('&', messages.getString("killstreakActivatedPlayer").replace("%player%", jugador.getName()).replace("%team%", teamName)
													.replace("%killstreak%", name));
											player.getJugador().sendMessage(msg);
										}
									}
									
									jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("killstreakActivated").replace("%killstreak%", name)));
									
									
									if(key.equalsIgnoreCase("strong_arm") || key.equalsIgnoreCase("triple_shoot") || key.equalsIgnoreCase("fury")) {
										int duration = Integer.valueOf(config.getString("killstreaks_items."+key+".duration"));
										if(j.getSelectedHat().equals("time_hat")) {
											duration = duration+5;
										}
										Killstreak k = new Killstreak(key,duration);
										j.agregarKillstreak(k);
										CooldownKillstreaks cooldown = new CooldownKillstreaks(plugin);
										cooldown.cooldownKillstreak(j, partida, key, duration);
									}else {
										PartidaManager.killstreakInstantanea(key, jugador, partida, plugin);
									}
									
									String[] separados = config.getString("killstreaks_items."+key+".activateSound").split(";");
									try {
										Sound sound = Sound.valueOf(separados[0]);
										if(separados.length >= 4) {
											if(separados[3].equalsIgnoreCase("global")) {
												for(JugadorPaintball player : partida.getJugadores()) {
													player.getJugador().playSound(player.getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
												}
											}else {
												jugador.playSound(jugador.getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
											}
										}else {
											jugador.playSound(jugador.getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
										}
										
									}catch(Exception ex) {
										Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PaintballBattle.prefix+"&7Sound Name: &c"+separados[0]+" &7is not valid."));
									}
									
									UtilidadesItems.crearItemKillstreaks(j, config);
								}else {
									jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("noSufficientCoins")));
								}
								return;
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void preLanzarSnowball(PlayerInteractEvent event) {
		Player jugador = event.getPlayer();
		ItemStack item = event.getItem();
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			if(item != null && (item.getType().name().contains("SNOW") || item.getType().name().contains("EGG"))) {
				Partida partida = plugin.getPartidaJugador(jugador.getName());
				if(partida != null) {
					event.setCancelled(true);
					JugadorPaintball player = partida.getJugador(jugador.getName());
					
					if(player.getKillstreak("fury") == null) {
						if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")
								|| Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
							if(player.getSelectedHat().equals("chicken_hat")) {
								jugador.getInventory().removeItem(new ItemStack(Material.EGG,1));
							}else {
								jugador.getInventory().removeItem(new ItemStack(Material.SNOWBALL,1));
							}
						}else {
							if(player.getSelectedHat().equals("chicken_hat")) {
								jugador.getInventory().removeItem(new ItemStack(Material.EGG,1));
							}else {
								jugador.getInventory().removeItem(new ItemStack(Material.valueOf("SNOW_BALL"),1));
							}	
						}
					}
					
					FileConfiguration config = plugin.getConfig();
					String[] separados = config.getString("snowballShootSound").split(";");
					try {
						Sound sound = Sound.valueOf(separados[0]);
						jugador.playSound(jugador.getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
					}catch(Exception ex) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PaintballBattle.prefix+"&7Sound Name: &c"+separados[0]+" &7is not valid."));
					}

			        if(player.getSelectedHat().equals("chicken_hat")) {
			        	jugador.launchProjectile(Egg.class, jugador.getLocation().getDirection());
			        }else {
			        	jugador.launchProjectile(Snowball.class, jugador.getLocation().getDirection());
			        }
			       
			        
					
					Killstreak k = player.getKillstreak("triple_shoot");
					if(k != null) {
						Vector direccion = jugador.getLocation().getDirection().clone();
						double anguloEntre = Math.toRadians(10);

						double xFinal = direccion.getX()*Math.cos(anguloEntre)-direccion.getZ()*Math.sin(anguloEntre);
						double zFinal = direccion.getX()*Math.sin(anguloEntre)+direccion.getZ()*Math.cos(anguloEntre);
						
						
						direccion = new Vector(xFinal,direccion.getY(),zFinal);

						if(player.getSelectedHat().equals("chicken_hat")) {
				        	 jugador.launchProjectile(Egg.class, direccion);
				        }else {
				        	 jugador.launchProjectile(Snowball.class, direccion);
				        }
				        
						direccion = jugador.getLocation().getDirection().clone();

						anguloEntre = Math.toRadians(-10);

						xFinal = direccion.getX()*Math.cos(anguloEntre)-direccion.getZ()*Math.sin(anguloEntre);
						zFinal = direccion.getX()*Math.sin(anguloEntre)+direccion.getZ()*Math.cos(anguloEntre);
						
						
						direccion = new Vector(xFinal,direccion.getY(),zFinal);
						
				        if(player.getSelectedHat().equals("chicken_hat")) {
				        	 jugador.launchProjectile(Egg.class, direccion);
				        }else {
				        	 jugador.launchProjectile(Snowball.class, direccion);
				        }
					}
				}
			}
		}
		
	}
	
	@EventHandler
	public void lanzarSnowball(ProjectileLaunchEvent event) {
		Projectile p = event.getEntity();
		ProjectileSource source = p.getShooter();
		FileConfiguration config = plugin.getConfig();
		if((p.getType().equals(EntityType.SNOWBALL) || p.getType().equals(EntityType.EGG)) && source instanceof Player) {
			Player jugador = (Player) source;
			Partida partida = plugin.getPartidaJugador(jugador.getName());
			if(partida != null) {
				p.setMetadata("PaintballBattle", new FixedMetadataValue(plugin,"proyectil"));
				p.setVelocity(p.getVelocity().multiply(1.25));
				JugadorPaintball player = partida.getJugador(jugador.getName());
				Killstreak k = player.getKillstreak("strong_arm");
				if(k != null) {
					p.setVelocity(p.getVelocity().multiply(2));
				}
				String particle = config.getString("snowball_particle");
				if(!particle.equals("none")) {
					if(!player.getSelectedHat().equals("chicken_hat")) {
						CooldownSnowballParticle c = new CooldownSnowballParticle(plugin,p,particle);
						c.cooldown();
					}
				}
			}
		}
	}
	
    @EventHandler
    public void tirarHuevo(PlayerEggThrowEvent event) {
    	Egg egg = event.getEgg();
    	if(egg.hasMetadata("PaintballBattle")) {
    		event.setHatching(false);
    	}
    }
}
