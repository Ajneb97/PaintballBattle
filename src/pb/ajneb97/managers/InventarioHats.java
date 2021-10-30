package pb.ajneb97.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import pb.ajneb97.PaintballBattle;
import pb.ajneb97.api.Hat;
import pb.ajneb97.api.PaintballAPI;
import pb.ajneb97.database.JugadorDatos;
import pb.ajneb97.database.MySQL;
import pb.ajneb97.utils.UtilidadesItems;

public class InventarioHats implements Listener{

	PaintballBattle plugin;
	public InventarioHats(PaintballBattle plugin) {
		this.plugin = plugin;
	}
	
	public static void crearInventario(Player jugador,PaintballBattle plugin) {
		FileConfiguration config = plugin.getConfig();
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', config.getString("hats_inventory_title")));
		ArrayList<Hat> hats = PaintballAPI.getHats(jugador);
		int slot = 0;
		if(hats.isEmpty()) {
			ItemStack item = UtilidadesItems.crearItem(config, "hats_items.no_hats");
			inv.setItem(13, item);
		}else {
			FileConfiguration messages = plugin.getMessages();
			for(Hat h : hats) {
				String name = h.getName();
				ItemStack item = UtilidadesItems.crearItem(config, "hats_items."+name);
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.getLore();
				String status = "";
				if(h.isSelected()) {
					status = messages.getString("hatStatusSelected");
				}else {
					status = messages.getString("hatStatusNotSelected");
				}
				for(int i=0;i<lore.size();i++) {
					lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i).replace("%status%", status)));
				}
				meta.setLore(lore);
				item.setItemMeta(meta);
				
				if(config.contains("hats_items."+name+".skull_id")) {
					String id = config.getString("hats_items."+name+".skull_id");
					String textura = config.getString("hats_items."+name+".skull_texture");
					item = UtilidadesItems.getCabeza(item, id, textura);
				}
				
				inv.setItem(slot, item);
				slot++;
			}
			
			ItemStack item = UtilidadesItems.crearItem(config, "hats_items.remove_hat");
			inv.setItem(26, item);
		}
		
		jugador.openInventory(inv);
	}
	
	@EventHandler
	public void clickInventario(InventoryClickEvent event){
		FileConfiguration config = plugin.getConfig();
		String pathInventory = ChatColor.translateAlternateColorCodes('&', config.getString("hats_inventory_title"));
		String pathInventoryM = ChatColor.stripColor(pathInventory);
		FileConfiguration messages = plugin.getMessages();
		String prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix"))+" ";
		if(ChatColor.stripColor(event.getView().getTitle()).equals(pathInventoryM)){
			if(event.getCurrentItem() == null){
				event.setCancelled(true);
				return;
			}
			if((event.getSlotType() == null)){
				event.setCancelled(true);
				return;
			}else{
				final Player jugador = (Player) event.getWhoClicked();
				event.setCancelled(true);
				if(event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
					ArrayList<Hat> hats = PaintballAPI.getHats(jugador);
					ItemStack item = event.getCurrentItem();
					if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
						if(event.getSlot() == 26) {
							if(MySQL.isEnabled(config)) {
								MySQL.deseleccionarHats(plugin, jugador.getName());
							}else {
								JugadorDatos jDatos = plugin.getJugador(jugador.getName());
								jDatos.deseleccionarHats();
							}
							jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("hatRemoved")));
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() {
									InventarioHats.crearInventario(jugador, plugin);
								}
							}, 5L);
							return;
						}
						for(Hat h : hats) {
							ItemStack itemConfig = UtilidadesItems.crearItem(config, "hats_items."+h.getName());
							ItemMeta meta = item.getItemMeta();
							ItemMeta metaConfig = itemConfig.getItemMeta();
							if(item.getType().equals(itemConfig.getType()) && meta.getDisplayName().equals(metaConfig.getDisplayName())) {
								//Seleccionar hat
								if(PaintballAPI.hasHatSelected(jugador, h.getName())) {
									jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("hatAlreadySelected")));
									return;
								}
								if(MySQL.isEnabled(config)) {
									MySQL.seleccionarHatAsync(plugin, jugador.getName(), h.getName());
								}else {
									JugadorDatos jDatos = plugin.getJugador(jugador.getName());
									jDatos.seleccionarHat(h.getName());
								}
								jugador.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("hatSelected").replace("%name%", config.getString("hats_items."+h.getName()+".name"))));
								Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									public void run() {
										InventarioHats.crearInventario(jugador, plugin);
									}
								}, 5L);
								return;
							}
							
						}
					}
				}
			}
		}
	}
}
