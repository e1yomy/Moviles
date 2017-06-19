package transparencia.itai.com.transparenciadigital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import static transparencia.itai.com.transparenciadigital.Conexion.nombresSO;
import static transparencia.itai.com.transparenciadigital.MisSolicitudes.ActualizarListaPrimaria;
import static transparencia.itai.com.transparenciadigital.MisSolicitudes.lv1;
import static transparencia.itai.com.transparenciadigital.MisSolicitudes.solicitudes;
import static transparencia.itai.com.transparenciadigital.Sesion.LimpiarCampos;
import static transparencia.itai.com.transparenciadigital.Sesion.btnVolverRegistro;
import static transparencia.itai.com.transparenciadigital.Sesion.layoutInicioSesion;
import static transparencia.itai.com.transparenciadigital.Sesion.layoutRegistro1;
import static transparencia.itai.com.transparenciadigital.SujetosObligados.listas;
import static transparencia.itai.com.transparenciadigital.SujetosObligados.txtTituloSO;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Splash.OnFragmentInteractionListener,
        Sesion.OnFragmentInteractionListener,
        NuevaSolicitudAcceso.OnFragmentInteractionListener,
        NuevaSolicitudRecurso.OnFragmentInteractionListener,
        NuevaSolicitudDenuncia.OnFragmentInteractionListener,
        MisSolicitudes.OnFragmentInteractionListener,
        Registro.OnFragmentInteractionListener,
        SujetosObligados.OnFragmentInteractionListener,
        QuienesSomos.OnFragmentInteractionListener,
        Mapa.OnFragmentInteractionListener

{

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
            //super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    static FragmentManager fragmentManager; //Administrador de fragmentos
    static FragmentTransaction fragmentTransaction;
    static boolean sesion=false;
    static Context c; //Variable de Contexto para mostrar Toast
    static Toolbar toolbar;  //Para modificar las opr de titulo
    static DrawerLayout drawer;
    static MenuItem misDatos, cerrarSesion;
    static SharedPreferences preferences;
    static NavigationView navigationView;
    static TextView txtNombreUsuario, txtEmailUsuario,txtNoSolicitudes;
    static Usuario usr;
    static ArrayList<WebView> paginas;
    View v;
    static View header;
    static int pantalla=0;
    AlertDialog.Builder msgAyuda;
    static JSONObject postDataParams = new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ma=MainActivity.this;
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header= navigationView.getHeaderView(0);
        txtNombreUsuario= (TextView)header.findViewById(R.id.txtNombreUsuario);
        txtEmailUsuario= (TextView)header.findViewById(R.id.txtEmailUsuario);
        preferences= getSharedPreferences("preferencias",Context.MODE_PRIVATE);

        try{
            //navigationView.getMenu().getItem(0).setChecked(true);
            c=this;
            msgAyuda= new AlertDialog.Builder(c);
            toolbar.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_principal,new Splash()).commit();
            CargarSujetosObligados();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(preferences.getBoolean("sesion",false))
                    {
                        toolbar.setVisibility(View.VISIBLE);
                        txtNombreUsuario.setText(preferences.getString("headernombreusuario","Nombre"));
                        txtEmailUsuario.setText(preferences.getString("headercorreo","alguien@example.com"));
                        RecuperarDatosDeUsuario();
                        navigationView.getMenu().getItem(0).setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_principal, new MisSolicitudes()).commit();
                        pantalla=1;
                    }
                    else
                    {
                        toolbar.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_principal, new Sesion()).commit();
                        pantalla=9;
                    }
                }
            },2000);

            fragmentManager=getSupportFragmentManager();
        }
        catch (Exception ex)
        {
            Toast.makeText( c, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(!preferences.getBoolean("sesion",false))
        {
            CambiarPantalla(new Sesion());
            pantalla=9;
        }
        else
        {
            if (id == R.id.nav_missolicitudes) {
                //Listado de solicitudes del usuario
                CambiarPantalla(new MisSolicitudes());
                pantalla=1;
                navigationView.getMenu().findItem(id).setChecked(true);
            }else if (id == R.id.nav_sujetosobligados) {
                // Handle the camera action
                CambiarPantalla(new SujetosObligados());
                pantalla=5;
                navigationView.getMenu().findItem(id).setChecked(true);
            }else if (id == R.id.nav_acceso) {
                //Solicitar acceso a informacion
                CambiarPantalla(new NuevaSolicitudAcceso());
                pantalla=2;
                navigationView.getMenu().findItem(id).setChecked(true);

            } else if (id == R.id.nav_denuncia) {
                //Solicitar recurso de revision
                CambiarPantalla(new NuevaSolicitudDenuncia());
                pantalla=4;
                navigationView.getMenu().findItem(id).setChecked(true);
            }
        }
        if(id==R.id.nav_salir) {
            finish();
        }
        else if(id==R.id.nav_quienessomos){
            CambiarPantalla(new QuienesSomos());
            pantalla=6;
            navigationView.setCheckedItem(id);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
        else if(id==R.id.nav_sitioitai){
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://itaibcs.org.mx/")));
            QuitarSeleccionMenu();
        }
        else if(id==R.id.nav_sitiopnt){
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.plataformadetransparencia.org.mx/")));
            QuitarSeleccionMenu();
        }
        else if (id == R.id.nav_mapa) {
            //Mostrar  mapa con direccion y telefono
            CambiarPantalla(new Mapa());
            pantalla=7;
            navigationView.setCheckedItem(id);
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private static void QuitarSeleccionMenu() {
        for(byte i=0;i<navigationView.getMenu().size();i++){
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            MostrarAyuda();
        }
        else if(id==R.id.action_misdatos){
            CambiarPantalla(new Registro());
            pantalla=8;

        } else if(id==R.id.action_cerrarsesion){
            preferences.edit().putBoolean("sesion",false).commit();
            HabilitarMenu(preferences.getBoolean("sesion",false));
            CambiarPantalla(new Sesion());
            pantalla=9;

            txtNombreUsuario.setText("");
            txtEmailUsuario.setText("");
            toolbar.setVisibility(View.GONE);


        }

        return super.onOptionsItemSelected(item);
    }

    private void MostrarAyuda() {
        String s1 = "", s2 = "";
        switch (pantalla) {
            case 1:
                //Mis solicitudes
                s1 = getResources().getString(R.string.missolisitudes);
                s2 = getResources().getString(R.string.ayuda1);
                break;
            case 2:
                //Solicitud de acceso
                s1 = getResources().getString(R.string.solicituddeinformacion);
                s2 = getResources().getString(R.string.ayuda2);
                break;
            case 3:
                //Recurso de revision
                s1 = getResources().getString(R.string.recursoderevision);
                s2 = getResources().getString(R.string.ayuda3);
                break;
            case 4:
                //Denuncia por incumplimiento
                s1 = getResources().getString(R.string.denunciaporincumplimiento);
                s2 = getResources().getString(R.string.ayuda4);
                break;
            case 5:
                //Sujetos obligados
                s1 = getResources().getString(R.string.sujetosobligados);
                s2 = getResources().getString(R.string.ayuda5);
                break;
            case 6:
                //Nosotros
                s1 = getResources().getString(R.string.itai);
                s2 = getResources().getString(R.string.ayuda6);
                break;
            case 7:
                //Encuentranos
                s1 = getResources().getString(R.string.encuentranos);
                s2 = getResources().getString(R.string.ayuda7);
                break;
            case 8:
                //Mis datos//registro
                s1 = getResources().getString(R.string.misdatos);
                s2 = getResources().getString(R.string.ayuda8);
                break;
            case 9:
                //Inicio de sesion
                s1 = getResources().getString(R.string.action_help);
                s2 = getResources().getString(R.string.ayuda9);
                QuitarSeleccionMenu();
                break;

        }
        Mensaje(s1,s2);
    }

    private void Mensaje(String titulo,String mensaje) {
        msgAyuda.setTitle(titulo);
        msgAyuda.setMessage(mensaje);
        msgAyuda.setPositiveButton("Aceptar",null);
        msgAyuda.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        misDatos=menu.getItem(1);
        cerrarSesion=menu.getItem(2);
        HabilitarMenu(preferences.getBoolean("sesion",false));

        return true;
    }

    public static void HabilitarMenu(boolean boo)
    {
        misDatos.setEnabled(boo);
        cerrarSesion.setEnabled(boo);
    }
    static byte ini=0; //Puede sustituirse por un boolean

    //Funcion que se encarga de verificar que la cuenta que se ha ingresado sea valida
    //
    public static void IniciarSesion(final String cuenta, final String contra){

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();
                    ///Borrar el tercer parametro para que vuelva a funcionar como antes
                    if(conexion.IniciarSesion(cuenta,contra)==1) {
                        toolbar.setVisibility(View.VISIBLE);
                        preferences.edit().putBoolean("sesion", true).commit();
                        HabilitarMenu(preferences.getBoolean("sesion", false));
                        navigationView.getMenu().getItem(0).setChecked(true);
                        txtNombreUsuario.setText(preferences.getString("headernombreusuario","Nombre"));
                        txtEmailUsuario.setText(preferences.getString("headercorreo","alguien@example.com"));
                        CambiarPantalla(new MisSolicitudes());
                        pantalla=1;
                    }
                }
                catch (Exception ex)
                {
                    String s= ex.getMessage();
                }
            }
        });
        tr.start();

    }

    public static void Registro( final String correo, final String contrasena, final String nombres, final String paterno, final String materno, final String calle, final String noExterno, final String noInterno, final String entreCalles, final String colonia, final String cp, final String entidadFederativa, final String municipio, final String telefono)
    {
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();
                    ///Borrar el tercer parametro para que vuelva a funcionar como antes
                    if(conexion.RegistrarUsuario( correo, contrasena, nombres, paterno, materno, calle, noExterno, noInterno, entreCalles, colonia, cp, entidadFederativa, municipio, telefono)==1) {
                        btnVolverRegistro.hide();
                        LimpiarCampos();
                        layoutInicioSesion.setVisibility(View.VISIBLE);
                        layoutRegistro1.setVisibility(View.GONE);
                    }
                }
                catch (Exception ex)
                {
                    String s= ex.getMessage();

                }
            }
        });
        tr.start();

    }

    public static String FormatoNombre(String nombre){
        return nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
    }
    public static void RecuperarDatosDeUsuario(){
        usr= new Usuario(
                preferences.getString("idUsuario",""),
                preferences.getString("correo",""),
                preferences.getString("contrasena",""),
                preferences.getString("nombre",""),
                preferences.getString("apellidoPaterno",""),
                preferences.getString("apellidoMaterno",""),
                preferences.getString("calle",""),
                preferences.getString("numeroExterior",""),
                preferences.getString("numeroInterior",""),
                preferences.getString("entreCalles",""),
                preferences.getString("colonia",""),
                preferences.getString("CP",""),
                preferences.getString("entidad",""),
                preferences.getString("municipio",""),
                preferences.getString("telefono","")
        );
    }

    public static void ListaSujetosObligados(final View view){

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();
                    if(conexion.ListarSujetos()==1) {
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, nombresSO);
                        listas.get(0).post(new Runnable() {
                            @Override
                            public void run() {
                                listas.get(0).setAdapter(arrayAdapter);
                            }
                        });
                        if(nombresSO.size()>0) {

                        }
                        else
                        {
                            txtTituloSO.setText("Listado de Sujetos Obligados" +
                                    "\n\n" +
                                    "No se ha encontrado un listado.\n" +
                                    "Revise su conexión e intente nuevamente.");
                        }
                    }
                    else
                        Snack(view,"Ha ocurrido un problema y la lista no ha podido cargarse");
                }
                catch (Exception ex)
                {
                    String s= ex.getMessage();
                    Snack(view,"Ha ocurrido un problema y la lista no ha podido cargarse");
                }
            }
        });
        tr.start();

    }
    public static void CargarSujetosObligados(){

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();
                    if(conexion.ListarSujetos()==1) {            }


                }
                catch (Exception ex)
                {
                    String s= ex.getMessage();
                }
            }
        });
        tr.start();

    }
    public static void ListarSolicitudesDeSujetoObligado(final String id){

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();
                    ///Borrar el tercer parametro para que vuelva a funcionar como antes
                    if(conexion.ListarSolicitudesDeSujetoObligado(id)==1) {

                        listas.get(1).post(new Runnable() {
                            @Override
                            public void run() {
                                if(solicitudes.size()>0) {
                                    AdaptadorLista adaptadorLista = new AdaptadorLista(c, solicitudes);
                                    listas.get(1).setAdapter(adaptadorLista);
                                }
                                else
                                {
                                    txtTituloSO.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtTituloSO.setText("No se han encontrado coincidencias.");
                                        }
                                    });

                                }
                            }
                        });
                    }
                }
                catch (Exception ex)
                {
                    String s= ex.getMessage();
                }
            }
        });
        tr.start();

    }
    static MainActivity ma;
    public static void CargarSolicitud(final View view, final String fecha, final String idUsuario, final String idNofiticaciones, final String idSujeto, final String nombreSujeto, final String descripcion, final String idTipoDeEntrega)
    {

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();

                    ///Borrar el tercer parametro para que vuelva a funcionar como antes
                    if(conexion.CargarSolicitud(fecha,idUsuario,idNofiticaciones,idSujeto,nombreSujeto,descripcion
                    ,idTipoDeEntrega)==1) {
                        ma.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Snack(view,"Solicitud enviada");
                                    CambiarPantalla(new MisSolicitudes());
                                    pantalla=1;
                                }
                                catch (Exception ex)
                                {
                                    String s= ex.getMessage();
                                }
                            }
                        });
                    }
                    else {
                        Snack(view,"Ha ocurrido un problema y la su solicitud no pudo ser enviada");
                    }
                }
                catch (Exception ex)
                {
                    Snack(view,"Ha ocurrido un problema y la su solicitud no pudo ser enviada");
                    String s= ex.getMessage();
                }
            }
        });
        tr.start();

    }
    public static void ListarSolicitudes(final View view)
    {
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();
                    ///Borrar el tercer parametro para que vuelva a funcionar como antes
                    if(conexion.ListarSolicitudes()==1) {
                        lv1.post(new Runnable() {
                            @Override
                            public void run() {
                                ActualizarListaPrimaria();
                            }
                        });
                    }
                    else
                        Snack(view,"Ha ocurrido un problema y la lista no ha podido cargarse");
                }
                catch (Exception ex)
                {
                    Snack(view,"Ha ocurrido un problema y la lista no ha podido cargarse");
                    String s= ex.getMessage();
                }
            }
        });
        tr.start();

    }
    public static void CambiarPantalla(Fragment f)
    {
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.entrada,R.anim.salida);
        fragmentTransaction.replace(R.id.content_principal,f);
        fragmentTransaction.commit();
        QuitarSeleccionMenu();
    }
    public static void CargarRecurso(final View view, final String id, final String s, final String s1, final String s2, final String toString, final String string, final String toString1, final int i, final String fecha)
    {
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();
                    ///Borrar el tercer parametro para que vuelva a funcionar como antes
                    if(conexion.CargarRecurso(id,s,s1,s2,toString,string,toString1,i,fecha)==1) {
                        ma.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Snack(view,"Recurso de revisión enviado");
                                    CambiarPantalla(new MisSolicitudes());
                                    pantalla=1;
                                }
                                catch (Exception ex)
                                {
                                    String s= ex.getMessage();
                                }
                            }
                        });
                    }
                    else
                        Snack(view,"Ha ocurrido un problema y su solicitud no pudo ser enviada");
                }
                catch (Exception ex)
                {
                    Snack(view,"Ha ocurrido un problema y su solicitud no pudo ser enviada");
                    String s= ex.getMessage();
                }
            }
        });
        tr.start();

    }
    public static void Snack(final View view, final String mensaje)
    {
        final Snackbar s=Snackbar.make(view,mensaje+".",Snackbar.LENGTH_INDEFINITE);
        if(mensaje.contains("Solicitud")||mensaje.contains("Recurso") ||mensaje.contains("Denuncia"))
        {
            s.setDuration(2000);
        }
        else
        {
        s.setAction("Ocultar", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.dismiss();
            }
        });
        }
        s.show();
    }
    public static void CargarDemanda(final View view, final String id, final String IdtipoDeEntrega, final String idSujeto, final String nombreSujeto, final String descripcion, final String fecha)
    {
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Conexion conexion = new Conexion();
                    ///Borrar el tercer parametro para que vuelva a funcionar como antes
                    if(conexion.CargarDemanda(id,IdtipoDeEntrega,idSujeto,nombreSujeto,descripcion,fecha)==1) {
                        ma.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Snack(view,"Denuncia por incumplimiento enviada");
                                    CambiarPantalla(new MisSolicitudes());
                                    pantalla=1;
                                }
                                catch (Exception ex)
                                {
                                    String s= ex.getMessage();
                                }
                            }
                        });
                    }
                    else
                        Snack(view,"Ha ocurrido un problema y su solicitud no pudo ser enviada");
                }
                catch (Exception ex)
                {
                    Snack(view,"Ha ocurrido un problema y su solicitud no pudo ser enviada");
                    String s= ex.getMessage();
                }
            }
        });
        tr.start();
    }
}
