package com.example.android.conde.com.contacts.db.entity;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactDB extends RoomDatabase {
    public abstract ContactDAO getContactDAO();
}
