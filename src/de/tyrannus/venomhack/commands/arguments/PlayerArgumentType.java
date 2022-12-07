package de.tyrannus.venomhack.commands.arguments;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.tyrannus.venomhack.Venomhack;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.client.network.PlayerListEntry;

public class PlayerArgumentType implements ArgumentType<GameProfile> {
   public GameProfile parse(StringReader reader) throws CommandSyntaxException {
      if (Venomhack.mc.player == null) {
         return null;
      } else {
         String input = reader.readString();

         for(PlayerListEntry entry : Venomhack.mc.player.networkHandler.getPlayerList()) {
            if (entry.getProfile().getName().equalsIgnoreCase(input)) {
               return entry.getProfile();
            }
         }

         throw new SimpleCommandExceptionType(Text.literal("Invalid player.")).createWithContext(reader);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      ArrayList<String> candidates = new ArrayList<>();

      for(PlayerListEntry entry : Venomhack.mc.player.networkHandler.getPlayerList()) {
         candidates.add(entry.getProfile().getName());
      }

      return context.getSource() instanceof CommandSource ? CommandSource.suggestMatching(candidates, builder) : Suggestions.empty();
   }
}
