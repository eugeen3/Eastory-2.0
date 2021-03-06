package com.prodadimhaski.eastory2.Eastory2.OldVersion.TaskConstructor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.prodadimhaski.eastory2.Eastory2.OldVersion.DBManager.DatabaseHelper;
import com.prodadimhaski.eastory2.Eastory2.OldVersion.Interfaces.Language;
import com.prodadimhaski.eastory2.Eastory2.OldVersion.Interfaces.TypeOfTest;

import java.sql.SQLException;
import java.util.Random;

public class TaskManager implements Language, TypeOfTest {

    //private List<Task> listTask;
    private Task[] listTask = new Task[SIZE];
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase myDb;
    private Context context;

    public Task[] createList() {
        myDBHelper = new DatabaseHelper(context);
        myDBHelper.create_db();
        try {
            myDb = myDBHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor cursor = myDb.rawQuery("SELECT * FROM " + setting.getPeriod(), null);

        for (int i = 0; i < SIZE; i++) {
            cursor.moveToPosition(i);
            listTask[i] = createTask(cursor);
        }

        cursor.close();
        myDb.close();
        return listTask;
    }

    private Task createTask(Cursor cursor) {
        final Random random = new Random();
        String text = new String();
        byte[] image;
        String[] answers = new String[4];
        int rightAnswer;
        String textDescription = new String();

        //cursor.moveToPosition(random.nextInt(20));

        if (change.getLanguage().equals("by")) {
            text = cursor.getString(2);
            textDescription = cursor.getString(14);
            for (int j = 7, i = 0; j < 11; j++, i++) {
                answers[i] = cursor.getString(j);
            }
        }

        if (change.getLanguage().equals("ru")) {
            text = cursor.getString(1);
            textDescription = cursor.getString(13);
            for (int j = 3, i = 0; j < 7; j++, i++) {
                answers[i] = cursor.getString(j);
            }
        }

        rightAnswer = cursor.getInt(11);
        image = cursor.getBlob(12);

        Task task = new Task(answers, rightAnswer, text, textDescription);
        return task;
    }

    public TaskManager(Context context) {
        this.context = context;
    }
}