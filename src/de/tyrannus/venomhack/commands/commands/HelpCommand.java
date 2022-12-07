package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.Commands;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.TextUtils;
import net.minecraft.server.command.ServerCommandSource;

public class HelpCommand extends Command {
   public HelpCommand() {
      super("help", "Displays a list of all available commands.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.executes(context -> {
         ChatUtils.info("All available commands:");

         for(Command cmd : Commands.COMMANDS) {
            if (!(cmd instanceof ModuleCommand)) {
               ChatUtils.sendMsg(TextUtils.parseName(cmd.getName()) + ": " + cmd.getDescription());
            }
         }

         return 1;
      });
   }
}
