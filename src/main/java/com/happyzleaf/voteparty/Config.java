package com.happyzleaf.voteparty;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigRoot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The code is all mine (happyz), but the I kept the schema from the original author (aust101).
 */
public class Config {
	private final Path path;
	private final ConfigurationLoader<CommentedConfigurationNode> loader;

	private int goal = 350;
	private int counter = 0;

	private String complete = "&b[&e&lVOTEPARTY&b] &aThe VoteParty was successfully reached and rewards have been given!\n&e&lRewards:\n  &f- &6Random Shiny to all online\n  &f- &6$5000 in-game cash";
	private Text completeText = TextSerializers.FORMATTING_CODE.deserialize(complete);

	private List<String> commands = new ArrayList<>(Arrays.asList(
			"pokegive %player% random s",
			"say %randomplayer% is the best!"
	));

	public Config() {
		ConfigRoot root = Sponge.getConfigManager().getSharedConfig(VoteParty.instance);
		path = root.getConfigPath();
		loader = root.getConfig();
	}

	public void load() {
		if (Files.notExists(path)) {
			save(false);
		}

		try {
			ConfigurationNode node = loader.load();

			counter = node.getNode("data", "counter").getInt(counter);

			goal = node.getNode("goal").getInt(goal);
			complete = node.getNode("messages", "on-complete").getString(complete);
			completeText = TextSerializers.FORMATTING_CODE.deserialize(complete);

			commands.clear();
			commands.addAll(node.getNode("reward-commands").getList(TypeToken.of(String.class), commands));
		} catch (Exception e) {
			VoteParty.LOGGER.error("There was a problem while trying to load the configuration.", e);
		}
	}

	public void save(boolean onlyCounter) {
		try {
			CommentedConfigurationNode node = loader.load();

			node.getNode("data", "counter").setValue(counter);
			if (onlyCounter) {
				return;
			}

			node.getNode("goal").setValue(goal);
			node.getNode("messages", "on-complete").setValue(complete);
			node.getNode("reward-commands").setComment("You can use %player% to get the player's nickname, and %randomplayer% to get a random player from the server, but you can't use both in the same command!").setValue(commands);

			loader.save(node);
		} catch (IOException e) {
			VoteParty.LOGGER.error("There was a problem while trying to save the configuration.", e);
		}
	}

	public int getVotesLeft() {
		return goal - counter;
	}

	public int getCounter() {
		return counter;
	}

	public void incrementCounter(int increment) {
		counter += increment;
		if (counter < 0) {
			counter = 0;
		}

		testCounter();
	}

	private void testCounter() {
		if (counter >= goal) {
			Task.builder().execute(this::giveRewards).submit(VoteParty.instance);
		}
	}

	public void giveRewards() {
		counter = 0;

		for (String c : commands) {
			if (c.contains("%player%")) {
				Sponge.getServer().getOnlinePlayers().forEach(player ->
						Sponge.getCommandManager().process(Sponge.getServer().getConsole(), c.replace("%player%", player.getName()))
				);
			} else if (c.contains("%randomplayer%")) {
				Player random = Utils.getRandomElement(Sponge.getServer().getOnlinePlayers());
				if (random != null) {
					Sponge.getCommandManager().process(Sponge.getServer().getConsole(), c.replace("%randomplayer%", random.getName()));
				}
			} else {
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(), c);
			}
		}

		MessageChannel.TO_PLAYERS.send(completeText);
	}
}
