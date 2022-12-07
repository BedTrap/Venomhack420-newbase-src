package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.ChunkLoadEvent;
import java.util.BitSet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChunkDataS2CPacket.class})
public class ChunkDataS2CPacketMixin {
   @Inject(
      method = {"<init>(Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/chunk/light/LightingProvider;Ljava/util/BitSet;Ljava/util/BitSet;Z)V"},
      at = {@At("TAIL")}
   )
   private void onInit(WorldChunk chunk, LightingProvider lightProvider, BitSet skyBits, BitSet blockBits, boolean nonEdge, CallbackInfo ci) {
      Venomhack.EVENTS.post(ChunkLoadEvent.get(chunk));
   }
}
