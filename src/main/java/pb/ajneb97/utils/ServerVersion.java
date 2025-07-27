package pb.ajneb97.utils;

import org.bukkit.Bukkit;

public enum ServerVersion {
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3,
    v1_20_R4,
    v1_21_R1,
    v1_21_R2,
    v1_21_R3,
    v1_21_R4; // Added for Paper 1.21.8

    public boolean serverVersionGreaterEqualThan(ServerVersion version1, ServerVersion version2) {
        return version1.ordinal() >= version2.ordinal();
    }

    /**
     * Detect the server version safely.
     * This method will try to map the CraftBukkit package version to an enum constant.
     * If it cannot find one, it falls back to the latest known version.
     */
    public static ServerVersion detect() {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        // Bukkit.getLogger().info(pkg);
        // Typically ends with v1_x_Ry
        String detected = pkg.substring(pkg.lastIndexOf('.') + 1);

        try {
            ServerVersion version = ServerVersion.valueOf(detected);
            Bukkit.getLogger().info("[PaintballBattle] Detected server version: " + version.name());
            return version;
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().warning("[PaintballBattle] Unknown server version '" + detected + "', defaulting to v1_21_R4");
            return ServerVersion.v1_21_R4;
        }
    }
}
