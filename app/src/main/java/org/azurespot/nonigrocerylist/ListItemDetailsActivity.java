package org.azurespot.nonigrocerylist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListItemDetailsActivity extends AppCompatActivity {
    EditText itemDetails;
    EditText itemQty;
    TextView itemTitle;
    public static String itemName;
    String savedDetails;
    String savedQty;
    String qty;
    ListItemsModel model;
    private boolean allowChangeButtonPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_details);

        getSupportActionBar().setTitle("");

        itemTitle = (TextView)findViewById(R.id.itemTitle);
        itemQty = (EditText) findViewById(R.id.qty);
        itemDetails = (EditText)findViewById(R.id.itemDetails);

        // disables return key
        itemQty.setSingleLine(true);

        // retrieve Intent data for item name
        Bundle extras = getIntent().getExtras();
        itemName = extras.getString("item name");
        itemTitle.setText(itemName);
        qty = extras.getString("quantity");
        itemQty.setText(qty);

        // set your title and qty into model
        model = new ListItemsModel(itemName, qty);

        // set onClick listener to itemQty, so select all when clicked
        itemQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemQty.selectAll();
            }

        });

        // set click listener to detect if itemQty is used, if not, don't allow Change button
        itemQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowChangeButtonPress = true;
            }
        });
    }

    public void saveDetails (View view){
        // persist the details
        writeToPrefs(itemName, null);

        makeToast("Details saved!");

        // soft keyboard down
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(itemDetails.getWindowToken(), 0);

    }

    public void changeQty (View view){
        if (allowChangeButtonPress) {
            // set qty text in model
            model.setQty(itemQty.getText().toString());

            // write to shared preferences
            writeToPrefs(itemName, "q");

            makeToast("Quantity changed!");
            allowChangeButtonPress = false;

            // soft keyboard down
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(itemQty.getWindowToken(), 0);

            MainListActivity.allowChangeQty = true;
        } else{
            makeToast("Enter new quantity.");
        }
    }

    public void clearDetails (View view) {
        itemDetails.setText("");
        writeToPrefs(itemName, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        // retrieves preferences for both details and quantity
        SharedPreferences pref = getSharedPreferences(itemName, MODE_PRIVATE);
        savedDetails = pref.getString(itemName, "");
        itemDetails.setText(savedDetails);

        savedQty = pref.getString(itemName + "q", "");
        itemQty.setText(savedQty);
    }

    // use itemName as a key to keep item details with right item, add "q" to key for quantity
    public void writeToPrefs(String itemName, String q){
        SharedPreferences pref = getSharedPreferences(itemName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (q != null) {
            editor.putString(itemName + q, itemQty.getText().toString());
        } else {
            editor.putString(itemName, itemDetails.getText().toString());
        }
        editor.apply();
    }

    public void makeToast(String message){
        // custom toast
        Toast toast = Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        toastLayout.setBackgroundColor(getResources().getColor(R.color.toast_color));
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(30);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 390);
        toast.show();
    }
}