package com.suda.msgcenter.util;

import java.io.File;

import com.suda.msgcenter.main.SudaApplication;
import com.suda.msgcenter.main.SudaConstants;



public class DiskCache {
	public static final String DISK_CACHE_DIR = "ivcache";

	private final File mCacheDir;

	private DiskCache(File cacheDir) {
		mCacheDir = cacheDir;
	}

	public static DiskCache openCache() {
		File cacheDir = getDiskCacheDir(DISK_CACHE_DIR);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		if (cacheDir.isDirectory() && cacheDir.canWrite()) {
			return new DiskCache(cacheDir);
		}
		return null;
	}

	public static File getDiskCacheDir(String uniqueName) {
		final String cachePath = FileUtils.getSDPath();
		return new File(cachePath + File.separator + uniqueName);

	}

	private static void clearCache(File cacheDir) {
		final File[] files = cacheDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	public static String getFilePath(String url) {
		return FunctionUtil.EncoderByMd5(url) + ".cac";
	}

	public String createFilePath(File cacheDir, String key) {
		return cacheDir.getAbsolutePath() + File.separator + getFilePath(key);

	}

	/**
	 * Create a constant cache file path using the current cache directory and
	 * an image key.
	 * 
	 * @param key
	 * @return
	 */
	public String createFilePath(String key) {
		return createFilePath(mCacheDir, key);
	}

	public String get(String key) {
		final String existingFile = createFilePath(mCacheDir, key);
		File file = new File(existingFile);
		if (file != null && file.exists()) {
			file.setLastModified(System.currentTimeMillis());
			return existingFile;
		}
		return null;

	}

	public static long getFolderSize(java.io.File file) {
		long size = 0;
		if (file.exists()) {
			if (file.isDirectory()) {
				java.io.File[] fileList = file.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					size = size + getFolderSize(fileList[i]);
				}
			} else {
				size = size + file.length();
			}
		}
		return size;
	}

	/**
	 * Checks if a specific key exist in the cache.
	 * 
	 * @param key
	 *            The unique identifier for the bitmap
	 * @return true if found, false otherwise
	 */
	public boolean containsKey(String key) {
		final String existingFile = createFilePath(mCacheDir, key);
		File file = new File(existingFile);
		if (file != null && file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * Removes all disk cache entries from this instance cache dir
	 */
	public void clearCache() {
		DiskCache.clearCache(mCacheDir);
	}

	public static void clearOldCache() {
		File cacheDir = getDiskCacheDir(DISK_CACHE_DIR);
		delOldCache(cacheDir, 15);
		File appcache = new File(SudaApplication.getInstance().getFilesDir().getAbsolutePath() + "/" + SudaConstants.APP_DIR);
		delOldCache(appcache, 5);
	}

	/**
	 * 删除超过days天未使用的文件
	 * */
	private static void delOldCache(File cacheDir, int days) {
		if (cacheDir != null && cacheDir.exists()) {
			if (cacheDir.isDirectory()) {
				final File[] files = cacheDir.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						delOldCache(files[i], days);
					}
				}
			} else{
				long ts = cacheDir.lastModified();
				long tt = System.currentTimeMillis();
				if ((ts + days * 24 * 3600000) < tt) {
					cacheDir.delete();
					cacheDir=null;
				}
			}
		}
	}

	public void clearCache(String uniqueName) {
		File cacheDir = getDiskCacheDir(uniqueName);
		clearCache(cacheDir);
	}

}
