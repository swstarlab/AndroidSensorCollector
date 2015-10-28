package kr.ac.snu.bi.sensorcollector.tab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandContactEvent;
import com.microsoft.band.sensors.BandContactEventListener;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.BandUVEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.SampleRate;
import com.microsoft.band.sensors.UVIndexLevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import kr.ac.snu.bi.sensorcollector.FileTransferActivity;
import kr.ac.snu.bi.sensorcollector.MainActivity;
import kr.ac.snu.bi.sensorcollector.R;


@SuppressLint("ValidFragment")
public class BandTab extends Fragment {
    private Context context;

    private BandClient client = null;
    private Button btnStart;
    private Button btnRead;
    private Button btnDrink;
    private Button btnDrunk;
    private Button btnStagger;
    private Button btnStartExperiment;
    private Button btnStopDrink;
    private Button btnFNDrunk;
    private Button btnEnd;

    private Button btnTransfer;


    private TextView txtStatus;
    private TextView HRtxtStatus;
    private TextView GyrotxtStatus;
    private TextView DistancetxtStatus;
    private TextView PedometertxtStatus;
    private TextView TemperaturetxtStatus;
    private TextView UVtxtStatus;
    private TextView ContacttxtStatus;
    private TextView HRstatustxtStatus;

    //private TextView CaloriestxtStatus;
    private TextView SpeedtxtStatus;
    private TextView MotiontxtStatus;

    //Graphs
    private GraphView HeartRateGraph;
    private int HRString;

    private GraphView TemperatureGraph;
    private float TemperatureString;


    private GraphView AccXGraph;
    private GraphView AccYGraph;
    private GraphView AccZGraph;
    private float AccXString;
    private float AccYString;
    private float AccZString;

    private GraphView GyroXGraph;
    private GraphView GyroYGraph;
    private GraphView GyroZGraph;
    private float GyroXString;
    private float GyroYString;
    private float GyroZString;

    private TextView tv;

    //Logging
    private static final String TAG = BandTab.class.getName();
    private static final String FILENAME = "BandSensorFile.txt";
    private static final String HRFILENAME = "HeartRateFile.txt";
    private static final String HRSTATUSFILENAME = "HeartRateStatusFile.txt";
    private static final String GYROFILENAME = "GyroFile.txt";
    private static final String MODEFILENAME = "ModeFile.txt";
    private static final String SPEEDFILENAME = "SpeedFile.txt";
    private static final String DISTANCEFILENAME = "DistanceFile.txt";
    private static final String CONTACTFILENAME = "ContactStatusFile.txt";
    private static final String STEPSFILENAME = "StepsFile.txt";
    private static final String TEMPFILENAME = "TemperatureFile.txt";
    private static final String UVFILENAME = "UVFile.txt";
    private static final String ACCFILENAME = "AccelerometerFile.txt";


    public BandTab(Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.activity_band, null);
        txtStatus = (TextView) view.findViewById(R.id.AccelerometerText);
        HRtxtStatus= (TextView) view.findViewById(R.id.HeartRateText);
        HRstatustxtStatus= (TextView) view.findViewById(R.id.HeartRateStatusText);

        GyrotxtStatus= (TextView) view.findViewById(R.id.GyroText);
        DistancetxtStatus= (TextView) view.findViewById(R.id.DistanceText);
        PedometertxtStatus = (TextView) view.findViewById(R.id.PedometerText);
        TemperaturetxtStatus = (TextView) view.findViewById(R.id.TemperatureText);
        UVtxtStatus = (TextView) view.findViewById(R.id.UVText);
        ContacttxtStatus = (TextView) view.findViewById(R.id.ContactText);
        //CaloriestxtStatus = (TextView) view.findViewById(R.id.CaloriesText);
        SpeedtxtStatus = (TextView) view.findViewById(R.id.SpeedText);
        MotiontxtStatus= (TextView) view.findViewById(R.id.MotionText);
        tv = (TextView)view.findViewById(R.id.printText);


        btnStart = (Button) view.findViewById(R.id.InfoButton);
        btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                txtStatus.setText("");
                HRtxtStatus.setText("");
                HRstatustxtStatus.setText("");
                GyrotxtStatus.setText("");
                DistancetxtStatus.setText("");
                PedometertxtStatus.setText("");
                TemperaturetxtStatus.setText("");
                UVtxtStatus.setText("");
                ContacttxtStatus.setText("");

                SpeedtxtStatus.setText("");


                new appTask().execute();
                mTimer2 = new Runnable() {
                    @Override
                    public void run() {
                        graph2LastXValue += 1d;
                        HRseries.appendData(new DataPoint(graph2LastXValue, HRString), true, 40);
                        TemperatureSeries.appendData(new DataPoint(graph2LastXValue, TemperatureString), true, 40);
                        AccXSeries.appendData(new DataPoint(graph2LastXValue, AccXString), true, 40);
                        AccYSeries.appendData(new DataPoint(graph2LastXValue, AccYString), true, 40);
                        AccZSeries.appendData(new DataPoint(graph2LastXValue, AccZString), true, 40);
                        GyroXSeries.appendData(new DataPoint(graph2LastXValue, GyroXString), true, 40);
                        GyroYSeries.appendData(new DataPoint(graph2LastXValue, GyroYString), true, 40);
                        GyroZSeries.appendData(new DataPoint(graph2LastXValue, GyroZString), true, 40);

                        mHandler.postDelayed(this, 200);
                    }
                };
                mHandler.postDelayed(mTimer2, 1000);
            }
        });
        btnRead = (Button) view.findViewById(R.id.ReadButton);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                String text = readFromFile(ACCFILENAME);
//                tv.setText(text.toString()); ////Set the text to text view.


                MainActivity temp=  ((MainActivity)getActivity());

//                Intent i = new Intent(getActivity(), FileTransferActivity.class);
//                getActivity().startActivity(i);

            }
        });

        btnDrink = (Button) view.findViewById(R.id.DrinkButton);
        btnDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String saveStr = "Drink"+"#";
                writeToFile(saveStr,FILENAME);
                writeToFile(saveStr,HRFILENAME);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,GYROFILENAME);
                writeToFile(saveStr,MODEFILENAME);
                writeToFile(saveStr,SPEEDFILENAME);
                writeToFile(saveStr,DISTANCEFILENAME);
                writeToFile(saveStr,CONTACTFILENAME);
                writeToFile(saveStr,STEPSFILENAME);
                writeToFile(saveStr,TEMPFILENAME);
                writeToFile(saveStr,UVFILENAME);
                writeToFile(saveStr,ACCFILENAME);
            }
        });

        btnDrunk = (Button) view.findViewById(R.id.DrunkButton);
        btnDrunk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String saveStr ="Drunk"+"#";
                writeToFile(saveStr,FILENAME);
                writeToFile(saveStr,HRFILENAME);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,GYROFILENAME);
                writeToFile(saveStr,MODEFILENAME);
                writeToFile(saveStr,SPEEDFILENAME);
                writeToFile(saveStr,DISTANCEFILENAME);
                writeToFile(saveStr,CONTACTFILENAME);
                writeToFile(saveStr,STEPSFILENAME);
                writeToFile(saveStr,TEMPFILENAME);
                writeToFile(saveStr,UVFILENAME);
                writeToFile(saveStr,ACCFILENAME);
            }
        });


        btnStagger = (Button) view.findViewById(R.id.StaggerButton);
        btnStagger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String saveStr = "Stagger"+"#";
                writeToFile(saveStr,FILENAME);
                writeToFile(saveStr,HRFILENAME);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,GYROFILENAME);
                writeToFile(saveStr,MODEFILENAME);
                writeToFile(saveStr,SPEEDFILENAME);
                writeToFile(saveStr,DISTANCEFILENAME);
                writeToFile(saveStr,CONTACTFILENAME);
                writeToFile(saveStr,STEPSFILENAME);
                writeToFile(saveStr,TEMPFILENAME);
                writeToFile(saveStr,UVFILENAME);
                writeToFile(saveStr,ACCFILENAME);
            }
        });
        btnStartExperiment = (Button) view.findViewById(R.id.StartButton);
        btnStartExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String saveStr = "Start"+"#";
                writeToFile(saveStr,FILENAME);
                writeToFile(saveStr,HRFILENAME);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,GYROFILENAME);
                writeToFile(saveStr,MODEFILENAME);
                writeToFile(saveStr,SPEEDFILENAME);
                writeToFile(saveStr,DISTANCEFILENAME);
                writeToFile(saveStr,CONTACTFILENAME);
                writeToFile(saveStr,STEPSFILENAME);
                writeToFile(saveStr,TEMPFILENAME);
                writeToFile(saveStr,UVFILENAME);
                writeToFile(saveStr,ACCFILENAME);
            }
        });
        btnStopDrink = (Button) view.findViewById(R.id.StopDrinkButton);
        btnStopDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String saveStr = "StopDrink"+"#";
                writeToFile(saveStr,FILENAME);
                writeToFile(saveStr,HRFILENAME);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,GYROFILENAME);
                writeToFile(saveStr,MODEFILENAME);
                writeToFile(saveStr,SPEEDFILENAME);
                writeToFile(saveStr,DISTANCEFILENAME);
                writeToFile(saveStr,CONTACTFILENAME);
                writeToFile(saveStr,STEPSFILENAME);
                writeToFile(saveStr,TEMPFILENAME);
                writeToFile(saveStr,UVFILENAME);
                writeToFile(saveStr,ACCFILENAME);
            }
        });
        btnFNDrunk = (Button) view.findViewById(R.id.FNDrunkButton);
        btnFNDrunk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String saveStr = "FND"+"#";
                writeToFile(saveStr,FILENAME);
                writeToFile(saveStr,HRFILENAME);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,GYROFILENAME);
                writeToFile(saveStr,MODEFILENAME);
                writeToFile(saveStr,SPEEDFILENAME);
                writeToFile(saveStr,DISTANCEFILENAME);
                writeToFile(saveStr,CONTACTFILENAME);
                writeToFile(saveStr,STEPSFILENAME);
                writeToFile(saveStr,TEMPFILENAME);
                writeToFile(saveStr,UVFILENAME);
                writeToFile(saveStr,ACCFILENAME);
            }
        });
        btnEnd = (Button) view.findViewById(R.id.EndButton);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String saveStr = "End"+"#";
                writeToFile(saveStr,FILENAME);
                writeToFile(saveStr,HRFILENAME);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,GYROFILENAME);
                writeToFile(saveStr,MODEFILENAME);
                writeToFile(saveStr,SPEEDFILENAME);
                writeToFile(saveStr,DISTANCEFILENAME);
                writeToFile(saveStr,CONTACTFILENAME);
                writeToFile(saveStr,STEPSFILENAME);
                writeToFile(saveStr,TEMPFILENAME);
                writeToFile(saveStr,UVFILENAME);
                writeToFile(saveStr,ACCFILENAME);
            }
        });
        btnTransfer = (Button) view.findViewById(R.id.TransferButton);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                try {
//                    transferFile(FILENAME, "Alltest.txt");
//                    transferFile(HRFILENAME, "HRtest.txt");
//                    transferFile(HRSTATUSFILENAME, "HRstatusTest.txt");
//                    transferFile(GYROFILENAME, "GyroTest.txt");
//                    transferFile(MODEFILENAME, "ModeTest.txt");
//                    transferFile(SPEEDFILENAME, "SpeedTest.txt");
//                    transferFile(DISTANCEFILENAME, "DistTest.txt");
//                    transferFile(CONTACTFILENAME, "ContactTest.txt");
//                    transferFile(STEPSFILENAME, "StepsTest.txt");
//                    transferFile(TEMPFILENAME, "TempTest.txt");
//                    transferFile(UVFILENAME, "UVtest.txt");
//                    transferFile(ACCFILENAME, "AccTest.txt");
                    MainActivity temp=  ((MainActivity)getActivity());
                    Intent i = new Intent(temp, FileTransferActivity.class);
                    startActivity(i);
                //Intent intent = new Intent(getActivity(), FileTransferActivity.class);
                //((MainActivity) getActivity()).startActivity(intent);


//                } catch (IOException e) {
//                    //e.printStackTrace();
//                    MainActivity temp=  ((MainActivity)getActivity());
//                    Toast.makeText(temp.getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
//
//                }
//                String saveStr = "Stagger"+"#";
//                writeToFile(saveStr,FILENAME);
//                writeToFile(saveStr,HRFILENAME);
//                writeToFile(saveStr,HRSTATUSFILENAME);
//                writeToFile(saveStr,GYROFILENAME);
//                writeToFile(saveStr,MODEFILENAME);
//                writeToFile(saveStr,SPEEDFILENAME);
//                writeToFile(saveStr,DISTANCEFILENAME);
//                writeToFile(saveStr,CONTACTFILENAME);
//                writeToFile(saveStr,STEPSFILENAME);
//                writeToFile(saveStr,TEMPFILENAME);
//                writeToFile(saveStr,UVFILENAME);
//                writeToFile(saveStr,ACCFILENAME);
            }
        });



        //Logging ====================================
//        String textToSaveString = "Hello Android";
//
//
//        //writeToFile(textToSaveString);
//
//        String textFromFileString =  readFromFile();
//
//        if ( textToSaveString.equals(textFromFileString) ) {
//            MainActivity temp= ((MainActivity) getActivity());
//            Toast.makeText(temp.getApplicationContext(), "both string are equal", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            MainActivity temp= ((MainActivity) getActivity());
//            Toast.makeText(temp.getApplicationContext(), "there is a problem", Toast.LENGTH_SHORT).show();
//        }

        HeartRateGraph = (GraphView) view.findViewById(R.id.HeartRateGraph);
        HRseries = new LineGraphSeries<DataPoint>();
        HeartRateGraph.addSeries(HRseries);
        HeartRateGraph.getViewport().setXAxisBoundsManual(true);
        HeartRateGraph.getViewport().setMinX(0);
        HeartRateGraph.getViewport().setMaxX(40);

        TemperatureGraph = (GraphView) view.findViewById(R.id.TemperatureGraph);
        TemperatureSeries = new LineGraphSeries<DataPoint>();
        TemperatureGraph.addSeries(TemperatureSeries);
        TemperatureGraph.getViewport().setXAxisBoundsManual(true);
        TemperatureGraph.getViewport().setMinX(0);
        TemperatureGraph.getViewport().setMaxX(40);

        AccXGraph = (GraphView) view.findViewById(R.id.AccelerationXGraph);
        AccXSeries = new LineGraphSeries<DataPoint>();
        AccXGraph.addSeries(AccXSeries);
        AccXGraph.getViewport().setXAxisBoundsManual(true);
        AccXGraph.getViewport().setMinX(0);
        AccXGraph.getViewport().setMaxX(40);

        AccYGraph = (GraphView) view.findViewById(R.id.AccelerationYGraph);
        AccYSeries = new LineGraphSeries<DataPoint>();
        AccYGraph.addSeries(AccYSeries);
        AccYGraph.getViewport().setXAxisBoundsManual(true);
        AccYGraph.getViewport().setMinX(0);
        AccYGraph.getViewport().setMaxX(40);


        AccZGraph = (GraphView) view.findViewById(R.id.AccelerationZGraph);
        AccZSeries = new LineGraphSeries<DataPoint>();
        AccZGraph.addSeries(AccZSeries);
        AccZGraph.getViewport().setXAxisBoundsManual(true);
        AccZGraph.getViewport().setMinX(0);
        AccZGraph.getViewport().setMaxX(40);


        GyroXGraph = (GraphView) view.findViewById(R.id.GyroXGraph);
        GyroXSeries = new LineGraphSeries<DataPoint>();
        GyroXGraph.addSeries(GyroXSeries);
        GyroXGraph.getViewport().setXAxisBoundsManual(true);
        GyroXGraph.getViewport().setMinX(0);
        GyroXGraph.getViewport().setMaxX(40);

        GyroYGraph = (GraphView) view.findViewById(R.id.GyroYGraph);
        GyroYSeries = new LineGraphSeries<DataPoint>();
        GyroYGraph.addSeries(GyroYSeries);
        GyroYGraph.getViewport().setXAxisBoundsManual(true);
        GyroYGraph.getViewport().setMinX(0);
        GyroYGraph.getViewport().setMaxX(40);


        GyroZGraph = (GraphView) view.findViewById(R.id.GyroZGraph);
        GyroZSeries = new LineGraphSeries<DataPoint>();
        GyroZGraph.addSeries(GyroZSeries);
        GyroZGraph.getViewport().setXAxisBoundsManual(true);
        GyroZGraph.getViewport().setMinX(0);
        GyroZGraph.getViewport().setMaxX(40);

//        GraphView HeartRateGraph = (GraphView) view.findViewById(R.id.HeartRategraph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        HeartRateGraph.addSeries(series);


        //=============================================
        // Inflate the layout for this fragment
        return view;
    }

    private void writeToFile(String data) {
        try {
            FileOutputStream fileout=context.openFileOutput(FILENAME, 32768); //MODE_APPEND
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
            outputStreamWriter.append(data);
//            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }

    }
    private void writeToFile(String data, String filename) {
        try {
            FileOutputStream fileout=context.openFileOutput(filename, 32768); //MODE_APPEND
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
            outputStreamWriter.append(data);
//            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }

    }

    private void writeToFile2() {
//        try {
//            FileOutputStream fileout=context.openFileOutput(filename, 32768); //MODE_APPEND
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
//            outputStreamWriter.append(data);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e(TAG, "File write failed: " + e.toString());
//        }
        String content = "hello world";
        File file;
        FileOutputStream outputStream;
        try {
            String s = Environment.getExternalStorageDirectory().toString();
            file = new File(Environment.getExternalStorageDirectory(), "MyCache");
            MainActivity temp=  ((MainActivity)getActivity());
            Toast.makeText(temp.getApplicationContext(), s, Toast.LENGTH_LONG).show();

            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile() {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    private String readFromFile(String filename) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


    /////////////
    private class appTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");

                    //Accelerometer
                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);

                    //Gyroscope
                    client.getSensorManager().registerGyroscopeEventListener(mGyroscopeEventListener, SampleRate.MS128);

                    //Distance
                    client.getSensorManager().registerDistanceEventListener(mDistanceEventListener);

                    //Heart Rate
                    if(client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        // user has not consented, request it
                        // the calling class is both an Activity and implements
                        // HeartRateConsentListener
                        startHRListener();
                    }
                    else{
                        MainActivity temp;
                        temp =  ((MainActivity)getActivity());
                        client.getSensorManager().requestHeartRateConsent(temp, mHeartRateConsentListener);

                    }

                    //Pedometer
                    client.getSensorManager().registerPedometerEventListener(mPedometerEventListener);

                    //Temperature
                    client.getSensorManager().registerSkinTemperatureEventListener(mTemperatureEventListener);

                    //UV
                    client.getSensorManager().registerUVEventListener(mUVEventListener);

                    //Device Contact
                    client.getSensorManager().registerContactEventListener(mContactEventListener);

                    //Calories
                    //client.getSensorManager().registerCaloriesEventListener(mCaloriesEventListener);

                    //GSR

                    //Ambient Light Sensor

                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage();
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    private void appendToUI(final String string) {
        MainActivity temp = ((MainActivity) getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ContacttxtStatus.setText(string);
                //txtStatus.setText(string);
                String marker = "#";
                String saveStr = string + marker;
                writeToFile(saveStr);


            }
        });
    }
    private void appendToUIAcc(final String string) {
        MainActivity temp = ((MainActivity) getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
                String marker = "#";
                String saveStr = "Acc, " + string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,ACCFILENAME);

            }
        });
    }
    private void appendToUIGyro(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GyrotxtStatus.setText(string);
                String marker = "#";
                String saveStr ="Angular, " + string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,GYROFILENAME);

            }
        });
    }
    private void appendToUIDistance(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DistancetxtStatus.setText(string);
                String marker = "#";
                String saveStr = "Distance, " + string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,DISTANCEFILENAME);
            }
        });
    }
    private void appendToUISpeed(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SpeedtxtStatus.setText(string);
                String marker = "#";
                String saveStr = "Speed, " +string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,SPEEDFILENAME);
            }
        });
    }
    private void appendToUIMotion(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MotiontxtStatus.setText(string);
                String marker = "#";
                String saveStr = "Motion, " +string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,MODEFILENAME);
            }
        });
    }

    private void appendToUIHR(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HRtxtStatus.setText(string);
                String marker = "#";
                String saveStr ="HR, " + string + marker;
                writeToFile(saveStr);

                //save HR record on separate file
                writeToFile(saveStr,HRFILENAME);
            }
        });
    }
    private void appendToUIHRStatus(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HRstatustxtStatus.setText(string);
                String marker = "#";
                String saveStr = "HRstatus, " +string + marker;
                writeToFile(saveStr);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,HRFILENAME);

            }
        });
    }


    private void appendToUIPedometer(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PedometertxtStatus.setText(string);
                String marker = "#";
                String saveStr = "Steps, " +string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,STEPSFILENAME);


            }
        });
    }
    private void appendToUITemperature(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TemperaturetxtStatus.setText(string);
                String marker = "#";
                String saveStr = "Temperature, "+string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,TEMPFILENAME);

            }
        });
    }
    private void appendToUIUV(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UVtxtStatus.setText(string);
                String marker = "#";
                String saveStr = "UV, "+string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,UVFILENAME);
            }
        });
    }
    private void appendToUIContact(final String string) {
        MainActivity temp=  ((MainActivity)getActivity());
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ContacttxtStatus.setText(string);
                String marker = "#";
                String saveStr = "Contact, "+string + marker;
                writeToFile(saveStr);

                writeToFile(saveStr,CONTACTFILENAME);

                //Tag Contact Status to all other separate files
                writeToFile(saveStr,UVFILENAME);
                writeToFile(saveStr,TEMPFILENAME);
                writeToFile(saveStr,STEPSFILENAME);
                writeToFile(saveStr,HRSTATUSFILENAME);
                writeToFile(saveStr,HRFILENAME);
                writeToFile(saveStr,MODEFILENAME);
                writeToFile(saveStr,SPEEDFILENAME);
                writeToFile(saveStr,DISTANCEFILENAME);
                writeToFile(saveStr,ACCFILENAME);
                writeToFile(saveStr,GYROFILENAME);
            }
        });
    }



    //ACCELEROMETER
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {
                //appendToUI(String.format(" X = %.3f  Y = %.3f  Z = %.3f", event.getAccelerationX(),
                //        event.getAccelerationY(), event.getAccelerationZ()));
                appendToUIAcc(String.format(" X = %.3f  Y = %.3f  Z = %.3f", event.getAccelerationX(),
                        event.getAccelerationY(), event.getAccelerationZ()));

                AccXString = event.getAccelerationX();
                AccYString = event.getAccelerationY();
                AccZString = event.getAccelerationZ();


            }
        }
    };

    //GYROSCOPE
    private BandGyroscopeEventListener mGyroscopeEventListener = new BandGyroscopeEventListener() {

        public void onBandGyroscopeChanged(final BandGyroscopeEvent event) {
            if (event != null) {
                appendToUIGyro(String.format(" X = %.3f  Y = %.3f  Z = %.3f", event.getAngularVelocityX(),
                        event.getAngularVelocityY(), event.getAngularVelocityZ()));
                GyroXString = event.getAccelerationX();
                GyroYString = event.getAccelerationY();
                GyroZString = event.getAccelerationZ();

            }
        }
    };
    //DISTANCE
    private BandDistanceEventListener mDistanceEventListener = new BandDistanceEventListener() {

        public void onBandDistanceChanged(final BandDistanceEvent event) {
            if (event != null) {
                appendToUIDistance(""+event.getTotalDistance());
                appendToUISpeed("" + event.getSpeed());
                appendToUIMotion("" + event.getMotionType());
                //"Current Pace: " + event.getPace()+ "\n"

            }
        }
    };

    //HEART RATE
    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                appendToUIHR("" + event.getHeartRate());
                appendToUIHRStatus("" + event.getQuality());
                HRString = event.getHeartRate();
            }
        }
    };

    private HeartRateConsentListener mHeartRateConsentListener = new HeartRateConsentListener() {
        public void userAccepted(final boolean accept) {
            if (accept) {
                // Consent has been given, start HR sensor event listener
                startHRListener();
            }
            else {
                // Consent hasn't been given
                appendToUIHR(String.valueOf(accept));
            }
        }
    };
    public void startHRListener() {
        try {
            // register HR sensor event listener
            client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
        } catch (BandIOException ex) {
            appendToUIHR(ex.getMessage());
        } catch (BandException e) {
            String exceptionMessage="";
            switch (e.getErrorType()) {
                case UNSUPPORTED_SDK_VERSION_ERROR:
                    exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
                    break;
                case SERVICE_ERROR:
                    exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
                    break;
                default:
                    exceptionMessage = "Unknown error occurred: " + e.getMessage();
                    break;
            }
            appendToUIHR(exceptionMessage);

        } catch (Exception e) {
            appendToUIHR(e.getMessage());
        }
    }

    //PEDOMETER
    private BandPedometerEventListener mPedometerEventListener = new BandPedometerEventListener() {

        public void onBandPedometerChanged(final BandPedometerEvent event) {
            if (event != null) {
                appendToUIPedometer("" + event.getTotalSteps());
            }
        }
    };

    //TEMPERATURE
    private BandSkinTemperatureEventListener mTemperatureEventListener = new BandSkinTemperatureEventListener() {

        public void onBandSkinTemperatureChanged(final BandSkinTemperatureEvent event) {
            if (event != null) {
                appendToUITemperature("" + event.getTemperature());
                TemperatureString = event.getTemperature();
            }
        }
    };

    //UV
    private BandUVEventListener mUVEventListener = new BandUVEventListener() {

        public void onBandUVChanged(final BandUVEvent event) {
            if (event != null) {
                UVIndexLevel temp = event.getUVIndexLevel();

                appendToUIUV("" + temp);
            }
        }
    };

    //DEVICE CONTACT
    private BandContactEventListener mContactEventListener = new BandContactEventListener() {

        public void onBandContactChanged(final BandContactEvent event) {
            if (event != null) {
                appendToUIContact("" + event.getContactState());
            }
        }
    };
    //CALORIES
//    private BandCaloriesEventListener mCaloriesEventListener = new BandCaloriesEventListener() {
//
//        public void onBandCaloriesChanged(final BandCaloriesEvent event) {
//            if (event != null) {
//                appendToUICalories("Total Calories: " + event.getCalories());
//                //appendLog("Total Calories: " + event.getCalories());
//            }
//        }
//    };



    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }

            MainActivity temp = ((MainActivity)getActivity());
            client = BandClientManager.getInstance().create(temp.getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
        appendToUI("Band is connecting...\n");
        //appendToUIContact("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    /////////////
    private final Handler mHandler = new Handler();
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> TemperatureSeries;
    private LineGraphSeries<DataPoint> AccXSeries;
    private LineGraphSeries<DataPoint> AccYSeries;
    private LineGraphSeries<DataPoint> AccZSeries;
    private LineGraphSeries<DataPoint> HRseries;
    private LineGraphSeries<DataPoint> GyroXSeries;
    private LineGraphSeries<DataPoint> GyroYSeries;
    private LineGraphSeries<DataPoint> GyroZSeries;
    private double graph2LastXValue = 5d;

//    public void onResume() {
//        super.onResume();
//
//        mTimer2 = new Runnable() {
//            @Override
//            public void run() {
//                graph2LastXValue += 1d;
//                HRseries.appendData(new DataPoint(graph2LastXValue, HRString), true, 40);
//                TemperatureSeries.appendData(new DataPoint(graph2LastXValue, TemperatureString), true, 40);
//                AccXSeries.appendData(new DataPoint(graph2LastXValue, AccXString), true, 40);
//                AccYSeries.appendData(new DataPoint(graph2LastXValue, AccYString), true, 40);
//                AccZSeries.appendData(new DataPoint(graph2LastXValue, AccZString), true, 40);
//                GyroXSeries.appendData(new DataPoint(graph2LastXValue, GyroXString), true, 40);
//                GyroYSeries.appendData(new DataPoint(graph2LastXValue, GyroYString), true, 40);
//                GyroZSeries.appendData(new DataPoint(graph2LastXValue, GyroZString), true, 40);
//
//                mHandler.postDelayed(this, 200);
//            }
//        };
//        mHandler.postDelayed(mTimer2, 1000);
//
//    }

    private void writeTextFile(String fileStr, String txtToSave) throws IOException {
        // The name of the file to open.
        //String fileName = "temp.txt";
//        String fileName = file;
//        try {
//            // Assume default encoding.
//            FileWriter fileWriter = new FileWriter(fileName);
//
//            // Always wrap FileWriter in BufferedWriter.
//            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//
//            // Note that write() does not automatically
//            // append a newline character.
//            bufferedWriter.write(txtToSave);
//            //bufferedWriter.newLine();
//            // Always close files.
//            bufferedWriter.close();
//        }
//        catch(IOException ex) {
//            System.out.println("Error writing to file '"+ fileName + "'");
//            // Or we could just do this:
//            // ex.printStackTrace();
//        }
        String content = txtToSave;
        File file;
        FileOutputStream outputStream;
        try {
            String s = Environment.getExternalStorageDirectory().toString();
            file = new File(Environment.getExternalStorageDirectory(), fileStr);
            MainActivity temp=  ((MainActivity)getActivity());
            Toast.makeText(temp.getApplicationContext(), s, Toast.LENGTH_LONG).show();

            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Transfer contents in android file to desktop text file
    private void transferFile(String androidFile, String desktopFile) throws IOException {
        String str = readFromFile(androidFile);
        writeTextFile(desktopFile, str);
    }

}
