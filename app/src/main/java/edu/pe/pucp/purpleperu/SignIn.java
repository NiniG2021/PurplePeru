package edu.pe.pucp.purpleperu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("usuario");

    boolean correoValido = true;
    boolean passwordValido = true;
    boolean codigoValido = true;
    boolean verifyPasswordValido = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setTitle("Registro de usuario");
        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void validarRegistro(View view) {

        TextInputLayout correo = findViewById(R.id.correoInputLayout);
        TextInputLayout password = findViewById(R.id.passwordInputLayout);
        TextInputLayout verifyPassword = findViewById(R.id.confirmpasswordInputLayout);
        TextInputLayout codigo = findViewById(R.id.codigoInputLayout);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";

        System.out.println("------AQUI EMPIEZA LA PRUEBA--------");
        System.out.println("tenemos el codigo de la vista : " + codigo.getEditText().getText().toString());

        boolean correoValido = true;
        if (correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals("")) {
            if (!correo.getEditText().getText().toString().matches(emailPattern)) {
                correo.setError("Ingrese un correo válido");
                correoValido = false;
            } else {
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

        boolean codigoValido = true;
        if (codigo.getEditText().getText().toString() != null && !codigo.getEditText().getText().toString().equals("")) {
            codigo.setErrorEnabled(false);
        } else {
            codigo.setError("Ingrese una contraseña");
            codigoValido = false;
        }




        if (verifyPassword.getEditText().getText().toString() != null && !verifyPassword.getEditText().getText().toString().equals("")) {

            verifyPassword.setErrorEnabled(false);
        } else {
            verifyPassword.setError("Debe verificar su contraseña");
            verifyPasswordValido = false;
        }

        System.out.println(verifyPassword.getEditText().getText().toString() );
        System.out.println(password.getEditText().getText().toString()  );


        boolean repeValido = true;
        if(verifyPassword.getEditText().getText().toString().equals(password.getEditText().getText().toString())){
            verifyPassword.setErrorEnabled(false);

        }else {
            verifyPassword.setError("Debe verificar que las contraseñas sean iguales");
            repeValido = false;

        }


        if (correoValido && passwordValido && verifyPasswordValido && repeValido && codigoValido) {
            Log.d("task", "Registro valido");

            firebaseAuth.createUserWithEmailAndPassword(correo.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("task", "Registro exitoso");
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("task", "Envio de correo de verifiacion exitoso");
                                String key = databaseReference.push().getKey();
                                databaseReference.child(key).child("correo").setValue(correo.getEditText().getText().toString());
                                databaseReference.child(key).child("rol").setValue("cliente");
                                databaseReference.child(key).child("codigo").setValue(codigo.getEditText().getText().toString());
                                databaseReference.child(key).child("key").setValue(key);
                                Intent intent = new Intent(SignIn.this, LoginActivity.class);
                                intent.putExtra("exito", "Revise su correo para la verificacion de su cuenta");
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("task", "Error al envio de correo de verificacion - " + e.getMessage());
                            }
                        });

                    } else {
                        Toast.makeText( SignIn.this, "Error: Este correo ya esta siendo utilizado", Toast.LENGTH_SHORT).show();
                        Log.d("task", "Error en el momento de registro - " + task.getException().getMessage());
                    }
                }
            });
        }
    }





    public void GoLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}