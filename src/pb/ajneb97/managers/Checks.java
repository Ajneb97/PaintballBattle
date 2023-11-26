package pb.ajneb97.managers;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import pb.ajneb97.PaintballBattle;

public class Checks {

	public static boolean checkTodo(PaintballBattle plugin,CommandSender jugador){
		FileConfiguration messages = plugin.getMessages();
		FileConfiguration config = plugin.getConfig();
		String nombre = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"))+ " ";
		String mensaje = nombre+messages.getString("materialNameError");
		
		//Check config.yml
		if(!comprobarMaterial(config.getString("leave_item.item"),jugador,mensaje) &&
				!comprobarMaterial(config.getString("killstreaks_item.item"),jugador,mensaje)
				&& !comprobarMaterial(config.getString("play_again_item.item"),jugador,mensaje)){
			return false;
		}		
		for(String key : config.getConfigurationSection("teams").getKeys(false)) {
			if(!comprobarMaterial(config.getString("teams."+key+".item"),jugador,mensaje)){
				return false;
			}
		}
		
		for(String key : config.getConfigurationSection("killstreaks_items").getKeys(false)) {
			if(!comprobarMaterial(config.getString("killstreaks_items."+key+".item"),jugador,mensaje)){
				return false;
			}
		}
		for(String key : config.getConfigurationSection("hats_items").getKeys(false)) {
			if(!comprobarMaterial(config.getString("hats_items."+key+".item"),jugador,mensaje)){
				return false;
			}
		}
		FileConfiguration shop = plugin.getShop();
		for(String key : shop.getConfigurationSection("shop_items").getKeys(false)) {
			if(!comprobarMaterial(shop.getString("shop_items."+key+".item"),jugador,mensaje)){
				return false;
			}
		}
		for(String key : shop.getConfigurationSection("perks_items").getKeys(false)) {
			if(!comprobarMaterial(shop.getString("perks_items."+key+".item"),jugador,mensaje)){
				return false;
			}
		}
		for(String key : shop.getConfigurationSection("hats_items").getKeys(false)) {
			if(!comprobarMaterial(shop.getString("hats_items."+key+".item"),jugador,mensaje)){
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings({ "deprecation", "unused" })
	public static boolean comprobarMaterial(String key,CommandSender jugador,String mensaje){
		   try{
			   if(key.contains(":")){
					  String[] idsplit = key.split(":");
					  String stringDataValue = idsplit[1];
					  short DataValue = Short.valueOf(stringDataValue);
					  Material mat = Material.getMaterial(idsplit[0].toUpperCase()); 
					  ItemStack item = new ItemStack(mat,1,(short)DataValue); 
			   }else {
				   ItemStack item = new ItemStack(Material.getMaterial(key),1);
			   }
			   
			   return true;
		   }catch(Exception e){
			   jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', mensaje.replace("%material%", key)));
			   return false;
		   }
	}
	
	//Al iniciar el server, revisa TODOS LOS PATHS ORIGINALES (IDs numericas) y los modifica si no es 1.13+
	public static void checkearYModificar(PaintballBattle plugin, boolean primeraVez) {
				if(!Bukkit.getVersion().contains("1.13") && !Bukkit.getVersion().contains("1.14") && !Bukkit.getVersion().contains("1.15")
						&& !Bukkit.getVersion().contains("1.16") && !Bukkit.getVersion().contains("1.17") && !Bukkit.getVersion().contains("1.18")
						&& !Bukkit.getVersion().contains("1.19") && !Bukkit.getVersion().contains("1.20")) {
					FileConfiguration config = plugin.getConfig();
					FileConfiguration shop = plugin.getShop();
					if(primeraVez) {
						modificarPath(config,"teams.blue.item","WOOL:11");
						modificarPath(config,"teams.red.item","WOOL:14");
						modificarPath(config,"teams.yellow.item","WOOL:4");
						modificarPath(config,"teams.green.item","WOOL:13");
						modificarPath(config,"teams.orange.item","WOOL:1");
						modificarPath(config,"teams.purple.item","WOOL:10");
						modificarPath(config,"teams.black.item","WOOL:15");
						modificarPath(config,"teams.white.item","WOOL");
						modificarPath(config,"play_again_item.item","INK_SACK:12");
						modificarPath(config,"killedBySound","NOTE_PLING;10;0.1");
						modificarPath(config,"killSound","FIREWORK_BLAST;10;2");
						modificarPath(config,"expireKillstreakSound","NOTE_SNARE_DRUM;10;2");
						modificarPath(config,"snowballShootSound","SHOOT_ARROW;10;0.5");
						modificarPath(config,"shopUnlockSound","FIREWORK_BLAST;10;2");
						modificarPath(config,"hatAbilityActivatedSound","CHEST_OPEN;10;1.5");
						modificarPath(config,"explosiveHatSound","EXPLODE;10;1");
						modificarPath(config,"killstreaks_items.more_snowballs.item","SNOW_BALL");
						modificarPath(config,"killstreaks_items.lightning.item","GOLD_AXE");
						modificarPath(config,"hats_items.present_hat.item","SKULL_ITEM:3");
						modificarPath(config,"hats_items.assassin_hat.item","SKULL_ITEM:3");
						modificarPath(config,"hats_items.chicken_hat.item","SKULL_ITEM:3");
						modificarPath(config,"hats_items.time_hat.item","GOLD_HELMET");
						modificarPath(config,"killstreaks_items.more_snowballs.activateSound","VILLAGER_YES;10;1");
						modificarPath(config,"killstreaks_items.strong_arm.activateSound","ANVIL_USE;10;2");
						modificarPath(config,"killstreaks_items.triple_shoot.activateSound","ANVIL_USE;10;2");
						modificarPath(config,"killstreaks_items.3_lives.activateSound","BAT_TAKEOFF;10;1.5;global");
						modificarPath(config,"killstreaks_items.teleport.activateSound","ENDERMAN_TELEPORT;10;1");
						modificarPath(config,"killstreaks_items.lightning.activateSound","AMBIENCE_THUNDER;10;1");
						modificarPath(config,"killstreaks_items.nuke.activateSound","WOLF_HOWL;10;2;global");
						modificarPath(config,"killstreaks_items.nuke.finalSound","EXPLODE;10;1;global");
						modificarPath(config,"killstreaks_items.fury.activateSound","PISTON_EXTEND;10;1.5;global");
						plugin.saveConfig();
						
						modificarPath(shop,"perks_items.decorative_item.item","STAINED_GLASS_PANE:10");
						modificarPath(shop,"hats_items.present_hat.item","SKULL_ITEM:3");
						modificarPath(shop,"hats_items.assassin_hat.item","SKULL_ITEM:3");
						modificarPath(shop,"hats_items.chicken_hat.item","SKULL_ITEM:3");
						modificarPath(shop,"hats_items.time_hat.item","GOLD_HELMET");
						plugin.saveShop();
					}
				}
	}
				
	public static void modificarPath(FileConfiguration config,String path,String idNueva) {
		if(config.contains(path)) {
			config.set(path, idNueva);
		}
	}
}
