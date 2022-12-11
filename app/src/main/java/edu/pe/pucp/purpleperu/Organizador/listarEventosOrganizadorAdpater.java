package edu.pe.pucp.purpleperu.Organizador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.pe.pucp.purpleperu.Beans.Evento;
import edu.pe.pucp.purpleperu.R;

public class listarEventosOrganizadorAdpater extends RecyclerView.Adapter<listarEventosOrganizadorAdpater.myViewHolder> {
    Context context;
    ArrayList<Evento> list;
    FirebaseDatabase firebaseDatabase;



    public listarEventosOrganizadorAdpater(Context context, ArrayList<Evento> list) {
        this.context = context;
        this.list = list;

    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        Evento evento;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_listar_eventos_organizador, parent, false);
        return new myViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref1  = firebaseDatabase.getReference("organizador").child("listaEventos");

        Evento e= list.get(position);
        holder.evento = e;
        TextView nombre = holder.itemView.findViewById(R.id.nombreEventoOrganizador2);
        TextView cantidad = holder.itemView.findViewById(R.id.cantidadInvitados);
        ImageView img = holder.itemView.findViewById(R.id.imageView4);

        nombre.setText(e.getNombre());
        cantidad.setText(e.getCantidadInvitados());

        String url = e.getUrl();

        Glide.with(holder.itemView.getContext()).load(url).into(img);
        String id = e.getId();

        Button btn_editar = holder.itemView.findViewById(R.id.edita_btn_evento);
        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), editar_evento_organizador.class);
                intent.putExtra("idEquipo2", id);
                holder.itemView.getContext().startActivity(intent);
                System.out.println(list.get((position)));
            }
        });

        Button btn_eliminar = holder.itemView.findViewById(R.id.btn_eliminar_equipo);
        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(nombre.getContext());
                builder.setTitle("¿Estas seguro?");
                builder.setMessage("No puedes deshacer esta accion");

                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ref1.child(id).removeValue();
                        Intent intent2 = new Intent(holder.itemView.getContext(), listado_eventos_organizador.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        holder.itemView.getContext().startActivity(intent2);

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(nombre.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
