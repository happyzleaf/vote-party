package com.happyzleaf.voteparty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(id = VoteParty.MOD_ID, name = VoteParty.MOD_NAME, version = VoteParty.VERSION,
		authors = {"aust101", "happyzleaf"})
public class VoteParty {
	public static final String MOD_ID = "voteparty";
	public static final String MOD_NAME = "VoteParty";
	public static final String VERSION = "2.1.3";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static VoteParty instance;

	// Modules
	public static Config config;
	public static VoteListener listener;
	public static Placeholders placeholders = null; // Remains null if papi is missing.

	@Listener
	public void preInit(GamePreInitializationEvent event) {
		instance = this;

		listener = new VoteListener();
		listener.register();

		config = new Config();
		config.load();
	}

	@Listener
	public void init(GameInitializationEvent event) {
		if (Sponge.getPluginManager().isLoaded("placeholderapi")) {
			placeholders = new Placeholders();

			if (placeholders.load()) {
				LOGGER.info("PlaceholderAPI found and correctly loaded.");
			} else {
				LOGGER.error("There was a problem with PlaceholderAPI. Try to use a different version or contact the author.");
			}
		} else {
			LOGGER.info("Missing PlaceholderAPI. Some functionalities will remain disabled.");
		}

		Sponge.getCommandManager().register(this,
				CommandSpec.builder().
						description(Text.of("Adds fake votes to the counter."))
						.permission(MOD_ID + ".command.fakevote")
						.arguments(
								GenericArguments.optional(GenericArguments.integer(Text.of("votes")))
						)
						.executor((src, args) -> {
							int votes = (int) args.getOne("votes").orElse(1);

							config.incrementCounter(votes);

							return CommandResult.success();
						})
						.build()
				, "fakevote");

		Sponge.getCommandManager().register(this,
				CommandSpec.builder()
						.permission(MOD_ID + ".command.fakevotereward")
						.description(Text.of("Triggers the vote's goal rewards like if the goal would have been reached."))
						.executor((src, args) -> {
							config.giveRewards();

							return CommandResult.success();
						})
						.build(),
				"fakevotesreward");


		LOGGER.info("Loaded. This plugin was made by happyzleaf. Originally from aust101. (http://www.happyzleaf.com/)");
	}

	@Listener
	public void reload(GameReloadEvent event) {
		config.save(true);
		config.load();
	}

	@Listener
	public void serverStopping(GameStoppingServerEvent event) {
		config.save(true);
	}
}
