package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.ModuleArgumentType;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.utils.ChatUtils;
import net.minecraft.server.command.ServerCommandSource;

public class DrawnCommand extends Command {
   public DrawnCommand() {
      super("drawn", "Removes / adds the module from / to the array list hud.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.then(this.arg("module", new ModuleArgumentType()).executes(context -> {
         Module module = (Module)context.getArgument("module", Module.class);
         module.drawn = !module.drawn;
         StringBuilder stringBuilder = new StringBuilder();
         if (module.drawn) {
            stringBuilder.append("Added ");
         } else {
            stringBuilder.append("Removed ");
         }

         stringBuilder.append(module.getParsedName());
         if (module.drawn) {
            stringBuilder.append(" to ");
         } else {
            stringBuilder.append(" from ");
         }

         ChatUtils.info(stringBuilder.append("the array list hud.").toString());
         return 1;
      }));
   }
}
