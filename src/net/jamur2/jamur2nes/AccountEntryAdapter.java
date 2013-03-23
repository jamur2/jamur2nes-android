package net.jamur2.jamur2nes;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class AccountEntryAdapter extends ArrayAdapter<Account> {
    private ArrayList<Account> accountDataItems;
    private Activity context;


    public AccountEntryAdapter(Activity context, int textViewResourceId,
            ArrayList<Account> accountDataItems) {
        super(context, textViewResourceId, accountDataItems);
        this.context = context;
        this.accountDataItems = accountDataItems;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)this.context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.account_item, null);
        }

        Account account = accountDataItems.get(position);
        TextView emailTextView = (TextView) v.findViewById(
            R.id.email_text_view);
        emailTextView.setText(account.name);
        return v;
    }

}
