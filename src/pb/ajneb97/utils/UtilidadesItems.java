package pb.ajneb97.utils;

import pb.ajneb97.PaintballBattle;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import pb.ajneb97.juego.JugadorPaintball;

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
		  	if(Bukkit.getVersion().contains("1.15") || UtilidadesOtros.isNew()) {
		  		meta.setUnbreakable(true);
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
		SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

		ServerVersion serverVersion = PaintballBattle.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R2)){
            UUID uuid = id != null ? UUID.fromString(id) : UUID.randomUUID();
            PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
            PlayerTextures textures = profile.getTextures();
            URL url;
            try {
                String decoded = new String(Base64.getDecoder().decode(textura));
                String decodedFormatted = decoded.replaceAll("\\s", "");
                JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
                String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN")
                        .getAsJsonObject().get("url").getAsString();

                url = new URL(urlText);
            } catch (Exception error) {
                error.printStackTrace();
                return null;
            }
            textures.setSkin(url);
            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
        }else{
            GameProfile profile = null;
            if(id == null) {
                profile = new GameProfile(UUID.randomUUID(), "");
            }else {
                profile = new GameProfile(UUID.fromString(id), "");
            }
            profile.getProperties().put("textures", new Property("textures", textura));

            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
                error.printStackTrace();
            }
        }

        item.setItemMeta(skullMeta);
        
		return item;	
	}
}
