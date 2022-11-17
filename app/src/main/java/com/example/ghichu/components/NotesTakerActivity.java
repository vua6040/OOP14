package com.example.ghichu.components;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import com.example.ghichu.CameraPicture;
import com.example.ghichu.MainActivity;
import com.example.ghichu.api.ApiService;
import com.example.ghichu.models.NoteModel;
import com.example.ghichu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotesTakerActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int MY_REQUEST_CODE = 10;
    private static Boolean isImgFCamera=false;

    public static final String TAG=Manifest.class.getName();
    private TextView textView_select,textView_take_photo;
    private CardView cardView,model_card,cardView_reminder;
    private EditText editText_title,editText_notes;
    private ImageView imageView_back,imageView_img;
    private  Uri sImage;
    private ImageButton imageView_pin,imageView_timer,imageView_save;
    private LinearLayout layout_body, reminder_container;
    private TextView tvDate, reminder_time;
    private TextView tvTime;
    private TextView btnSave;
    private TextView btnCancel;
    private NotificationManager notificationManager;
    private int selectedMonth, selectedYear, selectedDay = 0;
    private boolean isSelectTime = false;
    private boolean isSelectDate = false;
    private Calendar todayCalender;
    private Calendar selectedDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
<<<<<<< Updated upstream
    public static final String APP_NAME = "Note App";
=======
    public static String APP_NAME = "Note App";

>>>>>>> Stashed changes
    FirebaseStorage storage;
    NoteModel noteModel;

    private boolean isOldNote=false;
    private static boolean pinned=false;

    private ProgressDialog mProgressDialog;

    ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),(result)->{
                Log.e(TAG,"onActivityResult");
                if(result.getResultCode()==Activity.RESULT_OK){
                    Intent data= result.getData();

                    if(data==null){
                        return;
                    }

                    Uri uri=data.getData();
                    System.out.println(uri);
                    sImage = uri;
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        imageView_img.setImageBitmap(bitmap);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }

            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        // init Reminder
        init();
        //save data to store
        DataLocalManager.init(getApplicationContext());

        imageView_save = findViewById(R.id.imageView_save);
        editText_notes = findViewById(R.id.editText_notes);
        editText_title = findViewById(R.id.editText_title);
        imageView_back = findViewById(R.id.imageView_back);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        layout_body = findViewById(R.id.layout_body);
        imageView_timer=findViewById(R.id.imageView_timer);
        cardView_reminder=findViewById(R.id.cardView_reminder);

        cardView = findViewById(R.id.cardView);
        model_card = findViewById(R.id.model_card);
        textView_select = findViewById(R.id.textView_select);
        textView_take_photo = findViewById(R.id.textView_take_photo);

        imageView_img = findViewById(R.id.imageView_img);
        imageView_pin = findViewById(R.id.imageView_pin);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait ...");

        //FIREBASE
        storage = FirebaseStorage.getInstance();


        //EDITTING NOTE OLD
        noteModel = new NoteModel();
        try {
            int idNote = (int) getIntent().getSerializableExtra("noteOld");
                ApiService.apiService.getNote(idNote).enqueue(new Callback<NoteModel>() {
                    @Override
                    public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                        noteModel = response.body();
                        editText_title.setText(noteModel.getTitle());
                        editText_notes.setText(noteModel.getNotes());
                        if(!noteModel.getImg().isEmpty()){
                            StorageReference storageReference = storage.getReferenceFromUrl("gs://ghi-chu-8944e.appspot.com/images/").child(noteModel.getImg());
                            try {
                                final File file = File.createTempFile("image","jpg");
                                storageReference.getFile(file).addOnSuccessListener(taskSnapshot -> {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    imageView_img.setImageBitmap(bitmap);
                                }).addOnFailureListener(e -> Toast.makeText(NotesTakerActivity.this,"Image faild to load",Toast.LENGTH_LONG).show());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NoteModel> call, Throwable t) {
                        Toast.makeText(NotesTakerActivity.this, t.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            isOldNote=true;
        }catch (Exception ex){
            Log.e("Note Old","not selected yet");
        }

        //CLICK PINNED
        imageView_pin.setOnClickListener(view -> {
            if(isOldNote){
                pinned=noteModel.getPinned();
            }
            if(pinned){
                pinned = false;
                Toast.makeText(NotesTakerActivity.this,"Cancel Pinned",Toast.LENGTH_LONG).show();
            }else{
                pinned = true;
                Toast.makeText(NotesTakerActivity.this,"Pinned",Toast.LENGTH_LONG).show();
            }
        });

        //SAVE DATA
        imageView_save.setOnClickListener(view -> {
            mProgressDialog.show();
            String title = editText_title.getText().toString().trim();
            String description = editText_notes.getText().toString().trim();

            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
            Date date = new Date();

            if(!isOldNote) noteModel = new NoteModel();

            //handle image save in firebase
            if(sImage!=null){
                String rId=UUID.randomUUID().toString();
                StorageReference reference = storage.getReference().child("images/"+rId );
                reference.putFile(sImage).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("upload","success");
                    }else{
                        Log.e("upload","fail");
                    }
                });
                noteModel.setImg(rId);
            }

            if(pinned) noteModel.setPinned(true);

            noteModel.setTitle(title);
            noteModel.setTimeCreate(formatter.format(date));
            noteModel.setNotes(description);
            noteModel.setUserId(DataLocalManager.getFirstUser());

            showNotification();
            //CALL API ADD NOTE
            if(description.isEmpty()&&title.isEmpty()&&(sImage==null || String.valueOf(sImage).isEmpty())){
                Log.e("save","Error");
                mProgressDialog.dismiss();
                Toast.makeText(NotesTakerActivity.this,"Please add some notes!",Toast.LENGTH_LONG).show();
            }else{
                ApiService.apiService.addNote(noteModel).enqueue(new Callback<NoteModel>() {
                    @Override
                    public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                        mProgressDialog.dismiss();
                        if(isImgFCamera){
                            Intent i =new Intent(NotesTakerActivity.this,MainActivity.class);
                            startActivity(i);
                        }
                        Intent intent = new Intent();
                        String jsonString="";

                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        jsonString = gson.toJson(response.body());
                        intent.putExtra("newNote",jsonString);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<NoteModel> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Log.e("picture",t.getMessage());
                        Toast.makeText(NotesTakerActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }

            //TAKE A PICTURE
            textView_take_photo.setOnClickListener(view1 -> {
                Intent take = new Intent(NotesTakerActivity.this, CameraPicture.class);
                startActivity(take);
            });

        });

        //IMAGE FROM CAMERA PICTURE
        Intent tentFrom = getIntent();
        String urlStringFCamera = tentFrom.getStringExtra("takePicture");
        if(urlStringFCamera!=null){
            Uri pathFCamera = Uri.parse(urlStringFCamera);
            sImage = pathFCamera;
            isImgFCamera = true;
            imageView_img.setImageURI(pathFCamera);
        }

        //switch bg
        boolean isSwitch = DataLocalManager.getFirstInstalled();
        if(isSwitch){
            editText_title.setTextColor(getResources().getColor(R.color.white));
            editText_notes.setTextColor(getResources().getColor(R.color.white));
            layout_body.setBackgroundColor(getResources().getColor(R.color.black));
        }else{
            editText_title.setTextColor(getResources().getColor(R.color.black));
            editText_notes.setTextColor(getResources().getColor(R.color.black));
            layout_body.setBackgroundColor(getResources().getColor(R.color.white));
        }

        //BACK HOME
        imageView_back.setOnClickListener(view -> {
            Intent intent = new Intent(NotesTakerActivity.this,MainActivity.class);
            startActivity(intent);
        });

        //SELECT IMAGE
        textView_select.setOnClickListener(view -> onClickRequestPermission());

        textView_take_photo.setOnClickListener(this::onClick);

        if(cardView_reminder.isFocused()){
            cardView_reminder.setVisibility(View.INVISIBLE);
        }

    }

    private void onClickRequestPermission() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
            cardView.setVisibility(View.INVISIBLE);
            model_card.setTranslationY(2000);
            openGallery();
            return;
        }

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            cardView.setVisibility(View.INVISIBLE);
            model_card.setTranslationY(2000);
            openGallery();
        }else{
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission,MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_REQUEST_CODE){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }

    public void adds(View view){
        cardView.setVisibility(View.VISIBLE);
        model_card.animate().translationY(1660).setDuration(500).setStartDelay(500);
    }

    //Cancel Edit calender
//    public void btnCancel(View view){
//        cardView_reminder.setVisibility(View.INVISIBLE);
//    }

    public void picture(View view){
        int visible=cardView.getVisibility();
        if(visible==4)
            cardView.setVisibility(View.VISIBLE);
        else {
            cardView.setVisibility(View.INVISIBLE);
            model_card.setTranslationY(2000);
        }
    }

    public void editReminder(View view){
        int visible=cardView_reminder.getVisibility();
        if(visible==4)
            cardView_reminder.setVisibility(View.VISIBLE);
        else
            cardView_reminder.setVisibility(View.INVISIBLE);
    }

    public void select_drawing(View view){
        Intent i = new Intent(NotesTakerActivity.this,DrawingActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                cancelNotification();
                cardView_reminder.setVisibility(View.INVISIBLE);
            case R.id.btn_save:
//                showNotification();
                cardView_reminder.setVisibility(View.INVISIBLE);
                break;
            case R.id.textView_take_photo:
                Intent i = new Intent(NotesTakerActivity.this, CameraPicture.class);
                startActivity(i);
                break;
            case R.id.tv_date:

                selectDate();
                break;
            case R.id.tv_time:
                selectTime();
                break;
            default:
                break;
        }
    }

    private void init() {
        //Create Channel
        createNotificationChannel();

        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        //Listeners
        tvDate.setOnClickListener( this :: onClick);
        tvTime.setOnClickListener(this :: onClick);
        btnSave.setOnClickListener(this :: onClick);
        btnCancel.setOnClickListener(this :: onClick);


    }

    private void selectDate() {
        todayCalender = Calendar.getInstance();
        mYear = todayCalender.get(Calendar.YEAR);
        mMonth = todayCalender.get(Calendar.MONTH);
        mDay = todayCalender.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog( this,  this, mYear, mMonth, mDay);
        dialog.show();
    }
    public boolean isValidDate(String d1, String d2)   {
        SimpleDateFormat dfDate  = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            return dfDate.parse(d1).before(dfDate.parse(d2)) || dfDate.parse(d1).equals(dfDate.parse(d2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void selectTime() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, selectedYear);
                        selectedDate.set(Calendar.MONTH, selectedMonth);
                        selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);
                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDate.set(Calendar.MINUTE, minute);

                        Date date2 = new Date(selectedDate.getTimeInMillis());
                        Date current = new Date(c.getTimeInMillis());

                        if (current.after(date2)) {
                            Toast.makeText(NotesTakerActivity.this, "Wrong time selected. Please verify!", Toast.LENGTH_SHORT).show();
                            tvTime.setText("Pick Time");
                            isSelectTime = false;
                        } else {
                            tvTime.setText(String.format("%s:%s", hourOfDay, minute));
                            isSelectTime = true;
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(APP_NAME, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedMonth = month;
        selectedYear = year;
        selectedDay = dayOfMonth;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if(isValidDate(todayCalender.getTime().toString(), cal.getTime().toString())) {
            tvDate.setText(String.format("%s - %s - %s", dayOfMonth, month+1, year));
            isSelectDate = true;
            selectTime();
        } else {
            isSelectDate = false;
            Toast.makeText(this, "Select Valid Date !", Toast.LENGTH_SHORT).show();
        }
    }
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public void scheduleNotification(Context context, long delay, String title, String message) {//delay is after how much time(in millis) from current time you want to schedule the notification

        int randomNotificationId = true ? (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE) : 0;

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, randomNotificationId);
        notificationIntent.putExtra(MyNotificationPublisher.KEY_EXPAND, true);
        notificationIntent.putExtra(MyNotificationPublisher.KEY_MULTIPLE, true);
        notificationIntent.putExtra(MyNotificationPublisher.KEY_SOUND, true);
        notificationIntent.putExtra(MyNotificationPublisher.KEY_MESSAGE, message);
        notificationIntent.putExtra(MyNotificationPublisher.KEY_TITLE, title);

        pendingIntent = PendingIntent.getBroadcast(context, randomNotificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private void showNotification() {
        if (TextUtils.isEmpty(editText_title.getText())) {

            editText_title.setError("Please enter Message!");
            return;
        }
        if (isSelectDate && isSelectTime) {
            long diffInMs = selectedDate.getTimeInMillis() - todayCalender.getTimeInMillis();
            long diffInSec = TimeUnit.MILLISECONDS.toMillis(diffInMs);
            scheduleNotification(this, diffInSec, "Note App", editText_title.getText().toString());
        } else {
            Toast.makeText(this, "Select a valid date and time!", Toast.LENGTH_SHORT).show();
        }

    }

    private void cancelNotification() {
        Intent intent = new Intent(this, MyNotificationPublisher.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        if(alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Cancel Reminder!",Toast.LENGTH_SHORT).show();
    }


}