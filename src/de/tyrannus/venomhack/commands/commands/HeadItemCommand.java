package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;

public class HeadItemCommand extends Command {
   public HeadItemCommand() {
      super("head-item", "Puts the item you're currently holding in your mainhand into your helmet slot.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.executes(context -> {
         mc.interactionManager
            .clickSlot(mc.player.currentScreenHandler.syncId, 36 + mc.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, mc.player);
         return 1;
      });
   }
}
