package kr.ac.snu.bi.sensorcollector.dialog;


import kr.ac.snu.bi.sensorcollector.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class DateTimeDialog extends Dialog {

	private DateTimePicker dateTimePicker;
	private OnModifyDateTimeListener onModifyDateTimeListener;
	
	public DateTimeDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.1f;
		getWindow().setAttributes(lpWindow);

		setContentView(R.layout.date_time_dialog);
	}
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		
		dateTimePicker = (DateTimePicker) findViewById(R.id.date_time_picker);
		
        Button cancelButton = (Button)findViewById(R.id.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
        
        Button resetButton = (Button)findViewById(R.id.reset_date_time_btn);
        resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dateTimePicker.reset();
			}
		});
        
        Button okButton = (Button)findViewById(R.id.set_date_time_btn);
        okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onModifyDateTimeListener != null)
					onModifyDateTimeListener.onModifyDateTime(dateTimePicker.getDateTimeMillis());
			}
		});
        
        dateTimePicker.reset();
	}
	
	public void setOnModifyDateTimeListener(
			OnModifyDateTimeListener onModifyDateTimeListener) {
		this.onModifyDateTimeListener = onModifyDateTimeListener;
	}
	
	public interface OnModifyDateTimeListener {
		void onModifyDateTime(long time);
	}
}
