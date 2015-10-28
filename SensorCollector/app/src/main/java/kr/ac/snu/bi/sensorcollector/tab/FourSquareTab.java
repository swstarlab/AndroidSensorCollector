package kr.ac.snu.bi.sensorcollector.tab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kr.ac.snu.bi.sensorcollector.R;
import kr.ac.snu.bi.sensorcollector.collector.CollectorManager;
import kr.ac.snu.bi.sensorcollector.collector.LocationMonitor;
import kr.ac.snu.bi.sensorcollector.data.LocationData;
import kr.ac.snu.bi.sensorcollector.data.Venue;
import kr.ac.snu.bi.sensorcollector.data.VenueDataManager;
import kr.ac.snu.bi.sensorcollector.dialog.CustomVenueDialog;
import kr.ac.snu.bi.sensorcollector.network.FourSquareConn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class FourSquareTab extends Fragment implements CustomVenueDialog.OnCustomVenueListener{
	
	private Context context;
	private LocationMonitor locationMonitor;
	private VenueDataManager venueDataManager;
	
	private EditText queryEditText;
	private TextView venueTextView;
    private CustomVenueDialog customVenueDialog;
	private VenueArrayAdapter adapter; 
	
	private Venue selectedVenue;
	
	
	public FourSquareTab(Context context) {
		this.context = context;
		this.locationMonitor = CollectorManager.getInstance().getLocationMonitor();
		this.venueDataManager = VenueDataManager.getInstance();

        customVenueDialog = new CustomVenueDialog(context);
        customVenueDialog.setOnCustomVenueListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_foursquare, null);
		
		queryEditText = (EditText) view.findViewById(R.id.query_edittext);
		venueTextView = (TextView) view.findViewById(R.id.venue_text);
		
		Button queryBtn = (Button) view.findViewById(R.id.query_btn);
		queryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				venueSearch();
			}
		});
		
		ImageButton favoriteBtn = (ImageButton) view.findViewById(R.id.favorite_btn);
		favoriteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listFavorites();
			}
		});
		
		Button addToFavoriteBtn = (Button) view.findViewById(R.id.add_to_favorite_btn);
		addToFavoriteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addToFavorite(selectedVenue);
			}
		});

        Button emptyBtn = (Button) view.findViewById(R.id.empty_btn);
        emptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                venueTextView.setText("");
                selectedVenue = null;
            }
        });

        Button customVenueBtn = (Button) view.findViewById(R.id.custom_venue_btn);
        customVenueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                customVenueDialog.show();
            }
        });
		
		adapter = new VenueArrayAdapter(context, new ArrayList<Venue>());
		
		ListView venueListView = (ListView) view.findViewById(R.id.venue_listview);
		venueListView.setAdapter(adapter);
		venueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Venue venue = adapter.getItem(position);
				venueTextView.setText(venue.name + " - " + venue.category + "\n" + venue.latitude + ", " + venue.longitude);
			
				selectedVenue = venue;
			}
		});

    	return view;
	}
	
	private void venueSearch(){
		final String query = queryEditText.getText().toString();
		if (query == null || query.length() <= 0)
			return;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				LocationData locationData = locationMonitor.createLocationData();
				double lat = 0;
				double lng = 0;
				
				if (locationData.gpsEnabled){
					lat = locationData.gpsLatitude;
					lng = locationData.gpsLongitude;
				} else {
					lat = locationData.netLatitude;
					lng = locationData.netLongitude;
				}
				
				List<Venue> venueList = null;
				try {
					venueList = FourSquareConn.getInstance().venuesSearch(lat, lng, query);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (venueList != null){
					Message msg = new Message();
					msg.obj = venueList;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	private void listFavorites(){
		render(venueDataManager.getVenueList());
	}
	
	private void addToFavorite(Venue venue){
		if (venue == null)
			return;
		
		venueDataManager.addNewVenue(venue);
        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
	}
	
	public void render(List<Venue> venueList){
		if (venueTextView == null)
			return;
		
		adapter.clear();
		adapter.addAll(venueList);
		adapter.notifyDataSetChanged();
	}

    @Override
    public void onCreateVenue(String category, String name) {
        Venue venue = new Venue();
        venue.category = category;
        venue.name = name;

        venueTextView.setText(venue.name + " - " + venue.category);
        selectedVenue = venue;

        customVenueDialog.dismiss();
    }

    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			@SuppressWarnings("unchecked")
			List<Venue> venueList = (List<Venue>)msg.obj;
			render(venueList);
		};
	};
	
	public Venue getSelectedVenue() {
		return selectedVenue;
	}
	
	
	// ---------------------------------------------------------------------------------------------------------
	
	private static class VenueArrayAdapter extends ArrayAdapter<Venue> {

		private final Context context;
		private List<Venue> venueItemList;

		public VenueArrayAdapter(Context context, List<Venue> venueItemList) {
			super(context, R.layout.fs_list_item);
			this.context = context;
			this.venueItemList = venueItemList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Venue item = getItem(position);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if (convertView == null){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.fs_list_item, parent, false);
				holder.textView = (TextView)convertView.findViewById(R.id.label_text);
				convertView.setTag(holder);
			} else {
                holder = (ViewHolder)convertView.getTag();
            }
			
			holder.textView.setText(item.name + " - " + item.category);

			return convertView;
		}
		
		@Override
		public int getCount() {
			return venueItemList.size();
		}
		
		@Override
		public Venue getItem(int position) {
			return venueItemList.get(position);
		}
		
		@Override
		public void clear() {
			venueItemList.clear();
		}
		
		@Override
		public void add(Venue venue) {
			venueItemList.add(venue);
		}
		
		@Override
		public void addAll(Collection<? extends Venue> collection) {
			venueItemList.addAll(collection);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		private static class ViewHolder {
			public TextView textView;
		}
	}
}



