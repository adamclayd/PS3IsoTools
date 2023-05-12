package com.ps3isotools.ui.objects;

public enum ProgressThing {
	ITEMS("Items"),
	FILES("Files"),
	BYTES("b"),
	KILOBYTES("Kb"),
	MEGABYTES("Mb"),
	GIGABYTES("Gb");
	
	public String label;
	ProgressThing(String string) {
		label = string;
	}
}
