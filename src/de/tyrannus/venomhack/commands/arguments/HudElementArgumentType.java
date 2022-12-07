package de.tyrannus.venomhack.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.modules.render.hud.HudElements;
import de.tyrannus.venomhack.settings.AbstractSettingHolder;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class HudElementArgumentType implements ArgumentType<HudElement> {
   public HudElement parse(StringReader reader) throws CommandSyntaxException {
      HudElement element = HudElements.get(reader.readString());
      if (element == null) {
         throw new SimpleCommandExceptionType(Text.literal("Invalid hud element")).createWithContext(reader);
      } else {
         return element;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching(Arrays.stream(HudElements.ELEMENTS).map(AbstractSettingHolder::getName), builder);
   }
}
