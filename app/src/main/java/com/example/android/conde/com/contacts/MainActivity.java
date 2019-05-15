package com.example.android.conde.com.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.conde.com.contacts.adapter.ContactRecyclerViewAdapter;
import com.example.android.conde.com.contacts.databinding.ActivityMainBinding;
import com.example.android.conde.com.contacts.db.entity.Contact;
import com.example.android.conde.com.contacts.db.entity.ContactDB;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {

    List<Contact> mContacts = new ArrayList<>();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    ContactRecyclerViewAdapter mAdapter;
    ActivityMainBinding mActivityMainBinding;
    ClickEventHandler mClickEventHandler;
    //FloatingActionButton mFab_add;
    ContactDB mContactDB;
    public static final int ACTION_ADD = 1;
    public static final int ACTION_EDIT = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mClickEventHandler = new ClickEventHandler(this);
        mActivityMainBinding.setClickEventHandler(mClickEventHandler);
        mContactDB = Room.databaseBuilder(getApplicationContext(), ContactDB.class, "ContactDB")
                .allowMainThreadQueries().build();
        mContacts.addAll(mContactDB.getContactDAO().getAllContacts());
        //mRecyclerView = findViewById(R.id.rv_contacts);
        mRecyclerView = mActivityMainBinding.recyclerContent.rvContacts;
        //mFab_add = findViewById(R.id.fab_add);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ContactRecyclerViewAdapter(mContacts, this, this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        //mFab_add.setOnClickListener(this);

        /**Swipe to delete*/
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                mContactDB.getContactDAO().
                        deleteContactById(mAdapter.getContactAt(position).getId());
                final Contact removedContact = mContacts.remove(position);
                mAdapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar.make( mRecyclerView, R.string.contact_deleted_message, Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        undoDelete(position, removedContact);
                    }
                });
                snackbar.show();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

//    @Override
//    public void onClick(View v) {
//        addOrEditContact(ACTION_ADD, null, -1);
//    }



    public void addOrEditContact(int action, final Contact contact, final int position){
        AlertDialog alertDialog;
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        final TextView tvTitle = view.findViewById(R.id.tv_title);
        final EditText etFirstName = view.findViewById(R.id.et_first_name);
        final EditText etLastName = view.findViewById(R.id.et_last_name);
        final EditText etEmail = view.findViewById(R.id.et_email);
        final EditText etPhoneNumber = view.findViewById(R.id.et_phone_number);


        if(action == ACTION_ADD) {
            builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String firstName = etFirstName.getText().toString();
                    String lastName = etLastName.getText().toString();
                    String email = etEmail.getText().toString();
                    String phoneNumber = etPhoneNumber.getText().toString();

                    long id = mContactDB.getContactDAO().addContact(new Contact(firstName, lastName, email, phoneNumber));
                    Contact newContact = mContactDB.getContactDAO().getContact(id);
                    Toast.makeText(MainActivity.this, "" + id, Toast.LENGTH_SHORT).show();
                    if (newContact != null) {
                        mAdapter.addContact(0, newContact);
                    }
                }
            });
        }else
            if(action == ACTION_EDIT){

                    tvTitle.setText(R.string.edit_contact_title);
                    etFirstName.setText(contact.getFirstName());
                    etLastName.setText(contact.getLastName());
                    etEmail.setText(contact.getEmail());
                    etPhoneNumber.setText(String.valueOf(contact.getPhoneNumber()));

                builder.setPositiveButton("SAVE CHANGES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      updateContact(etFirstName.getText().toString(),
                              etLastName.getText().toString(),
                              etEmail.getText().toString(),
                              etPhoneNumber.getText().toString(), position);
                    }
                });
            }

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }


    public void updateContact(String firstName, String lastName, String email,
                              String phoneNumber, int position){
        Contact contact = mContacts.get(position);
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneNumber(phoneNumber);
        mContacts.set(position, contact);
        mContactDB.getContactDAO().updateContact(contact);
        mAdapter.notifyDataSetChanged();

    }

    public void undoDelete(int position, Contact contact){
        mContacts.add(position, contact);
        mAdapter.notifyDataSetChanged();
        mContactDB.getContactDAO().addContact(contact);
    }

    /**
     * DataBinding clickHandler
     */
    public class ClickEventHandler{
        Context mContext;

        public ClickEventHandler(Context context) {
            mContext = context;
        }

        public void onFabClicked(View view){
            addOrEditContact(ACTION_ADD, null, -1);
        }

    }



}
