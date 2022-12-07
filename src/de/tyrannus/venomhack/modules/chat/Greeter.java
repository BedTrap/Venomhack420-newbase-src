package de.tyrannus.venomhack.modules.chat;

import com.mojang.authlib.GameProfile;
import de.tyrannus.venomhack.events.PlayerListChangeEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.TextUtils;
import de.tyrannus.venomhack.utils.players.Friends;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.client.network.OtherClientPlayerEntity;

public class Greeter extends Module {
   private final Setting<Integer> delay = this.setting("delay", "Minimum delay in ticks between sending messages.", Integer.valueOf(5));
   private final Setting<Boolean> clientSide = this.setting("client-side", "Notifies you when someone joins or leaves the server.", Boolean.valueOf(false));
   private final Setting<Greeter.JoinLeaveEvent> mode = this.setting("mode", "What to announce.", Greeter.JoinLeaveEvent.BOTH);
   private final Setting<Boolean> onlyFriends = this.setting("only-friends", "Will only greet friends.", Boolean.valueOf(false));
   private final Setting<Boolean> dms = this.setting("dm-welcome", "Will greet the player in their dms.", Boolean.valueOf(false));
   private final Setting<String> welcomeMsg = this.setting(
      "welcome-message", "Use {player} for the players name and {server.name} for the server's name.", "Welcome {player} to {server.name}."
   );
   private final Setting<String> byeMsg = this.setting(
      "goodbye-message", "Use {player} for the players name and {server.name} for the server's name.", "See you later {player}."
   );
   private int delayLeft;

   public Greeter() {
      super(Module.Categories.CHAT, "greeter", "Sends a welcome message when someone joins the server.");
   }

   @Override
   protected void onEnable() {
      this.delayLeft = 0;
   }

   @EventHandler
   private void onJoin(PlayerListChangeEvent.Join event) {
      if (this.mode.get().joins()) {
         if (this.welcomeMsg.get().isEmpty()) {
            ChatUtils.info(Text.translatable("greeter.noWelcome"));
         } else {
            GameProfile profile = event.getPlayer().getProfile();
            String name = profile.getName();
            if (this.onlyFriends.get() && !Friends.isFriend(new OtherClientPlayerEntity(mc.world, profile, null))) {
               return;
            }

            if (this.clientSide.get()) {
               ChatUtils.sendMsg(Text.literal(name).append(Text.translatable("greeter.join")).formatted(Formatting.YELLOW));
               return;
            }

            if (this.delayLeft > 0) {
               return;
            }

            StringBuilder msg = new StringBuilder();
            if (this.dms.get()) {
               msg.insert(0, "/msg " + name + " ");
            }

            msg.append(this.welcomeMsg.get());
            TextUtils.sendNewMessage(msg.toString().replace("{player}", name));
            this.delayLeft = this.delay.get();
         }
      }
   }

   @EventHandler
   private void onLeave(PlayerListChangeEvent.Leave event) {
      if (this.mode.get().leaves()) {
         if (this.byeMsg.get().isEmpty()) {
            ChatUtils.info(Text.translatable("greeter.noGoodbye"));
         } else {
            GameProfile profile = event.getPlayer().getProfile();
            String name = profile.getName();
            if (this.onlyFriends.get() && !Friends.isFriend(new OtherClientPlayerEntity(mc.world, profile, null))) {
               return;
            }

            if (this.clientSide.get()) {
               ChatUtils.sendMsg(Text.literal(name).append(Text.translatable("greeter.leave")).formatted(Formatting.YELLOW));
               return;
            }

            if (this.delayLeft > 0) {
               return;
            }

            TextUtils.sendNewMessage(this.byeMsg.get().replace("{player}", name));
            this.delayLeft = this.delay.get();
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      --this.delayLeft;
   }

   private static enum JoinLeaveEvent {
      JOIN,
      LEAVE,
      BOTH,
      NONE;

      public boolean joins() {
         return this == JOIN || this == BOTH;
      }

      public boolean leaves() {
         return this == LEAVE || this == BOTH;
      }
   }
}
