package pb.ajneb97.managers;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import pb.ajneb97.PaintballBattle;

public class TopHologramAdmin {

	int taskID;
	private PaintballBattle plugin;
	public TopHologramAdmin(PaintballBattle plugin){		
		this.plugin = plugin;		
	}
	
	public int getTaskID() {
		return this.taskID;
	}
	
	public void actualizarHologramas() {
		FileConfiguration config = plugin.getConfig();
		long ticks = Long.valueOf(config.getString("top_hologram_update_time"))*20;
	    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() { 
            	ejecutarActualizarHologramas();
            }
        },ticks, ticks);
	}

	protected void ejecutarActualizarHologramas() {
		ArrayList<TopHologram> hologramas = plugin.getTopHologramas();
		for(int i=0;i<hologramas.size();i++) {
			hologramas.get(i).actualizar(plugin);
			
		}
	}
}
