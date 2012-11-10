package net.jamur2.jamur2nes;

import android.accounts.*;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.view.View;

public class LoginActivity extends ListActivity
{
        protected AccountManager accountManager;
        protected Intent intent;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        this.setListAdapter(new ArrayAdapter(this,
            R.layout.list_item, accounts));
    }

        @Override
        protected void onListItemClick(ListView l, View v, int position,
                long id) {
            Account account = (Account)getListView().getItemAtPosition(
                position);
            Intent intent = new Intent(this, AppInfo.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
}
