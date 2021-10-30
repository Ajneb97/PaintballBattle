package pb.ajneb97.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitScheduler;

import pb.ajneb97.PaintballBattle;
import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.juego.Partida;
import pb.ajneb97.utils.UtilidadesOtros;

public class CooldownSnowballParticle {

	int taskID;
	private PaintballBattle plugin;
	private Projectile snowball;
	private String particula;
	public CooldownSnowballParticle(PaintballBattle plugin,Projectile snowball,String particula){		
		this.plugin = plugin;		
		this.snowball = snowball;
		this.particula = particula;
	}
	
	public void cooldown(){
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutar()){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 3L);
	}

	protected boolean ejecutar() {
		if(snowball != null && !snowball.isDead()) {
			Location l = snowball.getLocation();
			UtilidadesOtros.generarParticula(particula, l, 0.01F, 0.01F, 0.01F, 0.01F, 1);
			return true;
		}else {
			return false;
		}
		
	}
}
