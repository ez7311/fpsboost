package id.fpsboost;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FpsBoostClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("fpsboost");

	private FpsBoostConfig config;
	private int savedFpsLimit = -1;

	@Override
	public void onInitializeClient() {
		config = FpsBoostConfig.load();

		ClientLifecycleEvents.CLIENT_STARTED.register(mc -> {
			if (config.autoApplyOnStart) {
				Optimizer.apply(mc, config, Preset.parse(config.preset));
				LOGGER.info("Preset {} diterapkan", config.preset);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(this::tickFocus);

		ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> releaseFpsLimit(mc));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerCommands(dispatcher));
	}

	private void tickFocus(Minecraft mc) {
		if (mc.options == null) {
			return;
		}
		boolean inactive = !mc.isWindowActive();
		if (config.limitWhenUnfocused && inactive && savedFpsLimit < 0) {
			savedFpsLimit = mc.options.framerateLimit().get();
			mc.options.framerateLimit().set(Math.max(1, config.unfocusedFpsLimit));
		} else if ((!inactive || !config.limitWhenUnfocused) && savedFpsLimit >= 0) {
			releaseFpsLimit(mc);
		}
	}

	private void releaseFpsLimit(Minecraft mc) {
		if (savedFpsLimit >= 0 && mc.options != null) {
			mc.options.framerateLimit().set(savedFpsLimit);
			savedFpsLimit = -1;
		}
	}

	private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(ClientCommandManager.literal("fpsboost")
			.executes(ctx -> {
				msg(ctx.getSource(), "FPS Boost - preset aktif: " + config.preset
					+ " | auto-apply: " + config.autoApplyOnStart
					+ " | limit saat tidak fokus: " + config.limitWhenUnfocused + " (" + config.unfocusedFpsLimit + " FPS)");
				msg(ctx.getSource(), "Perintah: /fpsboost balanced | extreme | restore | auto <true/false> | unfocused <true/false>");
				return 1;
			})
			.then(ClientCommandManager.literal("balanced").executes(ctx -> applyPreset(ctx.getSource(), Preset.BALANCED)))
			.then(ClientCommandManager.literal("extreme").executes(ctx -> applyPreset(ctx.getSource(), Preset.EXTREME)))
			.then(ClientCommandManager.literal("restore").executes(ctx -> {
				boolean ok = Optimizer.restore(Minecraft.getInstance(), config);
				msg(ctx.getSource(), ok ? "Pengaturan asli dikembalikan." : "Belum ada cadangan pengaturan asli.");
				return ok ? 1 : 0;
			}))
			.then(ClientCommandManager.literal("auto")
				.then(ClientCommandManager.argument("nilai", BoolArgumentType.bool()).executes(ctx -> {
					config.autoApplyOnStart = BoolArgumentType.getBool(ctx, "nilai");
					config.save();
					msg(ctx.getSource(), "Auto-apply saat start: " + config.autoApplyOnStart);
					return 1;
				})))
			.then(ClientCommandManager.literal("unfocused")
				.then(ClientCommandManager.argument("nilai", BoolArgumentType.bool()).executes(ctx -> {
					config.limitWhenUnfocused = BoolArgumentType.getBool(ctx, "nilai");
					config.save();
					msg(ctx.getSource(), "Limit FPS saat tidak fokus: " + config.limitWhenUnfocused);
					return 1;
				}))));
	}

	private int applyPreset(FabricClientCommandSource src, Preset p) {
		Optimizer.apply(Minecraft.getInstance(), config, p);
		msg(src, "Preset " + p.name() + " diterapkan. Gunakan /fpsboost restore untuk kembali ke pengaturan awal.");
		return 1;
	}

	private static void msg(FabricClientCommandSource src, String text) {
		src.sendFeedback(Component.literal(text));
	}
        }
