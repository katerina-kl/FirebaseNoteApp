package com.example.yourtaskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourtaskapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    //update

    private EditText titleUp;
    private EditText noteUp;
    private Button btnDeleteUp;
    private Button btnUpdate;


    //variable

    private String title;
    private String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Notes");

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("TaskNote");
        mDatabase.keepSynced(true);

         //Recycler view
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fabBtn=findViewById(R.id.fab_btn);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);

                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                View myView = inflater.inflate(R.layout.custominputfield,null);

                myDialog.setView(myView);

                final AlertDialog dialog =myDialog.create();

                final EditText title = myView.findViewById(R.id.edt_title);
                final EditText note = myView.findViewById(R.id.edt_note);
                Button btnSave = myView.findViewById(R.id.btn_save);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String mTitle = title.getText().toString().trim();
                        String mNote = note.getText().toString().trim();

                        if(TextUtils.isEmpty(mTitle)){
                            title.setError("Required field");
                            return;

                        }
                        if(TextUtils.isEmpty(mNote)){
                            note.setError("Required field");
                            return;
                        }

                        String id= mDatabase.push().getKey();
                        String datee = DateFormat.getDateInstance().format(new Date());
                        Data data = new Data(mTitle,mNote,datee,id);
                        mDatabase.child(id).setValue(data);
                        Toast.makeText(getApplicationContext(), "Data inserted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                myDialog.show();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                ( Data.class, R.layout.item_data,MyViewHolder.class, mDatabase)
        {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, final Data data, final int i) {
                myViewHolder.setTitle(data.getTitle());
                myViewHolder.setNote(data.getNote());
                myViewHolder.setDate(data.getDate());

                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(i).getKey();
                        title=data.getTitle();
                        note=data.getNote();

                        updateData();
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View myView;

        public MyViewHolder(View itemView){

            super(itemView);
            myView=itemView;
        }

        public void setTitle (String title){
            TextView mTitle = myView.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note){
            TextView mNote = myView.findViewById(R.id.note);
            mNote.setText(note);
        }


        public void setDate(String date){

            TextView mDate = myView.findViewById(R.id.date);
            mDate.setText(date);
        }

    }

    public void updateData(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater= LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.updateinputfield,null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();

        titleUp = myview.findViewById(R.id.edt_title_update);
        noteUp = myview.findViewById(R.id.edt_note_update);

        titleUp.setText(title);
        titleUp.setSelection(title.length());

        noteUp.setText(note);
        noteUp.setSelection(note.length());

        btnDeleteUp = myview.findViewById(R.id.btn_delete_update);
        btnUpdate = myview.findViewById(R.id.btn_update);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title= titleUp.getText().toString().trim();
                note= noteUp.getText().toString().trim();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(title,note,mDate,post_key);
                mDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });


        btnDeleteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              mDatabase.child(post_key).removeValue();

                dialog.dismiss();

            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
