package pb.ajneb97.versiones;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class V1_20 {

	/*BARRIER
	BLOCK_CRACK
	BLOCK_DUST
	CLOUD
	CRIT
	CRIT_MAGIC
	DAMAGE_INDICATOR
	DRAGON_BREATH
	DRIP_LAVA
	DRIP_WATER
	ENCHANTMENT_TABLE
	END_ROD
	EXPLOSION_HUGE
	EXPLOSION_LARGE
	EXPLOSION_NORMAL
	FIREWORKS_SPARK
	FLAME
	FOOTSTEP
	HEART
	ITEM_CRACK
	ITEM_TAKE
	LAVA
	MOB_APPEARANCE
	NOTE
	PORTAL
	REDSTONE
	SLIME
	SMOKE_LARGE
	SMOKE_NORMAL
	SNOW_SHOVEL
	SNOWBALL
	SPELL
	SPELL_INSTANT
	SPELL_MOB
	SPELL_MOB_AMBIENT
	SPELL_WITCH
	SUSPENDED
	SUSPENDED_DEPTH
	SWEEP_ATTACK
	TOWN_AURA
	VILLAGER_ANGRY
	VILLAGER_HAPPY
	WATER_BUBBLE
	WATER_DROP
	WATER_SPLASH
	WATER_WAKE */

	public void generarParticula(String particle, Location loc, float xOffset, float yOffset, float zOffset, float speed, int count){
		loc.getWorld().spawnParticle(Particle.valueOf(particle),loc,count,xOffset,yOffset,zOffset,speed);
	  }
	
	public ItemStack getCabeza(ItemStack item, String id,String textura) {
		if (textura.isEmpty()) return item;

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        UUID uuid = UUID.randomUUID();
		PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
		PlayerTextures textures = profile.getTextures();
		URL url;
		try {
			String decoded = new String(Base64.getDecoder().decode(textura));
			String decodedFormatted = decoded.replaceAll("\\s", "");
			int firstIndex = decodedFormatted.indexOf("\"SKIN\":{\"url\":")+15;
			int lastIndex = decodedFormatted.indexOf("}",firstIndex+1);
			url = new URL(decodedFormatted.substring(firstIndex,lastIndex-1));
		} catch (MalformedURLException error) {
			error.printStackTrace();
			return null;
		}
		textures.setSkin(url);
		profile.setTextures(textures);
		skullMeta.setOwnerProfile(profile);

        item.setItemMeta(skullMeta);
        return item;
	}

}
