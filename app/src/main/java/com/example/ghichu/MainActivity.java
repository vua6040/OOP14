package com.example.ghichu;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ghichu.adapters.NotesListAdapter;
import com.example.ghichu.components.DrawingActivity;
import com.example.ghichu.components.NotesTakerActivity;
import com.example.ghichu.database.RoomDB;
import com.example.ghichu.models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    SearchView searchView_home;
    Notes selectedNote;
    LottieAnimationView searchView_loader,search_load;
    Timer timer;

    //sidebar
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CardView cardView;
    Toolbar toolbar;

    Button view_list;

    private static Boolean toggle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.header);
        searchView_loader = findViewById(R.id.searchView_loader);

        recyclerView = findViewById(R.id.recycle_home);
        fab_add = findViewById(R.id.fab_add);
        searchView_home = findViewById(R.id.searchView_home);
        search_load = findViewById(R.id.search_load);
        database=RoomDB.getInstance(this);
        cardView=findViewById(R.id.cardView);

        //toggle view
        view_list=findViewById(R.id.view_list);

        notes = database.mainDAO().getAll();

        updateRecycler(notes,2);

        //add note
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
                startActivityIfNeeded(intent,101); //WRITE_PERMISSION
            }
        });

        //Search note
        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return true;
            }

            //services
            private void filter(String s) {
                List<Notes> filteredList = new ArrayList<>();
                for(Notes singleNote:notes){
                    if(singleNote.getTitle().toLowerCase().contains(s.toLowerCase())||singleNote.getNotes().toLowerCase().contains(s.toLowerCase())){
                        filteredList.add(singleNote);
                    }
                }
                notesListAdapter.filteredList(filteredList);
            }
        });

        //siderbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_open,
                R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if(cardView.isFocused()){
            cardView.setVisibility(View.INVISIBLE);
        }
    }

    //Button
    public void drawing(View view){
        Intent clickDraw = new Intent(MainActivity.this, DrawingActivity.class);
        startActivity(clickDraw);
    }

    public void picture(View view){
        int visible=cardView.getVisibility();
        if(visible==4)
            cardView.setVisibility(View.VISIBLE);
        else
            cardView.setVisibility(View.INVISIBLE);
    }



    //toggle view
    public void toggleView(View view){
        if(toggle) {
            view_list.setBackgroundResource(R.drawable.verticals);
            updateRecycler(notes,1);
            toggle=false;
        }else{
            view_list.setBackgroundResource(R.drawable.grid);
            updateRecycler(notes,2);
            toggle=true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            //WRITE_PERMISSION
            if(requestCode==101){
                if(resultCode== Activity.RESULT_OK){
                    Notes new_note = (Notes) data.getSerializableExtra("newNote");
                    database.mainDAO().insertNote(new_note);
                    notes.clear();
                    notes.addAll(database.mainDAO().getAll());
                    notesListAdapter.notifyDataSetChanged();
                }
            }else if(requestCode==102){
                if(resultCode== Activity.RESULT_OK){
                    Notes new_note = (Notes) data.getSerializableExtra("newNote");
                    database.mainDAO().updateNote(new_note.getId(), new_note.getTitle(),new_note.getNotes(),new_note.getImg());
                    notes.clear();
                    notes.addAll(database.mainDAO().getAll());
                    notesListAdapter.notifyDataSetChanged();
                }
            }
    }

    //Grid:split two column
    private void updateRecycler(List<Notes> notes,int numberColumn) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(numberColumn, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this,notes,notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
                Intent intent = new Intent(MainActivity.this,NotesTakerActivity.class);
                intent.putExtra("noteOld",notes);
                startActivityIfNeeded(intent,102); //EDITING
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopup(cardView);
        }

        private void showPopup(CardView cardView) {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this,cardView);
            popupMenu.setOnMenuItemClickListener(MainActivity.this);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.show();
        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.pin:
                if(selectedNote.isPinned()){
                    database.mainDAO().pin(selectedNote.getId(),false);
                    Toast.makeText(MainActivity.this,"Unpinned",Toast.LENGTH_LONG);
                }else{
                    database.mainDAO().pin(selectedNote.getId(),true);
                    Toast.makeText(MainActivity.this,"Pinned",Toast.LENGTH_LONG);
                }
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete:
                notes.remove(selectedNote);
                database.mainDAO().deleteNote(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"Note Deleted",Toast.LENGTH_LONG);
                return true;
            case R.id.clone:
                notes.add(selectedNote);
                database.mainDAO().insertNote(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //animation search loader
        searchView_loader.animate().translationX(-2600).setDuration(800).setStartDelay(1800);
        search_load.animate().scaleY(0).setDuration(500).setStartDelay(1800);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                searchView_home.setQueryHint("Search notes...");
            }
        },3000);
    }
}