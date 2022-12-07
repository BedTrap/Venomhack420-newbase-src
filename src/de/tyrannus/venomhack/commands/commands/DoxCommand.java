package de.tyrannus.venomhack.commands.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.PlayerArgumentType;
import de.tyrannus.venomhack.utils.ChatUtils;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.minecraft.util.Formatting;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import net.minecraft.text.ClickEvent.class_2559;
import net.minecraft.text.HoverEvent.class_5247;

public class DoxCommand extends Command {
   private final Executor executor = Executors.newSingleThreadExecutor();

   public DoxCommand() {
      super("dox", "Attempts to dox the specified person by fetching their name history.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.then(this.arg("player", new PlayerArgumentType()).executes(context -> {
         this.executor.execute(() -> this.dox((GameProfile)context.getArgument("player", GameProfile.class)));
         return 1;
      }));
   }

   private void dox(GameProfile victim) {
      if (mc.getServer() != null && !mc.getServer().isOnlineMode()) {
         ChatUtils.info("Can't dox cracked accounts!");
      } else {
         ArrayList<DoxCommand.NameHistoryObject> history = new ArrayList<>();

         try {
            for(JsonElement element : JsonParser.parseReader(
                  new InputStreamReader(new URL("https://namemc.com/profile/" + victim.getId().toString()).openStream())
               )
               .getAsJsonArray()) {
               JsonObject object = element.getAsJsonObject();
               JsonElement nameElement = object.get("name");
               if (nameElement != null) {
                  JsonElement timeElement = object.get("changedToAt");
                  history.add(new DoxCommand.NameHistoryObject(nameElement.getAsString(), timeElement == null ? 0L : timeElement.getAsLong()));
               }
            }
         } catch (IOException | JsonIOException var10) {
            var10.printStackTrace();
            ChatUtils.info("An error occured while attemting to fetch the users dox.");
            return;
         }

         if (history.isEmpty()) {
            ChatUtils.info("There was an error fetching that users name history.");
         } else {
            MutableText initial = Text.literal(victim.getName());
            initial.append(Text.literal("'s"));
            initial.setStyle(
               initial.getStyle()
                  .withColor(TextColor.fromRgb(Color.MAGENTA.getRGB()))
                  .withClickEvent(new ClickEvent(class_2559.OPEN_URL, "https://namemc.com/search?q=" + victim.getName()))
                  .withHoverEvent(
                     new HoverEvent(
                        class_5247.SHOW_TEXT,
                        Text.literal("View on NameMC").formatted(Formatting.YELLOW).formatted(Formatting.ITALIC)
                     )
                  )
            );
            ChatUtils.info(initial.append(Text.literal(" dox is:").formatted(Formatting.GRAY)));

            for(DoxCommand.NameHistoryObject nameHistoryObject : history) {
               if (nameHistoryObject.name != null) {
                  MutableText nameText = Text.literal(nameHistoryObject.name);
                  nameText.formatted(Formatting.AQUA);
                  if (nameHistoryObject.changedToAt != 0L) {
                     MutableText changed = Text.literal("Changed at: ");
                     changed.formatted(Formatting.GRAY);
                     Date date = new Date(nameHistoryObject.changedToAt);
                     SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss, dd/MM/yyyy");
                     changed.append(Text.literal(formatter.format(date)).formatted(Formatting.WHITE));
                     nameText.setStyle(nameText.getStyle().withHoverEvent(new HoverEvent(class_5247.SHOW_TEXT, changed)));
                  }

                  ChatUtils.sendMsg(nameText);
               }
            }
         }
      }
   }

   private static record NameHistoryObject(String name, long changedToAt) {
   }
}
