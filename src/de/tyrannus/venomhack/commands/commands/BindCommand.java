package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.ModuleArgumentType;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.TextUtils;
import de.tyrannus.venomhack.utils.Utils;
import java.awt.Color;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.client.util.InputUtil.class_306;

public class BindCommand extends Command {
   public BindCommand() {
      super("bind", "Binds the specified module to the specified key.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.then(
         ((RequiredArgumentBuilder)this.arg("module", new ModuleArgumentType())
               .executes(
                  context -> {
                     Module module = (Module)context.getArgument("module", Module.class);
                     ChatUtils.info(
                        Text.literal(module.getParsedName())
                           .append(module.bind.get().getCode() <= 0 ? " has not been keybound yet" : " is bound to the key ")
                           .append(
                              module.bind.get().getCode() == -1
                                 ? Text.literal("")
                                 : TextUtils.coloredTxt(module.bind.get().getKey().getLocalizedText().getString(), Color.ORANGE)
                           )
                           .append(".")
                     );
                     return 1;
                  }
               ))
            .then(this.arg("bind", StringArgumentType.greedyString()).executes(context -> {
               doBind((String)context.getArgument("bind", String.class), (Module)context.getArgument("module", Module.class));
               return 1;
            }))
      );
   }

   public static void doBind(String argument, Module module) {
      class_306 key = Utils.keyFromString(argument);
      if (key == null) {
         if (argument.equals("reset")) {
            module.bind.reset();
            ChatUtils.info(Text.literal("Unbound ").append(module.getParsedName()).append("."));
         } else {
            ChatUtils.info(Text.literal("Can't find key ").append(TextUtils.coloredTxt(argument, Color.ORANGE)).append("!"));
         }
      } else {
         module.bind.set(key);
         ChatUtils.info(
            Text.literal("Bound ")
               .append(module.getParsedName())
               .append(" to key ")
               .append(TextUtils.coloredTxt(argument, Color.ORANGE))
               .append(".")
         );
      }
   }
}
