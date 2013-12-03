package org.shirdrn.document.processor.component.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Context;
import org.shirdrn.document.processor.component.AbstractOutputtingQuantizedData;

public class OutputtingQuantizedTestData extends AbstractOutputtingQuantizedData {

	private static final Log LOG = LogFactory.getLog(OutputtingQuantizedTestData.class);
	
	public OutputtingQuantizedTestData(Context context) {
		super(context);
	}

	@Override
	public void fire() {
		super.fire();
	}

	@Override
	protected void quantizeTermVectors() {
		// load label vector
		loadLabelVector();
		// load term vectors
		loadTermVectors();
	}

	private void loadLabelVector() {
		// <label, labelId> pairs
		Map<String, Integer> globalLabelToIdMap = new HashMap<String, Integer>(0);
		// <labelId, label> pairs
		Map<Integer, String> globalIdToLabelMap = new HashMap<Integer, String>(0);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(labelVectorFile), charSet));
			String line = null;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					String[] aLabel = line.split("\\s+");
					if(aLabel.length == 2) {
						int labelId = Integer.parseInt(aLabel[0]);
						String label = aLabel[1];
						globalIdToLabelMap.put(labelId, label);
						globalLabelToIdMap.put(label, labelId);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		context.getVectorMetadata().putIdToLabelPairs(globalIdToLabelMap);
		context.getVectorMetadata().putLabelToIdPairs(globalLabelToIdMap);
	}
	
	private void loadTermVectors() {
		File[] vectorFiles = outputDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().endsWith("-terms.txt");
			}
		});	
		for(File file : vectorFiles) {
			loadTermVectorData(file);
		}
	}

	private void loadTermVectorData(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
			String line = null;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					String[] aLabel = line.split("\\s+");
					if(aLabel.length == 3) {
						int labelId = Integer.parseInt(aLabel[0]);
						String word = aLabel[1];
						int wordId = Integer.parseInt(aLabel[2]);
						context.getVectorMetadata().putWordWithId(labelId, word, wordId);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
}
