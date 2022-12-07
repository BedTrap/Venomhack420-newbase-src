package de.tyrannus.venomhack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.commands.commands.ActiveCommand;
import de.tyrannus.venomhack.commands.commands.AddCommand;
import de.tyrannus.venomhack.commands.commands.BindCommand;
import de.tyrannus.venomhack.commands.commands.ConfigCommand;
import de.tyrannus.venomhack.commands.commands.DebugCommand;
import de.tyrannus.venomhack.commands.commands.DoxCommand;
import de.tyrannus.venomhack.commands.commands.DrawnCommand;
import de.tyrannus.venomhack.commands.commands.FriendsCommand;
import de.tyrannus.venomhack.commands.commands.HeadItemCommand;
import de.tyrannus.venomhack.commands.commands.HelpCommand;
import de.tyrannus.venomhack.commands.commands.HudElementCommand;
import de.tyrannus.venomhack.commands.commands.KitCommand;
import de.tyrannus.venomhack.commands.commands.ModulesCommand;
import de.tyrannus.venomhack.commands.commands.PrefixCommand;
import de.tyrannus.venomhack.commands.commands.ToggleCommand;
import de.tyrannus.venomhack.utils.ChatUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.Formatting;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.ClickEvent.class_2559;
import net.minecraft.text.HoverEvent.class_5247;

public class Commands {
   public static char PREFIX = ',';
   public static final List<Command> COMMANDS = new ArrayList<>();
   public static final CommandDispatcher<ServerCommandSource> DISPATCHER = new CommandDispatcher();

   public static void init() {
      COMMANDS.add(new ActiveCommand());
      COMMANDS.add(new AddCommand());
      COMMANDS.add(new BindCommand());
      COMMANDS.add(new ConfigCommand());
      COMMANDS.add(new DebugCommand());
      COMMANDS.add(new DoxCommand());
      COMMANDS.add(new DrawnCommand());
      COMMANDS.add(new FriendsCommand());
      COMMANDS.add(new HeadItemCommand());
      COMMANDS.add(new HelpCommand());
      COMMANDS.add(new HudElementCommand());
      COMMANDS.add(new ModulesCommand());
      COMMANDS.add(new KitCommand());
      COMMANDS.add(new PrefixCommand());
      COMMANDS.add(new ToggleCommand());
      COMMANDS.forEach(cmd -> DISPATCHER.register(cmd.getBuilder()));
      COMMANDS.sort(Comparator.comparing(Command::getName));
      DISPATCHER.findAmbiguities(
         (parent, child, sibling, inputs) -> Venomhack.LOGGER
               .warn("Ambiguity between arguments {} and {} with inputs: {}", new Object[]{DISPATCHER.getPath(child), DISPATCHER.getPath(sibling), inputs})
      );
      DISPATCHER.setConsumer((context, success, result) -> ((ServerCommandSource)context.getSource()).onCommandComplete(context, success, result));
   }

   public static void setPrefix(char prefix) {
      PREFIX = prefix;
   }

   public static boolean executeCommand(String command) {
      if (!command.isEmpty() && command.charAt(0) == PREFIX) {
         StringReader stringReader = new StringReader(command);
         ServerCommandSource commandSource = Venomhack.mc.player.getCommandSource();
         if (stringReader.canRead() && stringReader.peek() == PREFIX) {
            stringReader.skip();
         }

         try {
            DISPATCHER.execute(stringReader, commandSource);
         } catch (CommandException var7) {
            commandError(commandSource, var7.getTextMessage());
         } catch (CommandSyntaxException var8) {
            commandError(commandSource, Texts.toText(var8.getRawMessage()));
            if (var8.getInput() != null && var8.getCursor() >= 0) {
               int pos = Math.min(var8.getInput().length(), var8.getCursor());
               MutableText mutableText = Text.literal("")
                  .formatted(Formatting.GRAY)
                  .styled(style -> style.withClickEvent(new ClickEvent(class_2559.SUGGEST_COMMAND, command)));
               if (pos > 10) {
                  mutableText.append("...");
               }

               mutableText.append(var8.getInput().substring(Math.max(0, pos - 10), pos));
               if (pos < var8.getInput().length()) {
                  MutableText text = Text.literal(var8.getInput().substring(pos))
                     .formatted(new Formatting[]{Formatting.RED, Formatting.UNDERLINE});
                  mutableText.append(text);
               }

               mutableText.append(
                  Text.translatable("command.context.here").formatted(new Formatting[]{Formatting.RED, Formatting.ITALIC})
               );
               commandError(commandSource, mutableText);
            }
         } catch (Exception var9) {
            MutableText text = Text.literal(var9.getMessage() == null ? var9.getClass().getName() : var9.getMessage());
            commandError(
               commandSource,
               Text.translatable("command.failed").styled(style -> style.withHoverEvent(new HoverEvent(class_5247.SHOW_TEXT, text)))
            );
            if (SharedConstants.isDevelopment) {
               commandError(commandSource, Text.literal(Util.getInnermostMessage(var9)));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static void commandError(ServerCommandSource source, Text message) {
      if (source.output.shouldTrackOutput() && !source.silent) {
         source.output
            .sendMessage(Text.literal("").append(ChatUtils.prefixText()).append(message).formatted(Formatting.RED));
      }
   }
}
