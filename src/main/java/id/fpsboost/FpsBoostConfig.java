package id.fpsboost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FpsBoostConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("fpsboost.json");

	public boolean autoApplyOnStart = true;
	public String preset = "BALANCED";
	public boolean limitWhenUnfocused = true;
	public int unfocusedFpsLimit = 15;
	public Backup backup = null;

	public static final class Backup {
		public int renderDistance;
		public int simulationDistance;
		public int maxFps;
		public int biomeBlend;
		public int mipmap;
		public double entityDistance;
		public boolean shadows;
		public boolean vsync;
		public boolean ao;
		public String graphics;
		public String particles;
		public String clouds;
	}

	public static FpsBoostConfig load() {
		try {
			if (Files.exists(FILE)) {
				FpsBoostConfig cfg = GSON.fromJson(Files.readString(FILE), FpsBoostConfig.class);
				if (cfg != null) {
					return cfg;
				}
			}
		} catch (Exception e) {
			FpsBoostClient.LOGGER.warn("Gagal membaca config, memakai default", e);
		}
		FpsBoostConfig cfg = new FpsBoostConfig();		cfg.save();
		return cfg;
	}

	public void save() {
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, GSON.toJson(this));
		} catch (IOException e) {
			FpsBoostClient.LOGGER.warn("Gagal menyimpan config", e);
		}
	}
}
