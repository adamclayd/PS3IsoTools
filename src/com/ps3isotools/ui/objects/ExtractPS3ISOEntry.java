package com.ps3isotools.ui.objects;

import java.io.File;

public class ExtractPS3ISOEntry {
	public File iso;
	public String title;
	public String titleId;
	
	@Override
	public String toString() {
		return title + " [" + titleId + "]";
	}
}
