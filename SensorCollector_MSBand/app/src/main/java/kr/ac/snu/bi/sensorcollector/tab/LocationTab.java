package kr.ac.snu.bi.sensorcollector.tab;


import kr.ac.snu.bi.sensorcollector.R;
import kr.ac.snu.bi.sensorcollector.TestService;
import kr.ac.snu.bi.sensorcollector.collector.CollectorManager;
import kr.ac.snu.bi.sensorcollector.collector.LocationCollector;
import kr.ac.snu.bi.sensorcollector.data.LocationData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class LocationTab extends Fragment implements OnCheckedChangeListener{
	
	private Context context;
	
	private TextView gpsLatView;
	private TextView gpsLongView;
	private TextView netLatView;
	private TextView netLongView;
	private TextView cellIdView;
	private Switch collectSwitch;
	
	
	
	public LocationTab(Context context) {
		this.context = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		
		LocationCollector locationCollector = CollectorManager.getInstance().getLocationCollector();
		
		View view = inflater.inflate(R.layout.activity_location, null);
		
		gpsLatView = (TextView) view.findViewById(R.id.gps_lat);
		gpsLongView = (TextView) view.findViewById(R.id.gps_long);
		netLatView = (TextView) view.findViewById(R.id.net_lat);
		netLongView = (TextView) view.findViewById(R.id.net_long);
		cellIdView = (TextView) view.findViewById(R.id.cell_id);
		
		collectSwitch = (Switch) view.findViewById(R.id.collect_switch);
		collectSwitch.setChecked(locationCollector.isRunning());
		collectSwitch.setOnCheckedChangeListener(this);
		
    	return view;
	}
	
	public void render(LocationData data){
		gpsLatView.setText(String.format("%f", data.gpsLatitude));
		gpsLongView.setText(String.format("%f", data.gpsLongitude));
		netLatView.setText(String.format("%f", data.netLatitude));
		netLongView.setText(String.format("%f", data.netLongitude));
		cellIdView.setText("" + data.cellId);
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Intent service = new Intent(TestService.SERVICE_NAME);
		
		if (isChecked){
			context.startService(service);
		} else {
			context.stopService(service);
		}
	}
}

