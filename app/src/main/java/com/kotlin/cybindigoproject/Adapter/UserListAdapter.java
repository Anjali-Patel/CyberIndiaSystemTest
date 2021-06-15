package com.kotlin.cybindigoproject.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.images.ImageManager;
import com.kotlin.cybindigoproject.Model.UserModel;
import com.kotlin.cybindigoproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.NoteHolder>  {
private List<UserModel> employees;

        Context application;
public UserListAdapter(Context application, List<UserModel> employees) {
        this.employees = employees;
        this.application = application;

        }




@NonNull
@Override
public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
        }
@Override
public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        UserModel employee = employees.get(position);
        holder.name.setText(employee.getName());
        holder.email.setText(employee.getEmail());
    Picasso.with(application).load(employee.getPicture()).into(holder.image);

        }
@Override
public int getItemCount() {
        return employees.size();
        }




class NoteHolder extends RecyclerView.ViewHolder {
    private TextView name,email;
    private ImageView image;

    public NoteHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        email = itemView.findViewById(R.id.email);
        image = itemView.findViewById(R.id.image);

    }
}
}

