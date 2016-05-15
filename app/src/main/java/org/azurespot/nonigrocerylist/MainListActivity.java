package org.azurespot.nonigrocerylist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainListActivity extends AppCompatActivity {
    private ArrayList<ListItemsModel> itemsList = new ArrayList<>();
    private ListViewItemAdapter itemsAdapter;
    ListItemsModel itemModel;
    private ListView itemsView;
    private EditText newItemET;
    int currentItemPosition;
    ActionBar ab;
    public static boolean allowChangeQty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        newItemET = (EditText)findViewById(R.id.etNewItem);
        itemsView = (ListView) findViewById(R.id.lvItems);

        customizeActionBarTitle();

        // disables return key
        newItemET.setSingleLine(true);

        // read items that were persisted in list, before adapter is created with the list
        readItems();
        itemsAdapter = new ListViewItemAdapter(MainListActivity.this, R.layout.list_row, itemsList);
        itemsView.setAdapter(itemsAdapter);

        setUpListDetailsClickListener();
        setupDeleteItemClickListener();
    }

    // Enables a listener, to go to list details activity
    public void setUpListDetailsClickListener(){
        itemsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // must cast item at position from Object back to ListItemsModel type
                itemModel = (ListItemsModel) itemsView.getItemAtPosition(position);

                // capture current item position (to use in onResume) if change its quantity
                currentItemPosition = itemsAdapter.getPosition(itemModel);

                Log.d("QUANTITY: ", itemModel.getQty());

                Intent intent = new Intent(MainListActivity.this, ListItemDetailsActivity.class);
                intent.putExtra("item name", itemModel.getTitle());
                intent.putExtra("quantity", itemModel.getQty());
                startActivity(intent);
            }
        });
    }

    // Adds item to list
    public void addItem(View v) {
        if (!newItemET.getText().toString().equals("")) {
            itemModel = new ListItemsModel(newItemET.getText().toString(), "1");
            itemsList.add(itemModel);
            itemsAdapter.notifyDataSetChanged();
            newItemET.setText("");
            writeItems();
        }
            // soft keyboard down
            InputMethodManager mgr = (InputMethodManager)
                    getSystemService (Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(newItemET.getWindowToken(), 0);
    }

    // Enables listener to delete item
    private void setupDeleteItemClickListener() {
        itemsView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // First, clear prefs for item
                        String itemName = itemsAdapter.getItem(pos).getTitle();
                        SharedPreferences preferences = getSharedPreferences(itemName, 0);
                        // removes the quantity
                        preferences.edit().remove(itemName + "q").apply();
                        // removes the item details
                        preferences.edit().remove(itemName).apply();

                        // Now, remove entire item within list at position
                        itemsList.remove(pos);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        // update prefs
                        writeItems();

                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                });
    }

    // Reads list from file (previously saved)
    private void readItems() {

        FileInputStream fis = null;
        try {
            fis = openFileInput("groceries");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(fis);
            itemsList = (ArrayList<ListItemsModel>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Writes new items to list (saves it), or refreshes after delete item
    private void writeItems() {

        FileOutputStream fos = null;
        try {
            fos = openFileOutput("groceries", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(itemsList);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // custom actionbar title
    private void customizeActionBarTitle(){
        ab = getSupportActionBar();

        // Create a TextView programmatically.
        TextView tv = new TextView(getApplicationContext());
        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, // Width of TextView
                LayoutParams.WRAP_CONTENT); // Height of TextView

        // Apply layout parameters to TextView widget
        tv.setLayoutParams(lp);
        tv.setText("Noni's groceries");
        tv.setTextColor(Color.parseColor("#bdbdbd"));
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.SANS_SERIF);
        tv.setLetterSpacing (0.2f);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        // Set the ActionBar display option
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        // Set TextView as ActionBar custom view
        ab.setCustomView(tv);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ListItemDetailsActivity.itemName != null && allowChangeQty) {

            // retrieves preferences for quantity, updates model
            SharedPreferences pref = getSharedPreferences(ListItemDetailsActivity.itemName, MODE_PRIVATE);
            String savedQty = pref.getString(ListItemDetailsActivity.itemName + "q", "");
            itemModel = itemsAdapter.getItem(currentItemPosition);
            itemModel.setQty(savedQty);
            // since list model changed, update adapter
            itemsAdapter.notifyDataSetChanged();
            // replace item on list with updated one
            itemsList.set(currentItemPosition, itemModel);
            itemsAdapter.notifyDataSetChanged();

            // persist new change
            writeItems();
            allowChangeQty = false;
        }
    }
}
