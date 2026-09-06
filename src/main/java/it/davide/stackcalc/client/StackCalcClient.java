package it.davide.stackcalc.client;

import com.mojang.blaze3d.platform.InputConstants;
import it.davide.stackcalc.StackCalc;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class StackCalcClient implements ClientModInitializer {

	private KeyMapping openKey;

	@Override
	public void onInitializeClient() {
		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(StackCalc.MOD_ID, "main"));

		this.openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.stackcalc.open",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_EQUAL, // tasto '=', cambiabile dalle opzioni
				category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (this.openKey.consumeClick()) {
				// nella 26.2 setScreen si e' spostato da Minecraft a Gui
				client.gui.setScreen(new CalcScreen());
			}
		});

		StackCalcCommand.register();
	}
}
