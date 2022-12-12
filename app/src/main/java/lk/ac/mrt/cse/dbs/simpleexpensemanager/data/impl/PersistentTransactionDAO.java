package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    DBHelper dbHelper;
    public PersistentTransactionDAO(DBHelper dbHelper) {
        this.dbHelper=dbHelper;

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String dateString;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateString = dateFormat.format(date);

        ContentValues cv=new ContentValues();
        cv.put("date",dateString);
        cv.put("accountNO",accountNo);
        cv.put("expenseType",expenseType.name());
        cv.put("amount",amount);

        SQLiteDatabase db=dbHelper.getWritableDatabase();

        db.insert("transactions",null,cv);

        db.close();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        List<Transaction> transactionsList=new LinkedList<>();
        Cursor cursor= dbHelper.getAllFromTransactions();

        while(cursor.moveToNext()){
            String dateString=cursor.getString(4);
            Date date = null;
            try {
                date=new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String expenseTypeString=cursor.getString(2);
            ExpenseType et=ExpenseType.valueOf(expenseTypeString);

            Transaction ts=new Transaction(date,cursor.getString(1),et,cursor.getDouble(3));
            transactionsList.add(ts);
        }

        return transactionsList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactionsList=getAllTransactionLogs();

        int size = transactionsList.size();
        if (size <= limit) {
            return transactionsList;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactionsList.subList(size - limit, size);

    }
}
