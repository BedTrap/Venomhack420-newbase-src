package de.tyrannus.venomhack.modules.misc;

import com.mojang.authlib.GameProfile;
import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import java.util.UUID;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity.class_5529;

public class FakePlayer extends Module {
   private final Setting<String> name = this.setting("name", "The name of the fakeplayer.", "Fakeplayer");
   private OtherClientPlayerEntity fakePlayer;

   public FakePlayer() {
      super(Module.Categories.MISC, "fake-player", "Spawns a fake player for testing.");
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof GameJoinS2CPacket) {
         this.toggle(false);
      }
   }

   @Override
   public void onEnable() {
      String fakePlayerName = this.name.get();
      UUID fakePlayerUUID = UUID.randomUUID();
      this.fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(fakePlayerUUID, fakePlayerName), mc.player.getPublicKey());
      this.fakePlayer.copyPositionAndRotation(mc.player);
      this.fakePlayer.setId(-420);
      this.fakePlayer.copyFrom(mc.player);
      NbtCompound compoundTag = new NbtCompound();
      mc.player.writeCustomDataToNbt(compoundTag);
      this.fakePlayer.readCustomDataFromNbt(compoundTag);
      mc.world.addEntity(this.fakePlayer.getId(), this.fakePlayer);
      mc.world.getPlayers().add(this.fakePlayer);
   }

   @Override
   public void onDisable() {
      if (this.fakePlayer != null) {
         mc.world.removeEntity(this.fakePlayer.getId(), class_5529.DISCARDED);
      }
   }
}
