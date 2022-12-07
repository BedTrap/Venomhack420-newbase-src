package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.events.ChunkLoadEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.TextUtils;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.text.MutableText;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.text.ClickEvent.class_2559;
import net.minecraft.text.HoverEvent.class_5247;
import org.jetbrains.annotations.Nullable;

public class EGapFinder extends Module {
   private final Setting<EGapFinder.FinderMode> finderMode = this.setting(
      "finder-mode", "The egap finder mode. (AllChunks mode loads all chunks in the specified radius).", EGapFinder.FinderMode.MANUAL
   );
   private final Setting<Boolean> addWaypoint = this.setting("add-Waypoints", "Adds waypoints to chests that have egaps.", Boolean.valueOf(true));
   private final Setting<String> voxel = this.setting("voxelmap-waypoints", "Creates waypoints for voxelmap with this ip.", "phoenixanarchy.com");
   private final Setting<Boolean> chatNotif = this.setting("chat-info", "Send chat messages to update you on progress.", Boolean.valueOf(true));
   private final Setting<Boolean> playSound = this.setting("play-sound", "Plays a sound when you find an egap.", Boolean.valueOf(false));
   private final Setting<Boolean> pause = this.setting("pause", "Pauses all automated egap finder activity.", Boolean.valueOf(false));
   private final Setting<Integer> chunkCacheLimit = this.setting(
      "chunk-cache-limit", "The # of cached chunks at which the egap finder will pause until the count is below it.", Integer.valueOf(10000), 1.0F, 50000.0F
   );
   private final Setting<Integer> delayAfterFullCache = this.setting(
      "delay-after-full-cache", "How many ticks to wait after the cache limit is reached before resuming.", Integer.valueOf(100), 0.0F, 200.0F
   );
   private final Setting<Boolean> randomColor = this.setting("random-color", "Whether or not to randomise the waypoint's color.", Boolean.valueOf(true));
   private final Setting<Integer> radius = this.setting(
      "radius",
      "The radius of the square area to search for egaps.",
      Integer.valueOf(1600),
      () -> this.finderMode.get() == EGapFinder.FinderMode.ALL_CHUNKS,
      16.0F,
      32000.0F
   );
   private final Setting<Integer> chunkLoadDelay = this.setting(
      "chunk-load-delay",
      "How much time to wait before loading the next chunk, in ticks.",
      Integer.valueOf(30),
      () -> this.finderMode.get() == EGapFinder.FinderMode.ALL_CHUNKS,
      0.0F,
      60.0F
   );
   private final Setting<Integer> loadedChunksSize = this.setting(
      "loaded-chunks-size",
      "How many chunks to have loaded at any one time.",
      Integer.valueOf(40),
      () -> this.finderMode.get() == EGapFinder.FinderMode.ALL_CHUNKS,
      1.0F,
      400.0F
   );
   private final Setting<Integer> chunksPerTick = this.setting(
      "chunks-per-tick",
      "How many chunks to load each tick.",
      Integer.valueOf(5),
      () -> this.finderMode.get() == EGapFinder.FinderMode.ALL_CHUNKS,
      1.0F,
      100.0F
   );
   private final ArrayList<BlockPos> coordList = new ArrayList();
   private final ArrayList<BlockPos> chunkQueue = new ArrayList();
   private ServerWorld serverWorld;
   private int chunkDelay;
   private int currentChunkNum;
   private int waitTimer;
   private int egapsFoundCount;
   private boolean finishedQueue;
   private ExecutorService executor;
   private long startTime;
   private long lastPercentage;

   public EGapFinder() {
      super(Module.Categories.MISC, "egap-finder", "Automatically finds egaps for you.");
   }

   @Override
   public void onEnable() {
      try {
         if (mc.getServer() == null || mc.world == null) {
            return;
         }

         if (!mc.isInSingleplayer()) {
            this.toggleWithError("Not in singleplayer, disabling...");
            return;
         }

         this.executor = Executors.newSingleThreadExecutor();
         this.coordList.clear();
         this.egapsFoundCount = 0;
         this.waitTimer = 0;
         this.serverWorld = mc.getServer().getWorld(mc.world.getRegistryKey());
         if (this.finderMode.get() == EGapFinder.FinderMode.MANUAL) {
            return;
         }

         this.chunkDelay = this.chunkLoadDelay.get();
         this.currentChunkNum = 0;
         this.finishedQueue = false;
         this.chunkQueue.clear();
         this.startTime = System.nanoTime();
         this.lastPercentage = 0L;

         for(int currentChunk = 0; (double)currentChunk < Math.floor((double)this.radius.get().intValue() / 16.0) + 1.0; ++currentChunk) {
            for(int l = -currentChunk; l < currentChunk + 1; ++l) {
               for(int h = -currentChunk; h < currentChunk + 1; ++h) {
                  if (Math.abs(h) == Math.abs(currentChunk) || Math.abs(l) == Math.abs(currentChunk)) {
                     this.chunkQueue
                        .add(new BlockPos(mc.player.getBlockPos().getX() + 16 * l, 50, mc.player.getBlockPos().getZ() + 16 * h));
                  }
               }
            }
         }

         this.finishedQueue = true;
         this.info(this.chunkQueue.size() + " chunks, area of side length " + 2.0 * Math.floor((double)((float)this.radius.get().intValue() / 16.0F)));
      } catch (Exception var4) {
         var4.printStackTrace();
         this.toggleWithError("An error occured on enabling, toggling the module off.");
      }
   }

   @Override
   public void onDisable() {
      if (this.finderMode.get() == EGapFinder.FinderMode.ALL_CHUNKS && this.serverWorld != null) {
         TextUtils.sendNewMessage("forceload remove all", true);
         this.executor.shutdown();
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      try {
         --this.waitTimer;
         if (this.waitTimer > 0) {
            return;
         }

         this.waitTimer = 0;
         if (this.finderMode.get() == EGapFinder.FinderMode.MANUAL) {
            return;
         }

         if (this.serverWorld.getChunkManager().getLoadedChunkCount() >= this.chunkCacheLimit.get()) {
            TextUtils.sendNewMessage("forceload remove all", true);
            this.waitTimer = this.delayAfterFullCache.get();
         } else if (this.finishedQueue && !this.pause.get()) {
            this.executor.submit(this::doChunkLoading);
         }
      } catch (Exception var3) {
         var3.printStackTrace();
         this.toggleWithError("An error occured on tick, toggling the module off.");
      }
   }

   public void doChunkLoading() {
      --this.chunkDelay;
      if (this.chunkDelay <= 0) {
         for(int i = 0; i < this.chunksPerTick.get(); ++i) {
            if (this.currentChunkNum + i >= this.chunkQueue.size() - 1) {
               if (this.isActive()) {
                  this.toggleWithError("Done.");
               }

               return;
            }

            int loadX = ((BlockPos)this.chunkQueue.get(this.currentChunkNum + i)).getX();
            int loadZ = ((BlockPos)this.chunkQueue.get(this.currentChunkNum + i)).getZ();
            this.serverWorld.setChunkForced(ChunkSectionPos.getSectionCoord(loadX), ChunkSectionPos.getSectionCoord(loadZ), true);

            for(BlockEntity blockEntity : this.serverWorld.getWorldChunk((BlockPos)this.chunkQueue.get(this.currentChunkNum + i)).getBlockEntities().values()) {
               if (blockEntity instanceof ChestBlockEntity) {
                  this.checkChestForEgaps(blockEntity.getPos());
               }
            }

            long percentage = Math.round((double)(this.currentChunkNum + i) * 100.0 / (double)Math.max(1, this.chunkQueue.size()) * 100.0) / 100L;
            boolean e = percentage <= this.lastPercentage;
            this.lastPercentage = percentage;
            if (this.chatNotif.get() && !e && percentage % 5L == 0L) {
               this.lastPercentage = percentage;
               long passedTime = System.nanoTime() - this.startTime;
               long totalEstimatedTime = passedTime * 100L / Math.max(1L, percentage);
               long remainingSeconds = (totalEstimatedTime - passedTime) / 1000000000L;
               this.info(
                  "Checked "
                     + this.currentChunkNum
                     + " chunks so far ("
                     + percentage
                     + "%). Estimated time until completion: "
                     + remainingSeconds
                     + " seconds."
               );
            }
         }

         long passedTime = System.nanoTime() - this.startTime;
         long totalEstimatedTime = passedTime * 100L / Math.max(1L, this.lastPercentage);
         long remainingSeconds = (totalEstimatedTime - passedTime) / 1000000000L;
         mc.player
            .sendMessage(
               Text.of(
                  "Checked "
                     + this.currentChunkNum
                     + " chunks so far ("
                     + this.lastPercentage
                     + "%). Estimated time until completion: "
                     + remainingSeconds
                     + " seconds."
               ),
               true
            );
         if (this.serverWorld.getForcedChunks().size() >= this.loadedChunksSize.get()) {
            TextUtils.sendNewMessage("forceload remove all", true);
         }

         this.currentChunkNum += this.chunksPerTick.get();
         this.chunkDelay = this.chunkLoadDelay.get();
      }
   }

   private void checkChestForEgaps(BlockPos chestPos) {
      if (this.finderMode.get() != EGapFinder.FinderMode.ALL_CHUNKS) {
         this.serverWorld.getBlockState(chestPos);
      }

      BlockEntity stack = this.serverWorld.getWorldChunk(chestPos).getBlockEntity(chestPos);
      if (stack instanceof LootableContainerBlockEntity lootable) {
         lootable.checkLootInteraction(mc.player);
      }

      for(int i = 0; i < 27; ++i) {
         ItemStack stack = getInventory(this.serverWorld, chestPos).getStack(i);
         if (stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE && !this.coordList.contains(chestPos)) {
            this.coordList.add(chestPos);
            ++this.egapsFoundCount;
            MutableText text = Text.literal(
               "Egap " + this.egapsFoundCount + " at " + chestPos.getX() + " " + chestPos.getY() + " " + chestPos.getZ()
            );
            text.styled(
               style -> style.withClickEvent(
                        new ClickEvent(
                           class_2559.SUGGEST_COMMAND, "/tp @s " + chestPos.getX() + " " + (chestPos.getY() + 1) + " " + chestPos.getZ()
                        )
                     )
                     .withHoverEvent(new HoverEvent(class_5247.SHOW_TEXT, Text.literal("Tp to coords.")))
            );
            this.info(text);
            if (this.playSound.get()) {
               mc.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 2.0F, 1.0F);
            }

            if (this.addWaypoint.get()) {
               Color waypointColor = new Color(255, 215, 0);
               if (this.randomColor.get()) {
                  waypointColor = Color.getHSBColor(ThreadLocalRandom.current().nextFloat() * 360.0F, 1.0F, 1.0F);
               }

               RegistryKey<World> dim = mc.world.getRegistryKey();
               int gapX = chestPos.getX();
               int gapZ = chestPos.getZ();
               if (dim == World.NETHER) {
                  gapX *= 8;
                  gapZ *= 8;
               }

               writeToFile(
                  "name:Egap "
                     + this.egapsFoundCount
                     + ",x:"
                     + gapX
                     + ",z:"
                     + gapZ
                     + ",y:"
                     + chestPos.getY()
                     + ",enabled:true,red:"
                     + (float)waypointColor.getAlpha() / 255.0F
                     + ",green:"
                     + (float)waypointColor.getGreen() / 255.0F
                     + ",blue:"
                     + (float)waypointColor.getBlue() / 255.0F
                     + ",suffix:,world:,dimensions:"
                     + (dim == World.END ? "end#" : "overworld#the_nether#"),
                  this.voxel.get()
               );
            }
         }
      }
   }

   @EventHandler
   private void onChunkData(ChunkLoadEvent event) {
      try {
         if (this.finderMode.get() == EGapFinder.FinderMode.ALL_CHUNKS) {
            return;
         }

         for(BlockEntity blockEntity : event.getChunk().getBlockEntities().values()) {
            if (blockEntity instanceof ChestBlockEntity) {
               this.checkChestForEgaps(blockEntity.getPos());
            }
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         this.toggleWithError("An error occured on chunk data, toggling the module off.");
      }
   }

   @Nullable
   public static Inventory getInventory(World world, BlockPos pos) {
      ChunkPos chunkPos = world.getChunk(pos).getPos();
      if (world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
         BlockEntity var4 = world.getWorldChunk(pos).getBlockEntity(pos);
         if (var4 instanceof Inventory) {
            return (Inventory)var4;
         }
      }

      return null;
   }

   protected static void writeToFile(String coords, String server) {
      try {
         Files.createDirectories(Paths.get("voxelmap"));
         FileWriter fileWriter = new FileWriter("voxelmap//" + server + ".points", true);
         PrintWriter out = new PrintWriter(new BufferedWriter(fileWriter));
         out.println(coords);
      } catch (IOException var4) {
         var4.printStackTrace();
      }
   }

   @Override
   public String getArrayText() {
      return this.egapsFoundCount + " Egaps";
   }

   public static enum FinderMode {
      MANUAL,
      ALL_CHUNKS;
   }
}
