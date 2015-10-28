package kr.ac.snu.bi.sensorcollector.dialog;

import kr.ac.snu.bi.sensorcollector.R;
import kr.ac.snu.bi.sensorcollector.R.id;
import kr.ac.snu.bi.sensorcollector.R.layout;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class CategoryAddDialog extends Dialog{
	
	private OnCategoryAddListener onCategoryAddListener;
	private EditText categoryEditText;
	
	public CategoryAddDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.1f;
        getWindow().setAttributes(lpWindow);
         
        setContentView(R.layout.category_add_dialog);
    }
    
    @Override
    public void onContentChanged() {
    	super.onContentChanged();
    	
    	categoryEditText = (EditText)findViewById(R.id.category_text);
        
        Button createButton = (Button)findViewById(R.id.btn_create);
        createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onCategoryAddListener != null)
		    		onCategoryAddListener.onCreateCategory(categoryEditText.getText().toString());
			}
		});
        
        Button cancelButton = (Button)findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
    }
    
    public void setOnCategoryAddListener(OnCategoryAddListener onCategoryAddListener) {
		this.onCategoryAddListener = onCategoryAddListener;
	}
    
    public static interface OnCategoryAddListener {
    	
    	void onCreateCategory(String category);
    }
}