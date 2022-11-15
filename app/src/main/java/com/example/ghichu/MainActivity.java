package com.example.ghichu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ghichu.adapters.NotesListAdapter;
import com.example.ghichu.api.ApiService;
import com.example.ghichu.components.DataLocalManager;
import com.example.ghichu.components.DrawingActivity;
import com.example.ghichu.components.Identify;
import com.example.ghichu.components.NotesTakerActivity;
import com.example.ghichu.components.ReminderFragment;
import com.example.ghichu.components.ReminderViews;
import com.example.ghichu.models.NoteModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private List<NoteModel> notes;
    private SearchView searchView_home;
    private NoteModel selectedNote;
    private LottieAnimationView searchView_loader,search_load;
    private Timer timer;
    private TextView textView_select,textView_takeaphoto;
    private String userId="-1";
    private Integer viewColumn = 2;

    FloatingActionButton fab_add;
    NotesListAdapter notesListAdapter;
    LinearLayout noteEmpty;
    RelativeLayout wrapper;

    //sidebar
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CardView cardView;
    Toolbar toolbar;
    Switch switchBg;
    //    View fragment_container;

    Button view_list;

    private static Boolean isToggle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //save data to store
        DataLocalManager.init(getApplicationContext());

        wrapper = findViewById(R.id.wrapper);
        drawerLayout = findViewById(R.id.drawerlayout);
        searchView_loader = findViewById(R.id.searchView_loader);

        recyclerView = findViewById(R.id.recycle_home);
        noteEmpty = findViewById(R.id.note_empty);
        fab_add = findViewById(R.id.fab_add);
        searchView_home = findViewById(R.id.searchView_home);
        search_load = findViewById(R.id.search_load);
        cardView=findViewById(R.id.cardView);

        textView_select=findViewById(R.id.textView_select);
        textView_takeaphoto=findViewById(R.id.textView_takeaphoto);


        //sidebar
        toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);

        //click sidebar
        navigationView = findViewById(R.id.navigation_view);
        navigationView.bringToFront(); //binding
        navigationView.setNavigationItemSelectedListener(this);

        //siderbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_open,
                R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //DEFAULT SIDEBAR AUTO SELECT NOTE
        navigationView.setCheckedItem(R.id.note_menu);


        //toggle view
        view_list=findViewById(R.id.view_list);
        notes = new ArrayList<>();

        updateRecycler(viewColumn);
        if(notes.size()==0){
            noteEmpty.setVisibility(View.VISIBLE);
        }else{
            noteEmpty.setVisibility(View.INVISIBLE);
        }

        //add note
        fab_add.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            startActivityIfNeeded(intent,101); //WRITE_PERMISSION
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
                List<NoteModel> filteredList = new ArrayList<>();
                for(NoteModel singleNote:notes){
                    if(singleNote.getTitle().toLowerCase().contains(s.toLowerCase())||singleNote.getNotes().toLowerCase().contains(s.toLowerCase())){
                        filteredList.add(singleNote);
                    }
                }
                notesListAdapter.filteredList(filteredList);
            }
        });

        if(cardView.isFocused()){
            cardView.setVisibility(View.INVISIBLE);
        }
        
        //take a photo
        textView_takeaphoto.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this,CameraPicture.class);
            startActivity(i);
        });
        
        //select image
        textView_select.setOnClickListener(view->{
            Intent i = new Intent(MainActivity.this,NotesTakerActivity.class);
            startActivity(i);
        });

        //switch bg
        switchBg = findViewById(R.id.switchBg);
        switchBg.setOnClickListener(view->{
            if(!DataLocalManager.getFirstInstalled()){
                DataLocalManager.setFirstInstalled(true);
                wrapper.setBackgroundColor(getResources().getColor(R.color.black));
                navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                navigationView.setBackgroundColor(getResources().getColor(R.color.black));
                navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }else{
                DataLocalManager.setFirstInstalled(false);
                wrapper.setBackgroundColor(getResources().getColor(R.color.white));
                navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                navigationView.setBackgroundColor(getResources().getColor(R.color.white));
                navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            }
        });
        if(DataLocalManager.getFirstInstalled()){
            switchBg.setChecked(true);
            wrapper.setBackgroundColor(getResources().getColor(R.color.black));
            navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            navigationView.setBackgroundColor(getResources().getColor(R.color.black));
            navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }else{
            switchBg.setChecked(false);
            wrapper.setBackgroundColor(getResources().getColor(R.color.white));
            navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            navigationView.setBackgroundColor(getResources().getColor(R.color.white));
            navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        }

        //CHECK USER IS LOGIN
        String isExistUser = DataLocalManager.getFirstUser();
        userId=isExistUser;
        System.out.println(userId);
        if(isExistUser.length()==0 || isExistUser.isEmpty() || isExistUser.equals("null") || Integer.parseInt(userId)<=0){
            Intent i = new Intent(MainActivity.this,Identify.class);
            startActivity(i);
        }else{
            navigationView.getMenu().getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_baseline_logout_24));
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
        if(isToggle) {
            view_list.setBackgroundResource(R.drawable.verticals);
            viewColumn=1;
            updateRecycler(1);
            isToggle=false;
        }else{
            view_list.setBackgroundResource(R.drawable.grid);
            viewColumn=2;
            updateRecycler(2);
            isToggle=true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);


            //WRITE_PERMISSION
            if(requestCode==101){
                if(resultCode== Activity.RESULT_OK){
                    noteEmpty.setVisibility(View.INVISIBLE);
                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();
                    NoteModel new_note = gson.fromJson((String) data.getSerializableExtra("newNote"),NoteModel.class);
                    notes.add(new_note);
                    Toast.makeText(MainActivity.this,"Add Note Success",Toast.LENGTH_LONG).show();

                    notesListAdapter.notifyDataSetChanged();
                }
            }else if(requestCode==102){
                if(resultCode== Activity.RESULT_OK){
                    NoteModel new_note = (NoteModel) data.getSerializableExtra("newNote");
                    ApiService.apiService.updateNote(new_note).enqueue(new Callback<NoteModel>() {
                        @Override
                        public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                            notes.clear();
                            ApiService.apiService.getAllNotes().enqueue(new Callback<List<NoteModel>>() {
                                @Override
                                public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                                    List<NoteModel> noteOfUser = new ArrayList<>();
                                    noteOfUser.addAll(response.body());
                                    for(NoteModel n:noteOfUser){
                                        if (n.getUserId().equals(String.valueOf(userId))) {
                                            NoteModel note = new NoteModel(n.getId(),n.getTitle(),n.getNotes(),n.getImg(),n.getTimeCreate(),n.getPinned(),n.getUserId(),n.getReminder());
                                            notes.add(note);
                                        }
                                    }

                                    Toast.makeText(MainActivity.this,"Unpinned",Toast.LENGTH_LONG).show();
                                    notesListAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                                    Toast.makeText(MainActivity.this,"Unpinned Fail",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<NoteModel> call, Throwable t) {
                            Toast.makeText(MainActivity.this,"Unpinned Fail",Toast.LENGTH_LONG).show();
                        }
                    });
                    updateRecycler(viewColumn);
                    notesListAdapter.notifyDataSetChanged();
                }
            }
    }

    //Grid:split two column
    private void updateRecycler(int numberColumn) {
        notes.clear();
        ApiService.apiService.getAllNotes().enqueue(new Callback<List<NoteModel>>() {
            @Override
            public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                List<NoteModel> noteOfUser = new ArrayList<>();
                noteOfUser.addAll(response.body());
                for(NoteModel n:noteOfUser){
                    if (n.getUserId().equals(String.valueOf(userId))) {
                        NoteModel note = new NoteModel(n.getId(),n.getTitle(),n.getNotes(),n.getImg(),n.getTimeCreate(),n.getPinned(),n.getUserId(),n.getReminder());
                        notes.add(note);
                    }
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(numberColumn, LinearLayoutManager.VERTICAL));

                if(notes.size()>0){
                    noteEmpty.setVisibility(View.INVISIBLE);
                }
                notesListAdapter = new NotesListAdapter(MainActivity.this,notes,notesClickListener);
                recyclerView.setAdapter(notesListAdapter);
            }
            @Override
            public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                Log.e("getAllNotes",t.getMessage());
                Toast.makeText(MainActivity.this,"Error Get All Notes",Toast.LENGTH_LONG).show();
            }
        });

    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(NoteModel noteModal) {
            Intent intent = new Intent(MainActivity.this,NotesTakerActivity.class);
            intent.putExtra("noteOld", noteModal.getId());
                startActivityIfNeeded(intent,102); //EDITING
        }

        @Override
        public void onLongClick(NoteModel note, CardView cardView) {
            selectedNote = note;
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
                    ApiService.apiService.updatePinned(selectedNote.getId()).enqueue(new Callback<NoteModel>() {
                        @Override
                        public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                            notes.clear();
                            ApiService.apiService.getAllNotes().enqueue(new Callback<List<NoteModel>>() {
                                @Override
                                public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                                    List<NoteModel> noteOfUser = new ArrayList<>();
                                    noteOfUser.addAll(response.body());
                                    for(NoteModel n:noteOfUser){
                                        if (n.getUserId().equals(String.valueOf(userId))) {
                                            NoteModel note = new NoteModel(n.getId(),n.getTitle(),n.getNotes(),n.getImg(),n.getTimeCreate(),n.getPinned(),n.getUserId(),n.getReminder());
                                            notes.add(note);
                                        }
                                    }
                                    if(selectedNote.getPinned()==true){
                                        Toast.makeText(MainActivity.this,"Unpinned",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(MainActivity.this,"Pinned",Toast.LENGTH_LONG).show();
                                    }
                                    updateRecycler(viewColumn);
                                    notesListAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                                    if(selectedNote.getPinned()==true){
                                        Toast.makeText(MainActivity.this,"Unpinned Fail",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(MainActivity.this,"Pinned Fail",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<NoteModel> call, Throwable t) {
                            if(selectedNote.getPinned()==true){
                                Toast.makeText(MainActivity.this,"Unpinned Fail",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(MainActivity.this,"Pinned Fail",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                return true;
            case R.id.delete:
                ApiService.apiService.deleteNote(selectedNote.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        notes.remove(selectedNote);
                        updateRecycler(viewColumn);
                        Toast.makeText(MainActivity.this,"Delete Successful",Toast.LENGTH_LONG).show();
                        notesListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this,"Delete Fail",Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            case R.id.clone:
                ApiService.apiService.addNote(selectedNote).enqueue(new Callback<NoteModel>() {
                    @Override
                    public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                        notes.add(selectedNote);
                        updateRecycler(viewColumn);
                        Toast.makeText(MainActivity.this,"Clone success",Toast.LENGTH_LONG).show();
                        notesListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<NoteModel> call, Throwable t) {
                        Toast.makeText(MainActivity.this,"Clone Fail",Toast.LENGTH_LONG).show();
                    }
                });
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


    //handle click sidebar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.note_menu:
//                fragment_container.setVisibility(View.INVISIBLE);
//                recyclerView.setVisibility(View.VISIBLE);
                navigationView.setCheckedItem(R.id.note_menu);
                break;
            case R.id.reminder_menu:
                Intent i =new Intent(MainActivity.this, NotesTakerActivity.class);
                startActivity(i);
//                fragment_container.setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.INVISIBLE);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ReminderFragment()).commit();
                break;
            case R.id.login_menu:
                Intent in =new Intent(MainActivity.this, Identify.class);
                startActivity(in);
                break;
            }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}