package pb.ajneb97.managers;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import me.clip.placeholderapi.PlaceholderAPI;
import pb.ajneb97.PaintballBattle;
import pb.ajneb97.juego.Equipo;
import pb.ajneb97.juego.EstadoPartida;
import pb.ajneb97.juego.JugadorPaintball;
import pb.ajneb97.juego.Partida;
import pb.ajneb97.lib.fastboard.FastBoard;
import pb.ajneb97.utils.UtilidadesOtros;

public class ScoreboardAdmin {
	
	private int taskID;
	private PaintballBattle plugin;
	private final Map<UUID, FastBoard> boards = new HashMap<>();
	public ScoreboardAdmin(PaintballBattle plugin){		
		this.plugin = plugin;		
	}
	
	public int getTaskID() {
		return this.taskID;
	}
	
	public void crearScoreboards() {
	    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	    final FileConfiguration messages = plugin.getMessages();
    	final FileConfiguration config = plugin.getConfig();
 	    taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() { 
            	for(Player player : Bukkit.getOnlinePlayers()) {
            		actualizarScoreboard(player,messages,config);
                }
            }
        },0, 20L);
	}
	
	protected void actualizarScoreboard(final Player player,final FileConfiguration messages,final FileConfiguration config) {
		Partida partida = plugin.getPartidaJugador(player.getName());
		FastBoard board = boards.get(player.getUniqueId());
		if(partida != null) {
			JugadorPaintball jugador = partida.getJugador(player.getName());
			if(board == null) {
				board = new FastBoard(player);
				board.updateTitle(ChatColor.translateAlternateColorCodes('&',messages.getString("gameScoreboardTitle")));
				boards.put(player.getUniqueId(), board);
			}
			
			List<String> lista = messages.getStringList("gameScoreboardBody");
			Equipo equipo1 = partida.getTeam1();
			Equipo equipo2 = partida.getTeam2();
			String equipo1Nombre = config.getString("teams."+equipo1.getTipo()+".name");
			String equipo2Nombre = config.getString("teams."+equipo2.getTipo()+".name");

			for(int i=0;i<lista.size();i++) {
				String message = ChatColor.translateAlternateColorCodes('&', lista.get(i).replace("%status%", getEstado(partida,messages)).replace("%team_1%", equipo1Nombre)
						.replace("%team_2%", equipo2Nombre).replace("%team_1_lives%", equipo1.getVidas()+"").replace("%team_2_lives%", equipo2.getVidas()+"")
						.replace("%kills%", jugador.getAsesinatos()+"").replace("%arena%", partida.getNombre()).replace("%current_players%", partida.getCantidadActualJugadores()+"")
						.replace("%max_players%", partida.getCantidadMaximaJugadores()+""));
				if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI")!= null){
					message = PlaceholderAPI.setPlaceholders(player, message);
				}
				board.updateLine(i, message);
			}
		}else {
			if(board != null) {
				boards.remove(player.getUniqueId());
				board.delete();
			}
		}
	}
	
	private String getEstado(Partida partida,FileConfiguration messages) {
		//Remplazar variables del %time%
		if(partida.getEstado().equals(EstadoPartida.ESPERANDO)) {
			return messages.getString("statusWaiting");
		}else if(partida.getEstado().equals(EstadoPartida.COMENZANDO)) {
			int tiempo = partida.getTiempo();
			return messages.getString("statusStarting").replace("%time%", UtilidadesOtros.getTiempo(tiempo));
		}else if(partida.getEstado().equals(EstadoPartida.TERMINANDO)) {
			int tiempo = partida.getTiempo();
			return messages.getString("statusFinishing").replace("%time%", UtilidadesOtros.getTiempo(tiempo));
		}else {
			int tiempo = partida.getTiempo();
			return messages.getString("statusIngame").replace("%time%", UtilidadesOtros.getTiempo(tiempo));
		}
	}

}
