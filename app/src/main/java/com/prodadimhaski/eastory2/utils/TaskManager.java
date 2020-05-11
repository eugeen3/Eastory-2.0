package com.prodadimhaski.eastory2.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.MainThread;

import com.prodadimhaski.eastory2.Room.Dao.LanguageDao;
import com.prodadimhaski.eastory2.Room.Dao.QuestionDao;
import com.prodadimhaski.eastory2.Room.Dao.TestDao;
import com.prodadimhaski.eastory2.Room.Database;
import com.prodadimhaski.eastory2.Room.entities.Question;
import com.prodadimhaski.eastory2.interfaces.Language;
import com.prodadimhaski.eastory2.interfaces.TempList;
import com.prodadimhaski.eastory2.interfaces.TypeOfTest;
import com.prodadimhaski.eastory2.serverUtils.NetworkService;
import com.prodadimhaski.eastory2.serverUtils.POJO.TestOTD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Response;

public class TaskManager implements Language, TypeOfTest, TempList {

    private Task[] listTask = new Task[setting.getSizeOfTest()];
    private Context context;
    private Database db;
    private TestDao testDao;
    private QuestionDao questionDao;
    List<TestOTD> questions;

    public Task[] createList() {
        db = Database.getInstance(context);
        testDao = db.testDao();

        List<Question> questions = filterByLanguage(testDao.getTopicWithQuestionsById(setting.getType()));

        int[] position = sampleRandomNumbersWithoutRepetition(0, questions.size(), setting.getSizeOfTest());

        for (int i = 0; i < setting.getSizeOfTest(); i++) {
            listTask[i] = createTask(questions.get(position[i]));
        }
        return listTask;
    }

    public Task[] createListFromServer(int id) throws InterruptedException {

        db = Database.getInstance(context);
        testDao = db.testDao();
        questionDao = db.questionDao();

        Thread request = new Thread(() -> getList(id));
        request.start();
        request.join();

        List<Question> questions = filterByLanguage(testDao.getTopicWithQuestionsById(id));

        listTask = new Task[questions.size()];

        setting.setSizeOfTest(questions.size());
        for (int i = 0; i < setting.getSizeOfTest(); i++) {
            listTask[i] = createTask(questions.get(i));
        }
        return listTask;

    }

    public void getList(int id) {
        Response<List<TestOTD>> response = null;
        try {
            response = NetworkService
                    .getInstance()
                    .getJSONApi()
                    .getTestByID(id)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        questions = response.body();
    }

    public Task[] createMixedList() {
        db = Database.getInstance(context);
        testDao = db.testDao();

        int i = 0;
        for (int period : TYPEOFTTEST_INT) {
            List<Question> questions = filterByLanguage(testDao.getTopicWithQuestionsById(period));

            int[] position = sampleRandomNumbersWithoutRepetition(0, questions.size(), 2);

            for (int j = 0; j < 2; j++) {
                listTask[i] = createTask(questions.get(position[j]));
                i++;
            }
        }
        return listTask;
    }

    private Task createTask(Question question) {
        String text;
        byte[] image;
        String[] answers = new String[4];
        int rightAnswer;
        String textDescription;

        text = question.getQuestion();
        textDescription = question.getDescription();
        answers[0] = question.getAnswer_1();
        answers[1] = question.getAnswer_2();
        answers[2] = question.getAnswer_3();
        answers[3] = question.getAnswer_4();
        rightAnswer = question.getRight_answer();
        image = question.getImage();

        return new Task(answers, rightAnswer, text, textDescription, image);
    }

    public TaskManager(Context context) {
        this.context = context;
    }

    public static int[] sampleRandomNumbersWithoutRepetition(int start, int end, int count) {
        Random rng = new Random();

        int[] result = new int[count];
        int cur = 0;
        int remaining = end - start;
        for (int i = start; i < end && count > 0; i++) {
            double probability = rng.nextDouble();
            if (probability < ((double) count) / (double) remaining) {
                count--;
                result[cur++] = i;
            }
            remaining--;
        }
        return result;
    }

    private List<Question> filterByLanguage(List<Question> allQuestions) {
        LanguageDao languageDao = db.languageDao();
        List<Question> resultList = new ArrayList<>();

        for (Question question : allQuestions) {
            if (languageDao.getLanguage(question.getLanguage_id()).equals(change.getLanguage())) {
                resultList.add(question);
            }
        }

        return resultList;
    }
}