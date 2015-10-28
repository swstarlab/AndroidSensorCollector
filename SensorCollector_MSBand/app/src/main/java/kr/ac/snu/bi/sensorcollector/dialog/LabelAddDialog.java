package kr.ac.snu.bi.sensorcollector.dialog;

import java.util.ArrayList;
import java.util.List;

import kr.ac.snu.bi.sensorcollector.R;
import kr.ac.snu.bi.sensorcollector.data.LabelDataManager;
import kr.ac.snu.bi.sensorcollector.data.LabelItem;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class LabelAddDialog extends Dialog {
	
	private EditText labelEditText;
	private Spinner categorySpinner;
	private CategoryArrayAdapter adapter;
	private OnLabelCreateListener onLabelCreateListener;

	public LabelAddDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.1f;
		getWindow().setAttributes(lpWindow);

		setContentView(R.layout.label_add_dialog);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		
		List<LabelItem> labelItemList = LabelDataManager.getInstance().getLabelItemList();
		adapter = new CategoryArrayAdapter(getContext(), labelItemList);
		
		categorySpinner = (Spinner) findViewById(R.id.category_spinner);
		categorySpinner.setAdapter(adapter);
		
		labelEditText = (EditText) findViewById(R.id.label_edittext);
		
		Button createButton = (Button)findViewById(R.id.create_btn);
        createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String category = categorySpinner.getSelectedItem().toString();
				String label = labelEditText.getText().toString();
				
				if (onLabelCreateListener != null)
					onLabelCreateListener.onLabelCreate(category, label);
			}
		});
        
        Button cancelButton = (Button)findViewById(R.id.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void notifyLabelChange(){
		if (adapter != null)
			adapter.notifyDataSetChanged();
    }
	
	public void setOnLabelCreateListener(OnLabelCreateListener onLabelCreateListener) {
		this.onLabelCreateListener = onLabelCreateListener;
	}
	
	public static interface OnLabelCreateListener {
		void onLabelCreate(String category, String label);
	}
	
	private static class CategoryArrayAdapter extends ArrayAdapter<String> {
		
		private final Context context;
		private List<LabelItem> labelItemList;

		public CategoryArrayAdapter(Context context, List<LabelItem> labelItemList) {
			super(context, android.R.layout.simple_spinner_dropdown_item);
			this.context = context;
			this.labelItemList = labelItemList;
		}
		
		@Override
		public int getCount() {
			int count = 0;
			for (int i = 0; i < labelItemList.size(); i++){
				LabelItem item = labelItemList.get(i);
				if (item.isCategory()){
					count++;
				}
			}
			return count;
		}
		
		@Override
		public String getItem(int position) {
			
			int ind = 0;
			for (int i = 0; i < labelItemList.size(); i++){
				LabelItem item = labelItemList.get(i);
				if (item.isCategory()){
					if (ind == position)
						return item.getLabel();
					ind++;
				}
			}
			
			return "";
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
	}
}
