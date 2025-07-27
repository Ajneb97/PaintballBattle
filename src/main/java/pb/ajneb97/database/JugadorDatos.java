package pb.ajneb97.database;

import java.util.ArrayList;

import pb.ajneb97.api.Hat;
import pb.ajneb97.api.Perk;

public class JugadorDatos {

	private int wins;
	private int loses;
	private int ties;
	private int kills;
	private int coins;
	private String name;
	private String uuid;
	private ArrayList<Perk> perks;
	private ArrayList<Hat> hats;
	
	public JugadorDatos(String name, String uuid, int wins, int loses, int ties, int kills, int coins, ArrayList<Perk> perks, ArrayList<Hat> hats) {
		this.wins = wins;
		this.loses = loses;
		this.ties = ties;
		this.kills = kills;
		this.coins = coins;
		this.name = name;
		this.uuid = uuid;
		this.perks = perks;
		this.hats = hats;
	}

	public ArrayList<Hat> getHats(){
		return this.hats;
	}
	
	public void agregarHat(String hat) {
		hats.add(new Hat(hat,false));
	}
	
	public boolean tieneHat(String hat) {
		for(int i=0;i<hats.size();i++) {
			if(hats.get(i).getName().equals(hat)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean tieneHatSeleccionado(String hat) {
		for(int i=0;i<hats.size();i++) {
			if(hats.get(i).getName().equals(hat) && hats.get(i).isSelected()) {
				return true;
			}
		}
		return false;
	}
	
	public void deseleccionarHats() {
		for(int i=0;i<hats.size();i++) {
			hats.get(i).setSelected(false);
		}
	}
	
	public void seleccionarHat(String hat) {
		for(int i=0;i<hats.size();i++) {
			hats.get(i).setSelected(false);
			if(hats.get(i).getName().equals(hat)) {
				hats.get(i).setSelected(true);
			}
		}
	}
	
	public ArrayList<Perk> getPerks(){
		return this.perks;
	}
	
	public void setPerk(String perk,int level) {
		for(int i=0;i<perks.size();i++) {
			if(perks.get(i).getName().equals(perk)) {
				perks.get(i).setLevel(level);
				return;
			}
		}
		perks.add(new Perk(perk,level));
	}
	
	public int getNivelPerk(String perk) {
		for(int i=0;i<perks.size();i++) {
			if(perks.get(i).getName().equals(perk)) {
				return perks.get(i).getNivel();
			}
		}
		return 0;
	}
	
	public String getUUID() {
		return this.uuid;
	}

	public int getWins() {
		return wins;
	}

	public int getLoses() {
		return loses;
	}

	public int getTies() {
		return ties;
	}

	public int getKills() {
		return kills;
	}
	
	public int getCoins() {
		return coins;
	}

	public String getName() {
		return name;
	}
	
	public void aumentarWins() {
		this.wins++;
	}
	
	public void aumentarLoses() {
		this.loses++;
	}
	
	public void aumentarTies() {
		this.ties++;
	}
	
	public void aumentarCoins(int coins) {
		this.coins = this.coins+coins;
	}
	
	public void disminuirCoins(int coins) {
		this.coins = this.coins-coins;
	}
	
	public void aumentarKills(int kills) {
		this.kills = this.kills+kills;
	}
}
