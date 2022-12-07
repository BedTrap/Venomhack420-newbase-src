package de.tyrannus.venomhack.commands.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.FriendsArgumentType;
import de.tyrannus.venomhack.commands.arguments.PlayerArgumentType;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.players.Friends;
import java.io.File;
import java.io.IOException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtIo;

public class FriendsCommand extends Command {
   public FriendsCommand() {
      super("friends", "Allows you to add or remove other player as friends.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.executes(context -> {
         if (Friends.FRIENDS.isEmpty()) {
            ChatUtils.info("Lmfao you have no friends.");
            return 1;
         } else {
            ChatUtils.info("You currently have these people friended:");

            for(String friend : Friends.FRIENDS) {
               ChatUtils.sendMsg(friend);
            }

            return 1;
         }
      })).then(this.lit("import").executes(context -> {
         this.importMeteorFriends();
         return 1;
      }))).then(this.lit("add").then(this.arg("friend", new PlayerArgumentType()).executes(context -> {
         addFriend(((GameProfile)context.getArgument("friend", GameProfile.class)).getName());
         return 1;
      })))).then(this.lit("remove").then(this.arg("friend", new FriendsArgumentType()).executes(context -> {
         String name = (String)context.getArgument("friend", String.class);
         if (Friends.remove(name)) {
            ChatUtils.info("Removed " + name + " from friends list.");
         } else {
            ChatUtils.info(name + " has not been friended yet!");
         }

         return 1;
      })));
   }

   public static void addFriend(String name) {
      if (Friends.add(name)) {
         ChatUtils.info("Added " + name + " to friends list.");
      } else {
         ChatUtils.info(name + " is already friended!");
      }
   }

   private void importMeteorFriends() {
      File meteorFriendsFile = new File("meteor-client", "friends.nbt");
      if (!meteorFriendsFile.exists()) {
         ChatUtils.info("You don't have anyone friended on meteor.");
      } else {
         try {
            NbtCompound nbt = NbtIo.read(meteorFriendsFile);
            if (nbt == null || !nbt.contains("friends")) {
               return;
            }

            int importedFriends = 0;
            NbtList list = nbt.getList("friends", 10);

            for(int i = 0; i < list.size(); ++i) {
               if (Friends.add(list.getCompound(i).getString("name"))) {
                  ++importedFriends;
               }
            }

            ChatUtils.info("Imported " + importedFriends + " friend" + (importedFriends == 1 ? "" : "s") + " from meteor client.");
         } catch (IOException var6) {
            ChatUtils.info(var6.getMessage());
         }
      }
   }
}
