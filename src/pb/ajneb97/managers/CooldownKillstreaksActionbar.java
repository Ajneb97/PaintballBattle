package pb.ajneb97.managers;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import pb.ajneb97.PaintballBattle;
import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.juego.Killstreak;
import pb.ajneb97.juego.Partida;
import pb.ajneb97.lib.actionbarapi.ActionBarAPI;

public class CooldownKillstreaksActionbar {
	
	int taskID;
	private PaintballBattle plugin;
	public CooldownKillstreaksActionbar(PaintballBattle plugin){		
		this.plugin = plugin;		
	}
	
	public void crearActionbars() {
	    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	    final FileConfiguration messages = plugin.getMessages();
    	final FileConfiguration config = plugin.getConfig();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() { 
            	for(Player player : Bukkit.getOnlinePlayers()) {
            		actualizarActionbars(player,messages,config);
                }
            }
        },0, 20L);
	}
	
	protected void actualizarActionbars(final Player player,final FileConfiguration messages,final FileConfiguration config) {
		Partida partida = plugin.getPartidaJugador(player.getName());
		if(partida != null) {
			JugadorPaintball jugador = partida.getJugador(player.getName());
			Killstreak ultima = jugador.getUltimaKillstreak();
			if(ultima != null) {
				String name = config.getString("killstreaks_items."+ultima.getTipo()+".name");
				int tiempo = ultima.getTiempo();
				ActionBarAPI.sendActionBar(jugador.getJugador(), ChatColor.translateAlternateColorCodes('&', messages.getString("killstreakActionbar")
						.replace("%killstreak%", name).replace("%time%", tiempo+"")));
			}
		}
	}
}
