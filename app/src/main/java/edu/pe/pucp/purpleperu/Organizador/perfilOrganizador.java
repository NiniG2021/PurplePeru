package edu.pe.pucp.purpleperu.Organizador;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

import edu.pe.pucp.purpleperu.Beans.Usuario;
import edu.pe.pucp.purpleperu.LoginActivity;
import edu.pe.pucp.purpleperu.R;

public class perfilOrganizador extends AppCompatActivity {

    ImageView imageView;
    TextView uid, tv_nombre_edit, tv_correo_edit, tv_codigo_edit;
    Button btn_editarfoto, btn_eliminar;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference ref;

    ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    StorageReference storageReference;
    String id;

    private static final String TAG = "PerfilUsuarioTI";

    BottomNavigationView bottomNavigationView;
    private static final int GALLERY_INTENT = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_organizador_evento);
        setBottomNavigationView();

        imageView = findViewById(R.id.imageView444);
        tv_correo_edit = findViewById(R.id.tv_correo_edit);
        tv_codigo_edit = findViewById(R.id.tv_codigo_edit);

        btn_editarfoto = findViewById(R.id.btn_editarfoto);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        id =  getIntent().getStringExtra("key2");


        ref = firebaseDatabase.getReference().child("usuario");


        System.out.println(user.getUid());
        // OBTENER DATOS DEL USUARIO
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // SI EL USUARIO EXISTE
                for(DataSnapshot usuarios: snapshot.getChildren()){

                    Usuario usuario = usuarios.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                    System.out.println(id);

                    for(Usuario usuario1 : listaUsuarios){
                        if(Objects.equals(usuario1.getKey(), id)){
                            System.out.println("entra quiiiiiii");
                            System.out.println(id);
                            String codigo = usuario1.getCodigo();
                            String correo = usuario1.getCorreo();
                            String url = usuario1.getUrl();
                            tv_correo_edit.setText(correo);
                            tv_codigo_edit.setText(codigo);
                            Glide.with(perfilOrganizador.this).load(url).into(imageView);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        btn_editarfoto = findViewById(R.id.btn_editarfoto);


        btn_editarfoto.setOnClickListener(view -> {
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


            StorageReference filepath = storageReference.child("perfil").child(uri.getLastPathSegment());
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
                                DatabaseReference perfil = ref.child("usuario").child(id);

                                String download_uri=uri.toString();
                                HashMap usuario = new HashMap();
                                usuario.put("url", download_uri);
                                Glide.with(perfilOrganizador.this).load(download_uri).into(imageView);
                                perfil.updateChildren(usuario).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(perfilOrganizador.this, "Se subio correctamente la foto", Toast.LENGTH_SHORT).show();
                                    }
                                });




                            }
                        });
                    }
                }
            });
        }

    }


    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavigationUsuario);
        bottomNavigationView.clearAnimation();
        bottomNavigationView.setSelectedItemId(R.id.perfil);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.perfil:
                        return true;
                    case R.id.listado_eventos:
                        startActivity(new Intent(perfilOrganizador.this, listado_eventos_organizador.class));
                        overridePendingTransition(0,0);
                        return true;
                    }
                return false;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_cerrar,menu);
        return true;

    }


    public void accionCerrarSesionUsuarios(MenuItem item){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(perfilOrganizador.this, LoginActivity.class));



    }
}