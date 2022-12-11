package edu.pe.pucp.purpleperu.Organizador;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import edu.pe.pucp.purpleperu.Beans.Evento;
import edu.pe.pucp.purpleperu.R;

public class editar_evento_organizador extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;

    FirebaseAuth firebaseAuth;
    StorageReference storageReference;

    EditText descripcionText, lugarEditTextOrganizador, costoTextOrganizador, cantidadInivitadosOrganizador, nombreEventoAdmin, otroEditarText;
    String textdescrip, textlugar, textcosto, textInvitados, textnombre, textOtro, textTipo;
    Spinner tipoSpinner;
    String id;
    ArrayList<String> listaTipo;
    ImageView imageViewEditar;
    ArrayList<Evento> equipos;
    Button subirFoto;
    TextView editTextDateOrganizador, horaTextOrganizador;


    HashMap usuario = new HashMap();
    private static final int GALLERY_INTENT = 1;
    String urlString;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_evento_organizador);


        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref1 = firebaseDatabase.getReference("usuarioTI").child("listaEquipos");

        id = getIntent().getStringExtra("idEquipo2");

        nombreEventoAdmin = findViewById(R.id.editarNombreEvento);
        tipoSpinner = findViewById(R.id.spinnerEditarOrganizador);
        otroEditarText = findViewById(R.id.otroEditarText);
        descripcionText = findViewById(R.id.descripEditarOrganizador);
        editTextDateOrganizador = findViewById(R.id.editarDateOrganizador);
        horaTextOrganizador = findViewById(R.id.horaEditarOrganizador);
        lugarEditTextOrganizador = findViewById(R.id.lugarEditarOrganizador);
        costoTextOrganizador = findViewById(R.id.costoEditarOrganizador);
        cantidadInivitadosOrganizador = findViewById(R.id.cantidadInivitadosOrganizador);
        imageViewEditar = findViewById(R.id.imageViewEditar);

        if(listaTipo.isEmpty()){
            listaTipo.add("Otro");
            listaTipo.add("Cafe");
            listaTipo.add("Fiesta");
            listaTipo.add("Picnic");
            listaTipo.add("Bus");
        }


        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists ()) {
                    equipos = new ArrayList<>();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String tipoString = snapshot1.child("tipo").getValue(String.class);
                     /*   if(tipoString.equals("Laptop") || tipoString.equals("Celular") || tipoString.equals("Tablet")|| tipoString.equals("Monitor")){
                        }else{
                           listaTipo.add(tipoString);
                        }

                      */


                        Evento evento = snapshot1.getValue(Evento.class);
                        equipos.add(evento);

                        for (Evento evento1 : equipos ){
                            if(Objects.equals(evento1.getId(), id)){
                                urlString = evento1.getUrl();
                                textdescrip = evento1.getCaracteristicas();
                                textlugar = evento1.getLugar();
                                textcosto = evento1.getCosto();
                                textnombre= evento1.getNombre();
                                textInvitados = evento1.getCantidadInvitados();
                                textTipo = evento1.getTipo();

                                //   Glide.with(editar_equipo_usuarioti.this).load(urlString).into(imageViewDispositivoUsuario);
                            }

                        }




                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(editar_evento_organizador.this, android.R.layout.simple_spinner_dropdown_item, listaTipo);

                    adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                    tipoSpinner.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });



        storageReference = FirebaseStorage.getInstance().getReference();
        subirFoto = findViewById(R.id.ediatrBtn);

        subirFoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);

        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode==RESULT_OK){

            Uri uri = data.getData();


            StorageReference filepath = storageReference.child("fotos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    if (uriTask.isSuccessful()){
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download_uri = uri.toString();

                                usuario.put("url", download_uri);

                                firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference ref1 = firebaseDatabase.getReference().child("usuarioTI").child("listaEquipos");
                                ref1.child(id).updateChildren(usuario).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(editar_evento_organizador.this, "Se subio correctamente la foto", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                Glide.with(editar_evento_organizador.this).load(download_uri).into(imageViewEditar);

                            }
                        });
                    }
                }
            });
        }

    }

    public void updatear(View view){


       if(!tipoSpinner.getSelectedItem().toString().isEmpty()){
            usuario.put("tipo",  tipoSpinner.getSelectedItem().toString());
        }else{

            usuario.put("tipo", textTipo);
        }



        if(!descripcionText.getText().toString().isEmpty()){
            usuario.put("descripcion", descripcionText.getText().toString());
        }else{

            usuario.put("descripcion", textdescrip);

        }

        if(!costoTextOrganizador.getText().toString().isEmpty()){
            usuario.put("costo", costoTextOrganizador.getText().toString());
        }else{
            usuario.put("costo", textcosto);

        }
        if(!cantidadInivitadosOrganizador.getText().toString().isEmpty()){
            usuario.put("inivitados", cantidadInivitadosOrganizador.getText().toString());
        }else{
            usuario.put("costo", textInvitados);

        }

        if(!lugarEditTextOrganizador.getText().toString().isEmpty()){
            usuario.put("lugar", lugarEditTextOrganizador.getText().toString());
        }else{
            usuario.put("lugar", textlugar);

        }

        if(!nombreEventoAdmin.getText().toString().isEmpty()){
            usuario.put("nombre", nombreEventoAdmin.getText().toString());
        }else{
            usuario.put("nombre", textnombre);

        }



        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref1  = firebaseDatabase.getReference().child("organizador").child("listaEventos");

        ref1.child(id).updateChildren(usuario).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(editar_evento_organizador.this,"Editado correctamente", Toast.LENGTH_SHORT).show();

                Intent intent2 = new Intent(editar_evento_organizador.this, listado_eventos_organizador.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            }
        });
        Glide.with(editar_evento_organizador.this).load(urlString).into(imageViewEditar);



    }
    public void cancelar(View view){
        Intent intent = new Intent(editar_evento_organizador.this, listado_eventos_organizador.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}