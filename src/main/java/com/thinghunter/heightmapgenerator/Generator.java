package com.thinghunter.heightmapgenerator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;

public class Generator extends ChunkGenerator {

	BufferedImage image;
	public static HeightMap plugin;
	int imagew;
	int imageh;

	public Generator(HeightMap plugin) {
		this.plugin = plugin;

		plugin.getLogger().log(
				Level.INFO,
				"Loading Heightmap" + plugin.getDataFolder().getAbsolutePath()
						+ plugin.getConfig().getString("file"));
		File f = new File(plugin.getDataFolder().getAbsolutePath()
				+ plugin.getConfig().getString("file"));

		try {
			image = ImageIO.read(f);
			imagew = image.getWidth();
			imageh = image.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int xyzToByte(int x, int y, int z) {
		return (x * 16 + z) * 128 + y;
	}

	void setBlock(byte[][] chunk_data, int x, int y, int z, short blkid) {
		int sec_id = (y >> 4);
		int yy = y & 0xF;
		if (chunk_data[sec_id] == null) {
			chunk_data[sec_id] = new byte[4096];
		}
		chunk_data[sec_id][(yy << 8) | (z << 4) | x] = (byte) blkid;	}

	@Override
	public byte[][] generateBlockSections(World world, Random random,
			int cx, int cz, BiomeGrid biomes) {
		byte[][] result = new byte[16][];

		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {

				for (int y = 63; y < world.getMaxHeight(); y++)
					setBlock(result, x, y, z, (short) 0);

				for (int y = 0; y < 64; y++)
					setBlock(result, x, y, z, (short) Material.WATER.getId());

				setBlock(result, x, 0, z, (short) Material.BEDROCK.getId());

				int height = getHeight(cx * 16 + x, cz * 16 + z);
				for (int y = 0; y < height; y++) {
					setBlock(result, x, y, z, (short) Material.STONE.getId());
				}

				if (height < 64)
					world.setBiome(cx * 16 + x, cz * 16 + z, Biome.OCEAN);
				else if (height < 85)
					world.setBiome(cx * 16 + x, cz * 16 + z, Biome.PLAINS);
				else if (height < 128)
					world.setBiome(cx * 16 + x, cz * 16 + z,
							Biome.EXTREME_HILLS);
				else
					world.setBiome(cx * 16 + x, cz * 16 + z,
							Biome.SMALL_MOUNTAINS);

			}

		return result;
	}

	public int getHeight(int x, int y) {

		if (x >= imagew || y >= imageh || x <= 0 || y <= 0)
			return 0;
		
		Color colour = new Color(image.getRGB(x, y));
		
		
		int height = colour.getRed();
		if (height > 254)
			height = 254;

		return height;

	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, image.getWidth()/2, world.getHighestBlockAt(0, 0).getY(), image.getHeight());
	}
	
	
}
