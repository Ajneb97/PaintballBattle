package pb.ajneb97.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import pb.ajneb97.PaintballBattle;
/**
 * This class will automatically register as a placeholder expansion 
 * when a jar including this class is added to the directory 
 * {@code /plugins/PlaceholderAPI/expansions} on your server.
 * <br>
 * <br>If you create such a class inside your own plugin, you have to
 * register it manually in your plugins {@code onEbale()} by using 
 * {@code new YourExpansionClass().register();}
 */
public class ExpansionPaintballBattle extends PlaceholderExpansion {

    // We get an instance of the plugin later.
    private PaintballBattle plugin;

    public ExpansionPaintballBattle(PaintballBattle plugin) {
    	this.plugin = plugin;
    }
    
    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }
    /**
     * Since this expansion requires api access to the plugin "SomePlugin" 
     * we must check if said plugin is on the server or not.
     *
     * @return true or false depending on if the required plugin is installed.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return "Ajneb97";
    }
 
    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "paintball";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }
  
    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        if(identifier.equals("wins")){
            return PaintballAPI.getWins(player)+"";
        }
        
        if(identifier.equals("loses")){
            return PaintballAPI.getLoses(player)+"";
        }
        
        if(identifier.equals("ties")){
            return PaintballAPI.getTies(player)+"";
        }
        
        if(identifier.equals("coins")){
            return PaintballAPI.getCoins(player)+"";
        }
        
        if(identifier.equals("kills")){
            return PaintballAPI.getKills(player)+"";
        }
        
        if(identifier.startsWith("arenaplayers_count_")){
            String arena = identifier.replace("arenaplayers_count_", "");
            return PaintballAPI.getPlayersArena(arena)+"";
        }
        
        if(identifier.startsWith("arena_status_")){
            String arena = identifier.replace("arena_status_", "");
            return PaintballAPI.getStatusArena(arena)+"";
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) 
        // was provided
        return null;
    }
}
