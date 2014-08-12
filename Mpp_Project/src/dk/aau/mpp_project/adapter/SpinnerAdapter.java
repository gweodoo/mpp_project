package dk.aau.mpp_project.adapter;

import java.util.ArrayList;


import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.model.SpinnerModel;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
/***** Adapter class extends with ArrayAdapter ******/
public class SpinnerAdapter extends ArrayAdapter<String>{
     
    private Activity activity;
    private ArrayList data;
    public Resources res;
    SpinnerModel tempValues=null;
    LayoutInflater inflater;
     
    /*************  CustomAdapter Constructor *****************/
    public SpinnerAdapter(
                          MainActivity activitySpinner, //ilyas: j'ai des doutes sur cette ligne
                          int textViewResourceId,   
                          ArrayList objects,
                          Resources resLocal
                         ) 
     {
        super(activitySpinner, textViewResourceId, objects);
         
        /********** Take passed values **********/
        activity = activitySpinner;
        data     = objects;
        res      = resLocal;
    
        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
      }
 
    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
 
    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {
 
        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinner_rows, parent, false);
         
        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (SpinnerModel) data.get(position);
         
        TextView SpinnerUser  = (TextView)row.findViewById(R.id.spinnerUser);
        TextView sub          = (TextView)row.findViewById(R.id.sub);
        ImageView companyLogo = (ImageView)row.findViewById(R.id.image);
         
        if(position==0){
             
            // Default selected Spinner item 
            SpinnerUser.setText("Please select a shareMate");
            sub.setText("");
        }
        else
        {
            // Set values for spinner each row 
            SpinnerUser.setText(tempValues.getUserName());
            sub.setText(tempValues.getUrl());
            companyLogo.setImageResource(res.getIdentifier
                                         ("dk.aau.mpp_project:drawable/"
                                          + tempValues.getImage(),null,null));
            
            Toast.makeText(getContext(), tempValues.getImage(), Toast.LENGTH_SHORT).show();
             
        }   
 
        return row;
      }
 }