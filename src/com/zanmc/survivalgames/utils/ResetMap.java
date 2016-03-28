package com.zanmc.survivalgames.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.plugin.Plugin;

import com.zanmc.survivalgames.handlers.Map;

public class ResetMap {

	public static void rollback(String filename, Plugin pl) {
		File delFile = new File(filename);
		try {
			dirDelete(delFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File srcDir = new File(pl.getDataFolder(), File.separator + "backups" + File.separator + filename);

		File destDir = new File(filename);

		try {
			copyFolder(srcDir, destDir);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createBackup(Map map, Plugin p) {

		File backupDir = new File(p.getDataFolder(), "backups");
		if (!backupDir.exists()) {
			backupDir.mkdir();
		}

		File dest = new File(p.getDataFolder(), File.separator + "backups" + File.separator + map.getFileName());
		if (dest.exists()) {
			System.out.println("No backup needed.");
		} else {
			File src = new File(map.getFileName());
			try {
				copyFolder(src, dest);
			} catch (IOException e) {
				System.out.println("Could not make backup!");
				e.printStackTrace();
			}
		}
	}

	public static void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists())
				dest.mkdir();

			String files[] = src.list();

			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);

				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;

			while ((length = in.read(buffer)) > 0)
				out.write(buffer, 0, length);

			in.close();
			out.close();
		}
	}

	public static void dirDelete(File file) throws IOException {
		if (file.isDirectory()) {
			if (file.list().length == 0)
				file.delete();
			else {
				String files[] = file.list();

				for (String temp : files)
					dirDelete(new File(file, temp));

				if (file.list().length == 0)
					file.delete();
			}
		} else
			file.delete();
	}

}
