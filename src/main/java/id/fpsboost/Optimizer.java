package id.fpsboost;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;

public final class Optimizer {
	private Optimizer() {
	}

	public static void apply(Minecraft mc, FpsBoostConfig cfg, Preset p) {
		Options o = mc.options;
		if (cfg.backup == null) {
			cfg.backup = capture(o);
		}

		set(o.renderDistance(), p.renderDistance);
		set(o.simulationDistance(), p.simulationDistance);
		set(o.graphicsMode(), p.graphics);
		set(o.particles(), p.particles);
		set(o.cloudStatus(), p.clouds);
		set(o.entityShadows(), p.entityShadows);
		set(o.biomeBlendRadius(), p.biomeBlend);
		set(o.framerateLimit(), p.maxFps);
		set(o.enableVsync(), p.vsync);
		set(o.entityDistanceScaling(), p.entityDistance);
		set(o.mipmapLevels(), p.mipmap);
		set(o.ambientOcclusion(), p.ao);

		cfg.preset = p.name();
		cfg.save();
		o.save();
	}

	public static boolean restore(Minecraft mc, FpsBoostConfig cfg) {
		FpsBoostConfig.Backup b = cfg.backup;
		if (b == null) {
			return false;
		}
		Options o = mc.options;
		set(o.renderDistance(), b.renderDistance);
		set(o.simulationDistance(), b.simulationDistance);
		set(o.graphicsMode(), GraphicsStatus.valueOf(b.graphics));
		set(o.particles(), ParticleStatus.valueOf(b.particles));
		set(o.cloudStatus(), CloudStatus.valueOf(b.clouds));
		set(o.entityShadows(), b.shadows);
		set(o.biomeBlendRadius(), b.biomeBlend);
		set(o.framerateLimit(), b.maxFps);
		set(o.enableVsync(), b.vsync);
		set(o.entityDistanceScaling(), b.entityDistance);
		set(o.mipmapLevels(), b.mipmap);
		set(o.ambientOcclusion(), b.ao);

		cfg.backup = null;
		cfg.autoApplyOnStart = false;
		cfg.save();
		o.save();
		return true;
	}

	private static FpsBoostConfig.Backup capture(Options o) {
		FpsBoostConfig.Backup b = new FpsBoostConfig.Backup();
		b.renderDistance = o.renderDistance().get();
		b.simulationDistance = o.simulationDistance().get();
		b.graphics = o.graphicsMode().get().name();
		b.particles = o.particles().get().name();
		b.clouds = o.cloudStatus().get().name();
		b.shadows = o.entityShadows().get();
		b.biomeBlend = o.biomeBlendRadius().get();
		b.maxFps = o.framerateLimit().get();
		b.vsync = o.enableVsync().get();
		b.entityDistance = o.entityDistanceScaling().get();
		b.mipmap = o.mipmapLevels().get();
		b.ao = o.ambientOcclusion().get();
		return b;
	}

	private static <T> void set(OptionInstance<T> option, T value) {
		try {
			option.set(value);
		} catch (Exception e) {
			FpsBoostClient.LOGGER.warn("Gagal mengubah opsi: {}", value, e);
		}
	}
        }
