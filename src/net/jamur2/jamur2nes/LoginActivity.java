package net.jamur2.jamur2nes;

import android.accounts.*;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends ListActivity
{
        protected AccountManager accountManager;
        protected Intent intent;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts.length == 1) {
            // Only one account, let's use that one
            Intent intent = new Intent(this, SubscriptionListActivity.class);
            intent.putExtra("account", accounts[0]);
            startActivity(intent);
        }
        ArrayList<Account> accounts_arraylist = new ArrayList<Account>(
            Arrays.asList(accounts));

        this.setListAdapter(new AccountEntryAdapter(this,
            R.layout.account_item, accounts_arraylist));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position,
            long id) {
        Account account = (Account)getListView().getItemAtPosition(
            position);
        Intent intent = new Intent(this, SubscriptionListActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);
    }
}
