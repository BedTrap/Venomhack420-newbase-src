package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.utils.ChatUtils;
import net.minecraft.server.command.ServerCommandSource;

public class ModulesCommand extends Command {
   public ModulesCommand() {
      super("modules", "Displays a list of all available modules.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.executes(context -> {
         ChatUtils.info("All available modules:");

         for(Module module : Modules.modules()) {
            ChatUtils.sendMsg(module.getParsedName() + ": " + module.description);
         }

         return 1;
      });
   }
}
