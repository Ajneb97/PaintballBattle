package pb.ajneb97.versiones;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class V1_16 {

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
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", textura));

        try {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        item.setItemMeta(skullMeta);
        return item;
	}

}
