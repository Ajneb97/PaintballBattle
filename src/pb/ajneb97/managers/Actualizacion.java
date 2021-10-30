package pb.ajneb97.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pb.ajneb97.PaintballBattle;

public class Actualizacion implements Listener{

	private PaintballBattle plugin;
	public Actualizacion(PaintballBattle plugin){		
		this.plugin = plugin;		
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent event){
		Player jugador = event.getPlayer();
		FileConfiguration config = plugin.getConfig();		
		if(jugador.isOp() && !(plugin.version.equals(plugin.latestversion))){
			if(config.getString("new_version_reminder").equals("true")){
				jugador.sendMessage(PaintballBattle.prefix + ChatColor.RED +" There is a new version available. "+ChatColor.YELLOW+
		  				  "("+ChatColor.GRAY+plugin.latestversion+ChatColor.YELLOW+")");
		  		    jugador.sendMessage(ChatColor.RED+"You can download it at: "+ChatColor.GREEN+"https://www.spigotmc.org/resources/76676/");
			}			 
		}
	}
}
