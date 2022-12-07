package de.tyrannus.venomhack.commands.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.JsonFilesArgumentType;
import de.tyrannus.venomhack.utils.ChatUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.server.command.ServerCommandSource;

public class KitCommand extends Command {
   private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private final String path = "venomhack\\kits\\";

   public KitCommand() {
      super("kit", "Creates a kit for AutoRegear.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(
               this.lit("save").then(this.arg("name", StringArgumentType.greedyString()).executes(context -> {
                  String name = (String)context.getArgument("name", String.class);
                  String pathString = "venomhack\\kits\\" + name + ".json";
                  if (Files.exists(Path.of(pathString))) {
                     ChatUtils.info("This kit already exists.");
                     return 1;
                  } else {
                     JsonArray json = new JsonArray();
         
                     for(int i = 0; i < 36; ++i) {
                        json.add(mc.player.getInventory().getStack(i).getItem().toString());
                     }
         
                     try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(pathString));
                        writer.write(this.gson.toJson(json));
                        writer.close();
                        ChatUtils.info("Successfully created " + name + ".");
                     } catch (IOException var6) {
                        ChatUtils.info("An error occurred.");
                     }
         
                     return 1;
                  }
               }))
            ))
            .then(this.lit("delete").then(this.arg("kit", new JsonFilesArgumentType("venomhack\\kits\\")).executes(context -> {
               String name = (String)context.getArgument("kit", String.class);
      
               try {
                  Files.delete(Path.of("venomhack\\kits\\" + name + ".json"));
                  ChatUtils.info("Successfully deleted kit " + name + ".");
               } catch (IOException var4) {
                  ChatUtils.info("This kit doesn't exist.");
               }
      
               return 1;
            }))))
         .then(this.lit("load").then(this.arg("kit", new JsonFilesArgumentType("venomhack\\kits\\")).executes(context -> {
            String name = (String)context.getArgument("kit", String.class);
   
            try {
               BufferedWriter bw = new BufferedWriter(new FileWriter("venomhack\\kits\\loadedKit.kit"));
               bw.write(name);
               bw.close();
               ChatUtils.info("Successfully loaded " + name + ".");
            } catch (IOException var4) {
               ChatUtils.info("An error occurred.");
            }
   
            return 1;
         })));
   }
}
