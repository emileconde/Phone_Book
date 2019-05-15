package com.example.android.conde.com.contacts.db.entity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ContactDAO {

    @Insert
    long addContact(Contact contact);

    @Update
    void updateContact(Contact contact);

    @Delete
    void deleteContact(Contact contact);

    @Query("delete from contacts where contact_id =:id")
    int deleteContactById(long id);

    @Query("select * from contacts")
    List<Contact> getAllContacts();

    @Query("select * from contacts where contact_id =:contactID")
    Contact getContact(long contactID);


}
