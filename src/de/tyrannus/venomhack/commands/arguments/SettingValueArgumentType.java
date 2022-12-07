package de.tyrannus.venomhack.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.tyrannus.venomhack.settings.Setting;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;

public class SettingValueArgumentType implements ArgumentType<String> {
   public String parse(StringReader reader) {
      String text = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      return text;
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching(((Setting)context.getArgument("setting", Setting.class)).getSuggestions(), builder);
   }
}
