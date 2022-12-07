package de.tyrannus.venomhack.utils;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.KeyEvent;
import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.events.PlayerDeathEvent;
import de.tyrannus.venomhack.events.PlayerListChangeEvent;
import de.tyrannus.venomhack.events.SendMessageEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.events.TotemPopEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.utils.players.Friends;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.PlayerListEntry;

public final class Analyser {
   public static final Object2IntMap<UUID> totemPops = new Object2IntOpenHashMap();
   private static short kills;
   private static short killStreak;
   private static short deaths;
   private static final List<PlayerListEntry> lastList = new ArrayList();
   private static String lastMessage;
   private static final Map<PlayerEntity, Long> targets = new Object2LongOpenHashMap();
   public static final Map<BlockPos, Long> pendingObsidian = new ConcurrentHashMap<>();

   private Analyser() {
   }

   @EventHandler
   private static void onKey(KeyEvent.Pre event) {
      if (Venomhack.mc.currentScreen == null) {
         for(Module module : Modules.modules()) {
            if (module.bind.equalsKey(event.getKey()) && (event.getAction() == 1 || event.getAction() == 0 && module.toggleOnRelease)) {
               module.toggle();
            }
         }
      } else if (Venomhack.mc.currentScreen instanceof TitleScreen || Venomhack.mc.currentScreen instanceof MultiplayerScreen || Venomhack.mc.currentScreen instanceof SelectWorldScreen) {
         ClickGui clickGui = Modules.get(ClickGui.class);
         if (!clickGui.bind.equalsKey(event.getKey())) {
            return;
         }

         clickGui.toggle();
      }
   }

   @EventHandler
   private static void onReceivePacket(PacketEvent.Receive event) {
      if (event.getPacket() instanceof GameJoinS2CPacket) {
         kills = 0;
         killStreak = 0;
         deaths = 0;
         lastMessage = "";
         totemPops.clear();
         targets.clear();
         lastList.clear();
         pendingObsidian.clear();
      } else {
         Packet entity = event.getPacket();
         if (entity instanceof EntityStatusS2CPacket packet) {
            if (packet.getStatus() != 35) {
               return;
            }

            Entity entityx = packet.getEntity(Venomhack.mc.world);
            if (entityx == null) {
               return;
            }

            synchronized(totemPops) {
               int pops = totemPops.getOrDefault(entityx.getUuid(), 0) + 1;
               totemPops.put(entityx.getUuid(), pops);
               Venomhack.EVENTS.post(TotemPopEvent.get(entityx, pops, entityx instanceof PlayerEntity && targets.containsKey(entityx)));
            }
         } else {
            entity = event.getPacket();
            if (entity instanceof BlockUpdateS2CPacket packet) {
               pendingObsidian.remove(packet.getPos());
            }
         }
      }
   }

   @EventHandler(
      priority = 200
   )
   private static void onTick(TickEvent.Pre event) {
      if (Venomhack.mc.world != null) {
         for(Entry<PlayerEntity, Long> entry : targets.entrySet()) {
            if (entry.getValue() + 5000L < System.currentTimeMillis()) {
               targets.remove(entry.getKey());
            }
         }

         int latency = PlayerUtils.getLatency();

         for(Entry<BlockPos, Long> entry : pendingObsidian.entrySet()) {
            if ((double)(System.currentTimeMillis() - entry.getValue()) > (double)latency * 1.2) {
               pendingObsidian.remove(entry.getKey());
            }
         }

         synchronized(totemPops) {
            for(Entity entity : Venomhack.mc.world.getEntities()) {
               if (entity instanceof PlayerEntity player && player.getHealth() <= 0.0F) {
                  boolean isTarget = targets.containsKey(player);
                  if (player.equals(Venomhack.mc.player)) {
                     setDead();
                  } else if (isTarget) {
                     ++kills;
                     ++killStreak;
                     targets.remove(player);
                  }

                  Venomhack.EVENTS.post(PlayerDeathEvent.get(player, totemPops.removeInt(player.getUuid()), isTarget, false));
               }
            }
         }

         if (Venomhack.mc.getNetworkHandler() != null) {
            Collection<PlayerListEntry> nowList = Venomhack.mc.getNetworkHandler().getPlayerList();
            UUID sessionId = Venomhack.mc.getSession().getProfile().getId();
            ArrayList<PlayerListEntry> nowListCopy = new ArrayList(nowList);
            nowListCopy.removeAll(lastList);

            for(PlayerListEntry entry : nowListCopy) {
               if (!entry.getProfile().getId().equals(sessionId)) {
                  Venomhack.EVENTS.post(PlayerListChangeEvent.Join.get(entry));
               }
            }

            lastList.removeAll(nowList);

            for(PlayerListEntry entry : lastList) {
               if (!entry.getProfile().getId().equals(sessionId)) {
                  Venomhack.EVENTS.post(PlayerListChangeEvent.Leave.get(entry));
               }
            }

            lastList.clear();
            lastList.addAll(nowList);
         }
      }
   }

   @EventHandler
   private static void onPacketSend(PacketEvent.Send event) {
      if (Venomhack.mc.world != null && Venomhack.mc.player != null) {
         Packet target = event.getPacket();
         if (target instanceof PlayerInteractEntityC2SPacket packet) {
            Entity var3 = Venomhack.mc.world.getEntityById(packet.entityId);
            if (var3 instanceof PlayerEntity targetx) {
               if (Venomhack.mc.player == targetx || Friends.isFriend(targetx)) {
                  return;
               }

               targets.put(targetx, System.currentTimeMillis());
            }
         }
      }
   }

   @EventHandler
   private static void onMessageSend(SendMessageEvent event) {
      event.setMessage(applyPlaceholders(event.getMessage()));
      if (!event.isCommand()) {
         lastMessage = event.getMessage();
      }
   }

   private static String applyPlaceholders(String message) {
      boolean singlePlayer = Venomhack.mc.isInSingleplayer();
      return message.replace("{kills}", Short.toString(kills))
         .replace("{ks}", Short.toString(killStreak))
         .replace("{ksSuffix}", TextUtils.getGrammar(killStreak))
         .replace("{deaths}", Short.toString(deaths))
         .replace("{server.ip}", singlePlayer ? Venomhack.mc.getServer().getSaveProperties().getLevelName() : Venomhack.mc.getCurrentServerEntry().address)
         .replace("{server.name}", singlePlayer ? Venomhack.mc.getServer().getSaveProperties().getLevelName() : Venomhack.mc.getCurrentServerEntry().name)
         .replace("{server.online}", Integer.toString(Venomhack.mc.getNetworkHandler().getPlayerList().size()))
         .replace("{ping}", singlePlayer ? "0" : Integer.toString(Venomhack.mc.getNetworkHandler().getPlayerListEntry(Venomhack.mc.player.getUuid()).getLatency()))
         .replace("{kd}", Double.toString(getKD()));
   }

   public static boolean isTarget(PlayerListEntry playerListEntry) {
      if (targets.isEmpty()) {
         return false;
      } else {
         UUID playerId = playerListEntry.getProfile().getId();

         for(PlayerEntity player : targets.keySet()) {
            if (player.getUuid().equals(playerId)) {
               return true;
            }
         }

         return false;
      }
   }

   public static String getLastMessage() {
      return lastMessage;
   }

   public static void setDead() {
      ++deaths;
      killStreak = 0;
   }

   public static double getKD() {
      return deaths == 0 ? (double)kills : MathUtil.round((float)kills / (float)deaths, 1);
   }
}
