package com.ps3isotools.ui.threads;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExtractPS3ISOThread extends Thread {
	private String inPath;
	private String outPath;
	private String OS;
	private ThreadDoneListener doneListener;
	private String tmpDir;
	
	public ExtractPS3ISOThread(String inPath, String outPath) throws IOException, NoSuchMethodException {
		this.inPath = inPath;
		this.outPath = outPath;
		this.OS = System.getProperty("os.name").toLowerCase();
		doneListener = null;
		
		File tmp = File.createTempFile("tmp_", ".temp");
		String pth = tmp.getAbsolutePath();
		tmpDir = pth.substring(0, pth.lastIndexOf(File.separator));
		tmp.delete();
		
		File dir = new File(tmpDir + File.separator + "ps3isotools");
		
		if(!dir.exists()) {
			dir.mkdir();
		}
		
		if(isLinux()) {
			File file = new File(dir.getAbsoluteFile() + File.separator + "extractps3iso");
		
			if(!file.exists()) {
				InputStream in = new BufferedInputStream(getClass().getResourceAsStream("/resources/binaries/extractps3iso"));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath()));
			
				byte[] buffer = new byte[1024];
				int length;
				while((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
					out.flush();
				}
				
				in.close();
				out.close();
			}
		}
		else if(isWindows()) {
			File file = new File(dir.getAbsoluteFile() + File.separator + "extractps3iso.exe");
			
			if(!file.exists()) {
				InputStream in = new BufferedInputStream(getClass().getResourceAsStream("/resources/binaries/extractps3iso.exe"));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath()));
			
				byte[] buffer = new byte[1024];
				int length;
				while((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
					out.flush();
				}
				
				in.close();
				out.close();
			}
		}
		else {
			throw new NoSuchMethodException("extractps3iso is only implemented for Windows and Linux");
		}
	}
	
	public boolean isWindows() {
		return OS.contains("win");
	}
	
	public boolean isLinux() {
		return OS.contains("nix") || OS.contains("nux") || OS.contains("aix") || OS.contains("linux");
	}
	
	public void run() {
		String path = tmpDir + File.separator + "ps3isotools" + File.separator + "extractps3iso.exe";
		if(isLinux())
			path = tmpDir + File.separator + "ps3isotools" + File.separator + "extractps3iso";
		
		System.out.println("\"" + path + "\" \"" + inPath + "\" \"" + outPath + "\"");
		
		try {
			Process p = Runtime.getRuntime().exec("\"" + path + "\" \"" + inPath + "\" \"" + outPath + "\"");
			
			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		if(doneListener != null)
			doneListener.threadDone(this);
	}
	
	public void setThreadDoneListener(ThreadDoneListener listener) {
		doneListener = listener;
	}
}
