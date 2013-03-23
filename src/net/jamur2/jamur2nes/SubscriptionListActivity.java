package net.jamur2.jamur2nes;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class SubscriptionListActivity extends ListActivity {
    DefaultHttpClient http_client = new DefaultHttpClient();
    private final ReentrantLock http_lock = new ReentrantLock();

    protected ArrayList<Subscription> subscriptions = (
        new ArrayList<Subscription>());

    protected ArrayAdapter<Subscription> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_list);
        adapter = new SubscriptionEntryAdapter(
            this, R.layout.subscription_item, subscriptions);
        this.setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle options = new Bundle();
        Bundle authTokenBundle = null;
        AccountManager accountManager = AccountManager.get(
            getApplicationContext());
        Account account = (Account)intent.getExtras().get("account");
        accountManager.getAuthToken(
            account, "ah", false, new InvalidateAuthTokenCallback(),
            null);
        accountManager.getAuthToken(
            account, "ah", false, new GetAuthTokenCallback(),
            null);
    }


    private class InvalidateAuthTokenCallback implements AccountManagerCallback {
        public void run(AccountManagerFuture result) {
            Bundle bundle;
            try {
                    bundle = (Bundle) result.getResult();
                    AccountManager accountManager = AccountManager.get(
                        getApplicationContext());
                    Object authToken = bundle.get(AccountManager.KEY_AUTHTOKEN);
                    if (authToken != null) {
                        Log.v("subscriptions", "Invalidating token: " +
                            authToken.toString());
                        accountManager.invalidateAuthToken("com.google",
                            authToken.toString());
                    }
            } catch (OperationCanceledException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (AuthenticatorException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
        }
    };

    private class GetAuthTokenCallback implements AccountManagerCallback {
        public void run(AccountManagerFuture result) {
            Bundle bundle;
            try {
                    bundle = (Bundle) result.getResult();
                    Intent intent = (Intent)bundle.get(
                        AccountManager.KEY_INTENT);
                    if(intent != null) {
                            // User input required
                            startActivity(intent);
                    } else {
                            onGetAuthToken(bundle);
                    }
            } catch (OperationCanceledException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (AuthenticatorException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
        }
    };

    protected void onGetAuthToken(Bundle bundle) {
        String auth_token = bundle.getString(
            AccountManager.KEY_AUTHTOKEN);
        new GetCookieTask().execute(auth_token);
    }
    private class GetCookieTask extends AsyncTask <Object, Void, Boolean>{
        protected Boolean doInBackground(Object... tokens) {
            try {
                // Don't follow redirects
                http_client.getParams().setBooleanParameter(
                    ClientPNames.HANDLE_REDIRECTS, false);

                Log.v("subscriptions", "Token: " + tokens[0]);
                HttpGet http_get = new HttpGet(
                    "https://jamur2nes.appspot.com/_ah/login?auth=" +
                    tokens[0]);
                HttpResponse response;
                response = http_client.execute(http_get);
                if(response.getStatusLine().getStatusCode() != 302) {
                    // Response should be a redirect
                    return false;
                }

                for(Cookie cookie : http_client.getCookieStore().getCookies()) {
                    Log.v("subscriptions", "Got cookie: " + cookie.getName());
                    if(cookie.getName().equals("SACSID")) {
                        Log.v("subscriptions", "GOT SACSID cookie, returning");
                        return true;
                    }
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                http_client.getParams().setBooleanParameter(
                    ClientPNames.HANDLE_REDIRECTS, true);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            new SubscriptionsRequestTask().execute(
                "https://jamur2nes.appspot.com/api/user/subscriptions");
        }
    }

    private class SubscriptionRequestTask extends AuthenticatedRequestTask {

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json_subscription = new JSONObject(result);
                Subscription sub = new Subscription(
                    json_subscription.getString("title"));
                subscriptions.add(sub);
                SubscriptionListActivity.this.adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.v("subscriptions", "Got subscription: " + result);
        }
    }

    private class SubscriptionsRequestTask extends AuthenticatedRequestTask {

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray subscriptions = null;
                subscriptions = new JSONArray(result);
                for (int i = 0; i < subscriptions.length(); i++) {
                    new SubscriptionRequestTask().execute(
                        "https://jamur2nes.appspot.com/api/feed?key=" +
                        subscriptions.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class AuthenticatedRequestTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... urls) {
            try {
                http_lock.lock(); // MUST unlock in onPostExecute
                HttpGet http_get = new HttpGet((String)urls[0]);
                HttpResponse response = http_client.execute(http_get);
                String currentLine;
                String responseBody = "";
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
                while ((currentLine = reader.readLine()) != null) {
                    responseBody += currentLine;
                }
                http_lock.unlock();
                return responseBody;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                    Log.v("subscriptions", "Network response: " + result);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
