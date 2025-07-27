package pb.ajneb97.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

public class ValueOfPatch {

    public static Sound valueOf(String soundName){
        // like "block.note_block.harp"
        NamespacedKey key = NamespacedKey.minecraft(soundName.toLowerCase());

        Sound sound = Registry.SOUNDS.get(key);
        if (sound == null) {
            Bukkit.getLogger().warning("Sound "+soundName+" not found");
        }
        return sound;
    }

}
