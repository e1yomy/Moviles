package elyo.my.trackids;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import static android.content.Context.LOCATION_SERVICE;
import static elyo.my.trackids.Principal.c;
import static elyo.my.trackids.Principal.pantalla;
import static elyo.my.trackids.Principal.preferences;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Mapa.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Mapa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Mapa extends Fragment implements OnMapReadyCallback,LocationListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Mapa() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Mapa.
     */
    // TODO: Rename and change types and number of parameters
    public static Mapa newInstance(String param1, String param2) {
        Mapa fragment = new Mapa();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    GoogleMap m;
    LocationManager locationManager;
    Location l;
    boolean gps;
    LatLng la;
    String nombre;
    FloatingActionButton opciones,miUbicacion;
    SeekBar zoom;
    int zoomlevel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_mapa, container, false);
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.mapa1);
            mapFragment.getMapAsync(this);

            locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);
            gps = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            opciones = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
            miUbicacion = (FloatingActionButton) view.findViewById(R.id.floatingActionButton2);
            zoom=(SeekBar)view.findViewById(R.id.seekZoom);
            Zoom();
            BotonOpciones();
            BotonMiUbicacion();
            if(pantalla==1){
                opciones.setVisibility(View.INVISIBLE);
                miUbicacion.setVisibility(View.VISIBLE);
                //
            }
            else {
                opciones.setVisibility(View.VISIBLE);
                miUbicacion.setVisibility(View.VISIBLE);
                //Cargar la ubicacion del hijo seleccionado antes

            }
        }
        catch (Exception ex)
        {
            Toast.makeText( c, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void BotonMiUbicacion() {
        miUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pantalla==1)
                {
                    ActualizarMarcador(l);
                }
                else
                {
                    m.clear();
                    ActualizarMarcador(l);
                    MostrarPosicion();
                }

            }
        });
    }

    private void Zoom() {
        zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m.animateCamera(CameraUpdateFactory.newLatLngZoom(la,m.getCameraPosition().zoom));
                m.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getCameraPosition().target,progress));
                zoomlevel=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void BotonOpciones() {
        opciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert= new AlertDialog.Builder(c);
                alert.setTitle("Opciones");
                alert.setItems(R.array.opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //which es el valor del index de la lista de elementos que se muestran en el Dialog
                        switch (which)
                        {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                DatePickerDialog pick= new DatePickerDialog(c, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker v, int year, int month, int dayOfMonth) {
                                        month++; //meses está de 0 a 11

                                        //Cuando no haya ningun registro del hijo en la fecha seleccionada, deberá mostrar un Snackz
                                        //Snackbar.make(view,dayOfMonth+" "+(month+1)+" "+ year,Snackbar.LENGTH_LONG).show();

                                    }
                                }, Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                                pick.setTitle("Seleccionar fecha");
                                pick.show();
                                break;


                        }

                    }
                });
                alert.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            ConfigurarMapa(googleMap);

            if (ActivityCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 10f, (LocationListener) this);
            locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);

            if (gps) {
                if (locationManager != null) {
                    l = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                }
            }
            if(pantalla==1) {
                ActualizarMarcador(l);
            }
            else
            {
                ////////////Mostrar la ubicacion que se mando antes
                m.clear();
                ActualizarMarcador(l);
                MostrarPosicion();
            }

        } catch (Exception ex) {
            Toast.makeText( c, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ConfigurarMapa(GoogleMap googleMap){
        try {
            m = googleMap;
            m.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            //m.getUiSettings().setZoomControlsEnabled(true);
            m.getUiSettings().setZoomGesturesEnabled(true);
            m.getUiSettings().setCompassEnabled(true);
            m.getUiSettings().setScrollGesturesEnabled(true);
            m.getUiSettings().setMapToolbarEnabled(false);
            m.setPadding(0, 0, 15, 130);
            //zoom.setProgress(zoomlevel,true);
            m.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    ActualizarMarcador(l);
                    return false;
                }
            });
            m.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    zoom.setProgress((int) cameraPosition.zoom,true);
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText( c, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        if(pantalla==1) {
            m.clear();
            ActualizarMarcador(location);
        }
        else
        {
            m.clear();
            ActualizarMarcador(location);
            MostrarPosicion();
        }

    }

    public void ActualizarMarcador(Location lo){
        l=lo;
        if (l != null) {
            la=new LatLng(l.getLatitude(), l.getLongitude());
            MarkerOptions mar=new MarkerOptions().position(la).title("Tú").icon(BitmapDescriptorFactory.defaultMarker());
            m.addMarker(mar);
            m.animateCamera(CameraUpdateFactory.newLatLngZoom(la,zoomlevel));

        }
    }

    public void MostrarPosicion(){
        la=new LatLng(preferences.getFloat("lat",0.0f),preferences.getFloat("lon",0.0f));
        MarkerOptions mar=new MarkerOptions().position(la).title(preferences.getString("nombre","nombre")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        m.addMarker(mar);
        m.animateCamera(CameraUpdateFactory.newLatLngZoom(la,zoomlevel));


    }

}
