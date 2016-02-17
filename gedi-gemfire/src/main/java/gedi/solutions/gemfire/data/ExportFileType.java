package gedi.solutions.gemfire.data;

public enum ExportFileType {
    gfd("gfd"),
	json("json")
	//,ADP_FORMAT ("adp")
	;
	
	private final String s;
	
	ExportFileType(String s) {
		this.s = s;
	}
	
	public String toString() {
		return this.s;
	}
	

}
