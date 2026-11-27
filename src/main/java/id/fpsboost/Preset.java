package id.fpsboost;

import net.minecraft.client.CloudStatus;
import net.minecraft.server.level.ParticleStatus;

public enum Preset {
	BALANCED(10, 8, ParticleStatus.DECREASED, CloudStatus.OFF,
		false, 1, 260, false, 0.75, 2, true),
	EXTREME(6, 5, ParticleStatus.MINIMAL, CloudStatus.OFF,
		false, 0, 260, false, 0.5, 0, false);

	public final int renderDistance;
	public final int simulationDistance;
	public final ParticleStatus particles;
	public final CloudStatus clouds;
	public final boolean entityShadows;
	public final int biomeBlend;
	public final int maxFps;
	public final boolean vsync;
	public final double entityDistance;
	public final int mipmap;
	public final boolean ao;

	Preset(int renderDistance, int simulationDistance, ParticleStatus particles,
		   CloudStatus clouds, boolean entityShadows, int biomeBlend, int maxFps, boolean vsync,
		   double entityDistance, int mipmap, boolean ao) {
		this.renderDistance = renderDistance;
		this.simulationDistance = simulationDistance;
		this.particles = particles;
		this.clouds = clouds;
		this.entityShadows = entityShadows;
		this.biomeBlend = biomeBlend;
		this.maxFps = maxFps;
		this.vsync = vsync;
		this.entityDistance = entityDistance;
		this.mipmap = mipmap;
		this.ao = ao;
	}

	public static Preset parse(String s) {
		try {
			return valueOf(s.toUpperCase());
		} catch (Exception e) {
			return BALANCED;
		}
	}
}
