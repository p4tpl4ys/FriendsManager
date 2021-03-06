package com.abertay.friendsmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Patrick Kornek on 07.04.2018.
 * Emailcheck: https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
 * Datecheck: https://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android
 * Regular expression: https://stackoverflow.com/questions/2113908/what-regular-expression-will-match-valid-international-phone-numbers
 * both sources are customized, other code is combined from different sources and customized
 */

public class InputCheck {

    private Context mContext;

    public InputCheck(Context context) {
        mContext = context;
    }

    protected boolean areInputFieldsFilled(String friendName, String friendBirthday, String friendMobilPhone, String friendEmail) {
        if (friendName.equals("") || friendBirthday.equals("") || friendMobilPhone.equals("") || friendEmail.equals("")) {
            Log.d("Required Fields", "All fields are not filled");
            return false;
        } else {
            Log.d("Required Fields", "All fields filled");
            return true;
        }
    }

    protected boolean isEmailValid(String friendEmail) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(friendEmail).matches();
    }

    protected boolean isEmailAlreadyUsed(String friendEmail) {
        boolean checkEmail = false;
        ArrayList<Friend> friend = new ArrayList<>();
        try {
            friend = new AllFriendsTask(mContext).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(mContext, "Data transfer was interrupted: " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        for (int i = 0; i < friend.size(); i++) {
            if (friendEmail.equals(friend.get(i).email)) {
                return true;
            } else {
                checkEmail = false;
            }
        }
        return checkEmail;
    }

    protected boolean isPhoneNumberAlreadyUsed(String friendMobilePhone) {
        boolean checkMobilePhone = false;
        ArrayList<Friend> friend = new ArrayList<>();
        try {
            friend = new AllFriendsTask(mContext).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(mContext, "Data transfer was interrupted: " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        for (int i = 0; i < friend.size(); i++) {
            if (friendMobilePhone.equals(friend.get(i).mobilePhone)) {
                return true;
            } else {
                checkMobilePhone = false;
            }
        }
        return checkMobilePhone;
    }

    protected boolean isPhoneNumberValid(String friendMobilPhone) {
        String expression = "^[+]?[0-9]{10,13}$";  //regular expression tool "^\\+?\\(?[0-9]{1,3}\\)? ?-?[0-9]{1,3} ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})?"
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(friendMobilPhone);
        if (matcher.matches()) {  //changed true false
            return true;
        } else {
            return false;
        }
    }

    protected boolean isDateFormatValid(String friendBirthday) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat birthdayDate = new SimpleDateFormat("dd/MM/yyyy");
        birthdayDate.setLenient(false);
        if (birthdayDate.parse(friendBirthday, new ParsePosition(0)) != null) return true;
        else return false;
    }

    protected int parseDay(String friendBirthday) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date friendsDate = null;
        try {
            friendsDate = sdf.parse(friendBirthday);
        } catch (ParseException e) {
            //e.printStackTrace();   //Log handling
        }
        Date currentSystemDate = Calendar.getInstance().getTime();
        long diff = currentSystemDate.getTime() - friendsDate.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        return numOfDays;
    }

    // Most common case, friend is older than system date
    //not older than oldest person of all time ~123 years (44895): https://en.wikipedia.org/wiki/List_of_the_verified_oldest_people
    protected boolean isDateLogicForPast(String friendBirthday) {
        int numOfDays = parseDay(friendBirthday);
        Log.d("numOfDays", "" + numOfDays);
        return numOfDays < 44895;
    }

    //friend not born yet, not possible
    protected boolean isFutureDate(String friendBirthday) {
        int numOfDays = parseDay(friendBirthday);
        Log.d("numOfDays", "" + numOfDays);
        return numOfDays < 0;
    }

    //friend is a new born baby, unlikely case, but possible
    protected boolean isNewbornDate(String friendBirthday) {
        int numOfDays = parseDay(friendBirthday);
        Log.d("numOfDays", "" + numOfDays);
        return numOfDays == 0;
    }


    protected boolean isEmailAlreadyUsedOldAllowed(String oldEmail, String newEmail) {

        if(!oldEmail.equals(newEmail)) {
            ArrayList<Friend> friend = new ArrayList<>();
            try {
                friend = new AllFriendsTask(mContext).execute().get();
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(mContext, "Data transfer was interrupted: " + e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            //if new is already used
            for (int i = 0; i < friend.size(); i++) {
                if (newEmail.equals(friend.get(i).email)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isPhoneNumberAlreadyUsedOldAllowed(String oldMobilePhone, String newMobilePhone) {

        if(!oldMobilePhone.equals(newMobilePhone)) {
            ArrayList<Friend> friend = new ArrayList<>();
            try {
                friend = new AllFriendsTask(mContext).execute().get();
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(mContext, "Data transfer was interrupted: " + e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            for (int i = 0; i < friend.size(); i++) {
                if (newMobilePhone.equals(friend.get(i).mobilePhone)) {
                    return true;
                }
            }
        }

        return false;
    }
}
