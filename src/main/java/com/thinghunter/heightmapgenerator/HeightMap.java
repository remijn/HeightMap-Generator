package com.thinghunter.heightmapgenerator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class HeightMap extends JavaPlugin{

	public static HeightMap current;
	private Generator generator;
	BufferedImage image;
	
	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		saveDefaultConfig();
		generator = new Generator(this);
		
		File f = new File(getDataFolder().getAbsolutePath()
				+ getConfig().getString("file"));

		try {
			image = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return generator;
	}
	
}
