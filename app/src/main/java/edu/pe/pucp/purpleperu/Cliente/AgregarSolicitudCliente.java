package edu.pe.pucp.purpleperu.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.pe.pucp.purpleperu.Beans.Evento;
import edu.pe.pucp.purpleperu.Beans.Solicitud;
import edu.pe.pucp.purpleperu.R;

public class AgregarSolicitudCliente extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    Spinner spinner;
    String img;

    ArrayList<Evento> listaEvento;


    EditText nombreText,participanteText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_solicitud_cliene);



        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref1  = firebaseDatabase.getReference("organizador").child("listaEventos");

        nombreText = findViewById(R.id.nombreEvento);
        participanteText = findViewById(R.id.participantes);
        spinner = findViewById(R.id.spinner_tipo);

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> listaTipo = new ArrayList<>();
                listaEvento = new ArrayList<>();
                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String tipoString = snapshot1.child("tipo").getValue(String.class);
                        listaTipo.add(tipoString);

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AgregarSolicitudCliente.this, android.R.layout.simple_spinner_dropdown_item, listaTipo);

                    adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                    spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public void guardarsoli(View view){

        Solicitud solicitud = new Solicitud();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        DatabaseReference refSoli = databaseReference.child("solicitudes");

        String id = refSoli.push().getKey();

        solicitud.setId(id);
        solicitud.setCantidad(participanteText.getText().toString());
        solicitud.setNombre(nombreText.getText().toString());
        solicitud.setTipo(spinner.getSelectedItem().toString());
        solicitud.setEstado("Pendiente");


        refSoli.child(id).setValue(solicitud).addOnSuccessListener(unused -> {
            Toast.makeText(AgregarSolicitudCliente.this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(AgregarSolicitudCliente.this, listarEventosCliente.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        });


        nombreText.setText("");
        participanteText.setText("");

    }

    public void cancelar(View view){
        Intent intent = new Intent(AgregarSolicitudCliente.this, listarEventosCliente.class);
        startActivity(intent);
    }

}