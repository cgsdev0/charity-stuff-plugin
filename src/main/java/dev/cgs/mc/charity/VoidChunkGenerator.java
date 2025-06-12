package dev.cgs.mc.charity;

import java.util.Random;
import org.bukkit.HeightMap;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class VoidChunkGenerator extends ChunkGenerator {
  @Override
  public void generateNoise(
      WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {}

  @Override
  public boolean shouldGenerateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateCaves(WorldInfo worldInfo, Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateDecorations(
      WorldInfo worldInfo, Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateMobs(WorldInfo worldInfo, Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public boolean shouldGenerateStructures(
      WorldInfo worldInfo, Random random, int chunkX, int chunkZ) {
    return false;
  }

  @Override
  public int getBaseHeight(WorldInfo worldInfo, Random random, int x, int z, HeightMap heightMap) {
    return 0;
  }

  @Override
  public boolean isParallelCapable() {
    return true;
  }
}
