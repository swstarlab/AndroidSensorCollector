package kr.ac.snu.bi.sensorcollector.data;

public class LabelItem {
	
	private String label;
	private boolean checked;
	private boolean category;

	public LabelItem(String label, boolean checked, boolean category) {
		
		this.label = label;
		this.checked = checked;
		this.category = category;
	}
	
	public LabelItem(LabelItem item) {
		
		this.label = item.label;
		this.checked = item.checked;
		this.category = item.category;
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean isCategory() {
		return category;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setCategory(boolean category) {
		this.category = category;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
