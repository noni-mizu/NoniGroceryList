package org.azurespot.nonigrocerylist;

import java.io.Serializable;

/**
 * Created by mizu on 5/11/16.
 */
public class ListItemsModel implements Serializable {

    private String title;
    private String qty;

    public ListItemsModel(String t, String q){
        title = t;
        qty = q;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getQty(){
        return qty;
    }

    public void setQty(String qty){
        this.qty = qty;
    }
}
