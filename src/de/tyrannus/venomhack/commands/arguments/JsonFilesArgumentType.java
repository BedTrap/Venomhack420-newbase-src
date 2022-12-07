package de.tyrannus.venomhack.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class JsonFilesArgumentType implements ArgumentType<String> {
   private final File folder;

   public JsonFilesArgumentType(String path) {
      this.folder = new File(path);
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      File[] files = this.folder.listFiles();
      if (files != null) {
         String input = reader.readString();
         if (Arrays.stream(files).anyMatch(file -> file.getName().equals(input + ".json"))) {
            return input;
         }
      }

      throw new SimpleCommandExceptionType(Text.literal("File doesn't exist.")).createWithContext(reader);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      File[] files = this.folder.listFiles();
      return files == null
         ? Suggestions.empty()
         : CommandSource.suggestMatching(
            Arrays.stream(files)
               .filter(file -> file.getName().contains(".json"))
               .map(file -> file.getName().substring(0, file.getName().indexOf(".json")))
               .collect(Collectors.toList()),
            builder
         );
   }
}
