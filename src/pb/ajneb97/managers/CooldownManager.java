package pb.ajneb97.managers;

import java.util.ArrayList;

import pb.ajneb97.PaintballBattle;
import pb.ajneb97.juego.Equipo;
import pb.ajneb97.juego.EstadoPartida;
import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.juego.Partida;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;


public class CooldownManager {

	int taskID;
	int tiempo;
	private Partida partida;
	private PaintballBattle plugin;
	public CooldownManager(PaintballBattle plugin){		
		this.plugin = plugin;		
	}
	
	public void cooldownComenzarJuego(Partida partida,int cooldown){
		this.partida = partida;
		this.tiempo = cooldown;
		partida.setTiempo(tiempo);
		final FileConfiguration messages = plugin.getMessages();
		final FileConfiguration config = plugin.getConfig();
		final String prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"))+" ";
		ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
		for(int i=0;i<jugadores.size();i++) {
			jugadores.get(i).getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("arenaStartingMessage").replace("%time%", tiempo+"")));
		}
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarComenzarJuego(messages,config,prefix)){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}
	
	protected boolean ejecutarComenzarJuego(FileConfiguration messages,FileConfiguration config,String prefix) {
		if(partida != null && partida.getEstado().equals(EstadoPartida.COMENZANDO)) {
			if(tiempo <= 5 && tiempo > 0) {
				ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
				for(int i=0;i<jugadores.size();i++) {
					jugadores.get(i).getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("arenaStartingMessage").replace("%time%", tiempo+"")));
					String[] separados = config.getString("startCooldownSound").split(";");
					try {
						Sound sound = Sound.valueOf(separados[0]);
						jugadores.get(i).getJugador().playSound(jugadores.get(i).getJugador().getLocation(), sound, Float.valueOf(separados[1]), Float.valueOf(separados[2]));
					}catch(Exception ex) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PaintballBattle.prefix+"&7Sound Name: &c"+separados[0]+" &7is not valid."));
					}
				}
				partida.disminuirTiempo();
				tiempo--;
				return true;
			}else if(tiempo <= 0) {
				PartidaManager.iniciarPartida(partida,plugin);
				return false;
			}else {
				partida.disminuirTiempo();
				tiempo--;
				return true;
			}
		}else {
			ArrayList<JugadorPaintball> jugadores = partida.getJugadores();
			for(int i=0;i<jugadores.size();i++) {
				jugadores.get(i).getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("gameStartingCancelled")));
			}
			return false;
		}
	}
	
	public void cooldownJuego(Partida partida){
		this.partida = partida;
		this.tiempo = partida.getTiempoMaximo();
		partida.setTiempo(tiempo);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarJuego()){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}
	
	protected boolean ejecutarJuego() {
		if(partida != null && partida.getEstado().equals(EstadoPartida.JUGANDO)) {
			partida.disminuirTiempo();
			if(tiempo == 0) {
				PartidaManager.iniciarFaseFinalizacion(partida, plugin);
				return false;
			}else {
				tiempo--;
				return true;
			}
		}else {
			return false;
		}
	}
	
	public void cooldownFaseFinalizacion(Partida partida,int cooldown,final Equipo ganador){
		this.partida = partida;
		this.tiempo = cooldown;
		partida.setTiempo(tiempo);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarComenzarFaseFinalizacion(ganador)){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}
	
	protected boolean ejecutarComenzarFaseFinalizacion(Equipo ganador) {
		if(partida != null && partida.getEstado().equals(EstadoPartida.TERMINANDO)) {
			partida.disminuirTiempo();
			if(tiempo == 0) {
				PartidaManager.finalizarPartida(partida,plugin,false,ganador);
				return false;
			}else {
				tiempo--;
				if(ganador != null) {
					PartidaManager.lanzarFuegos(ganador.getJugadores());
				}
				return true;
			}
		}else {
			return false;
		}
	}
}
