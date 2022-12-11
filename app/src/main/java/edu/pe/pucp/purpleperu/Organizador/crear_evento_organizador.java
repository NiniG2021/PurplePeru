package edu.pe.pucp.purpleperu.Organizador;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import edu.pe.pucp.purpleperu.Beans.Evento;
import edu.pe.pucp.purpleperu.R;

public class crear_evento_organizador extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;

    DatabaseReference ref1;

    EditText opcionOtro, descripcionDispositivo, cantidadInivitados, costoText, lugarEditText, nombre_organizado_admin, editTextDate, horaText;
    ArrayList<String> listTipo = new ArrayList<>();

    Spinner spinner;

    Button subirFoto;
    ImageView imageView;

    FirebaseUser user;
    FirebaseAuth firebaseAuth;

    Evento evento = new Evento();

    String id;
    StorageReference storageReference;

    private static final int GALLERY_INTENT = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento_organizador);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        opcionOtro = findViewById(R.id.otroEditarText);
        descripcionDispositivo = findViewById(R.id.descripEditarOrganizador);
        cantidadInivitados = findViewById(R.id.cantidadInivitadosOrganizador);
        costoText = findViewById(R.id.costoEditarOrganizador);
        lugarEditText = findViewById(R.id.lugarEditarOrganizador);
        nombre_organizado_admin = findViewById(R.id.editarNombreEvento);
        lugarEditText = findViewById(R.id.lugarEditarOrganizador);
        editTextDate = findViewById(R.id.editarDateOrganizador);
        horaText = findViewById(R.id.horaEditarOrganizador);


        if(listTipo.isEmpty()){
            listTipo.add("Otro");
            listTipo.add("Cafe");
            listTipo.add("Fiesta");
            listTipo.add("Picnic");
            listTipo.add("Bus");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,listTipo);

        spinner = findViewById(R.id.spinnerEditarOrganizador);
        spinner.setAdapter(adapter);


        storageReference = FirebaseStorage.getInstance().getReference();
        subirFoto = findViewById(R.id.ediatrBtn);
        imageView = findViewById(R.id.imageViewEditar);


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
                                DatabaseReference ref = firebaseDatabase.getReference();
                                DatabaseReference refequipos = ref.child("organizador").child("listaEventos");

                                id = refequipos.push().getKey();

                                String download_uri=uri.toString();

                                Toast.makeText(crear_evento_organizador.this, "Se subio correctamente la foto", Toast.LENGTH_SHORT).show();
                                evento.setUrl(download_uri);

                                refequipos.child(id).setValue(evento).addOnSuccessListener(unused -> {
                                    Glide.with(crear_evento_organizador.this).load(download_uri).into(imageView);
                                });
                            }
                        });
                    }
                }
            });
        }

    }

    public void guardarEquipo(View view) {


        HashMap eventos = new HashMap();
        eventos.put("id", id);
        eventos.put("descripcion", descripcionDispositivo.getText().toString());
        eventos.put("invitados", cantidadInivitados.getText().toString());
        //eventos.put("fechayhora", .getText().toString());
        eventos.put("costo", costoText.getText().toString());
        eventos.put("lugar",  lugarEditText.getText().toString());

        if(spinner.getSelectedItem().toString().equals("Otro")){
            eventos.put("tipo", opcionOtro.getText().toString());
            listTipo.add(opcionOtro.getText().toString());

        }else{
            eventos.put("tipo", spinner.getSelectedItem().toString());
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref1  = firebaseDatabase.getReference().child("organizador").child("listaEventos");

        ref1.child(id).updateChildren(eventos).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(crear_evento_organizador.this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(crear_evento_organizador.this, listar_invitados_organizador.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            }
        });


    /*    if (listaId.isEmpty()){
            listaId.add("0");
        }

        int contador= listaId.size()-1;
        for(int i=contador;i<listaId.size();i++){
            equipo.setId(String.valueOf(i+1));


        }
        listaId.add(String.valueOf(contador+1));

     */


       /* opcionOtro.setText("");
        caracteristicasDispositivo.setText("");
        incluyeDispositivo.setText("");
        stockDispositivo.setText("");
        marcaDispositivo.setText("");

        */
    }


    public void cancelar(View view){
        Intent intent = new Intent(crear_evento_organizador.this, listado_eventos_organizador.class);
        startActivity(intent);
    }
}