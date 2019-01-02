package andreadelvecchio.pervasivestudent.gmail.it.flashmobclient;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by andrea on 29/12/17.
 */

public class Gallery extends ListActivity {

        private String baseUri = "http://192.168.2.117:8182/content/user";

        String[] picture_list;
        ArrayList<File> images = new ArrayList<File>() ;
        String flashMobName;
        Gallery myGallery = this;
        public final static String PERMISSION_WRITE_STORAGE ="android.permission.WRITE_EXTERNAL_STORAGE";
        public final static int MY_PERMISSIONS_REQUEST=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        flashMobName = intent.getStringExtra("FlashMobName");

        if (checkPermission()){
            new ListPictureTask().execute(flashMobName);
        } else {
            requestPermisison();
        }


    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                PERMISSION_WRITE_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermisison(){

            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSION_WRITE_STORAGE}, MY_PERMISSIONS_REQUEST);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ListPictureTask().execute(flashMobName);
            } else {
                Toast.makeText(getApplicationContext(), "No Permnissions", Toast.LENGTH_LONG).show();
            }
            return;
        }
    }



    @Override
    public void onDestroy(){
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/"+ flashMobName);

        String[]entries = storageDirectory.list();

        if(entries!=null) {
            for (String s : entries) {
                File currentFile = new File(storageDirectory.getPath(), s);
                currentFile.delete();
            }
        }

        storageDirectory.delete();
        Log.i("Attemp to delete file", "Metodo invocato");
        super.onDestroy();
    }






    public class ListPictureTask extends AsyncTask<String, Void, String> {
        private String response;
        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;

            cr = new ClientResource(baseUri+"/flashmob/"+flashMobName);
            String response=null;


            try {
                response = cr.get().getText();
            } catch (ResourceException |IOException e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Getting List",text);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res!=null) {
                Toast.makeText(getApplicationContext(), "DownloadList: OK", Toast.LENGTH_LONG).show();

                picture_list = new Gson().fromJson(res, String[].class);
                //Log.e("Getting List", lista.toString());

                for(String fileName: picture_list){
                    new PictureGetTask().execute(fileName);
                }

            }
            else{
                Toast.makeText(getApplicationContext(), "DownloadList: Failed", Toast.LENGTH_LONG).show();
            }
        }
    }



    public class PictureGetTask extends AsyncTask<String, Void, String> {
        private String result;
        Representation response;
        String fileName;
        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;
            fileName=params[0];
            cr = new ClientResource(baseUri+"/flashmob/"+flashMobName+"/"+params[0]);
            Log.e("Getting Picture",baseUri+"/flashmob/"+flashMobName+"/"+params[0]);
            response=null;
            result=null;
            File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/"+ flashMobName);
            if (!storageDirectory.exists()) storageDirectory.mkdir();
            File image = new File(storageDirectory.getAbsolutePath()+"/"+fileName);

            try {
                response =   cr.get();
                response.write(new FileOutputStream(image));
                images.add(image);

                result="OK";
            } catch (ResourceException|IOException  e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Getting Picture",text);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res!=null) {
                myGallery.setListAdapter(new ImageViewAdapter(getApplicationContext(),images));
                Toast.makeText(getApplicationContext(), "DownloadPICTURE: OK", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "DownloadPICTURE: Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}
