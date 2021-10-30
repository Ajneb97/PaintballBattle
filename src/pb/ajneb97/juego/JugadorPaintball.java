package pb.ajneb97.juego;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class JugadorPaintball {

	private Player jugador;
	private int asesinatos;
	private int muertes;
	private ElementosGuardados guardados;
	private boolean asesinadoRecientemente;
	private String preferenciaTeam;
	private int coins;
	private ArrayList<Killstreak> killstreaks;
	private Location deathLocation;
	
	private String selectedHat;
	private boolean efectoHatActivado;
	private boolean efectoHatEnCooldown;
	private int tiempoEfectoHat;
	private String lastKilledBy;
	
	@SuppressWarnings("deprecation")
	public JugadorPaintball(Player jugador) {
		this.jugador = jugador;
		this.guardados = new ElementosGuardados(jugador.getInventory().getContents().clone(),jugador.getEquipment().getArmorContents().clone(),jugador.getGameMode()
				,jugador.getExp(),jugador.getLevel(),jugador.getFoodLevel(),jugador.getHealth(),jugador.getMaxHealth(),jugador.getAllowFlight(),jugador.isFlying());
		this.asesinadoRecientemente = false;
		this.muertes = 0;
		this.asesinatos = 0;
		this.coins = 0;
		this.killstreaks = new ArrayList<Killstreak>();
		this.efectoHatActivado = false;
		this.efectoHatEnCooldown = false;
		this.tiempoEfectoHat = 0;
		this.selectedHat = "";
	}
	
	public String getLastKilledBy() {
		return lastKilledBy;
	}

	public void setLastKilledBy(String lastKilledBy) {
		this.lastKilledBy = lastKilledBy;
	}

	public boolean isEfectoHatActivado() {
		return efectoHatActivado;
	}

	public void setEfectoHatActivado(boolean efectoHatActivado) {
		this.efectoHatActivado = efectoHatActivado;
	}

	public boolean isEfectoHatEnCooldown() {
		return efectoHatEnCooldown;
	}

	public void setEfectoHatEnCooldown(boolean efectoHatEnCooldown) {
		this.efectoHatEnCooldown = efectoHatEnCooldown;
	}

	public int getTiempoEfectoHat() {
		return tiempoEfectoHat;
	}

	public void setTiempoEfectoHat(int tiempoEfectoHat) {
		this.tiempoEfectoHat = tiempoEfectoHat;
	}

	public void setSelectedHat(String hat) {
		this.selectedHat = hat;
	}
	
	public String getSelectedHat() {
		return this.selectedHat;
	}
	
	public void setDeathLocation(Location l) {
		this.deathLocation = l;
	}
	
	public Location getDeathLocation() {
		return this.deathLocation;
	}
	
	public void agregarKillstreak(Killstreak k) {
		this.killstreaks.add(k);
	}
	
	public Killstreak getKillstreak(String tipo) {
		for(Killstreak k : this.killstreaks) {
			if(k.getTipo().equals(tipo)) {
				return k;
			}
		}
		return null;
	}
	
	public void removerKillstreak(String tipo) {
		for(int i=0;i<killstreaks.size();i++) {
			if(killstreaks.get(i).getTipo().equals(tipo)) {
				this.killstreaks.remove(i);
			}
		}
	}
	
	public Killstreak getUltimaKillstreak() {
		if(killstreaks.isEmpty()) {
			return null;
		}else {
			return killstreaks.get(killstreaks.size()-1);
		}
	}
	
	public int getCoins() {
		return this.coins;
	}
	
	public void agregarCoins(int cantidad) {
		this.coins = this.coins+cantidad;
	}
	
	public void disminuirCoins(int cantidad) {
		this.coins = this.coins-cantidad;
	}
	
	public void setPreferenciaTeam(String team) {
		this.preferenciaTeam = team;
	}
	
	public String getPreferenciaTeam() {
		return this.preferenciaTeam;
	}
	
	public ElementosGuardados getGuardados() {
		return this.guardados;
	}
	
	public void aumentarAsesinatos() {
		this.asesinatos++;
	}
	
	public void aumentarMuertes() {
		this.muertes++;
	}
	
	public int getAsesinatos() {
		return this.asesinatos;
	}
	
	public int getMuertes() {
		return this.muertes;
	}
	
	public Player getJugador() {
		return this.jugador;
	}
	
	public void setAsesinadoRecientemente(boolean asesinadoRecientemente) {
		this.asesinadoRecientemente = asesinadoRecientemente;
	}
	
	public boolean haSidoAsesinadoRecientemente() {
		return this.asesinadoRecientemente;
	}
	
}
