package de.tyrannus.venomhack.mixins;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.commands.Commands;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.text.OrderedText;
import net.minecraft.SuggestionWindow;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChatInputSuggestor.class})
public abstract class CommandSuggestorMixin {
   @Shadow
   boolean completingSuggestions;
   @Shadow
   private ParseResults<CommandSource> parse;
   @Shadow
   @Final
   private boolean suggestingWhenEmpty;
   @Shadow
   @Final
   TextFieldWidget textField;
   @Shadow
   @Final
   private boolean slashOptional;
   @Shadow
   @Final
   private List<OrderedText> messages;
   @Shadow
   @Nullable
   private class_464 window;
   @Shadow
   @Nullable
   private CompletableFuture<Suggestions> pendingSuggestions;

   @Shadow
   private static int getStartOfCurrentWord(String input) {
      return 0;
   }

   @Shadow
   protected abstract void showCommandSuggestions();

   @Inject(
      method = {"refresh"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRefreshReturn(CallbackInfo ci) {
      String string = this.textField.getText();
      if (string.length() >= 1 && string.charAt(0) == Commands.PREFIX) {
         if (this.parse != null && !this.parse.getReader().getString().equals(string)) {
            this.parse = null;
         }

         if (!this.completingSuggestions) {
            this.textField.setSuggestion(null);
            this.window = null;
         }

         this.messages.clear();
         StringReader stringReader = new StringReader(string);
         boolean skip = stringReader.canRead() && stringReader.peek() == Commands.PREFIX;
         if (skip) {
            stringReader.skip();
         }

         boolean suggestCommands = this.slashOptional || skip;
         int cursor = this.textField.getCursor();
         if (suggestCommands) {
            if (this.parse == null) {
               this.parse = Commands.DISPATCHER
                  .parse(
                     stringReader,
                     new ServerCommandSource(null, Vec3d.ZERO, Vec2f.ZERO, null, 4, "Server", Text.literal("Server"), null, null)
                  );
            }

            int cursorPos = this.suggestingWhenEmpty ? stringReader.getCursor() : 1;
            if (cursor >= cursorPos && (this.window == null || !this.completingSuggestions)) {
               this.pendingSuggestions = Commands.DISPATCHER.getCompletionSuggestions(this.parse, cursor);
               this.pendingSuggestions.thenRun(() -> {
                  if (this.pendingSuggestions.isDone()) {
                     this.showCommandSuggestions();
                  }
               });
            }
         } else {
            String string2 = string.substring(0, cursor);
            int cursorPos = getStartOfCurrentWord(string2);
            Collection<String> collection = Venomhack.mc.player.networkHandler.getCommandSource().getPlayerNames();
            this.pendingSuggestions = CommandSource.suggestMatching(collection, new SuggestionsBuilder(string2, cursorPos));
         }

         ci.cancel();
      }
   }
}
