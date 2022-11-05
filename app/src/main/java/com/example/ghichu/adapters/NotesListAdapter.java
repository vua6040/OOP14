package com.example.ghichu.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghichu.NotesClickListener;
import com.example.ghichu.R;
import com.example.ghichu.models.NoteModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//handle one by one CARD NOTE
public class NotesListAdapter extends  RecyclerView.Adapter<NotesListAdapter.NotesViewHolder>{

    Context context;
    private List<NoteModel> list;
    NotesClickListener listener;
    FirebaseStorage storage;

    public NotesListAdapter(Context context, List<NoteModel> list, NotesClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }
    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.nodes_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        //FIREBASE
        if(!list.get(position).getImg().isEmpty() && list.get(position).getImg().length()>0){
            storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://ghi-chu-8944e.appspot.com/images/").child(list.get(position).getImg());
            try {
                final File file = File.createTempFile("image","jpg");
                storageReference.getFile(file).addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.imageView_img.setImageBitmap(bitmap);
                }).addOnFailureListener(e -> Log.e("adapter",e.getMessage()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        holder.textView_title.setText(list.get(position).getTitle());
        holder.textView_title.setSelected(true);

        holder.textView_notes.setText(list.get(position).getNotes());

        holder.textView_date.setText(list.get(position).getTimeCreate());
        holder.textView_date.setSelected(true);
        if(list.get(position).getPinned()){
            holder.imageView_pin.setImageResource(R.drawable.pin);
        }else{
            holder.imageView_pin.setImageResource(0);
        }

        int color_code=getRandomColor();
        holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code,null));

        holder.notes_container.setOnClickListener(view -> listener.onClick(list.get(holder.getAdapterPosition())));

        holder.notes_container.setOnLongClickListener(view -> {
            listener.onLongClick(list.get(holder.getAdapterPosition()), holder.notes_container);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //random color
    private int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);

        Random random=new Random();
        int random_color=random.nextInt(colorCode.size());
        return colorCode.get(random_color);
    }

    public void filteredList(List<NoteModel> filteredList){
        list=filteredList;
        notifyDataSetChanged();
     }
    public static class NotesViewHolder extends RecyclerView.ViewHolder{

        CardView notes_container;
        TextView textView_title,textView_notes,textView_date;
        ImageView imageView_pin,imageView_img;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            notes_container = itemView.findViewById(R.id.notes_container);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_notes = itemView.findViewById(R.id.textView_notes);
            imageView_pin = itemView.findViewById(R.id.imageView_pin);
            imageView_img = itemView.findViewById(R.id.imageView_img);
        }
    }
}


