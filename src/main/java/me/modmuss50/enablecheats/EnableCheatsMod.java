package me.modmuss50.enablecheats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Optional;

@Mod(modid = "enablecheats", name = "Enable Cheats", clientSideOnly = true, version = "@MODVERSION@")
public class EnableCheatsMod {

	GuiButton cheatsButton;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void initGui(GuiScreenEvent.InitGuiEvent event) {
		if (!isEscMenuSP(event.getGui())) {
			return;
		}
		GuiScreen gui = event.getGui();
		Optional<GuiButton> optionalButton = getButton(event.getButtonList(), 7);
		if (!optionalButton.isPresent()) {
			return;
		}
		GuiButton openToLan = optionalButton.get();
		openToLan.width = 98;

		GuiButton toggleCheats = new GuiButton(101, gui.width / 2 + 2, gui.height / 4 + 56, 98, 20, areCheatsEnabled() ? "Disable Cheats" : "Enable Cheats");
		cheatsButton = toggleCheats;
		event.getButtonList().add(toggleCheats);
	}

	@SubscribeEvent
	public void actionPeformedEvent(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if (!isEscMenuSP(event.getGui())) {
			return;
		}
		if (event.getButton().id == 101) {
			toggleCheats(!areCheatsEnabled());
			cheatsButton.displayString = areCheatsEnabled() ? "Disable Cheats" : "Enable Cheats";
			event.getGui().mc.player.sendMessage(new TextComponentString("Cheats are now " + (areCheatsEnabled() ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled")));
		}
	}

	public void toggleCheats(boolean enabled) {
		IntegratedServer integratedServer = Minecraft.getMinecraft().getIntegratedServer();
		integratedServer.getPlayerList().setCommandsAllowedForAll(enabled);
		Minecraft.getMinecraft().player.setPermissionLevel(enabled ? 4 : 0);
		integratedServer.worlds[0].getWorldInfo().setAllowCommands(enabled);
	}

	public boolean areCheatsEnabled() {
		return Minecraft.getMinecraft().player.getPermissionLevel() == 4;
	}

	public Optional<GuiButton> getButton(List<GuiButton> buttonList, int id) {
		return buttonList.stream().filter(guiButton -> guiButton.id == id).findFirst();
	}

	public boolean isEscMenuSP(GuiScreen guiScreen) {
		if (guiScreen == null) {
			return false;
		}
		if (!guiScreen.mc.isSingleplayer()) {
			return false;
		}
		return guiScreen instanceof GuiIngameMenu;
	}
}
