package elyo.my.trackids;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Mapa.OnFragmentInteractionListener,
        ListaHijos.OnFragmentInteractionListener,
        MisUbicaciones.OnFragmentInteractionListener,
        Registro.OnFragmentInteractionListener,
        IniciarSesion.OnFragmentInteractionListener

{

    public static Context c;
    public static byte pantalla=1;
    static SharedPreferences preferences;
    static int sesion=0; //0: ninguna 1:sesion 2: Facebook
    static Toolbar toolbar;
    static Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        c=this;
        preferences= getSharedPreferences("preferencias",Context.MODE_PRIVATE);
        fragmentManager = getSupportFragmentManager();
        try{
            navigationView.getMenu().getItem(0).setChecked(true);

            //preferences.edit().putInt("sesion",0).commit();
            if(preferences.getInt("sesion",0)!=0)
            {
                toolbar.setVisibility(View.VISIBLE);
                //txtNombreUsuario.setText(preferences.getString("headernombreusuario","Nombre"));
                //txtEmailUsuario.setText(preferences.getString("headercorreo","alguien@example.com"));
                if(preferences.getInt("sesion",0)!=0)
                    CargarUsuario();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_principal,new Mapa()).commit();
            }
            else
            {
                toolbar.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_principal,new IniciarSesion()).commit();

            }

        }
        catch (Exception ex)
        {
            Toast.makeText( c, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        //Servicio servicio = new Servicio(c);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_ajustes:

                break;
            case R.id.nav_misdatos:
                break;
            case R.id.nav_cerrarsesion:
                try {
                    preferences.edit().putInt("sesion", 0).commit();
                    LoginManager.getInstance().logOut();
                    PantallaInicioDeSesion();
                }
                catch (Exception ex)
                {

                }
                break;
        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    static FragmentManager fragmentManager;
    static FragmentTransaction transaction;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(preferences.getInt("sesion",0)!=0)
        {
            switch (id)
            {
                case R.id.nav_inicio:
                    pantalla=1;
                    PantallaMapa();
                    break;
                case R.id.nav_hijos:
                    pantalla=2;
                    PantallaHijos();
                    break;
                case R.id.nav_agregarhijo:
                    pantalla=3;
                    break;
                case R.id.nav_misubicaciones:
                    pantalla=4;
                    PantallaMisLugares();
                    break;
            }
            if(id==R.id.nav_salir)
                finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static void PantallaInicioDeSesion(){
        toolbar.setVisibility(View.GONE);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_principal,new IniciarSesion());
        transaction.commit();
    }
    public static void PantallaMisLugares(){
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Mis lugares");
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_principal,new MisUbicaciones());
        transaction.commit();
    }
    public static void PantallaRegistro(){
        try {
            toolbar.setVisibility(View.VISIBLE);

            toolbar.setTitle("Registro");
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_principal, new Registro());
            transaction.commit();
        }
        catch (Exception ex)
        {
            String e=ex.getMessage();
        }
    }
    public static void PantallaMapa(){
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Mi mapa");
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_principal,new Mapa());
        transaction.commit();
    }
    public static void PantallaHijos(){
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Lista de hijos");
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_principal,new ListaHijos());
        transaction.commit();
    }
    public static void GuardarCuenta(final String nombres, final String apellidos, final String correo, final String contrasena, final String telefono)
    {
        preferences.edit().putBoolean("exito",false).commit();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServiciosWeb sw = new ServiciosWeb();
                    if (sw.GuardarCuenta(nombres, apellidos, correo, contrasena, telefono) == 1) {
                        preferences.edit().putBoolean("exito", true).commit();
                    }

                }
                catch (Exception ex)
                {
                    String s=ex.getMessage();
                }
            }
        });
        t.start();
    }

    public static void ExisteCuenta(final String email, final String contra)
    {
        ProgressDialog p = new ProgressDialog(c);
        p.setIndeterminate(true);
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);
        p.setTitle("Espere");
        p.setMessage("Verificando sus credenciales");
        p.show();
        preferences.edit().putBoolean("procesoFinalizado",false).commit();
        preferences.edit().putBoolean("existe", false).commit();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServiciosWeb sw = new ServiciosWeb();
                    if (sw.ExisteCuenta(email, contra) == 1) {
                        preferences.edit().putBoolean("existe", true).commit();
                    }
                    preferences.edit().putBoolean("procesoFinalizado",true).commit();
                }
                catch (Exception ex)
                {
                    String s=ex.getMessage();
                }
            }
        });
        t.start();
        while (true)
        {
            if(preferences.getBoolean("procesoFinalizado",false)) {
                p.dismiss();
                return;
            }
        }
    }

    public void CargarUsuario() {
        usuario = new Usuario(
                preferences.getString("idUsuario",""),
                preferences.getString("correoUsuario", ""),
                preferences.getString("nombresUsuario", ""),
                preferences.getString("apellidosUsuario", ""),
                preferences.getString("telefonoUsuario", ""),
                preferences.getString("contrasenaUsuario", "")
        );
    }

}
