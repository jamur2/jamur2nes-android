package net.jamur2.jamur2nes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class SubscriptionEntryAdapter extends ArrayAdapter<Subscription> {
    private ArrayList<Subscription> subscriptionDataItems;
    private Activity context;

    public SubscriptionEntryAdapter(Activity context, int textViewResourceId,
            ArrayList<Subscription> subscriptionDataItems) {
        super(context, textViewResourceId, subscriptionDataItems);
        this.context = context;
        this.subscriptionDataItems = subscriptionDataItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)this.context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.subscription_item, null);
        }

        Subscription subscription = subscriptionDataItems.get(position);
        TextView titleTextView = (TextView) v.findViewById(
            R.id.title_text_view);
        titleTextView.setText(subscription.title);
        return v;
    }

}
