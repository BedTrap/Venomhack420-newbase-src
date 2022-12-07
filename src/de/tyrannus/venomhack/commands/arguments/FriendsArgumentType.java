package de.tyrannus.venomhack.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.tyrannus.venomhack.utils.players.Friends;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class FriendsArgumentType implements ArgumentType<String> {
   public String parse(StringReader reader) throws CommandSyntaxException {
      String input = reader.readString();

      for(String friend : Friends.FRIENDS) {
         if (friend.equalsIgnoreCase(input)) {
            return friend;
         }
      }

      throw new SimpleCommandExceptionType(Text.literal("Not friended.")).createWithContext(reader);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching(Friends.FRIENDS, builder);
   }
}
