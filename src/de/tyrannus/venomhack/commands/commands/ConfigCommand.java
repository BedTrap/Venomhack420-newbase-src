package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.JsonFilesArgumentType;
import de.tyrannus.venomhack.utils.ChatUtils;
import net.minecraft.server.command.ServerCommandSource;

public class ConfigCommand extends Command {
   public ConfigCommand() {
      super("config", "Interacts with your config.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      ((LiteralArgumentBuilder)builder.then(this.lit("save").then(this.arg("name", StringArgumentType.string()).executes(context -> {
         String configName = (String)context.getArgument("name", String.class);
         Venomhack.save(configName);
         ChatUtils.info("Saved current configs to " + configName + ".");
         return 1;
      })))).then(this.lit("load").then(this.arg("name", new JsonFilesArgumentType("venomhack\\profiles\\")).executes(context -> {
         String configName = (String)context.getArgument("name", String.class);
         Venomhack.loadConfig(configName);
         ChatUtils.info("Loaded config " + configName + ".");
         return 1;
      })));
   }
}
