package gedi.solutions.geode.data;

public enum ExportFileType {
    gfd("gfd"),
	json("json")
	;
	
	private final String s;
	
	ExportFileType(String s) {
		this.s = s;
	}
	
	public String toString() {
		return this.s;
	}
	

}
