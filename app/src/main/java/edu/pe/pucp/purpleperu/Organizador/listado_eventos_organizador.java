package edu.pe.pucp.purpleperu.Organizador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import edu.pe.pucp.purpleperu.Beans.Evento;
import edu.pe.pucp.purpleperu.LoginActivity;
import edu.pe.pucp.purpleperu.R;

public class listado_eventos_organizador extends AppCompatActivity {

    RecyclerView recyclerView;
    listarEventosOrganizadorAdpater adapter;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Evento> listaEventos;
    SearchView searchView;
    DatabaseReference ref1;
    FirebaseAuth mAuth;
    String id;
    FirebaseAuth firebaseAuth;

    BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_eventos_organizador);
        setBottomNavigationView();

        id =  getIntent().getStringExtra("key");

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Button btnAgregar = (Button) findViewById(R.id.agregar_evento_btn_organizador);
        searchView = findViewById(R.id.searchView1);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        listaEventos = new ArrayList<>();

        btnAgregar.setOnClickListener(view -> {
            Intent intent = new Intent(listado_eventos_organizador.this, crear_evento_organizador.class);
            startActivity(intent);
        });




        ref1  = firebaseDatabase.getReference("organizador").child("listaEventos");

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot snapshot1: snapshot.getChildren()){

                    Evento evento = snapshot1.getValue(Evento.class);
                    listaEventos.add(evento);


                }
                recyclerView = findViewById(R.id.recyclerEventoListado);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(listado_eventos_organizador.this));
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new listarEventosOrganizadorAdpater(this,listaEventos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ref1 != null){
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        listaEventos = new ArrayList<>();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            listaEventos.add(ds.getValue(Evento.class));
                        }
                        adapter = new listarEventosOrganizadorAdpater(listado_eventos_organizador.this, listaEventos);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(listado_eventos_organizador.this, error.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search (s);
                    return true;
                }
            });
        }
    }

    private void search(String str){
        ArrayList<Evento> mylist = new ArrayList<>();
        for (Evento object : listaEventos){
            if(object.getTipo().toLowerCase(Locale.ROOT).contains(str.toLowerCase())){
                mylist.add(object);
            }
        }
        listarEventosOrganizadorAdpater listadoEquiposUsuarioAdapter = new listarEventosOrganizadorAdpater(listado_eventos_organizador.this,mylist);
        recyclerView.setAdapter(listadoEquiposUsuarioAdapter);
    }


    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavigationUsuario);
        bottomNavigationView.clearAnimation();
        bottomNavigationView.setSelectedItemId(R.id.listado_eventos);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.listado_eventos:
                        return true;

                    case R.id.perfil:
                        Intent intent1 = new Intent(listado_eventos_organizador.this, perfilOrganizador.class);
                        intent1.putExtra("key2",id);
                        startActivity(intent1);
                }
                return false;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_organizador,menu);
        return true;

    }


    public void accionCerrarSesionUsuarios(MenuItem item){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(listado_eventos_organizador.this, LoginActivity.class));



    }
}