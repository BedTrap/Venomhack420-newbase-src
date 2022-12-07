package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.utils.ChatUtils;
import net.minecraft.server.command.ServerCommandSource;

public class ActiveCommand extends Command {
   public ActiveCommand() {
      super("active", "Shows which modules are currently active.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      ((LiteralArgumentBuilder)builder.executes(context -> {
         boolean noActive = true;

         for(Module module : Modules.modules()) {
            if (module.isActive()) {
               ChatUtils.sendMsg(module.getParsedName());
               noActive = false;
            }
         }

         if (noActive) {
            ChatUtils.info("No active modules!");
         }

         return 1;
      })).then(this.lit("toggle").executes(context -> {
         for(Module module : Modules.modules()) {
            if (module.isActive()) {
               module.toggle(false);
            }
         }

         ChatUtils.info("Toggled all active modules off.");
         return 1;
      }));
   }
}
