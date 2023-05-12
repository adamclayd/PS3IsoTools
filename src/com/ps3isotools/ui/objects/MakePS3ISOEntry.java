package com.ps3isotools.ui.objects;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;

public class MakePS3ISOEntry {
	public File gameDir;
	public String title;
	public String titleId;
	
	@Override
	public String toString() {
		return ((title != null ? title : "") + " " + (titleId != null ? titleId : "")).trim();
	}
}
