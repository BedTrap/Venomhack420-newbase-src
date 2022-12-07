package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.events.KeyEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.players.Friends;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.EntityHitResult;

public class MiddleClickFriend extends Module {
   public MiddleClickFriend() {
      super(Module.Categories.MISC, "middle-click-friend", "Allows you to friend / unfriend players by using your middle mouse button on them.");
   }

   @EventHandler
   private void onKey(KeyEvent.Post event) {
      if (mc.currentScreen == null && event.getKey().getCode() == 2 && event.getAction() != 0) {
         HitResult friend = mc.crosshairTarget;
         if (friend instanceof EntityHitResult result) {
            Entity var5 = result.getEntity();
            if (var5 instanceof PlayerEntity player) {
               String friendx = player.getEntityName();
               if (Friends.add(friendx)) {
                  ChatUtils.info("Added " + friendx + " to friends list.");
               } else {
                  Friends.remove(friendx);
                  ChatUtils.info("Removed " + friendx + " from friends list.");
               }

               return;
            }
         }
      }
   }
}
