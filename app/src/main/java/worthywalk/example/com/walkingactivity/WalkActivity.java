package worthywalk.example.com.walkingactivity;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class WalkActivity extends AppCompatActivity {
    int [] icons=  new int[]{R.drawable.ic_runer_silhouette_running_fast,R.drawable.ic_man_cycling,R.drawable.ic_treadmill};
    int [] circle=new int[]{R.drawable.walkcircle,R.drawable.cyclecircle,R.drawable.treadmillcircle};
    int [] colors=new int[]{R.color.walk,R.color.cycle,R.color.treadmill};
    int steps=0;
    long timeWhenStopped = 0;
    Context context;
    static WalkActivity Instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
//    TextView textView, tv_x, tv_y, tv_z;
    SensorManager sensorManager;
    Sensor accelerometer;
    Double _kms,cal;

    private String Dist;

    Button btn;

    public static WalkActivity getInstance() {
        return Instance;
    }
    ImageView iconset;
    TextView calorie,distance,timer;
    int index=0;
    TextView tap;
    Button startbtn;
    RelativeLayout relativeLayout;
    RelativeLayout relativeLayoutprog;
    ProgressBar progressBar;
    boolean start =false;
    boolean tapped=false;
    StepDetector stepDetector;
    FloatingActionButton fabbutton;
    Chronometer chronometer;
    User user=new User();
    LatLng point;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        Instance = this;
        Intent intent=getIntent();
        user=(User) intent.getSerializableExtra("User");
        Permission();
        setviews();
        setbuttons();


//
        stepDetector=new StepDetector();
        context=this;

     relativeLayoutprog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!start){

                    tap.setVisibility(View.GONE);
                    tapped=true;
                    index= index%3;
                    iconset.setBackgroundResource(icons[index]);
                    relativeLayout.setBackgroundResource(circle[index]);
                    progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(),colors[index]), PorterDuff.Mode.SRC_IN );;
                    index++;

                }

            }
        });

fabbutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.bonusdailog);
        dialog.setTitle("Bonus");
        final EditText text = (EditText) dialog.findViewById(R.id.promo);
        Button dialogButton = (Button) dialog.findViewById(R.id.use);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = text.getText().toString().trim();

//                        Query q=mDatabase.child("Bounus").orderByChild("Code").equalTo(x);

//                        q.addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                                String x = dataSnapshot.getKey();
//                                Float y = dataSnapshot.child("multiply").getValue(Float.class);
//                                divideby= divideby/y;
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
                dialog.dismiss();
            }

        });
        dialog.show();
    }

});

    }

    private void setbuttons() {

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tapped) {



                }
            }
        });



    }

    public void DialogBox(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /////make map clear
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.finishdilog);////your custom content
        TextView dialogbtn=(TextView) dialog.findViewById(R.id.finishworkout);
        TextView steps=(TextView) dialog.findViewById(R.id.steps);
        TextView discard=(TextView) dialog.findViewById(R.id.discardeddistance);
        MapView mMapView = (MapView) dialog.findViewById(R.id.map);
        discard.setText(String.format("%.2f",MyLocationService.walk.discardeddistance));
        SPForString("distance",String.format("%.2f",MyLocationService.walk.discardeddistance));
        steps.setText(String.valueOf(MyLocationService.walk.steps));
        dialog.show();
        MapsInitializer.initialize(context);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.setMyLocationEnabled(true);
                if(point!=null) {
                    CameraPosition myPosition = new CameraPosition.Builder()
                            .target(point).zoom(17).bearing(90).tilt(30).build();
                    googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(myPosition));////your lat lng
                }
                googleMap.addPolyline(new PolylineOptions()
                        .addAll(MyLocationService.loc)
                        .width(5)
                        .color(Color.RED));
            }
        });
        //Button dialogButton = (Button) dialog.findViewById(R.id.map);
        // if button is clicked, close the custom dialog
        dialogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setviews() {
        relativeLayoutprog = (RelativeLayout) findViewById(R.id.relativeprog);
        relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout);
        tap=(TextView)findViewById(R.id.tap);
        fabbutton=(FloatingActionButton) findViewById(R.id.promobutton) ;
        progressBar=(ProgressBar) findViewById(R.id.progressBar5);
        iconset=(ImageView)findViewById(R.id.iconview);
        calorie=(TextView)findViewById(R.id.calorie);
        distance=(TextView)findViewById(R.id._kms);
        chronometer=(Chronometer) findViewById(R.id.time);
        startbtn=(Button) findViewById(R.id.start);
    }

    public void timerstart() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

    }

    public void timerstop(){
        chronometer.stop();

    }
    private void Permission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        UpdateLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "Accept Location", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }

                }).check();

    }

    private void UpdateLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        BuildLocationRequest();


    }

    public PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void BuildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(2f);

    }

    public void UpdateTextView(final LatLng loc) {
        WalkActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                textView.setText(Val);
                Intent intent = new Intent(WalkActivity.this,SensorForeground.class);
//                intent.putExtra("intent",Val);
                Log.d("location12",MyLocationService.loc.toString());
                Dist = String.format("%.2f", MyLocationService.walk.Distance);
             distance.setText(Dist);
                point=loc;
            }
        });
        Toast.makeText(getApplicationContext(),loc.toString(),Toast.LENGTH_SHORT).show();
    }

    public void UpdateSensorTV(final String steps) {
        WalkActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                startbtn.setText(steps);
            }
        });
    }




    private void SPForString(String name,String Val){
        SharedPreferences SP =   getApplicationContext().getSharedPreferences("Temp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(name, Val);
        editor.commit();
    }

    private void SPForBoolean(String name,Boolean Val){
        SharedPreferences SP =   getApplicationContext().getSharedPreferences("Temp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        editor.putBoolean(name, Val);
        editor.commit();
    }

    private void SPForInt(String name,int Val){
        SharedPreferences SP =   getApplicationContext().getSharedPreferences("Temp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        editor.putInt(name, Val);
        editor.commit();
    }


    private String GetSPString(String val){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("Temp", val );
    }

    private boolean GetSPBoolean(Boolean val){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("temp",val );
    }

    private int GetSPInt(int val){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getInt("Temp", val );
    }


}
