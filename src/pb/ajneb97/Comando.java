package pb.ajneb97;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import pb.ajneb97.api.Hat;
import pb.ajneb97.api.Perk;
import pb.ajneb97.database.JugadorDatos;
import pb.ajneb97.database.MySQL;
import pb.ajneb97.juego.EstadoPartida;
import pb.ajneb97.juego.Partida;
import pb.ajneb97.juego.PartidaEditando;
import pb.ajneb97.managers.Checks;
import pb.ajneb97.managers.InventarioAdmin;
import pb.ajneb97.managers.InventarioShop;
import pb.ajneb97.managers.PartidaManager;
import pb.ajneb97.managers.TopHologram;
import pb.ajneb97.utils.UtilidadesOtros;

public class Comando implements CommandExecutor {
	
	PaintballBattle plugin;
	public Comando(PaintballBattle plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		FileConfiguration messages = plugin.getMessages();
		String prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"))+" ";
	   if (!(sender instanceof Player)){
		   if(args.length >= 1) {
			   if(args[0].equalsIgnoreCase("givecoins")) {
				   // /paintball givecoins <player> <amount>
				   giveCoins(sender,args,messages,prefix);
			   }else if(args[0].equalsIgnoreCase("reload")) {
				   // /paintball reload
				   plugin.reloadConfig();
				   plugin.reloadMessages();
				   plugin.reloadShop();
				   plugin.recargarCarteles();
				   plugin.recargarScoreboard();
				   plugin.recargarHologramas();
				   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("configReloaded"))); 
			   }
		   }
		   return false;   	
	   }
	   Player jugador = (Player)sender;
	   if(args.length >= 1) {
		   
		   if(args[0].equalsIgnoreCase("create")) {
			   // /paintball create <nombre>
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   if(args.length >= 2) {
					   if(plugin.getPartida(args[1]) == null) {
						   FileConfiguration config = plugin.getConfig();
						   if(!config.contains("MainLobby")) {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noMainLobby")));  
							   return true;
						   }
						   String equipo1 = "";
						   String equipo2 = "";
						   int i=0;
						   for(String key : config.getConfigurationSection("teams").getKeys(false)) {
							   if(i==0) {
								   equipo1 = key;
							   }else {
								   equipo2 = key;
								   break;
							   }
							   i++;
						   }
						   
						   Partida partida = new Partida(args[1],Integer.valueOf(config.getString("arena_time_default")),equipo1,equipo2,Integer.valueOf(config.getString("team_starting_lives_default")));
						   plugin.agregarPartida(partida);
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaCreated").replace("%name%", args[1]))); 
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaCreatedExtraInfo").replace("%name%", args[1]))); 
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaAlreadyExists")));  
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandCreateErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("delete")) {
			   // /paintball delete <nombre>
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   if(args.length >= 2) {
					   if(plugin.getPartida(args[1]) != null) {
						   plugin.removerPartida(args[1]);
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaDeleted").replace("%name%", args[1]))); 
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaDoesNotExists")));  
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandDeleteErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("reload")) {
			   // /paintball reload
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   plugin.reloadConfig();
				   plugin.reloadMessages();
				   plugin.reloadShop();
				   plugin.recargarCarteles();
				   plugin.recargarScoreboard();
				   plugin.recargarHologramas();
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("configReloaded"))); 
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("setmainlobby")) {
			   // /paintball setmainlobby
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   FileConfiguration config = plugin.getConfig();
				   
				   Location l = jugador.getLocation();
				   config.set("MainLobby.x", l.getX()+"");
				   config.set("MainLobby.y", l.getY()+"");
				   config.set("MainLobby.z", l.getZ()+"");
				   config.set("MainLobby.world", l.getWorld().getName());
				   config.set("MainLobby.pitch", l.getPitch());
				   config.set("MainLobby.yaw", l.getYaw());
				   plugin.saveConfig();
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("mainLobbyDefined"))); 
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("join")) {
			   // /paintball join <arena>
			   if(!Checks.checkTodo(plugin, jugador)) {
				   return false;
			   }
			   if(args.length >= 2) {
				   Partida partida = plugin.getPartida(args[1]);
				   if(partida != null) {
					   if(partida.estaActivada()) {
						   if(plugin.getPartidaJugador(jugador.getName()) == null) {
							   if(!partida.estaIniciada()) {
								   if(!partida.estaLlena()) {
									   if(!UtilidadesOtros.pasaConfigInventario(jugador, plugin.getConfig())) {
										   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("errorClearInventory"))); 
										   return true;
									   }
									   PartidaManager.jugadorEntra(partida, jugador,plugin);
								   }else {
									   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaIsFull"))); 
								   }
							   }else {
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaAlreadyStarted"))); 
							   }
						   }else {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("alreadyInArena"))); 
						   }
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaDisabledError"))); 
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaDoesNotExists"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandJoinErrorUse"))); 
			   }
		   }else if(args[0].equalsIgnoreCase("joinrandom")) {
			   // /paintball joinrandom
			   if(plugin.getPartidaJugador(jugador.getName()) == null) {
				    Partida partidaNueva = PartidaManager.getPartidaDisponible(plugin);
					if(partidaNueva == null) {
						jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("noArenasAvailable")));
					}else {
						PartidaManager.jugadorEntra(partidaNueva, jugador, plugin);
					}
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("alreadyInArena"))); 
			   }
		   }else if(args[0].equalsIgnoreCase("leave")) {
			   // /paintball leave
			   Partida partida = plugin.getPartidaJugador(jugador.getName());
			   if(partida != null) {  
				   PartidaManager.jugadorSale(partida, jugador, false, plugin, false);;
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("notInAGame"))); 
			   }
		   }else if(args[0].equalsIgnoreCase("shop")) {   
			   // /paintball shop
			   if(!Checks.checkTodo(plugin, jugador)) {
				   return false;
			   }
			   InventarioShop.crearInventarioPrincipal(jugador, plugin);
		   }else if(args[0].equalsIgnoreCase("enable")) {
			   // /paintball enable <arena>
			   //Para activar una arena todo debe estar definido
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   if(args.length >= 2) {
					   Partida partida = plugin.getPartida(args[1]);
					   if(partida != null) {
						   if(partida.estaActivada()) {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaAlreadyEnabled"))); 
						   }else {
							   if(partida.getLobby() == null) {
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("enableArenaLobbyError"))); 
								   return true;
							   }
							   if(partida.getTeam1().getSpawn() == null) {
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("enableArenaSpawnError").replace("%number%", "1"))); 
								   return true;
							   }
							   if(partida.getTeam2().getSpawn() == null) {
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("enableArenaSpawnError").replace("%number%", "2"))); 
								   return true;
							   }
							   
							   partida.setEstado(EstadoPartida.ESPERANDO);
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaEnabled").replace("%name%", args[1]))); 
						   }
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaDoesNotExists"))); 
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandEnableErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("disable")) {
			   // /paintball disable <arena>
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   if(args.length >= 2) {
					   Partida partida = plugin.getPartida(args[1]);
					   if(partida != null) {
						   if(!partida.estaActivada()) {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaAlreadyDisabled"))); 
						   }else {
							   partida.setEstado(EstadoPartida.DESACTIVADA);
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaDisabled").replace("%name%", args[1]))); 
						   }
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaDoesNotExists"))); 
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandDisableErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("edit")) {
			   // /paintball edit <arena>  
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   if(!Checks.checkTodo(plugin, jugador)) {
					   return false;
				   }
				   if(args.length >= 2) {
					   Partida partida = plugin.getPartida(args[1]);
					   if(partida != null) {
						   if(!partida.estaActivada()) {
							   PartidaEditando p = plugin.getPartidaEditando();
							   if(p == null) {
								   
								   InventarioAdmin.crearInventario(jugador,partida,plugin);
							   }else {
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaModifyingError"))); 
							   }
						   }else {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaMustBeDisabled")));  
						   }
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("arenaDoesNotExists"))); 
					   }
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandAdminErrorUse"))); 
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions"))); 
			   }
		   }else if(args[0].equalsIgnoreCase("createtophologram")) {
			   // /paintball createtophologram <name> kills/wins <global/monthly/weekly>
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   if (plugin.getServer().getPluginManager().getPlugin("HolographicDisplays") == null) {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', "&cYou need HolographicDisplays plugin to use this feature."));  
				        return true;
				   }
				   if(args.length >= 3) {
					   if(args[2].equalsIgnoreCase("kills") || args[2].equalsIgnoreCase("wins")) {
						   TopHologram topHologram = plugin.getTopHologram(args[1]);
						   if(topHologram == null) {
							   String period = "global";
							   if(args.length >= 4) {
								   period = args[3];
							   }
							   if(period.equalsIgnoreCase("global") || period.equalsIgnoreCase("monthly") || period.equalsIgnoreCase("weekly")) {
								   if(!MySQL.isEnabled(plugin.getConfig()) && (period.equalsIgnoreCase("monthly") || period.equalsIgnoreCase("weekly"))) {
									   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("topHologramPeriodSQLError")));  
									   return true;
								   }
								   TopHologram hologram = new TopHologram(args[1],args[2],jugador.getLocation(),plugin,period);
								   plugin.agregarTopHolograma(hologram);
								   hologram.spawnHologram(plugin);
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("topHologramCreated")));  
							   }else {
								   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandCreateHologramErrorUse")));  
							   }					    
						   }else {
							   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("topHologramAlreadyExists")));  
						   }
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandCreateHologramErrorUse")));   
					   }  
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandCreateHologramErrorUse")));
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("removetophologram")) {
			   // /paintball removetophologram <name>
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   if (plugin.getServer().getPluginManager().getPlugin("HolographicDisplays") == null) {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', "&cYou need HolographicDisplays plugin to use this feature."));  
				        return true;
				   }
				   if(args.length >= 2) {
					   TopHologram topHologram = plugin.getTopHologram(args[1]);
					   if(topHologram != null) {
						   plugin.eliminarTopHologama(args[1]);
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("topHologramRemoved")));  
					   }else {
						   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("topHologramDoesNotExists")));  
					   }  
				   }else {
					   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandRemoveHologramErrorUse")));
				   }
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }else if(args[0].equalsIgnoreCase("givecoins")) {
			   // /paintball givecoins <player> <amount>
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   giveCoins(sender,args,messages,prefix);
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
		   }
		   else {
			   // /paintball help /o cualquier otro comando
			   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
				   enviarAyuda(jugador);
			   }else {
				   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
			   }
			   
		   }
	   }else {
		   if(jugador.isOp() || jugador.hasPermission("paintball.admin")) {
			   enviarAyuda(jugador);
		   }else {
			   jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("noPermissions")));
		   }
	   }
	   
	   return true;
	   
	}
	
	public boolean giveCoins(CommandSender sender, String[] args, FileConfiguration messages, String prefix) {
		if(args.length >= 3) {
			   String player = args[1];
			   try {
				   int amount = Integer.valueOf(args[2]);
				   //Si el jugador no esta en la base de datos, o en un archivo, DEBE estar conectado para darle coins.
				   if(MySQL.isEnabled(plugin.getConfig())) {
					   if(MySQL.jugadorExiste(plugin, player)) {
						   MySQL.agregarCoinsJugadorAsync(plugin, player, amount);
						   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("giveCoinsMessage").replace("%player%", player).replace("%amount%", amount+"")));
						   Player p = Bukkit.getPlayer(player);
						   if(p != null) {
							   p.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("receiveCoinsMessage").replace("%amount%", amount+""))); 
						   } 
					   }else {
						   Player p = Bukkit.getPlayer(player);
						   if(p != null) {
							   MySQL.crearJugadorPartidaAsync(plugin, p.getUniqueId().toString(), p.getName(), "", 0, 0, 0, 0, amount, 1);
							   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("giveCoinsMessage").replace("%player%", player).replace("%amount%", amount+"")));
							   p.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("receiveCoinsMessage").replace("%amount%", amount+"")));
						   }else {
							   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("errorPlayerOnline")));
						   }
					   }
				   }else {
					   Player p = Bukkit.getPlayer(player);
					   if(p != null) {
						   plugin.registerPlayer(p.getUniqueId().toString()+".yml");
						   if(plugin.getJugador(p.getName()) == null) {
								plugin.agregarJugadorDatos(new JugadorDatos(p.getName(),p.getUniqueId().toString(),0,0,0,0,0,new ArrayList<Perk>(),new ArrayList<Hat>()));
						   }
						   JugadorDatos jDatos = plugin.getJugador(p.getName());
						   jDatos.aumentarCoins(amount);
						   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("giveCoinsMessage").replace("%player%", player).replace("%amount%", amount+"")));
						   p.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("receiveCoinsMessage").replace("%amount%", amount+""))); 
					   }else {
						   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("errorPlayerOnline")));
					   }
				   }
				   
			   }catch(NumberFormatException e) {
				   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("validNumberError")));
			   }
			   
		   }else {
			   sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("commandGiveCoinsErrorUse")));
		   }
		   return true;
	}
	
	public void enviarAyuda(Player jugador) {
		jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7[ [ &4[&fPaintball Battle&4] &7] ]"));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',""));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball create <arena> &8Creates a new arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball delete <arena> &8Deletes an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball join <arena> &8Joins an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball joinrandom &8Joins a random arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball leave &8Leaves from the arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball shop &8Opens the Paintball Shop."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball givecoins <player> <amount>"));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball setmainlobby &8Defines the minigame main lobby."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball enable <arena> &8Enables an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball disable <arena> &8Disables an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball edit <arena> &8Edit the properties of an arena."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball createtophologram <name> <kills/wins> <global/monthly/weekly>"));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball removetophologram <name>"));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6/paintball reload &8Reloads the configuration files."));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',""));
		   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7[ [ &4[&fPaintball Battle&4] &7] ]"));
	}
}
