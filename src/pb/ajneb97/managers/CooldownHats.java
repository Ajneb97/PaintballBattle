package pb.ajneb97.managers;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import pb.ajneb97.PaintballBattle;
import pb.ajneb97.juego.EstadoPartida;
import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.juego.Partida;

public class CooldownHats {

	int taskID;
	int tiempo;
	private JugadorPaintball jugador;
	private Partida partida;
	private PaintballBattle plugin;
	public CooldownHats(PaintballBattle plugin){		
		this.plugin = plugin;		
	}
	
	public void cooldownHat(final JugadorPaintball jugador, final Partida partida,int tiempo){
		this.jugador = jugador;
		this.tiempo = tiempo;
		this.partida = partida;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarCooldownHat()){
				FileConfiguration messages = plugin.getMessages();
				if(!partida.getEstado().equals(EstadoPartida.TERMINANDO)) {
					jugador.getJugador().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("hatCooldownFinished")));
				}
				
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}

	protected boolean ejecutarCooldownHat() {
		if(partida != null && partida.getEstado().equals(EstadoPartida.JUGANDO)) {
			if(tiempo <= 0) {
				jugador.setEfectoHatEnCooldown(false);
				return false;
			}else {
				tiempo--;
				jugador.setTiempoEfectoHat(tiempo);
				return true;
			}
		}else {
			jugador.setEfectoHatEnCooldown(false);
			return false;
		}
	}
	
	public void durationHat(final JugadorPaintball jugador, final Partida partida,int tiempo){
		this.jugador = jugador;
		this.tiempo = tiempo;
		this.partida = partida;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
		public void run(){
			if(!ejecutarDurationHat()){
				Bukkit.getScheduler().cancelTask(taskID);
				return;
			}	
 		   }
 	   }, 0L, 20L);
	}
	
	protected boolean ejecutarDurationHat() {
		if(partida != null && partida.getEstado().equals(EstadoPartida.JUGANDO)) {
			if(tiempo <= 0) {
				jugador.setEfectoHatActivado(false);
				return false;
			}else {
				tiempo--;
				return true;
			}
		}else {
			jugador.setEfectoHatActivado(false);
			return false;
		}
	}
}
