package kr.ac.snu.bi.sensorcollector;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PrintActivity extends ActionBarActivity {

    private static final String FILENAME = "BandSensorFile.txt";
    private static final String UVFILENAME = "UVFile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
//s
//        StringBuilder text = new StringBuilder();
//        try {
//            File sdcard = Environment.getExternalStorageDirectory();
//            File file = new File(sdcard,"BandSensorFile.txt");
//
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String line;
//            while ((line = br.readLine()) != null) {
//                text.append(line);
//                Log.i("Test", "text : " + text + " : end");
//                text.append('\n');
//            } }
//        catch (IOException e) {
//            e.printStackTrace();
//
//        }
////        finally{
////            br.close();
////        }
//        TextView tv = (TextView)findViewById(R.id.printText);
//
//        tv.setText(text.toString()); ////Set the text to text view.


    }
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
            //MainActivity temp=  ((MainActivity)getActivity());
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

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
    private String readFromFile(String filename) {
        String ret = "";

        try {
            InputStream inputStream = openFileInput(filename);

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


}
