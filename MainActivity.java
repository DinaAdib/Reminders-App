package com.example.dinaadeb.reminder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.content.Context;
import android.app.Activity;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    private Context context;
    private Activity activity;
    private EditText editNote = null;
    private CheckBox Important = null;
    private PopupWindow mPopupWindow;
    private RemindersDbAdapter DbAdapter;
    private RemindersSimpleCursorAdapter Adapter;
    private Cursor cursor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.TaskList);

        DbAdapter = new RemindersDbAdapter(this);
        DbAdapter.open();

    if (savedInstanceState == null) {
        DbAdapter.deleteAllReminders();
    }
    cursor = DbAdapter.fetchAllReminders();

    Adapter = new RemindersSimpleCursorAdapter(MainActivity.this, R.layout.listview_row, cursor, new String[]{RemindersDbAdapter.COL_CONTENT}, new int[]{R.id.noteTitle}, 0);
    listView.setAdapter(Adapter);

        //if clicked on any reminder
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                showMenu(view, position);

            }
        });
    }
    public void showMenu (final View view, final int position)
    {
        PopupMenu menu = new PopupMenu (this, view);
        menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
        {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onMenuItemClick (MenuItem item)
            {
                int id = item.getItemId(); //option chosen
                switch (id)
                {
                    case R.id.EditReminder:
                        showEditPopup(position, false);
                        break;
                    case R.id.DeleteReminder:
                        deleteReminder(position, view);
                        break;
                }
                return true;
            }
        });
        menu.inflate (R.menu.menu_main);
        menu.show();
    }

    public void showEditPopup(final int position, final boolean newReminder){

        String oldNote = "";
        boolean isChecked = false;

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Edit Reminder");
        dialog.setContentView(R.layout.edit_reminder);

        TextView message = (TextView) dialog.findViewById(R.id.notescontent);
        Important = (CheckBox) dialog.findViewById(R.id.Important);

        if(newReminder){
            message.setText("New Reminder");
            Important.setChecked(false);
        }
        else {

            int listItemId = (int)Adapter.getItemId(position);
            Reminder reminderClicked = DbAdapter.fetchReminderById(listItemId);
            oldNote = reminderClicked.getContent();
            message.setText("Edit Reminder");
            isChecked = reminderClicked.getImportant() == 1 ? true : false;
            Important.setChecked(isChecked);

        }

        editNote = (EditText) dialog.findViewById(R.id.notesinput);
        editNote.setText(oldNote);



        Button commitButton = (Button) dialog.findViewById(R.id.commit);
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int importantInt = (Important.isChecked())? 1 : 0;
                if(newReminder){
                   DbAdapter.createReminder(editNote.getText().toString(), importantInt);
                }
                else{
                    Reminder reminderClicked =DbAdapter.fetchReminderById((int)Adapter.getItemId(position));
                    reminderClicked.setContent(editNote.getText().toString());
                    reminderClicked.setImportant(importantInt);
                    DbAdapter.updateReminder(reminderClicked);
                }
                dialog.dismiss();
                Adapter.changeCursor(DbAdapter.fetchAllReminders());
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @SuppressLint("NewApi")
    public void deleteReminder(final int position, final View view){
        //for removal animation
        DbAdapter.deleteReminderById((int)Adapter.getItemId(position));
        Adapter.changeCursor(DbAdapter.fetchAllReminders());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.NewReminder) {
            showEditPopup(0, true);
        }

        if(id == R.id.Exit){
            finish();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }
}
