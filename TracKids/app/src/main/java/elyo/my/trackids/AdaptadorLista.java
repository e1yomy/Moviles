package elyo.my.trackids;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static elyo.my.trackids.ListaHijos.index;
import static elyo.my.trackids.ListaHijos.listaHijos;
import static elyo.my.trackids.Principal.EliminarHijo;
import static elyo.my.trackids.Principal.PantallaMapa;
import static elyo.my.trackids.Principal.c;
import static elyo.my.trackids.Principal.preferences;

/**
 * Created by elyo_ on 06/05/2017.
 */

public class AdaptadorLista extends BaseAdapter implements Mapa.OnFragmentInteractionListener{
    SharedPreferences p ;
    ArrayList<Hijo> hijos;
    Context context;
    Cursor c1;


    public AdaptadorLista(ArrayList<Hijo> hijos, Context context, BaseDatosHelper baseDatosHelper){
        this.hijos=hijos;
        this.context=context;
        //Cambiar alguien por el preferences (usuarioNombre)
        c1 = baseDatosHelper.selectLugares(preferences.getString("correoUsuario",""));
    }

    @Override
    public int getCount() {
        return hijos.size();
    }

    @Override
    public Object getItem(int position) {
        return hijos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            try {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View renglon = inflater.inflate(R.layout.lista_personalizada_hijos, null);
                TextView txtNombreHijo = (TextView) renglon.findViewById(R.id.txtNombreHijo);
                TextView txtUbicacionHijo = (TextView) renglon.findViewById(R.id.txtUbicacionHijo);
                FloatingActionButton btnLlamar = (FloatingActionButton) renglon.findViewById(R.id.btnLlamar);
                LinearLayout layoutDatos = (LinearLayout) renglon.findViewById(R.id.layoutDatos);
                txtNombreHijo.setText(hijos.get(position).nombre);
                //Verificar distancia con lugares guardados y en caso de no estar dentro de la distancia, mostrar lo siguiente

                txtUbicacionHijo.setText(hijos.get(position).latitud+", "+hijos.get(position).longitud);
                if(c1.moveToFirst())
                {
                    Location l1 = new Location("l1");
                    l1.setLatitude(Double.parseDouble(hijos.get(position).latitud));
                    l1.setLongitude(Double.parseDouble(hijos.get(position).longitud));
                    Location l2 = new Location("l2");
                    do {
                        l2.setLatitude(c1.getDouble(1));
                        l2.setLongitude(c1.getDouble(2));
                        if(l1.distanceTo(l2)<=35)
                        {
                            txtUbicacionHijo.setText(c1.getString(0));
                        }

                    }while (c1.moveToNext());

                }


                Clic(renglon,position);
                Llamar(btnLlamar, position);
                return renglon;
            }
            catch (Exception ex){
                //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return convertView;
    }

    private void Clic(View renglon, final int position) {
        renglon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(c, "asd", Toast.LENGTH_SHORT).show();
                preferences.edit()
                        .putString("idHijo",hijos.get(position).id)
                        .putString("nombre",hijos.get(position).nombre)
                        .putFloat("lat", Float.parseFloat(hijos.get(position).latitud))
                        .putFloat("lon", Float.parseFloat(hijos.get(position).longitud))
                        .commit();
                index=position;
                PantallaMapa();
            }
        });
        renglon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(context, v.toString(), Toast.LENGTH_SHORT).show();
                try{
                    //lista.add("Item " + lista.size() + " long");

                    AlertDialog.Builder alert= new AlertDialog.Builder(c);
                    alert.setTitle("Eliminar conexión");
                    alert.setMessage("Esto evitará que pueda seguir al tanto de los movimientos de "+hijos.get(position).nombre +".");
                    alert.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                EliminarHijo(preferences.getString("idUsuario", ""), listaHijos.get(position).id);
                                //ActualizarLista();
                            }
                            catch (Exception e)
                            {
                                //Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.show();

                }
                catch(Exception ex)
                {

                }
                return true;
            }
        });
    }

    private void Llamar(FloatingActionButton btnLlamar, final int position) {
        try {
            btnLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + hijos.get(position).telefono));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    context.startActivity(intent);

                }
            });

        }
        catch (Exception e)
        {
            String s= e.getMessage();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
