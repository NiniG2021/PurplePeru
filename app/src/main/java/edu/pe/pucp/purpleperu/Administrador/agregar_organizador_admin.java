package edu.pe.pucp.purpleperu.Administrador;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.pe.pucp.purpleperu.Beans.Usuario;
import edu.pe.pucp.purpleperu.R;

public class agregar_organizador_admin extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("usuario");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    TextInputLayout correo,codigo,password,confirm;

    Uri imageUri;


    Button btn_agregarTI,btn_foto;



    ActivityResultLauncher<Intent> openImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
        if(result.getResultCode()== Activity.RESULT_OK){
            imageUri=result.getData().getData();
        }
    });


    boolean verifyPasswordValido = true;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_organizador_admin);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();

        Boolean editar = getIntent().hasExtra("id_edit");
        Usuario usuarioEditar = (Usuario) intent.getSerializableExtra("id_edit");


        EditText correo = findViewById(R.id.correo_organizador_admin);
        EditText numero = findViewById(R.id.numero_organizador_admin);
        TextView nombre = findViewById(R.id.editarNombreEvento);

        EditText password = findViewById(R.id.et_contra_add);
        EditText verifyPassword = findViewById(R.id.et_confirm_add);
        Button btn_agregarTI = findViewById(R.id.btn_agregar_usuarioti2);
        Button btn_foto = findViewById(R.id.btn_foto);



        if (editar) {
            correo.setText(usuarioEditar.getCorreo());
            codigo.setText(usuarioEditar.getCodigo());
            titulo.setText("Editar Usuario TI");
            btn_agregarTI.setText("Actualizar");


        }

        btn_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagen();
            }
        });


        btn_agregarTI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = "" ;
                if(editar){



                    if(imageUri!=null){
                        String[] path= imageUri.toString().split("/");
                        filename = path[path.length-1];
                        StorageReference imageReference = storageReference.child("img/"+filename);
                        imageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                                Log.d("msg","Archivo Subido correctamente")).addOnFailureListener(e->Log.d("msg","Error",e.getCause()));
                    }
                    databaseReference.child(usuarioEditar.getKey()).child("correo").setValue(correo.getText().toString());
                    databaseReference.child(usuarioEditar.getKey()).child("codigo").setValue(codigo.getText().toString());
                    databaseReference.child(usuarioEditar.getKey()).child("filename").setValue(imageUri!=null?filename:usuarioEditar.getFilename());

                    Toast.makeText(agregar_usuarioti_admin.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(agregar_usuarioti_admin.this, listadoUsuarioAdmin.class);
                    startActivity(intent);

                }else{
                    //Pasamos a crear
                    if(imageUri!=null){
                        String[] path= imageUri.toString().split("/");
                        filename = path[path.length-1];
                        StorageReference imageReference = storageReference.child("img/"+filename);
                        imageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                                Log.d("msg","Archivo Subido correctamente")).addOnFailureListener(e->Log.d("msg","Error",e.getCause()));

                        boolean correoValido = true;

                        if (correo.getText().toString() != null && !correo.getText().toString().equals("")) {


                            if (correo.getText().toString().trim().matches("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+")) {
                                System.out.println("clogro");

                            } else {
                                System.out.println("correo vacio");
                                System.out.println("el correo es " + correo.getText().toString() + "asi que xd");
                                correo.setError("Ingrese un correo válido");
                                correoValido = false;

                            }
                        } else {
                            correo.setError("Ingrese un correo");
                            correoValido = false;

                        }

                        boolean codigoValido = true;
                        if (codigo.getText().toString() != null && !codigo.getText().toString().trim().equals("")) {

                        } else {
                            codigo.setError("Ingrese el código");
                            codigoValido = false;

                        }
                        boolean passwordValido = true;
                        if (password.getText().toString() != null && !password.getText().toString().trim().equals("")) {

                        } else {
                            password.setError("Ingrese una contraseña");
                            passwordValido = false;

                        }

                        if (verifyPassword.getText().toString() != null && !verifyPassword.getText().toString().trim().equals("")) {


                        } else {
                            verifyPassword.setError("Debe verificar su contraseña");
                            verifyPasswordValido = false;

                        }



                        if (correoValido && passwordValido && verifyPasswordValido && codigoValido) {
                            Log.d("task", "Registro valido");

                            String finalFilename = filename;
                            firebaseAuth.createUserWithEmailAndPassword(correo.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("task", "Registro exitoso");
                                        String key = databaseReference.push().getKey();
                                        databaseReference.child(key).child("correo").setValue(correo.getText().toString().trim());
                                        databaseReference.child(key).child("codigo").setValue(codigo.getText().toString().trim());

                                        databaseReference.child(key).child("filename").setValue(finalFilename);

                                        databaseReference.child(key).child("rol").setValue("usuarioTI");


                                        //Para la creacion de la imagen


                                        String[] path= imageUri.toString().split("/");
                                        String filename = path[path.length-1];
                                        StorageReference imageReference = storageReference.child("img/"+filename);
                                        imageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                                                Log.d("msg","Archivo Subido correctamente")).addOnFailureListener(e->Log.d("msg","Error",e.getCause()));
                                        //Guardo el valor en la base de datos

                                        //databaseReference.child(key).setValue(key);


                                        databaseReference.child(key).child("key").setValue(key);




                                        Intent intent = new Intent(agregar_usuarioti_admin.this, listadoUsuarioAdmin.class);

                                        startActivity(intent);


                                    } else {
                                        Toast.makeText( agregar_usuarioti_admin.this, "Error: Este correo ya esta siendo utilizado", Toast.LENGTH_SHORT).show();
                                        Log.d("task", "Error en el momento de registro - " + task.getException().getMessage());
                                    }
                                }
                            });
                        }

                    }else{
                        Toast.makeText(agregar_usuarioti_admin.this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();

                    }




                }






            }
        });





    }

    public void subirImagen(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        openImageLauncher.launch(intent);
    }



    public void golistaTI(View view) {
        Intent intent = new Intent(this, listar_organizadores_admin.class);
        startActivity(intent);
    }
}