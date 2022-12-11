package edu.pe.pucp.purpleperu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.pe.pucp.purpleperu.Administrador.listar_organizadores_admin;
import edu.pe.pucp.purpleperu.Beans.Usuario;
import edu.pe.pucp.purpleperu.Cliente.listarEventosCliente;
import edu.pe.pucp.purpleperu.Organizador.listado_eventos_organizador;


public class LoginActivity extends AppCompatActivity {

    //definimos las variables del firebase auth

    FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
    TextInputLayout correo, password;
    Button btnIniciarSesion, btnRegistrarUsuario;


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        correo = findViewById(R.id.correoInput);
        password = findViewById(R.id.passwordInputLayout);
        btnIniciarSesion = findViewById(R.id.btn_restablecer);
        btnRegistrarUsuario = findViewById(R.id.registrarbutton);


        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarEstadoInternet()) {
                    //Iniciar sesion
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
                    boolean correoValido = true;

                    if (correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals("")) {
                        if (!correo.getEditText().getText().toString().matches(emailPattern)) {
                            correo.setError("Ingrese un correo válido");
                            correoValido = false;
                        } else {
                            System.out.println("llego aqui");
                            correo.setErrorEnabled(false);
                        }
                    } else {
                        correo.setError("Ingrese un correo");
                        correoValido = false;
                    }

                    boolean passwordValido = true;
                    if (password.getEditText().getText().toString() != null && !password.getEditText().getText().toString().equals("")) {
                        password.setErrorEnabled(false);
                    } else {
                        password.setError("Ingrese una contraseña");
                        passwordValido = false;
                    }

                    if (correoValido && passwordValido) {
                        firebaseAuth.signInWithEmailAndPassword(correo.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("task", "EXITO EN REGISTRO");

                                    firebaseAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                                Log.d("correo", correo.getEditText().getText().toString());
                                                //Verificamos que rol posee el usuario
                                                databaseReference.child("usuario").orderByChild("correo").equalTo(correo.getEditText().getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.getValue() != null){
                                                            System.out.println(snapshot.getChildrenCount());
                                                            int i=0;
                                                            for (DataSnapshot children : snapshot.getChildren()){
                                                                i++;
                                                                System.out.println(i);
                                                                //Usuario usuario = children.getValue(Usuario.class);
                                                                Usuario usuario = new Usuario();
                                                                String correo_usu=children.child("correo").getValue(String.class);
                                                                String rol_usu=children.child("rol").getValue(String.class);
                                                                String codigo_usu=children.child("codigo").getValue(String.class);
                                                                String key_usu=children.child("key").getValue(String.class);
                                                                usuario.setCodigo(codigo_usu);
                                                                usuario.setKey(key_usu);
                                                                usuario.setRol(rol_usu);
                                                                usuario.setCorreo(correo_usu);



                                                                System.out.println("-----AQUI UNA PRUEBA-------");

                                                                System.out.println("EL VALOR DEL USUARIO ES: " + usuario);


                                                                System.out.println("EL VALOR DEL USUARIO ES: " + correo.getEditText().getText().toString());
                                                                System.out.println("EL VALOR DEL USUARIO ES: " + password.getEditText().getText().toString());
                                                                System.out.println("EL VALOR DEL USUARIO ES: " + firebaseAuth.getCurrentUser().getEmail());


                                                                //La parte comentada deberia salir
                                                                System.out.println(usuario.getCorreo());
                                                                System.out.println(usuario.getRol());


                                                                if (usuario.getCorreo() != null && usuario.getCorreo().equals(correo.getEditText().getText().toString())){

                                                                    if(usuario.getRol().equals("administrador")) {
                                                                        Intent intent = new Intent(LoginActivity.this, listar_organizadores_admin.class);
                                                                        intent.putExtra("key",usuario.getKey());
                                                                        startActivity(intent);
                                                                    }
                                                                    if(usuario.getRol().equals("organizador")) {
                                                                        Intent intent1 = new Intent(LoginActivity.this, listado_eventos_organizador.class);
                                                                        intent1.putExtra("key",usuario.getKey());
                                                                        startActivity(intent1);
                                                                    }

                                                                    if(usuario.getRol().equals("cliente")) {

                                                                        Intent intent2 = new Intent(LoginActivity.this, listarEventosCliente.class);
                                                                        intent2.putExtra("key",usuario.getKey());
                                                                        startActivity(intent2);
                                                                    }
                                                                }

                                                                //break;
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            } else {
                                                Snackbar.make(findViewById(R.id.constrain_sesion), "Su cuenta no ha sido verificada. Verifíquela para poder ingresar", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                } else {
                                    Log.d("task", "ERROR EN REGISTRO - " + task.getException().getMessage());
                                    Snackbar.make(findViewById(R.id.constrain_sesion), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                } else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoginActivity.this);
                    builder.setMessage("Verifique su conexión a internet para poder ingresar a la aplicación");
                    builder.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                }
            }
        });
        btnRegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarEstadoInternet()) {
                    //registrar Usuario
                    Intent intent = new Intent(LoginActivity.this,SignIn.class);
                    startActivity(intent);
                } else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoginActivity.this);
                    builder.setMessage("Verifique su conexión a internet para poder ingresar a la aplicación");
                    builder.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    public boolean verificarEstadoInternet(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void abrirSignIn(View view) {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    public void abrirForgetPassword(View view) {
        Intent intent = new Intent(this, ForgetPassword.class);
        startActivity(intent);
    }



}