package pb.ajneb97.managers;

import java.util.ArrayList;
import java.util.List;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;



import pb.ajneb97.PaintballBattle;
import pb.ajneb97.database.MySQL;
import pb.ajneb97.database.MySQLCallback;
import pb.ajneb97.utils.UtilidadesHologramas;

public class TopHologram {

	private String name;
	private String type;
	private Hologram hologram;
	private double yOriginal;
	private String period;

	public TopHologram(String name, String type, Location location, PaintballBattle plugin, String period) {
		this.type = type;
		this.name = name;
		this.period = period;
		this.yOriginal = location.getY();
		Location nuevaLoc = location.clone();
		nuevaLoc.setY(nuevaLoc.getY() + UtilidadesHologramas.determinarY(nuevaLoc, UtilidadesHologramas.getCantidadLineasHolograma(plugin)) + 1.4);
		this.hologram = DHAPI.createHologram(name, nuevaLoc, true);
	}

	public String getPeriod() {
		return this.period;
	}

	public double getyOriginal() {
		return yOriginal;
	}

	public void removeHologram() {
		this.hologram.delete();
	}

	public void spawnHologram(PaintballBattle plugin) {
		FileConfiguration messages = plugin.getMessages();
		FileConfiguration config = plugin.getConfig();
		final int topPlayersMax = Integer.valueOf(config.getString("top_hologram_number_of_players"));
		List<String> lineas = messages.getStringList("topHologramFormat");

		hologram.setDefaultVisibleState(true);
		//this.hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);

		String typeName = "";
		String periodName = "";
		if (type.equals("kills")) {
			typeName = messages.getString("topHologramTypeKills");
		} else {
			typeName = messages.getString("topHologramTypeWins");
		}
		if (period.equals("monthly")) {
			periodName = messages.getString("topHologramPeriodMonthly");
		} else if (period.equals("weekly")) {
			periodName = messages.getString("topHologramPeriodWeekly");
		} else {
			periodName = messages.getString("topHologramPeriodGlobal");
		}

		final String lineaMessage = messages.getString("topHologramScoreboardLine");
		for (int i = 0; i < lineas.size(); i++) {
			String linea = lineas.get(i).replace("%type%", typeName).replace("%period%", periodName);
			if (linea.contains("%scoreboard_lines%")) {
				if (MySQL.isEnabled(config) && !period.equals("global")) {
					UtilidadesHologramas.getTopPlayersSQL(plugin, type, period, new MySQLCallback() {
						@Override
						public void alTerminar(ArrayList<String> playersList) {
							for (int c = 0; c < topPlayersMax; c++) {
								int num = c + 1;
								try {
									String[] separados = playersList.get(c).split(";");
									DHAPI.addHologramLine(hologram,ChatColor.translateAlternateColorCodes('&', lineaMessage
											.replace("%position%", String.valueOf(num))
											.replace("%name%", separados[0])
											.replace("%points%", separados[1])));
								} catch (Exception e) {
									break;
								}
							}
						}
					});
				} else {
					UtilidadesHologramas.getTopPlayers(plugin, plugin.getJugadores(), type, new MySQLCallback() {
						@Override
						public void alTerminar(ArrayList<String> playersList) {
							for (int c = 0; c < topPlayersMax; c++) {
								int num = c + 1;
								try {
									String[] separados = playersList.get(c).split(";");
									DHAPI.addHologramLine(hologram,(ChatColor.translateAlternateColorCodes('&', lineaMessage
											.replace("%position%", String.valueOf(num))
											.replace("%name%", separados[0])
											.replace("%points%", separados[1]))));
								} catch (Exception e) {
									break;
								}
							}
						}
					});
				}
			} else {
				DHAPI.addHologramLine(hologram, ChatColor.translateAlternateColorCodes('&', linea));
				//hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&', linea));
			}
		}
	}

	public void actualizar(PaintballBattle plugin) {
		//Location loc = this.hologram.getPosition().toLocation();
		Location loc = this.hologram.getLocation();
		removeHologram();
		loc.setY(yOriginal);
		Location nuevaLoc = loc.clone();
		nuevaLoc.setY(nuevaLoc.getY() + UtilidadesHologramas.determinarY(nuevaLoc, UtilidadesHologramas.getCantidadLineasHolograma(plugin)) + 1.4);
		//this.hologram = HolographicDisplaysAPI.get(plugin).createHologram(nuevaLoc);
		this.hologram = DHAPI.createHologram(name,nuevaLoc);
		spawnHologram(plugin);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Hologram getHologram() {
		return hologram;
	}
}