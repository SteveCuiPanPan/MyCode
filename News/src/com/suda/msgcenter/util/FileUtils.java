package com.suda.msgcenter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.suda.msgcenter.main.SudaApplication;
import com.suda.msgcenter.main.SudaConstants;

import android.content.Context;
import android.os.Environment;


public class FileUtils {

	private static int FILESIZE = 4 * 1024;

	public FileUtils() {
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File createSDFile(String fileName) throws IOException {
		File file = new File(fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public static File createSDDir(String dirName) {
		File dir = new File(dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(FileUtils.getSDPath() + fileName);
		return file.exists();
	}
	
	public boolean delExistFile(String fileName) {
		File file = new File(FileUtils.getSDPath() + fileName);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}
	public static boolean delFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public static File write2SDFromInput(String fileName, InputStream input) {
		if (input == null) {
			return null;
		}

		File file = null;
		OutputStream output = null;
		try {
			File dir = (new File(fileName)).getParentFile();
			createSDDir(dir.getAbsolutePath());
			file = createSDFile(fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];

			int n = 0; // count = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public static File write2SDFromByte(String fileName, byte[] buffer) {

		File file = null;
		OutputStream output = null;
		try {
			File dir = (new File(fileName)).getParentFile();
			createSDDir(dir.getAbsolutePath());
			file = createSDFile(fileName);
			output = new FileOutputStream(file);
			output.write(buffer);
			output.flush();
		} catch (Exception e) {
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				
			}
		}
		return file;
	}

	public static long getSDCardFreeSpace() {
		long ret = 0;
		String state = android.os.Environment.getExternalStorageState();
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = android.os.Environment.getExternalStorageDirectory();
			android.os.StatFs sf = new android.os.StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			// long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			ret = availCount * blockSize / 1024;// kb
		}
		return ret;
	}
	
	//用户文件是否存在
	public static boolean isFileExist(Context ct,String fileName) {
		File file = new File(ct.getFilesDir() + "/" + fileName);
		return file.exists();
	}
	
	//保存文件到 应用本地
	public static void saveData2Native(Context ctx,String filename, byte[] bt) {
		FileOutputStream fout = null;
		try {
			fout = ctx.openFileOutput(filename, 0);
			fout.write(bt);
			fout.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fout != null) {
					fout.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getNativeFile(Context ctx, String filename) {
		String src = "";
		if (isFileExist(ctx, filename)) {
			InputStream in = null;
			BufferedReader reader = null;
			try {
				in = ctx.openFileInput(filename);
				reader = new BufferedReader(new InputStreamReader(in));
				String line = "";
				StringBuffer buffer = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
				src = buffer.toString();
			} catch (Exception e) {
				LogUtil.e(SudaConstants.APP_TAG, "getNativeFile");
			} finally {
				try {
					if (reader != null)
						reader.close();
					if (in != null)
						in.close();
				} catch (Exception e) {
				}
			}
		}
		return src;
	}
	public static void deleteNativeFile(Context ctx, String filename) {
		if (isFileExist(ctx, filename))
			ctx.deleteFile(filename);
	}
	
	public static String getSDPath() {
			String path = "";
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
			if (sdCardExist) {
				path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/"
						+ SudaConstants.APP_DIR;// 获取跟目录
			}else {
				path =SudaApplication.getInstance().getFilesDir().getAbsolutePath() + "/" + SudaConstants.APP_DIR;
			}
			FileUtils.createSDDir(path);
			return path;
		
	}
}