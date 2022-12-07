package de.tyrannus.venomhack.utils;

public class Timer {
   private long nanoTime = -1L;

   public void reset() {
      this.nanoTime = System.nanoTime();
   }

   public void setTicks(long ticks) {
      this.nanoTime = System.nanoTime() - convertTicksToNano(ticks);
   }

   public void setSec(long time) {
      this.nanoTime = System.nanoTime() - convertSecToNano(time);
   }

   public void setMs(long time) {
      this.nanoTime = System.nanoTime() - convertMillisToNano(time);
   }

   public long millisPassed() {
      return convertNanoToMillis(System.nanoTime() - this.nanoTime);
   }

   public long getTicks() {
      return convertNanoToTicks(this.nanoTime);
   }

   public boolean passedNano(long time) {
      return System.nanoTime() - this.nanoTime >= time;
   }

   public boolean passedMillis(long time) {
      return this.passedNano(convertMillisToNano(time));
   }

   public static long convertMillisToTicks(long time) {
      return time / 50L;
   }

   public static long convertTicksToMillis(long ticks) {
      return ticks * 50L;
   }

   public static long convertNanoToTicks(long time) {
      return convertMillisToTicks(convertNanoToMillis(time));
   }

   public static long convertTicksToNano(long ticks) {
      return convertMillisToNano(convertTicksToMillis(ticks));
   }

   public static long convertSecToMillis(long time) {
      return time * 1000L;
   }

   public static long convertSecToNano(long time) {
      return convertMicroToNano(convertMillisToMicro(convertSecToMillis(time)));
   }

   public static long convertMillisToMicro(long time) {
      return time * 1000L;
   }

   public static long convertMillisToNano(long time) {
      return convertMicroToNano(convertMillisToMicro(time));
   }

   public static long convertMicroToNano(long time) {
      return time * 1000L;
   }

   public static long convertNanoToMicro(long time) {
      return time / 1000L;
   }

   public static long convertNanoToMillis(long time) {
      return convertMicroToMillis(convertNanoToMicro(time));
   }

   public static long convertMicroToMillis(long time) {
      return time / 1000L;
   }
}
