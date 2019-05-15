package com.example.android.conde.com.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.conde.com.contacts.MainActivity;
import com.example.android.conde.com.contacts.R;
import com.example.android.conde.com.contacts.databinding.ContactItemBinding;
import com.example.android.conde.com.contacts.db.entity.Contact;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder> {

    private List<Contact> mContacts;
    private Context mContext;
    private MainActivity mMainActivity;

    public ContactRecyclerViewAdapter(List<Contact> contacts, Context context, MainActivity mainActivity) {
        mContacts = contacts;
        mContext = context;
        mMainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,
//                parent, false);
        ContactItemBinding contactItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.contact_item, parent, false);
        return new ContactViewHolder(contactItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            Contact currentContact = mContacts.get(position);
//            holder.mName.setText(String.format("%s %s", contact.getFirstName(), contact.getLastName()));
//            holder.mEmail.setText(contact.getEmail());
//            holder.mPhoneNumber.setText(String.valueOf(contact.getPhoneNumber()));
            holder.mContactItemBinding.setContact(currentContact);
    }

    @Override
    public int getItemCount(){
        return mContacts.size();
    }


    public Contact getContactAt(int position){
        return mContacts.get(position);
    }

    public void addContact(int index, Contact contact){
        mContacts.add(index, contact);
        notifyDataSetChanged();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
//        TextView mName, mEmail, mPhoneNumber;
//        AppCompatImageButton mEditImageButton;
        ContactItemBinding mContactItemBinding;
        EditButtonClickHandler mEditButtonClickHandler;

        public ContactViewHolder(@NonNull ContactItemBinding contactItemBinding) {
            super(contactItemBinding.getRoot());
//            mName = itemView.findViewById(R.id.tv_contact_name);
//            mEmail = itemView.findViewById(R.id.tv_contact_email);
//            mPhoneNumber = itemView.findViewById(R.id.tv_contact_number);
//            mEditImageButton = itemView.findViewById(R.id.btn_edit);
//            itemView.setOnClickListener(this);
//            mEditImageButton.setOnClickListener(this);
            this.mContactItemBinding = contactItemBinding;
            mEditButtonClickHandler = new EditButtonClickHandler(mContext);
            mContactItemBinding.setEditButtonCLickhandler(mEditButtonClickHandler);
        }

        //Edit button click handler
        public class EditButtonClickHandler{
            Context mContext;

            public EditButtonClickHandler(Context context) {
                mContext = context;
            }

            public void onEditButtonClicked(View view){
                int pos = getAdapterPosition();
                Contact contact = mContacts.get(pos);
                if(view.getId() == R.id.btn_edit)
                mMainActivity.addOrEditContact(MainActivity.ACTION_EDIT,contact, pos);
            }

        }

//        @Override
//        public void onClick(View v) {
//            int pos = getAdapterPosition();
//            Contact contact = mContacts.get(pos);
//            if(v.getId() == R.id.btn_edit)
//                mMainActivity.addOrEditContact(MainActivity.ACTION_EDIT,contact, pos);
//        }
    }

}
