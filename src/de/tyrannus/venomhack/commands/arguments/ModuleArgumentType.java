package de.tyrannus.venomhack.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.settings.AbstractSettingHolder;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class ModuleArgumentType implements ArgumentType<Module> {
   public Module parse(StringReader reader) throws CommandSyntaxException {
      Module module = Modules.get(reader.readString());
      if (module == null) {
         throw new SimpleCommandExceptionType(Text.literal("Invalid module.")).createWithContext(reader);
      } else {
         return module;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching(Arrays.stream(Modules.modules()).map(AbstractSettingHolder::getName), builder);
   }
}
