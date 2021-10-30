package pb.ajneb97.versiones;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class V1_15 {

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
		net.minecraft.server.v1_15_R1.ItemStack cabeza = org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack.asNMSCopy(item);		
		net.minecraft.server.v1_15_R1.NBTTagCompound tag = cabeza.hasTag() ? cabeza.getTag() : new net.minecraft.server.v1_15_R1.NBTTagCompound();
		net.minecraft.server.v1_15_R1.NBTTagCompound skullOwnerCompound = new net.minecraft.server.v1_15_R1.NBTTagCompound();
		net.minecraft.server.v1_15_R1.NBTTagCompound propiedades = new net.minecraft.server.v1_15_R1.NBTTagCompound();
		
		
		net.minecraft.server.v1_15_R1.NBTTagList texturas = new net.minecraft.server.v1_15_R1.NBTTagList();
		net.minecraft.server.v1_15_R1.NBTTagCompound texturasObjeto = new net.minecraft.server.v1_15_R1.NBTTagCompound();
		texturasObjeto.setString("Value", textura);
		texturas.add(texturasObjeto);
		propiedades.set("textures", texturas);
		skullOwnerCompound.set("Properties", propiedades);
		
		skullOwnerCompound.setString("Id", id);
		
		tag.set("SkullOwner", skullOwnerCompound);
		cabeza.setTag(tag);
		
		
		return org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack.asBukkitCopy(cabeza);
	}

}
