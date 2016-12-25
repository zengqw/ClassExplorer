package com.zqw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class FileUtil {
	
	/**
	 * 复制单个文件
	 * 
	 * @param srcPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param destPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String src, String dest,String file) {
		String srcPath = src+file;
		String destPath = dest+file;
		mkdir(dest,file.split("\\\\"));
		try {
			int byteread = 0;
			File oldfile = new File(srcPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(srcPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(destPath);
				byte[] buffer = new byte[4096];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	private static void mkdir(String path,String[] dirArrs){
		String tempPath="";
		for(int i=0;i<dirArrs.length-1;i++){
			String dir =  dirArrs[i];
			if(dir==null||dir.length()==0){
				continue;
			}
			tempPath+=File.separator+dir;
			mkdir(path+tempPath);
		}
	}
	private static void mkdir(String path){
		File dest  = new File(path);
		if(!dest.exists()){
			dest.mkdirs();
		}
	}
	
	public static void copyFile(List<String> filePaths,String srcPath,String destPath){
		mkdir(destPath);
		for(String file:filePaths){
			copyFile(srcPath, destPath,file);
		}
	}
	public static String handleSpace(String path){
		if(path != null){
			path = path.replaceAll(" ", "%20");
		}
		return path;
	}
	
	public static void clearFiles(String workspaceRootPath) {
		File file = new File(workspaceRootPath);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
		}
	}

	public static void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
		}
		file.delete();
	}
}
