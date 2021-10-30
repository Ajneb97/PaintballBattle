package pb.ajneb97.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.versiones.V1_10;
import pb.ajneb97.versiones.V1_11;
import pb.ajneb97.versiones.V1_12;
import pb.ajneb97.versiones.V1_13;
import pb.ajneb97.versiones.V1_13_R2;
import pb.ajneb97.versiones.V1_14;
import pb.ajneb97.versiones.V1_15;
import pb.ajneb97.versiones.V1_16;
import pb.ajneb97.versiones.V1_17;
import pb.ajneb97.versiones.V1_8_R1;
import pb.ajneb97.versiones.V1_8_R2;
import pb.ajneb97.versiones.V1_8_R3;
import pb.ajneb97.versiones.V1_9_R1;
import pb.ajneb97.versiones.V1_9_R2;

public class UtilidadesItems {

	@SuppressWarnings("deprecation")
	public static ItemStack crearItem(FileConfiguration config,String path){
		String id = config.getString(path+".item");
		String[] idsplit = new String[2]; 
		  int DataValue = 0;
		  ItemStack stack = null;
		  if(id.contains(":")){
			  idsplit = id.split(":");
			  String stringDataValue = idsplit[1];
			  DataValue = Integer.valueOf(stringDataValue);
			  Material mat = Material.getMaterial(idsplit[0].toUpperCase()); 
			  stack = new ItemStack(mat,1,(short)DataValue);             	  
		  }else{
			  Material mat = Material.getMaterial(id.toUpperCase());
			  stack = new ItemStack(mat,1);  			  
		  }
		  	ItemMeta meta = stack.getItemMeta();
		  	if(config.contains(path+".name")) {
		  		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path+".name")));
		  	}
		  	if(config.contains(path+".lore")) {
		  		List<String> lore = config.getStringList(path+".lore");
				for(int c=0;c<lore.size();c++) {
					lore.set(c, ChatColor.translateAlternateColorCodes('&', lore.get(c)));
				}
				meta.setLore(lore);
				
		  	}
		  	meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_UNBREAKABLE,ItemFlag.HIDE_POTION_EFFECTS);
		  	if(Bukkit.getVersion().contains("1.15") || Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
		  		meta.setUnbreakable(true);
		  	}else {
		  		meta.spigot().setUnbreakable(true); //SOLO FUNCIONA CON SPIGOT
		  	}
		  	stack.setItemMeta(meta);
			
			return stack;
	}
	
	public static void crearItemKillstreaks(JugadorPaintball jugador,FileConfiguration config) {
		if(config.getString("killstreaks_item_enabled").equals("true")) {
			int coins = jugador.getCoins();
			ItemStack item = UtilidadesItems.crearItem(config, "killstreaks_item");
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("killstreaks_item.name").replace("%amount%", coins+"")));
			item.setItemMeta(meta);
			if(coins <= 1) {
				item.setAmount(1);
			}else if(coins >= 64) {
				item.setAmount(64);
			}else {
				item.setAmount(coins);
			}
			jugador.getJugador().getInventory().setItem(8, item);
		}
	}
	
	public static ItemStack getCabeza(ItemStack item, String id,String textura){
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		if(packageName.contains("1_17_")){
			V1_17 u = new V1_17();
			ItemStack stack = u.getCabeza(item,id,textura);			
			return stack;
		}
		if(packageName.contains("1_16_")){
			V1_16 u = new V1_16();
			ItemStack stack = u.getCabeza(item,id,textura);			
			return stack;
		}
		if(packageName.contains("1_15_R1")){
			V1_15 u = new V1_15();
			ItemStack stack = u.getCabeza(item,id,textura);			
			return stack;
		}
		else if(packageName.contains("1_14_R1")){
			V1_14 u = new V1_14();
			ItemStack stack = u.getCabeza(item,id,textura);			
			return stack;
		}
		else if(packageName.contains("1_13_R2")){
			V1_13_R2 u = new V1_13_R2();
			ItemStack stack = u.getCabeza(item,id,textura);			
			return stack;
		}
		else if(packageName.contains("1_13_R1")){
			V1_13 u = new V1_13();
			ItemStack stack = u.getCabeza(item,id,textura);			
			return stack;
		}
		else if(packageName.contains("1_12_R1")){
			V1_12 u = new V1_12();
			ItemStack stack = u.getCabeza(item,id,textura);			
			return stack;
		}
		else if(packageName.contains("1_11_R1")){
			V1_11 u = new V1_11();
			ItemStack stack = u.getCabeza(item,id,textura);				
			return stack;
		}
		else if(packageName.contains("1_10_R1")){
			V1_10 u = new V1_10();
			ItemStack stack = u.getCabeza(item,id,textura);				
			return stack;
		}
		else if(packageName.contains("1_9_R2")){
			V1_9_R2 u = new V1_9_R2();
			ItemStack stack = u.getCabeza(item,id,textura);				
			return stack;
		}
		else if(packageName.contains("1_9_R1")){
			V1_9_R1 u = new V1_9_R1();
			ItemStack stack = u.getCabeza(item,id,textura);				
			return stack;
		}
		else if(packageName.contains("1_8_R3")){
			V1_8_R3 u = new V1_8_R3();
			ItemStack stack = u.getCabeza(item,id,textura);				
			return stack;
		}
		else if(packageName.contains("1_8_R2")){
			V1_8_R2 u = new V1_8_R2();
			ItemStack stack = u.getCabeza(item,id,textura);				
			return stack;
		}
		else if(packageName.contains("1_8_R1")){
			V1_8_R1 u = new V1_8_R1();
			ItemStack stack = u.getCabeza(item,id,textura);				
			return stack;
		}
		else{
			return item;
		}		
	}
}
