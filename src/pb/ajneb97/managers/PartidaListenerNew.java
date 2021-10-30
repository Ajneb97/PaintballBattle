package pb.ajneb97.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import pb.ajneb97.PaintballBattle;
import pb.ajneb97.juego.Partida;

public class PartidaListenerNew implements Listener{

	PaintballBattle plugin;
	public PartidaListenerNew(PaintballBattle plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void alCambiarDeMano(PlayerSwapHandItemsEvent event) {
		Player jugador = event.getPlayer();
		Partida partida = plugin.getPartidaJugador(jugador.getName());
		if(partida != null) {
			event.setCancelled(true);
		}
	}
}
