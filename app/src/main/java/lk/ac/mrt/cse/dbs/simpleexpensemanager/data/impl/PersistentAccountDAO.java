package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.database.Cursor;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final DBHelper dbHelper;
    public PersistentAccountDAO(DBHelper dbHelper) {
        this.dbHelper=dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNoList=new LinkedList<>();
        Cursor cursor= dbHelper.getAllfromAccounts();
        while(cursor.moveToNext()){
            accountNoList.add(cursor.getString(0));
        }
        return accountNoList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountList=new ArrayList<>();
        Cursor cursor=this.dbHelper.getAllfromAccounts();
        while(cursor.moveToNext()){
            Account ac=new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
            accountList.add(ac);
        }
        cursor.close();
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor=this.dbHelper.getAllfromAccounts();
        Account ac=new Account("",null,null,0);

        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(accountNo)){
                ac=new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
                return ac;
            }
        }


        return ac;

    }

    @Override
    public void addAccount(Account account) {

        ContentValues cv=new ContentValues();
        cv.put("accountNo",account.getAccountNo());
        cv.put("bankName",account.getBankName());
        cv.put("accountHolderName",account.getAccountHolderName());
        cv.put("balance",account.getBalance());
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.insert("accounts",null,cv);
        String SqlCmd_selectAccounts="select * from accounts";
        dbHelper.setAllfromAccounts(db.rawQuery(SqlCmd_selectAccounts,null));
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String sql_command="delete from accounts where accountNo="+"'"+accountNo+"'";
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL(sql_command);
        String SqlCmd_selectAccounts="select * from accounts";
        dbHelper.setAllfromAccounts(db.rawQuery(SqlCmd_selectAccounts,null));
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        Account ac=getAccount(accountNo);
        if (!ac.getAccountNo().equals(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {
            Account acc=getAccount(accountNo);
            double balance=acc.getBalance();
            SQLiteDatabase db=dbHelper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            String where="accountNo="+"'"+accountNo+"'";


            switch (expenseType) {
                case EXPENSE:
                    cv.put("balance",balance-amount);
                    db.update("accounts",cv,where,null);
                    db.close();
                    break;
                case INCOME:
                    cv.put("balance",balance+amount);
                    db.update("accounts",cv,where,null);
                    db.close();
                    break;
            }
            db.close();
        }

    }
}
