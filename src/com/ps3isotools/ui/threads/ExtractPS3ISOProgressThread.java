package com.ps3isotools.ui.threads;

import com.ps3isotools.ui.ProgressDialog;
import java.io.File;
import com.ps3isotools.ui.objects.ExtractPS3ISOEntry;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

public class ExtractPS3ISOProgressThread extends Thread {
	private ProgressDialog dlg;
	private int[] index;
	private DefaultListModel<ExtractPS3ISOEntry> entries;
	private File outDir;
	
	public ExtractPS3ISOProgressThread(ProgressDialog dlg, DefaultListModel<ExtractPS3ISOEntry> entries, File outDir, int[] index) {
		this.dlg = dlg;
		this.index = index;
		this.entries = entries;
		this.outDir = outDir;
	}
	
	public void run() {
		double total = 0;
		for(int i = 0; i < entries.size(); i++)
			total += entries.get(i).iso.length();
		
		while(index[0] < entries.size()) {
			double processed = 0;
			
			for(int i = 0; i <= index[0]; i++)
				processed += getDirSize(new File(outDir.getAbsolutePath() + File.separator + entries.get(i)));
			
			dlg.setProgress((int)((processed / total) * 100));
			
			try {
				Thread.sleep(500L);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private long getDirSize(File dir) {
		long size = 0;
		
		File[] files = dir.listFiles();
		
		for(int i = 0; files != null && i < files.length; i++) {
			if(files[i].isDirectory())
				size += getDirSize(files[i]);
			else
				size += files[i].length();
		}
		
		return size;
	}
}

