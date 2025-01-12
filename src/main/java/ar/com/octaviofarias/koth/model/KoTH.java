package ar.com.octaviofarias.koth.model;

import ar.com.octaviofarias.koth.configuration.KoTHConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * This class contains the information of a server KoTH
 */
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
    private KoTHConfiguration config;
    private List<KoTHScheduler> schedulers;
}
