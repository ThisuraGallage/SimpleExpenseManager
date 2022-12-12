package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private Cursor allfromAccounts;
    private Cursor allFromTransactions;

    public void setAllfromAccounts(Cursor allfromAccounts) {
        this.allfromAccounts = allfromAccounts;
    }

    public void setAllFromTransactions(Cursor allFromTransactions) {
        this.allFromTransactions = allFromTransactions;
    }



    public Cursor getAllfromAccounts() {
        SQLiteDatabase db=getWritableDatabase();
        String SqlCmd_selectAccounts="select * from accounts";
        allfromAccounts=db.rawQuery(SqlCmd_selectAccounts,null);
        return allfromAccounts;

    }
    public Cursor getAllFromTransactions() {
        SQLiteDatabase db=getWritableDatabase();
        String SqlCmd_selectTransactions="select * from transactions";
        allFromTransactions=db.rawQuery(SqlCmd_selectTransactions,null);
        return allFromTransactions;
    }


    public DBHelper(@Nullable Context context) {
        super(context,"ExpenseManagerDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_accounts="create table accounts(accountNO TEXT primary key,bankName TEXT, accountHolderName TEXT, balance real)";
        String create_transactions2="create table transactions(transactionNo integer primary key autoincrement,accountNo text,expenseType text,amount real,date date,foreign key(accountNo) references accounts(AccountNo))";
        db.execSQL(create_accounts);
        db.execSQL(create_transactions2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+"transactions");
        sqLiteDatabase.execSQL("drop table if exists "+"accounts");
        onCreate(sqLiteDatabase);


    }
}