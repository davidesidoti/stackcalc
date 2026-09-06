package it.davide.stackcalc;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/** Roba condivisa tra GUI e comando: conversioni, colori e formattazione. */
public final class StackCalc {

	public static final String MOD_ID = "stackcalc";

	// palette condivisa (RGB, senza alpha)
	public static final int COLOR_SHULKER = 0xAA66FF;
	public static final int COLOR_STACK = 0xFFAA00;
	public static final int COLOR_ITEM = 0x55FFFF;
	public static final int COLOR_SEP = 0x808080;

	private StackCalc() {
	}

	/**
	 * Scompone il totale in [shulker, stack, resto].
	 * Con perStack <= 1 finisce tutto nel resto.
	 */
	public static long[] parts(long totale, int perStack) {
		if (perStack <= 1) return new long[]{0L, 0L, totale};
		long perShulker = perStack * 27L;
		return new long[]{
				totale / perShulker,
				(totale % perShulker) / perStack,
				totale % perStack
		};
	}

	/** Testo tradotto di un singolo pezzo, es. "4 stack". */
	public static Component unit(String key, long n) {
		return Component.translatable("stackcalc.unit." + key, n);
	}

	/** Breakdown colorato pronto per la chat. */
	public static MutableComponent breakdown(long totale, int perStack) {
		if (totale == 0) {
			return Component.translatable("stackcalc.unit.zero")
					.withStyle(Style.EMPTY.withColor(COLOR_ITEM));
		}

		long[] p = parts(totale, perStack);
		MutableComponent out = Component.empty();
		boolean primo = true;

		if (p[0] > 0) {
			out.append(unit("shulker", p[0]).copy().withStyle(Style.EMPTY.withColor(COLOR_SHULKER)));
			primo = false;
		}
		if (p[1] > 0) {
			if (!primo) out.append(sep());
			out.append(unit("stack", p[1]).copy().withStyle(Style.EMPTY.withColor(COLOR_STACK)));
			primo = false;
		}
		if (p[2] > 0) {
			if (!primo) out.append(sep());
			out.append(unit("item", p[2]).copy().withStyle(Style.EMPTY.withColor(COLOR_ITEM)));
		}
		return out;
	}

	private static Component sep() {
		return Component.literal(" + ").withStyle(Style.EMPTY.withColor(COLOR_SEP));
	}

	/** Toglie il .0 dai risultati interi. */
	public static String trim(double d) {
		if (d == Math.rint(d) && Math.abs(d) < 1e15) {
			return String.valueOf((long) d);
		}
		return String.valueOf(Math.round(d * 1000d) / 1000d);
	}
}
