package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.Commands;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.TextUtils;
import java.awt.Color;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class PrefixCommand extends Command {
   public PrefixCommand() {
      super("prefix", "Allows to redefine the command prefix Venomhack uses.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.then(this.arg("prefix", StringArgumentType.greedyString()).executes(context -> {
         char prefix = ((String)context.getArgument("prefix", String.class)).charAt(0);
         Commands.setPrefix(prefix);
         ChatUtils.info(Text.literal("Set new prefix to ").append(TextUtils.coloredTxt(String.valueOf(prefix), Color.ORANGE)));
         return 1;
      }));
   }
}
