package org.shirdrn.document.processor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class UTF8Encoding {

	public static void main(String[] args) {
		String src = "F:\\数据挖掘\\SogouC\\SogouC\\ClassFile";
		String dst = "F:\\数据挖掘\\SogouC\\SogouC\\UTF8\\ClassFile";
		File dstRoot = new File(dst);
		if(!dstRoot.exists()) {
			dstRoot.mkdirs();
		}
		File srcRoot = new File(src);
		File[] dirs = srcRoot.listFiles();
		for(File srcDir : dirs) {
			File[] files = srcDir.listFiles();
			for(File file : files) {
				readAndWrite(srcRoot, srcDir, dstRoot, file);
			}
		}
	}

	private static void readAndWrite(File srcRoot, File srcDir, File dstRoot, File file) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			File srcFile = new File(new File(srcRoot, srcDir.getName()), file.getName());
			File dstDir = new File(dstRoot, srcDir.getName());
			if(!dstDir.exists()) {
				dstDir.mkdirs();
			}
			File dstFile = new File(dstDir, file.getName());
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), "GBK"));
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstFile), "UTF-8"));
			String line = null;
			while((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
