package de.tyrannus.venomhack.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.modules.render.hud.HudElements;
import de.tyrannus.venomhack.settings.AbstractSettingHolder;
import de.tyrannus.venomhack.settings.Setting;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SettingArgumentType implements ArgumentType<Setting<?>> {
   @Nullable
   private final AbstractSettingHolder settingHolder;

   public SettingArgumentType(@Nullable AbstractSettingHolder settingHolder) {
      this.settingHolder = settingHolder;
   }

   public Setting<?> parse(StringReader reader) throws CommandSyntaxException {
      String input = reader.readString();

      for(Setting<?> setting : this.get(reader).SETTINGS) {
         if (setting.getName().equalsIgnoreCase(input)) {
            return setting;
         }
      }

      throw new SimpleCommandExceptionType(Text.literal("Invalid setting.")).createWithContext(reader);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching(this.get(context).SETTINGS.stream().map(Setting::getName), builder);
   }

   @NotNull
   private <S> AbstractSettingHolder get(CommandContext<S> context) {
      return this.settingHolder == null ? (AbstractSettingHolder)context.getArgument("element", HudElement.class) : this.settingHolder;
   }

   @NotNull
   private AbstractSettingHolder get(StringReader reader) throws CommandSyntaxException {
      if (this.settingHolder == null) {
         Screen var3 = Venomhack.mc.currentScreen;
         if (var3 instanceof ChatScreen screen) {
            return HudElements.get(screen.chatField.getText().split(" ")[1]);
         } else {
            throw new SimpleCommandExceptionType(Text.literal("Invalid setting.")).createWithContext(reader);
         }
      } else {
         return this.settingHolder;
      }
   }
}
