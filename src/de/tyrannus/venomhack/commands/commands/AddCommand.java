package de.tyrannus.venomhack.commands.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.PlayerArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class AddCommand extends Command {
   public AddCommand() {
      super("add", "Shortcut for friends add (Adds specified player to friends list).");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.then(this.arg("name", new PlayerArgumentType()).executes(context -> {
         FriendsCommand.addFriend(((GameProfile)context.getArgument("name", GameProfile.class)).getName());
         return 1;
      }));
   }
}
