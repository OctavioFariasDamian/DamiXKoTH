package ar.com.octaviofarias.koth.model;

import ar.com.octaviofarias.koth.utils.configuration.DamianConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

@Data
@AllArgsConstructor
public class KoTH {
    @NotNull
    private String name;
    private int captureTime;

    private @Nullable Location firstPosition, secondPosition;
    @NotNull
    private KoTHRewards rewards;
    @NotNull
    private File file;
    @NotNull
    private DamianConfig config;
    private Set<KoTHScheduler> schedulers;
}
