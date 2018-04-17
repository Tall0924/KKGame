package com.xtone.game87873.general.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @Description:文件操作类
 * @author: chenyh  
 * @date:   2014-7-23 下午2:40:18   
 *
 */
public class FileUtil {
	public static final String ROOT_DIR_NAME = "/mEduPlatform";
	public static final String ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ File.separator + ROOT_DIR_NAME;
	public static final long EXPIRE = 648000000;

	/**
	 * 删除一个文件，或者一个目录下的所有文件
	 * 
	 * @param file
	 *            文件或者目录
	 */
	public static boolean deleteDir(File file) {
		try {
			if (file == null||!file.exists()) {
				return true;
			}
			if (file.exists() && file.isDirectory()) {
				File[] files = file.listFiles();
				if (files.length > 0) {
					for (File child : files) {
						deleteDir(child);
					}
				}
				if (file.listFiles().length == 0) {
					file.delete();
				}
			} else if (file.isFile()) {
				return file.delete();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 通过一个uri,和要保存的文件夹名字，获得文件的名字
	 * 
	 * @param uri
	 *            图片的uri 
	 * @param temp
	 *            是否是获取下载图片缓存文件的File
	 * @return 返回对应uri的文件名
	 */
	public static File getCacheFileFromUri(String uri,boolean temp) {
		String fileName = getLongFileNameFromUri(uri);
		File dirFile = getDir(null);
		if (dirFile == null) {
			return null;
		}
		if(temp){
			fileName = fileName+".temp";
		}
		File file = new File(dirFile, "." + fileName);
		file.setLastModified(System.currentTimeMillis());
		return file;
	}
	public static File getCacheFileFromUri(String uri) {
		return getCacheFileFromUri(uri, false);
	}
	
	/**
	 * 清除过期的图片。其中时间间隔为7天
	 */
	public static void clearExpiredImage() {
		try {
			File dir = getDir(null);
			if (dir != null && dir.exists() && dir.isDirectory()) {
				String[] files = dir.list();
				for (String fileName : files) {
					File file = new File(dir, fileName);
					if (file != null && file.exists() && file.isFile()) {
						long modified = file.lastModified();
						long distance = System.currentTimeMillis() - modified;
						if (distance > EXPIRE) {
							Log.e("clearTempImage",
									("图片未使用时间：" + distance / 1000 / 60 / 60
											/ 24)
											+ "天" + "=======delete");
							file.delete();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * 获取一个在.mCommondLibrary文件夹下的文件对象
	 * @param fileName 文件路径以及名字  可以携带目录，如"filedir1/filedir2/a.apk" 但是，注意，字符串中的"/"仅代表 {@link File#separator}
	 *        <br/>如果没有带目录，默认为缓存图片目录下
	 * @return 
	 */
	public static File getFile(String fileName) {
		String dir = null;
		int index = fileName.lastIndexOf(File.separator);
		if(index!=-1){
			dir = fileName.substring(0, index);
			fileName = fileName.substring(index+1, fileName.length());
		}
		if(dir!=null&&TextUtils.isEmpty(dir.trim())){
			dir = null;
		}
		File dirFile = getDir(dir);
		File file = new File(dirFile, "." + fileName);
		return file;
	}
	/**
	 * 获取一个在.m4399文件夹下的文件对象
	 * @param dir 文件夹路径，可以为多级 如:"filedir1/filedir2".如果为null，默认为缓存图片目录下
	 * @param fileName 文件名字
	 * @return
	 */
	public static File getFile(String dir,String fileName){
		File dirFile = getDir(dir);
		File file = new File(dirFile, "." + fileName);
		return file;
	}
	
	
	/**
	 * 
	 * @param dir
	 *            目录的名称 如果dir为空，则返回 缓存文件路径下的图片文件路径
	 * @return sdcard/mCommondLibrary/ 目录
	 */
	public static File getDir(String dir) {
		StringBuffer path = null;
		File file = null;
		if (SDCardUtil.checkSDcard()) {
			path = new StringBuffer(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			path.append(ROOT_DIR_NAME);
			if (dir == null) {
				dir = "images";
			}
			path.append(File.separatorChar);
			path.append(".").append(dir);
			file = new File(path.toString());
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return file;
	}
	
	
	/**
	 * 检查一个目录下有没有包含这个key的文件
	 * @param file
	 * @param key
	 * @param ignoreCase
	 * @return
	 */
	public static boolean dirContainsFileNameKey(File file,String key,boolean ignoreCase){
		if(ignoreCase){
			key = key.toLowerCase(Locale.getDefault());
		}
		if(file!=null&&file.exists()&&file.isDirectory()){
			File[] files = file.listFiles();
			int i=0;
			while(files!=null&&files.length>i){
				File temp = files[i];
				if(temp==null||!temp.exists()){
					continue;
				}
				String tempName = null;
				tempName = temp.getName();
				if(ignoreCase){
					tempName = tempName.toLowerCase(Locale.getDefault());
				}
				if(tempName.contains(key)){
					return true;
				}
				if(temp.isDirectory()){
					if(dirContainsFileNameKey(temp, key, ignoreCase)){
						return true;
					}
				}
				i++;
			}
		}
		return false;
	}
	/**
	 * 检查一个目录下
	 * @param file
	 * @param key
	 * @param ignoreCase
	 * @return
	 */
	public static List<File> dirContainsList(File file,String key,boolean ignoreCase){
		List<File> result = new ArrayList<File>();
		if(ignoreCase){
			key = key.toLowerCase(Locale.getDefault());
		}
		if(file!=null&&file.exists()&&file.isDirectory()){
			File[] files = file.listFiles();
			int i=0;
			while(files!=null&&files.length>i){
				File temp = files[i];
				if(temp==null||!temp.exists()){
					continue;
				}
				String tempName = null;
				tempName = temp.getName();
				if(ignoreCase){
					tempName = tempName.toLowerCase(Locale.getDefault());
				}
				if(tempName.contains(key)){
					result.add(temp);
				}
				if(temp.isDirectory()){
					result.addAll(dirContainsList(temp, key, ignoreCase));
				}
				i++;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @Title: clear   
	 * @Description: 清除所有缓存文件  
	 * @param:       
	 * @return: void      
	 * @throws
	 */
	public static void clear() {
		File dir = getDir(null);
		deleteDir(dir);
	}
	
	
	/**
	 * 将一个文件复制到另外一个文件中
	 * @param src 源文件 
	 * @param tar 目标文件，即最开始的空文件
	 * @return
	 */
	public static boolean copyFile(File src,File tar){
		boolean result = false;
		if(src.exists()&&src.isFile()&&tar!=null){
			FileInputStream read = null;
			FileOutputStream out = null;
			if(tar.exists()){
				//如果存在则删除
				FileUtil.deleteDir(tar);
			}
			try {
				read = new FileInputStream(src);
				out = new FileOutputStream(tar);
				byte [] bts = new byte[1024];
				int lenth = 0;
				while((lenth=read.read(bts))!=-1){
					out.write(bts, 0, lenth);
				}
				result = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				IOUtil.closeStream(read);
				IOUtil.closeStream(out);
			}
		}
		return result;
	}
	
	// 取得文件大小
	public static long getFileSizes(File f){
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(f);
				s = fis.available();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("getFileSizes","文件不存在");
		}
		return s;
	}
	
	public static boolean isExistSDCard() {  
	  if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {  
		  return true;  
	  } else  
	   return false;  
	 }  
	
	/**
	 * 获取一个文件，或者一个目录下的所有文件
	 * 
	 * @param file
	 *            文件或者目录
	 */
	public static String getFileLength(){
		String formetFileSize = "0KB";
		if(isExistSDCard()){
			File file = getDir(null);
			long fileSize = 0;
			if(file.exists()){
				fileSize = getFileSize(file);
				formetFileSize = FormetFileSize(fileSize);
			}
			if(formetFileSize.equals("0.00B")){
				formetFileSize = "0KB";
			}
			return formetFileSize;
		}else{
			return formetFileSize;
		}
	}

	// 递归  取得文件夹大小
	public static long getFileSize(File f)
	{
		if(!isExistSDCard()){
			return 0;
		}
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	// 转换文件大小
	public static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#0.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	// 递归求取目录文件个数
	public static long getlist(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;
	}
	
	
	/**
	 * 把一个uri地址中的.和：切换成_生成对应的文件名字
	 * 
	 * @param uri
	 * @return 返回/后的名字，如果异常，则返回Null
	 */
	public static String getLongFileNameFromUri(String uri) {
		if (uri == null || "".equals(uri)) {
			return null;
		}

		String fileName = uri.replaceAll("/", "_");
		fileName = fileName.replaceAll(" +", "");
		fileName = fileName.replaceAll(":", "_");
		fileName = fileName.replaceAll("\\.", "_");
		fileName = fileName.replaceAll("\\?", "_");
		fileName = fileName.replaceAll("_+", "_");
		return fileName;
	}
	
	
	public static boolean isDirExist(String dirPath) {
		File dir = new File(dirPath);
		return dir.exists() && dir.isDirectory();
	}

	/**
	 * 判断SD卡上的文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	public static void checkRoot() {
		if (!isDirExist(ROOTPATH)) {
			createDir(ROOTPATH);
		}
	}

	public static void createDir(String... dirPath) {
		File dir = null;
		for (int i = 0; i < dirPath.length; i++) {
			dir = new File(dirPath[i]);
			if (!dir.exists() && !dir.isDirectory()) {
				dir.mkdirs();
			}
		}
	}

	public static void initFolders() {
		createDir(ROOTPATH);
	}

	public static String getBaseFilePath() {
		return ROOTPATH;
	}

	public static String getImageFolder() {
		createDir(ROOTPATH + File.separator + "image");
		return ROOTPATH + File.separator + "image";
	}
	public static String getPhotoFolder() {
		createDir(ROOTPATH + File.separator + "photo");
		return ROOTPATH + File.separator + "photo";
	}

	public static String getTmpFolder() {
		createDir(ROOTPATH + File.separator + "tmp");
		return ROOTPATH + File.separator + "tmp";
	}

	public static String createTmpFile(String name) {
		return getTmpFolder() + File.separator + name;
	}

	public static String getFavImageFolder() {
		createDir(ROOTPATH + File.separator + "fav");
		return ROOTPATH + File.separator + "fav";
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File createSDFile(String path) throws IOException {
		File file = new File(path);
		if (!file.exists())
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
		File file = new File(dirName);
		if (!file.exists())
			file.mkdir();
		return file;
	}

	public static void createFileDir(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			parentFile = null;
		}
		file = null;
	}

	public static boolean deleteFile(String fileName) {
		try {
			if (fileName == null) {
				return false;
			}
			File f = new File(fileName);

			if (f == null || !f.exists()) {
				return false;
			}

			if (f.isDirectory()) {
				return false;
			}
			return f.delete();
		} catch (Exception e) {
			// Log.d(FILE_TAG, e.getMessage());
			return false;
		}
	}

	public static boolean deleteFileOfDir(String dirName, boolean isRecurse) {
		boolean blret = false;
		try {
			File f = new File(dirName);
			if (f == null || !f.exists()) {
				// Log.d(FILE_TAG, "file" + dirName + "not isExist");
				return false;
			}

			if (f.isFile()) {
				blret = f.delete();
				return blret;
			} else {
				File[] flst = f.listFiles();
				if (flst == null || flst.length <= 0) {
					return true;
				}

				int filenumber = flst.length;
				File[] fchilda = f.listFiles();
				for (int i = 0; i < filenumber; i++) {
					File fchild = fchilda[i];
					if (fchild.isFile()) {
						blret = fchild.delete();
						if (!blret) {
							break;
						}
					} else if (isRecurse) {
						blret = deleteFileDir(fchild.getAbsolutePath(), true);
					}
				}
			}
		} catch (Exception e) {
			blret = false;
		}

		return blret;
	}

	public static boolean deleteFileDir(String dirName, boolean isRecurse) {
		boolean blret = false;
		try {
			File f = new File(dirName);
			if (f == null || !f.exists()) {
				// Log.d(FILE_TAG, "file" + dirName + "not isExist");
				return false;
			}
			if (f.isFile()) {
				blret = f.delete();
				return blret;
			} else {
				File[] flst = f.listFiles();
				if (flst == null || flst.length <= 0) {
					f.delete();
					return true;
				}
				int filenumber = flst.length;
				File[] fchilda = f.listFiles();
				for (int i = 0; i < filenumber; i++) {
					File fchild = fchilda[i];
					if (fchild.isFile()) {
						blret = fchild.delete();
						if (!blret) {
							break;
						}
					} else if (isRecurse) {
						blret = deleteFileDir(fchild.getAbsolutePath(), true);
					}
				}

				// 删除当前文件夹
				blret = new File(dirName).delete();
			}
		} catch (Exception e) {
			// Log.d(FILE_TAG, e.getMessage());
			blret = false;
		}

		return blret;
	}

	/**
	 * 移动文件
	 * 
	 * @param filePath
	 */
	public static void removeToDir(String filePath, String toFilePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}
		file.renameTo(new File(toFilePath));
	}
}
