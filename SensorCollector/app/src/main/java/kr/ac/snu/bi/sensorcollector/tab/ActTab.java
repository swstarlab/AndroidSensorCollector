package kr.ac.snu.bi.sensorcollector.tab;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import kr.ac.snu.bi.sensorcollector.R;
import kr.ac.snu.bi.sensorcollector.data.Act;
import kr.ac.snu.bi.sensorcollector.data.LabelDataManager;
import kr.ac.snu.bi.sensorcollector.data.LabelItem;
import kr.ac.snu.bi.sensorcollector.dialog.CategoryAddDialog;
import kr.ac.snu.bi.sensorcollector.dialog.DateTimeDialog;
import kr.ac.snu.bi.sensorcollector.dialog.LabelAddDialog;
import kr.ac.snu.bi.sensorcollector.dialog.CategoryAddDialog.OnCategoryAddListener;
import kr.ac.snu.bi.sensorcollector.dialog.DateTimeDialog.OnModifyDateTimeListener;
import kr.ac.snu.bi.sensorcollector.dialog.LabelAddDialog.OnLabelCreateListener;
import kr.ac.snu.bi.sensorcollector.data.Act;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class ActTab extends Fragment implements OnCategoryAddListener, OnLabelCreateListener, OnModifyDateTimeListener{
	
	private Context context;
	private LabelDataManager dataManager;
	private CategoryAddDialog categoryAddDialog;
	private LabelAddDialog labelAddDialog;
	private DateTimeDialog dateTimeDialog;
	private LabelArrayAdapter adapter;
	private TextView timeTextView;
	
	private boolean syncTime;
	private long manualTime;


	public ActTab(Context context) {
		this.context = context;
		dataManager = LabelDataManager.getInstance();
		
		categoryAddDialog = new CategoryAddDialog(context);
		categoryAddDialog.setOnCategoryAddListener(this);
		
		labelAddDialog = new LabelAddDialog(context);
		labelAddDialog.setOnLabelCreateListener(this);
		
		dateTimeDialog = new DateTimeDialog(context);
		dateTimeDialog.setOnModifyDateTimeListener(this);
		
		syncTime = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_act, null);
		
		adapter = new LabelArrayAdapter(context, loadLabelItemList());

		ListView listView = (ListView) view.findViewById(R.id.label_listview);
		listView.setAdapter(adapter);
		
		timeTextView = (TextView) view.findViewById(R.id.time_text);
		
		Button categoryBtn = (Button) view.findViewById(R.id.add_category_btn);
		categoryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				categoryAddDialog.show();
			}
		});
		
		Button labelBtn = (Button) view.findViewById(R.id.add_label_btn);
		labelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				labelAddDialog.show();
			}
		});
		
		Button timeBtn = (Button) view.findViewById(R.id.time_btn);
		timeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dateTimeDialog.show();
			}
		});
		
		Button syncBtn = (Button) view.findViewById(R.id.sync_btn);
		syncBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				syncTime = true;
			}
		});
		
		return view;
	}
	
	private List<LabelItem> loadLabelItemList(){
		return dataManager.getLabelItemList();
	}
	
	public void renderTime(){
		if (!syncTime)
			return;
		
		renderTime(System.currentTimeMillis());
	}
	
	private void renderTime(long time){
		if (timeTextView == null)
			return;
		
		Date date = new Date(time);
		
		DateFormat format = DateFormat.getDateTimeInstance();
		String dateStr = format.format(date);

		timeTextView.setText(dateStr);
	}

	public List<Act> getCheckedActList(){
		List<Act> actList = new ArrayList<Act>();
		
		if (adapter == null)
			return actList;
		
		List<LabelItem> allItemList = adapter.getLabelItemList();
		String curCategory = null;
		for (int i = 0; i < allItemList.size(); i++){
			LabelItem item = allItemList.get(i);
			if (item.isCategory()){
				curCategory = item.getLabel();
			} else if (item.isChecked()){
				if (curCategory == null){
					throw new NullPointerException();
				}
				Act act = new Act();
				act.category = curCategory;
				act.activity = item.getLabel();
				actList.add(act);
			}
		}
		
		return actList;
	}
	
	public long getSelectedTime(){
		
		if (syncTime){
			return System.currentTimeMillis();
		} else {
			return manualTime;
		}
	}
	
	@Override
	public void onCreateCategory(String category) {
		
		if (category == null || category.length() == 0)
			return;
		
		List<LabelItem> labelItemList = dataManager.getLabelItemList();
		labelItemList.add(new LabelItem(category, false, true));
		dataManager.save();
		
		categoryAddDialog.dismiss();
		
		labelAddDialog.notifyLabelChange();
		
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onLabelCreate(String category, String label) {
		if (category == null || category.length() == 0)
			return;
		
		if (label == null || label.length() == 0)
			return;
		
		List<LabelItem> labelItemList = dataManager.getLabelItemList();
		int idx = -1;
		for (int i = 0; i < labelItemList.size(); i++){
			LabelItem item = labelItemList.get(i);
			if (item.isCategory() && item.getLabel().compareTo(category) == 0){
				idx = i;
				break;
			}
		}
		
		if (idx == -1){
			Toast.makeText(context, R.string.no_category, Toast.LENGTH_LONG).show();
			return;
		}
		
		int insertIdx = idx + 1;
		for (int i = idx + 1; i < labelItemList.size(); i++){
			LabelItem item = labelItemList.get(i);
			if (item.isCategory()){
				insertIdx = i;
				break;
			}
		}
		
		labelItemList.add(insertIdx, new LabelItem(label, false, false));
		dataManager.save();
		
		labelAddDialog.dismiss();
		
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onModifyDateTime(long time) {
		dateTimeDialog.dismiss();
		
		syncTime = false;
		manualTime = time;
		renderTime(time);
	}
	
	private static class LabelArrayAdapter extends ArrayAdapter<LabelItem> {
		
		private static final int VIEW_TYPE_COUNT = 2;
		
		private static final int VIEW_TYPE_ITEM = 0;
		private static final int VIEW_TYPE_CATEGORY = 1;

		private final Context context;
		private List<LabelItem> labelItemList;

		public LabelArrayAdapter(Context context, List<LabelItem> labelItemList) {
			super(context, R.layout.label_list_item);
			this.context = context;
			this.labelItemList = labelItemList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			LabelItem item = getItem(position);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int type = getItemViewType(position);
			
			if (convertView == null){
				holder = new ViewHolder();
				switch (type){
				case VIEW_TYPE_ITEM:
					convertView = inflater.inflate(R.layout.label_list_item, parent, false);
					holder.textView = (TextView)convertView.findViewById(R.id.label_text);
					holder.checkBox = (CheckBox)convertView.findViewById(R.id.label_checkbox);
					break;
				case VIEW_TYPE_CATEGORY:
					convertView = inflater.inflate(R.layout.label_list_item_category, parent, false);
					holder.textView = (TextView)convertView.findViewById(R.id.label_text);
					break;
				}
				convertView.setTag(holder);
			} else {
                holder = (ViewHolder)convertView.getTag();
            }
			
			holder.textView.setText(item.getLabel());
			if (holder.checkBox != null){
				holder.checkBox.setTag(item);
				holder.checkBox.setChecked(item.isChecked());
				holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						LabelItem item = (LabelItem)buttonView.getTag();
						item.setChecked(isChecked);
					}
				});
			}

			return convertView;
		}
		
		@Override
		public int getCount() {
			return labelItemList.size();
		}
		
		@Override
		public LabelItem getItem(int position) {
			return labelItemList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public int getViewTypeCount() {
			return VIEW_TYPE_COUNT;
		}
		
		@Override
		public int getItemViewType(int position) {
			if (getItem(position).isCategory())
				return VIEW_TYPE_CATEGORY;
			else
				return VIEW_TYPE_ITEM;
		}
		
		public List<LabelItem> getLabelItemList() {
			return labelItemList;
		}
		
		private static class ViewHolder {
			public TextView textView;
			public CheckBox checkBox;
		}
	}
}

