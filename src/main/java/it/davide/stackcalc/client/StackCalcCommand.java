package it.davide.stackcalc.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import it.davide.stackcalc.Calc;
import it.davide.stackcalc.CalcException;
import it.davide.stackcalc.StackCalc;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/**
 * Comando eseguito interamente sul client, quindi funziona anche
 * su server vanilla dove la mod non e' installata.
 */
public final class StackCalcCommand {

	private StackCalcCommand() {
	}

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> dispatcher.register(
				ClientCommands.literal("stack")
						// /stack <amount> [perstack]
						.then(ClientCommands.argument("amount", IntegerArgumentType.integer(0))
								.executes(ctx -> plain(ctx, 64))
								.then(ClientCommands.argument("perstack", IntegerArgumentType.integer(1, 64))
										.executes(ctx -> plain(ctx, IntegerArgumentType.getInteger(ctx, "perstack")))))
						// /stack calc <expression>
						.then(ClientCommands.literal("calc")
								.then(ClientCommands.argument("expression", StringArgumentType.greedyString())
										.executes(StackCalcCommand::expression)))));
	}

	private static int plain(CommandContext<FabricClientCommandSource> ctx, int perStack) {
		long totale = IntegerArgumentType.getInteger(ctx, "amount");
		ctx.getSource().sendFeedback(line(String.valueOf(totale), totale, perStack));
		return 1;
	}

	private static int expression(CommandContext<FabricClientCommandSource> ctx) {
		String raw = StringArgumentType.getString(ctx, "expression");
		try {
			double d = Calc.eval(raw);
			long tot = (long) Math.floor(Math.abs(d));
			ctx.getSource().sendFeedback(line(StackCalc.trim(d), tot, 64));
			return 1;
		} catch (CalcException e) {
			ctx.getSource().sendError(Component.translatable("stackcalc.command.error", e.component()));
			return 0;
		} catch (Exception e) {
			ctx.getSource().sendError(Component.translatable("stackcalc.command.error",
					Component.translatable("stackcalc.error.generic")));
			return 0;
		}
	}

	private static MutableComponent line(String testa, long totale, int perStack) {
		return Component.literal(testa)
				.withStyle(Style.EMPTY.withColor(0x55FF55))
				.append(Component.literal(" = ").withStyle(Style.EMPTY.withColor(StackCalc.COLOR_SEP)))
				.append(StackCalc.breakdown(totale, perStack));
	}
}
