package de.tyrannus.venomhack.modules.combat.autocrystal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PlacementInfo {
   private BlockPos blockPos;
   private Vec3d pos;
   private Direction direction;
   private LivingEntity target;
   private float damage = 0.0F;
   private float selfDamage = 0.0F;
   private float friendDamage = 0.0F;
   private byte pops = 0;
   private byte friendPops = 0;
   private byte surroundBreak = 0;
   private boolean faceplace = false;
   private boolean surroundHold = false;
   private boolean support = false;
   private boolean hasToBreak = false;
   private boolean selfPop = false;

   public PlacementInfo() {
   }

   public PlacementInfo(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   public void setSelfPop() {
      this.selfPop = true;
   }

   public void setVec3d(Vec3d vec3d) {
      this.pos = vec3d;
   }

   public void setSelfDamage(float selfDamage) {
      this.selfDamage = selfDamage;
   }

   public void addFriendDamage(float friendDamage) {
      this.friendDamage += friendDamage;
   }

   public void incrementFriendPops() {
      ++this.friendPops;
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public void setTarget(LivingEntity target) {
      this.target = target;
   }

   public void incrementPops() {
      ++this.pops;
   }

   public void setSurroundBreak(byte surroundBreak) {
      this.surroundBreak = surroundBreak;
   }

   public void setFaceplace(boolean faceplace) {
      this.faceplace = faceplace;
   }

   public void setSurroundHold(boolean surroundHold) {
      this.surroundHold = surroundHold;
   }

   public void setShouldBreak(boolean shouldBreak) {
      this.hasToBreak = shouldBreak;
   }

   public void setSupport(boolean support) {
      this.support = support;
   }

   public void setDirection(Direction direction) {
      this.direction = direction;
   }

   public void reset(LivingEntity target) {
      this.faceplace = false;
      this.surroundHold = false;
      this.surroundBreak = 0;
      this.target = target;
   }

   public void reset(LivingEntity target, float damage) {
      this.faceplace = false;
      this.surroundHold = false;
      this.surroundBreak = 0;
      this.damage = damage;
      this.target = target;
   }

   public void copy(PlacementInfo oldPlace) {
      this.blockPos = oldPlace.blockPos;
      this.pos = oldPlace.getPos();
      this.direction = oldPlace.direction;
      this.target = oldPlace.target;
      this.damage = oldPlace.damage;
      this.selfDamage = oldPlace.selfDamage;
      this.friendDamage = oldPlace.friendDamage;
      this.selfPop = oldPlace.selfPop;
      this.pops = oldPlace.pops;
      this.friendPops = oldPlace.friendPops;
      this.surroundBreak = oldPlace.surroundBreak;
      this.faceplace = oldPlace.faceplace;
      this.surroundHold = oldPlace.surroundHold;
      this.support = oldPlace.support;
      this.hasToBreak = oldPlace.hasToBreak;
   }

   public LivingEntity getTarget() {
      return this.target;
   }

   public float value() {
      return 1.0F;
   }

   public float getDamage() {
      return this.damage;
   }

   public float getSelfDamage() {
      return this.selfDamage;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public Vec3d getPos() {
      return this.pos;
   }

   public byte getSurroundBreak() {
      return this.surroundBreak;
   }

   public boolean isFaceplace() {
      return this.faceplace;
   }

   public boolean isSurroundHold() {
      return this.surroundHold;
   }

   public boolean isSupport() {
      return this.support;
   }

   public boolean shouldBreak() {
      return this.hasToBreak;
   }

   public Direction getDirection() {
      return this.direction;
   }
}
