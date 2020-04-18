package com.happyzleaf.voteparty;

import com.vexsoftware.votifier.sponge.event.VotifierEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;

public class VoteListener implements EventListener<VotifierEvent> {
	public void register() {
		Sponge.getEventManager().registerListener(VoteParty.instance, VotifierEvent.class, this);
	}

	@Override
	public void handle(VotifierEvent event) throws Exception {
		VoteParty.config.incrementCounter(1);
	}
}
