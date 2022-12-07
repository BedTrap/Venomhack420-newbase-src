package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.ModuleArgumentType;
import de.tyrannus.venomhack.modules.Module;
import net.minecraft.server.command.ServerCommandSource;

public class ToggleCommand extends Command {
   public ToggleCommand() {
      super("toggle", "Toggles the specified module on or off.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.then(this.arg("module", new ModuleArgumentType()).executes(context -> {
         Module module = (Module)context.getArgument("module", Module.class);
         module.toggle();
         return 1;
      }));
   }
}
