package com.happyzleaf.voteparty;

import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import org.spongepowered.api.Sponge;

public class Placeholders {
	public boolean load() {
		PlaceholderService service = Sponge.getServiceManager().provide(PlaceholderService.class).orElse(null);
		if (service == null) {
			return false;
		}

		service.loadAll(this, VoteParty.instance).forEach(builder -> {
			try {
				builder.plugin(VoteParty.instance).author("happyzleaf").version(VoteParty.VERSION).description("VoteParty's left votes.").url("http://happyzleaf.com/").buildAndRegister();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return true;
	}

	@Placeholder(id = "votesleft")
	public int votesleft() {
		return VoteParty.config.getVotesLeft();
	}
}
