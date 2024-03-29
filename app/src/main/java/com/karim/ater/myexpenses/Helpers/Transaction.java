package com.karim.ater.myexpenses.Helpers;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

public class Transaction extends CategoryItem {
    private String transactionDay, transactionTime, transactionDate, transactionNote;
    private int transactionId;

    public Transaction() {
    }


    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(transactionDay);
        out.writeString(transactionTime);
        out.writeString(transactionDate);
        out.writeString(transactionNote);
        out.writeInt(transactionId);
    }

    private Transaction(Parcel in) {
        super(in);
        transactionDay = in.readString();
        transactionTime = in.readString();
        transactionDate = in.readString();
        transactionNote = in.readString();
        transactionId = in.readInt();
    }

    public Transaction(CategoryItem categoryItem) {
        this.setCategoryId(categoryItem.getCategoryId());
        this.setDirection(categoryItem.getDirection());
        this.setMainCategory(categoryItem.getMainCategory());
        this.setCategoryName(categoryItem.getCategoryName());
        this.setCategoryType(categoryItem.getCategoryType());
        this.setPeriodIdentifier(categoryItem.getPeriodIdentifier());
        this.setExpenseName(categoryItem.getExpenseName());
        this.setCost(categoryItem.getCost());
        this.setCategoryLimiter(categoryItem.getCategoryLimiter());
        this.setItemLimiter(categoryItem.getItemLimiter());
        this.setIcon(categoryItem.getIcon());

    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDay() {
        return transactionDay;
    }

    public void setTransactionDay(String transactionDay) {
        this.transactionDay = transactionDay;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
        String[] splitStr = transactionDate.split("\\s+");
        this.transactionDay = splitStr[0];
        this.transactionTime = splitStr[1];
    }

    public String getTransactionNote() {
        return transactionNote;
    }

    public void setTransactionNote(String transactionNote) {
        this.transactionNote = transactionNote;
    }

    public void delete(Activity activity) {
        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        databaseConnector.deleteTransaction(transactionId);
    }

    public void update(Activity activity) {
        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        databaseConnector.updateTransaction(this);
    }

    @Override
    public int hashCode() {
        return transactionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Transaction))
            return false;
        Transaction obj1 = (Transaction) obj;
        return this.getTransactionId() == obj1.getTransactionId();
    }

    public void add(Activity activity) {
        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        databaseConnector.addExpense(Transaction.this);
    }
}
