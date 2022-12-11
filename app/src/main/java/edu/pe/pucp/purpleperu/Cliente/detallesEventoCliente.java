package edu.pe.pucp.purpleperu.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import edu.pe.pucp.purpleperu.Beans.Evento;
import edu.pe.pucp.purpleperu.LoginActivity;
import edu.pe.pucp.purpleperu.R;

public class detallesEventoCliente extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    BottomNavigationView bottomNavigationView;
    TextView descriText, invitadosText, fechaText, horaText, lugarText, costoText, nombreText;
    ImageView imageView;
    String id, url;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_evento_cliente);

        firebaseDatabase = FirebaseDatabase.getInstance();


        id =  getIntent().getStringExtra("idEquipo8");
        DatabaseReference ref1  = firebaseDatabase.getReference().child("organizador").child("listaEventos");
        nombreText = findViewById(R.id.nombre_editable);
        descriText = findViewById(R.id.text_descrip_edit);
        invitadosText = findViewById(R.id.participante_edit);
        fechaText = findViewById(R.id.fecha_edit);
        horaText = findViewById(R.id.hora_edit);
        lugarText = findViewById(R.id.lugar_text);
        costoText = findViewById(R.id.costo_detalle);
        imageView = findViewById(R.id.imageView444);


        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Evento> listaEventos = new ArrayList<>();
                if (snapshot.exists()) {

                    for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Evento evento = snapshot1.getValue(Evento.class);
                        listaEventos.add(evento);
                    }

                    for(Evento evento1: listaEventos){

                        if(Objects.equals(evento1.getId(), id)){
                            nombreText.setText(evento1.getTipo());
                            invitadosText.setText(evento1.getCantidadInvitados());
                          //  fechaText.setText(evento1.getFechayHora());
                            descriText.setText(evento1.getCaracteristicas());

                          //  fechaText = findViewById(R.id.fecha_edit);
                          //  horaText = findViewById(R.id.hora_edit);
                            lugarText.setText(evento1.getLugar());
                            costoText.setText(evento1.getCosto());
                            url = evento1.getUrl();

                            Glide.with(detallesEventoCliente.this).load(url).into(imageView);

                        }

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public void solicitarEquipo(View view) {

        Intent intent = new Intent(detallesEventoCliente.this,AgregarSolicitudCliente.class);
        startActivity(intent);


    }


    public void cancelar(View view){
        Intent intent = new Intent(detallesEventoCliente.this, listarEventosCliente.class);
        startActivity(intent);
    }


    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_cerrar,menu);
        return true;

    }


    public void accionCerrarSesionUsuarios(MenuItem item){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(detallesEventoCliente.this, LoginActivity.class));



    }
}