package pb.ajneb97.juego;

import org.bukkit.entity.Player;

public class PartidaEditando {

	private Player jugador;
	private Partida partida;
	private String paso;
	public PartidaEditando(Player jugador, Partida partida) {
		this.jugador = jugador;
		this.partida = partida;
		this.paso = "";
	}
	public Player getJugador() {
		return jugador;
	}
	public void setJugador(Player jugador) {
		this.jugador = jugador;
	}
	public Partida getPartida() {
		return partida;
	}
	public void setPartida(Partida partida) {
		this.partida = partida;
	}
	public void setPaso(String paso) {
		this.paso = paso;
	}
	public String getPaso() {
		return this.paso;
	}
}
