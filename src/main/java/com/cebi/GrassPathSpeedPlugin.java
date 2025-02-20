package com.cebi;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrassPathSpeedPlugin extends JavaPlugin {

    // Oyuncuya verilecek Speed II efektinin süresi (tick); 20 tick = 1 saniye
    private final int effectDuration = 60; // 3 saniye
    // Speed II için amplifier = 1
    private final int amplifier = 1;
    // Blok temasını kaybettikten sonra şu kadar süre (ms) efekt devam etsin
    private final long gracePeriodMillis = 1000; // 1 saniye

    // Her oyuncu için son “path üzerinde olduğu” zaman (milisaniye)
    private final Map<UUID, Long> lastOnPathTime = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("GrassPathSpeedPlugin etkinleştirildi.");

        // Her 5 tickte (0.25 saniyede bir) tüm oyuncuları kontrol eden periyodik görev
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            long now = System.currentTimeMillis();

            for (Player player : Bukkit.getOnlinePlayers()) {
                // Oyuncu path üzerinde mi? (Köşe kontrolü || alt blok kontrolü)
                if (isFullyOnDirtPath(player) || isAboveDirtPath(player)) {

                    // Path üzerindeyse "son path zamanı" güncellenir
                    lastOnPathTime.put(player.getUniqueId(), now);

                    // Efekti her döngüde 3 saniyelik şekilde yenileyerek asla sıfırlanmamasını sağlıyoruz
                    player.addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, effectDuration, amplifier, true, false, false),
                        true
                    );

                } else {
                    // Path üzerinde değil
                    Long lastTime = lastOnPathTime.get(player.getUniqueId());
                    if (lastTime != null && (now - lastTime) <= gracePeriodMillis) {
                        // Grace period içerisindeyse efekt devam etsin
                        player.addPotionEffect(
                            new PotionEffect(PotionEffectType.SPEED, effectDuration, amplifier, true, false, false),
                            true
                        );
                    } else {
                        // Grace bitti, efekti kaldır
                        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                            player.removePotionEffect(PotionEffectType.SPEED);
                        }
                        lastOnPathTime.remove(player.getUniqueId());
                    }
                }
            }
        }, 0L, 5L);
    }

    @Override
    public void onDisable() {
        getLogger().info("GrassPathSpeedPlugin devre dışı bırakıldı.");
    }

    /**
     * 1) KÖŞE KONTROLÜ (Corner Check):
     *    Oyuncu yatayda tamamen Dirt Path üzerinde mi?
     *    4 köşesinde de DIRT_PATH varsa true, yoksa false döner
     */
    private boolean isFullyOnDirtPath(Player player) {
        double y = player.getLocation().getY() - 0.05; // minik offset
        double x = player.getLocation().getX();
        double z = player.getLocation().getZ();
        World world = player.getWorld();

        double halfWidth = 0.3; // oyuncu yarıçapı
        double[][] corners = {
            { x + halfWidth, z + halfWidth },
            { x + halfWidth, z - halfWidth },
            { x - halfWidth, z + halfWidth },
            { x - halfWidth, z - halfWidth }
        };

        for (double[] corner : corners) {
            int blockX = (int) Math.floor(corner[0]);
            int blockY = (int) Math.floor(y);
            int blockZ = (int) Math.floor(corner[1]);

            Material mat = world.getBlockAt(blockX, blockY, blockZ).getType();
            if (mat != Material.DIRT_PATH) {
                return false;
            }
        }
        return true;
    }

    /**
     * 2) ALT BLOK KONTROLÜ (Single Block Check):
     *    Ayaklarımızın tam altındaki blok DIRT_PATH ise
     *    ve çok yükselmediysek (zıplama anında) "pathte" sayalım.
     */
    private boolean isAboveDirtPath(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        int bx = loc.getBlockX();
        int by = loc.getBlockY() - 1;
        int bz = loc.getBlockZ();

        Material mat = world.getBlockAt(bx, by, bz).getType();
        if (mat == Material.DIRT_PATH) {
            // Oyuncu ne kadar yukarıda?
            double footY = loc.getY() - by;
            // Normal zıplama ~1.2-1.3. Biraz tolerans (1.4)
            return (footY <= 1.4);
        }
        return false;
    }
}
