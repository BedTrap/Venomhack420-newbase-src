package de.tyrannus.venomhack.commands.commands;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.HudEditorScreen;
import de.tyrannus.venomhack.utils.ChatUtils;
import net.minecraft.server.command.ServerCommandSource;

public class DebugCommand extends Command {
   public DebugCommand() {
      super("debug", "debug");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      ((LiteralArgumentBuilder)builder.then(this.lit("rebuild-gui").executes(context -> {
         ClickGuiScreen.buildGUI();
         ChatUtils.info("Resetting the gui.");
         return 1;
      }))).then(this.lit("hud").executes(context -> {
         RenderSystem.recordRenderCall(() -> mc.setScreen(new HudEditorScreen()));
         return 1;
      }));
   }
}
