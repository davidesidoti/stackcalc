package it.davide.stackcalc;

import net.minecraft.network.chat.Component;

/**
 * Errore del parser che si porta dietro un messaggio traducibile,
 * cosi' GUI e comando possono mostrarlo nella lingua del giocatore.
 */
public class CalcException extends RuntimeException {

	private final Component component;

	public CalcException(String key, Object... args) {
		super(key);
		this.component = Component.translatable(key, args);
	}

	public Component component() {
		return this.component;
	}
}
