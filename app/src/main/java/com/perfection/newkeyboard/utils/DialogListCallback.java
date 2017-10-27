package com.perfection.newkeyboard.utils;

/**
 * <H1>Vidmoji Keyboard</H1>
 * <H1>DialogListCallback</H1>
 *
 * <p>Represents an interface that helps in tracking the list item clicked in a dialog</p>
 *
 * @author Divya Thakur
 * @since 10/12/16
 * @version 1.0
 */
public interface DialogListCallback {

    /**
     * Callback method that gets the item clicked in dialog on activity
     * @param time holds the country code
     */
    void getSelectedItem(String time);
}
