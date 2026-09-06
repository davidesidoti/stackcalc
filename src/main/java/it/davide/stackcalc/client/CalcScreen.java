package it.davide.stackcalc.client;

import it.davide.stackcalc.Calc;
import it.davide.stackcalc.CalcException;
import it.davide.stackcalc.StackCalc;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CalcScreen extends Screen {

	private static final int[] SIZES = {64, 16, 1};

	private static final int BOX_W = 220;
	private static final int PANEL_PAD = 8;

	// colori dei testi disegnati a mano (ARGB)
	private static final int C_SHULKER = 0xFF000000 | StackCalc.COLOR_SHULKER;
	private static final int C_STACK = 0xFF000000 | StackCalc.COLOR_STACK;
	private static final int C_ITEM = 0xFF000000 | StackCalc.COLOR_ITEM;
	private static final int C_SEP = 0xFF808080;
	private static final int C_OK = 0xFF55FF55;
	private static final int C_ERR = 0xFFFF5555;
	private static final int C_PLACEHOLDER = 0xFF999999;
	private static final int C_HINT = 0xFF8A8A8A;
	private static final int C_PANEL = 0xE0101010;
	private static final int C_BORDER = 0xFF4A4A4A;

	// colori del syntax highlight dentro la EditBox (RGB, senza alpha)
	private static final int S_NUM = 0xFFFFFF;
	private static final int S_OP = StackCalc.COLOR_STACK;
	private static final int S_PAREN = StackCalc.COLOR_ITEM;
	private static final int S_SUF = StackCalc.COLOR_SHULKER;
	private static final int S_BAD = 0xFF5555;

	// persistono tra un'apertura e l'altra
	private static String lastInput = "";
	private static int sizeIndex = 0;

	private EditBox input;
	private Button sizeButton;

	private final List<Seg> segs = new ArrayList<>();
	private String risultato = "";
	private String dettaglio = "";
	private String hint = "";
	private boolean errore = false;

	private record Seg(String text, int color) {}

	public CalcScreen() {
		super(Component.literal("Stack Calc"));
	}

	private static int perStack() {
		return SIZES[sizeIndex];
	}

	private static String tr(String key, Object... args) {
		return Component.translatable(key, args).getString();
	}

	private int panelX() {
		return (this.width - BOX_W) / 2;
	}

	private int panelY() {
		return this.height / 3;
	}

	@Override
	protected void init() {
		int x = panelX();
		int y = panelY();

		this.input = new EditBox(this.font, x, y, BOX_W, 20, Component.translatable("stackcalc.gui.input"));
		this.input.setMaxLength(256);
		this.input.setValue(lastInput);
		this.input.setResponder(this::ricalcola);
		this.input.setTextShadow(true);
		// colora i caratteri mentre scrivi
		this.input.addFormatter((testo, offset) -> highlight(testo));
		this.addRenderableWidget(this.input);
		this.setInitialFocus(this.input);

		this.sizeButton = Button.builder(labelSize(), b -> {
			sizeIndex = (sizeIndex + 1) % SIZES.length;
			b.setMessage(labelSize());
			ricalcola(this.input.getValue());
		}).bounds(x + BOX_W + 4, y, 42, 20).build();
		this.addRenderableWidget(this.sizeButton);

		ricalcola(lastInput);
	}

	private static Component labelSize() {
		return Component.literal("/" + perStack())
				.withStyle(Style.EMPTY.withColor(S_OP).withBold(true));
	}

	/** Spezza la stringa in blocchi dello stesso colore e la restituisce colorata. */
	private static FormattedCharSequence highlight(String s) {
		if (s == null || s.isEmpty()) return FormattedCharSequence.EMPTY;
		MutableComponent out = Component.empty();
		int i = 0;
		while (i < s.length()) {
			int col = colorOf(s.charAt(i));
			int j = i + 1;
			while (j < s.length() && colorOf(s.charAt(j)) == col) j++;
			out.append(Component.literal(s.substring(i, j)).withStyle(Style.EMPTY.withColor(col)));
			i = j;
		}
		return out.getVisualOrderText();
	}

	private static int colorOf(char c) {
		if (Character.isDigit(c) || c == '.' || c == ' ') return S_NUM;
		if (Character.isLetter(c)) return S_SUF;
		if (c == '(' || c == ')') return S_PAREN;
		if ("+-*/%^".indexOf(c) >= 0) return S_OP;
		return S_BAD;
	}

	private void push(String testo, int colore) {
		if (!this.segs.isEmpty()) this.segs.add(new Seg(" + ", C_SEP));
		this.segs.add(new Seg(testo, colore));
	}

	private void ricalcola(String testo) {
		lastInput = testo;
		this.hint = "";
		this.segs.clear();

		if (testo == null || testo.isBlank()) {
			this.risultato = "";
			this.dettaglio = tr("stackcalc.gui.placeholder");
			this.errore = false;
			return;
		}

		try {
			double d = Calc.eval(testo);
			this.errore = false;
			this.risultato = StackCalc.trim(d);
			this.dettaglio = "";

			long tot = (long) Math.floor(Math.abs(d));
			long[] p = StackCalc.parts(tot, perStack());

			if (tot == 0) {
				this.segs.add(new Seg(tr("stackcalc.unit.zero"), C_ITEM));
			} else {
				if (p[0] > 0) push(tr("stackcalc.unit.shulker", p[0]), C_SHULKER);
				if (p[1] > 0) push(tr("stackcalc.unit.stack", p[1]), C_STACK);
				if (p[2] > 0) push(tr("stackcalc.unit.item", p[2]), C_ITEM);
			}
			this.hint = tr("stackcalc.gui.copy");
		} catch (CalcException e) {
			this.errore = true;
			this.risultato = "?";
			this.dettaglio = e.component().getString();
		} catch (Exception e) {
			this.errore = true;
			this.risultato = "?";
			this.dettaglio = tr("stackcalc.error.generic");
		}
	}

	/**
	 * Il pannello va disegnato qui e non in extractRenderState, altrimenti finisce
	 * SOPRA i widget e li offusca (extractRenderState disegna solo i renderables).
	 */
	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractBackground(graphics, mouseX, mouseY, delta);

		int x = panelX();
		int y = panelY();

		int x0 = x - PANEL_PAD;
		int y0 = y - 24;
		int x1 = x + BOX_W + 46 + PANEL_PAD;
		int y1 = y + 66;

		graphics.fill(x0, y0, x1, y1, C_PANEL);
		graphics.fill(x0, y0, x1, y0 + 1, C_BORDER);
		graphics.fill(x0, y1 - 1, x1, y1, C_BORDER);
		graphics.fill(x0, y0, x0 + 1, y1, C_BORDER);
		graphics.fill(x1 - 1, y0, x1, y1, C_BORDER);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);

		int x = panelX();
		int y = panelY();

		// titolo bicolore (nome della mod, non si traduce)
		graphics.text(this.font, "Stack", x, y - 16, 0xFFFFFFFF, true);
		graphics.text(this.font, "Calc", x + this.font.width("Stack "), y - 16, C_STACK, true);

		// riga risultato
		graphics.text(this.font, "=", x, y + 28, C_SEP, true);
		if (!this.risultato.isEmpty()) {
			graphics.text(this.font, this.risultato, x + this.font.width("= "), y + 28,
					this.errore ? C_ERR : C_OK, true);
		}

		// riga breakdown, un colore per pezzo
		if (!this.segs.isEmpty()) {
			int cx = x;
			for (Seg s : this.segs) {
				graphics.text(this.font, s.text(), cx, y + 41, s.color(), true);
				cx += this.font.width(s.text());
			}
		} else if (!this.dettaglio.isEmpty()) {
			graphics.text(this.font, this.dettaglio, x, y + 41,
					this.errore ? C_ERR : C_PLACEHOLDER, true);
		}

		if (!this.hint.isEmpty()) {
			graphics.text(this.font, this.hint, x, y + 55, C_HINT, false);
		}
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int key = event.key();
		if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
			if (!this.errore && !this.risultato.isEmpty() && this.minecraft != null) {
				this.minecraft.keyboardHandler.setClipboard(this.risultato);
				this.hint = tr("stackcalc.gui.copied");
			}
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
