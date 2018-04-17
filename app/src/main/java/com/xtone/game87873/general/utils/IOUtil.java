package com.xtone.game87873.general.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *关闭输入输出流工具类
 * @author: chenyh
 * @Date: 2014-8-11下午6:01:26
 */
public class IOUtil {

	public static void closeStream(InputStream is){
		if(is!=null){
			try {
				is.close();
			} catch (IOException e) {
				is = null;
			}finally{
				is = null;
			}
		}
	}
	public static void closeStream(OutputStream os){
		if(os!=null){
			try {
				os.flush();
				os.close();
			} catch (IOException e) {
				os = null;
			}finally{
				os = null;
			}
		}
	}
	public static void close(Closeable close){
		if(close!=null){
			try {
				close.close();
			} catch (IOException e) {
			}finally{
				close = null;
			}
		}
	}
	
}
