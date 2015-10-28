package kr.ac.snu.bi.sensorcollector.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import kr.ac.snu.bi.sensorcollector.R.id;
import kr.ac.snu.bi.sensorcollector.R.layout;

public class CustomVenueDialog extends Dialog{

	private OnCustomVenueListener onCustomVenueListener;
    private EditText venueCategoryEditText;
	private EditText venueNameEditText;

	public CustomVenueDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.1f;
        getWindow().setAttributes(lpWindow);

        setContentView(layout.custom_venue_dialog);
    }

    @Override
    public void onContentChanged() {
    	super.onContentChanged();

        venueCategoryEditText = (EditText)findViewById(id.venue_category);
    	venueNameEditText = (EditText)findViewById(id.venue_name);

        Button createButton = (Button)findViewById(id.create_btn);
        createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onCustomVenueListener != null){
                    String name = venueNameEditText.getText().toString();
                    String category = venueCategoryEditText.getText().toString();
                    onCustomVenueListener.onCreateVenue(category, name);
                }
			}
		});

        Button cancelButton = (Button)findViewById(id.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
    }
    
    public void setOnCustomVenueListener(OnCustomVenueListener onCustomVenueListener) {
		this.onCustomVenueListener = onCustomVenueListener;
	}
    
    public static interface OnCustomVenueListener {
    	
    	void onCreateVenue(String category, String name);
    }
}