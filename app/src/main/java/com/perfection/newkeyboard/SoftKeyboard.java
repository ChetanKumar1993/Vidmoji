package com.perfection.newkeyboard;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.MetaKeyKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.perfection.newkeyboard.Model.ServerResult;
import com.perfection.newkeyboard.Model.UserData;
import com.perfection.newkeyboard.controller.AppSettingsActivity;
import com.perfection.newkeyboard.database.DatabaseHelper1;
import com.perfection.newkeyboard.database.UserDao;
import com.perfection.newkeyboard.rest.ApiClientConnection;
import com.perfection.newkeyboard.rest.ApiInterface;
import com.perfection.newkeyboard.utils.ImageLoadedCallback;
import com.perfection.newkeyboard.utils.PrefUtils;
import com.perfection.newkeyboard.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoftKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, SpellCheckerSession.SpellCheckerSessionListener, ImageLoadedCallback {
    static final boolean DEBUG = false;
    static final boolean PROCESS_HARD_KEYS = true;
    static final int WIDTH = 200;
    static final int HEIGHT = 150;


    private InputMethodManager mInputMethodManager;

    SQLiteDatabase db;

    private LatinKeyboardView mInputView;
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;

    private StringBuilder mComposing = new StringBuilder();
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;

    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mQwertyKeyboard;

    private LatinKeyboard mCurKeyboard;

    private String mWordSeparators;

    private SpellCheckerSession mScs;
    private List<String> mSuggestions;
    LinearLayout.LayoutParams layoutParameter = new LinearLayout.LayoutParams(WIDTH, HEIGHT);
    private int LastPrimaryCode;
    private boolean isAlphaNumCharSent = false;

    LinearLayout mainlayout, imagelayout, logolayout, suggestionlayout, linearLayout;
    LinearLayout.LayoutParams layoutParams, layoutParams1;
    ImageView imageView1;
    boolean status = true, isImageViewLongPressed = false, isVideoViewLongPressed = false,
            isMediaLongPressed = false, isStatus = false, isAudioStatus = false;
    List<String> sb;
    MediaPlayer mediaPlayer;
    AlertDialog alert;
    Dialog dialog;
    private Bitmap finalBmp1;
    private ImageView finalImageView;
    List<UserData.DataBeanX.DataBean> dataList;
    private List<UserData.DataBeanX.DataBean> dataBeanList;
    private VideoView viz;
    private ImageView audView;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // sym_keyboard_space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override
    public View onCreateInputView() {
        mainlayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null);
        mainlayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroundcolor));
        mainlayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        imagelayout = new LinearLayout(this);
        imagelayout.setOrientation(LinearLayout.HORIZONTAL);
        final int inPixels = (int) getApplicationContext().getResources().getDimension(R.dimen.suggestion_height);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, inPixels);
        logolayout = new LinearLayout(this);
        logolayout.setOrientation(LinearLayout.HORIZONTAL);
        layoutParams1 = new LinearLayout.LayoutParams(120, 120);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        layoutParams1.gravity = Gravity.CENTER;
        imageView1 = new ImageView(SoftKeyboard.this);
        imageView1.setAdjustViewBounds(true);
        imageView1.setId(1);
        imageView1.setPadding(20, 20, 20, 20);
        imageView1.setScaleType(ImageView.ScaleType.CENTER);

        logolayout.setLayoutParams(layoutParams1);
        imageView1.setImageResource(R.drawable.keylogo);
        imageView1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView1.setOnClickListener(getSettingPageListener());
        imagelayout.setBackgroundColor(getResources().getColor(R.color.lightest_gray));
        imagelayout.addView(logolayout);

        suggestionlayout = new LinearLayout(this);
        suggestionlayout.setLayoutParams(layoutParams);
        imagelayout.addView(horizontalScrollView);
        dialog = new Dialog(getApplicationContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logolayout.addView(imageView1);
        horizontalScrollView.addView(suggestionlayout);

        imagelayout.setLayoutParams(layoutParams);

        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParams);
        layoutParams.gravity = Gravity.CENTER;
        linearLayout.setVisibility(View.GONE);
        mInputView = (LatinKeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setPreviewEnabled(false);
        setLatinKeyboard(mQwertyKeyboard);
        mainlayout.addView(linearLayout);
        mainlayout.addView(imagelayout);
        mainlayout.addView(mInputView);


        return mainlayout;
    }


    private View.OnClickListener getSettingPageListener() {
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mInHome;
                if (PrefUtils.getSharedPrefBoolean(SoftKeyboard.this, PrefUtils.IS_LOGGEDIN)) {
                    mInHome = new Intent(SoftKeyboard.this, AppSettingsActivity.class);
                } else {
                    mInHome = new Intent(SoftKeyboard.this, LoginActivity.class);
                }
                mInHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SoftKeyboard.this.startActivity(mInHome);
            }
        };
        return l;
    }


    @Override
    public void onComputeInsets(Insets outInsets) {
        super.onComputeInsets(outInsets);
        if (!isFullscreenMode()) {
            outInsets.contentTopInsets = outInsets.visibleTopInsets;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        final boolean shouldSupportLanguageSwitchKey =
                mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
        nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
        mInputView.setKeyboard(nextKeyboard);
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override
    public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        return mCandidateView;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return Service.START_STICKY;
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);


        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);
        updateCandidates();


        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = mQwertyKeyboard;
                mPredictionOn = true;

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false;
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false;
                }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false;
                    mCompletionOn = isFullscreenMode();
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = mQwertyKeyboard;
                updateShiftKeyState(attribute);
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();

        // Clear current composing text and candidates.
        mComposing.setLength(0);
//        updateCandidates();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
        updateShiftKeyState(getCurrentInputEditorInfo());
        mInputView.closing();
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
//            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < completions.length; i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length() - 1);
            }
        }

        onKey(c, null);

        return true;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (dialog != null) {
            dialog.dismiss();
        }
        if (alert != null)
            alert.dismiss();

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:


                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {

                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:

                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;
            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                /*
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState()&KeyEvent.META_ALT_ON) != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }*/
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }


        return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
            updateCandidates();
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null
                && mInputView != null && mQwertyKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShifted(mCapsLock || caps != 0);
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    // Implementation of KeyboardViewListener

    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d("Test", "KEYCODE: " + primaryCode);

        if (dialog != null) {
            dialog.dismiss();
        }
        if (alert != null)
            alert.dismiss();
        if (isWordSeparator(primaryCode)) {
            // Handle separator
            if (primaryCode == 32 && primaryCode == LastPrimaryCode && isAlphaNumCharSent == true) {
                handleBackspace();  // delete first space
                handleCharacter(46, keyCodes);  //send period second
                handleCharacter(primaryCode, keyCodes);  //then send space
                isAlphaNumCharSent = false;
            } else if (mComposing.length() > -1) {
                commitTyped(getCurrentInputConnection());
                sendKey(primaryCode);
                updateShiftKeyState(getCurrentInputEditorInfo());
            }
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
//            handleShift();
            mCapsLock = !mCapsLock;
            mQwertyKeyboard.setShifted(mCapsLock);
            mInputView.invalidateAllKeys();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
            // Show a menu or somethin'
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
                setLatinKeyboard(mQwertyKeyboard);
            } else {
                setLatinKeyboard(mSymbolsKeyboard);
                mSymbolsKeyboard.setShifted(false);
            }

        } else {
            isAlphaNumCharSent = true;
            handleCharacter(primaryCode, keyCodes);
        }

        LastPrimaryCode = primaryCode;

    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private boolean isSentenceSpellCheckSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    private void updateCandidates() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                //list.add(mComposing.toString());
                Log.d("SoftKeyboard", "REQUESTING: " + mComposing.toString());
                if (isSentenceSpellCheckSupported()) {
                    if (mScs != null)
                        mScs.getSentenceSuggestions(new TextInfo[]{new TextInfo(mComposing.toString())}, 10);
                    setSuggestions(list, true, true);
                }
            } else {
                setSuggestions(null, false, false);
            }
        }
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        mSuggestions = suggestions;
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }
        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            setLatinKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        if (mPredictionOn) {
            mComposing.append((char) primaryCode);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateShiftKeyState(getCurrentInputEditorInfo());
//            updateCandidates();
        } else {
            getCurrentInputConnection().commitText(
                    String.valueOf((char) primaryCode), 1);
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), false /* onlyCurrentIme */);
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {
            if (mPredictionOn && mSuggestions != null && index >= 0) {
                mComposing.replace(0, mComposing.length(), mSuggestions.get(index));
            }
            commitTyped(getCurrentInputConnection());
        }
    }

    public void swipeRight() {
        Log.d("SoftKeyboard", "Swipe right");
        if (mCompletionOn || mPredictionOn) {
            pickDefaultCandidate();
        }
    }

    public void swipeLeft() {
        Log.d("SoftKeyboard", "Swipe left");
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }


    public void onRelease(int primaryCode) {

        if (PrefUtils.getSharedPrefBoolean(this, PrefUtils.SUGGEST_VIDMOGI)) {

            suggestionlayout.setVisibility(View.VISIBLE);
        } else {
            suggestionlayout.setVisibility(View.GONE);
        }

        layoutParameter.setMargins(15, 1, 15, 1);


        final Resources resources;
        updateCandidates();
        String word = null;
        suggestionlayout.removeAllViews();
        suggestionlayout.setBackgroundColor(getResources().getColor(R.color.lightest_gray));
        final AudioManager mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager.isMusicActive()) {
            isStatus = true;
            isAudioStatus = true;
        }
        resources = getResources();
        byte[] imag = new byte[0];
        final List<byte[]> Imag;
        Imag = new ArrayList<>();
        String type, name;
        if (mComposing.length() > 0) {
            word = mComposing.toString();
            Log.d("hg", word);
        }
        final List<String> Type, Name;
        Type = new ArrayList<String>();
        Name = new ArrayList<String>();
        Imag.clear();
        Type.clear();
        Name.clear();
        final String packageName = getCurrentInputEditorInfo().packageName;
        if (packageName.equals("com.android.chrome") || packageName.equals("com.android.browser")
                || packageName.equals("com.android.vending") || packageName.equals("com.gl9.cloudBrowser")
                || packageName.equals("org.mozilla.firefox") || packageName.equals("com.opera.mini.native")
                || packageName.equals("com.opera.browser") || packageName.equals("com.UCMobile.intl")
                || packageName.equals("com.uc.browser.en") || packageName.equals("com.google.android.youtube")) {
            imagelayout.setVisibility(View.GONE);
        } else {
            final DatabaseHelper1 myDbHelper = new DatabaseHelper1(this);
            try {
                myDbHelper.createDatabase();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }
            try {
                myDbHelper.openDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            if (!word.equals(null)) {
//                    String[] smallText = word.split("");
//                    String b = "", full = "";
//                    for (String a : smallText) {
//                       System.out.println(a);
//                        b += a;
//                        full += b+",";
//                    }
//                full = full.startsWith(",") ? full.substring(1) : full;
//                full = full.endsWith(",") ? full.substring(1) : full;
//                System.out.println(full.substring(1, full.length()-1));
//                }

            db = myDbHelper.getWritableDatabase();
            Cursor cur;
         /*   if (word != null && !word.equals("'")) {
                cur = db.rawQuery("SELECT * FROM testable WHERE Tags LIKE '%" + word + "%'", null);
                if (cur.moveToFirst()) {
                    do {
                        imag = cur.getBlob(cur.getColumnIndexOrThrow("image"));
                        type = cur.getString(cur.getColumnIndexOrThrow("Type"));
                        name = cur.getString(cur.getColumnIndexOrThrow("name"));
                        Imag.add(imag);
                        Type.add(type);
                        Name.add(name);
                    } while (cur.moveToNext());
                }
            }*/
/*            Log.e("image", "" + Imag);
            Log.e("name", "" + Name);*/
//            Log.e("query", "SELECT * FROM databasetablegif WHERE tags LIKE '%" + word + "%'");

            if (dataList == null) {
                dataList = UserDao.getInstance(this).queryAll();

            }

            dataBeanList = new ArrayList<>();


            while (dataList == null) ;
            for (UserData.DataBeanX.DataBean dataBean : dataList) {
                if (!TextUtils.isEmpty(dataBean.getTitle()) && !TextUtils.isEmpty(word)) {
                    if (dataBean.getTitle().contains(word.toLowerCase())) {
                        if (new File(dataBean.getLocalVideo().replace("file://", "")).exists() && new File(dataBean.getLocalThumb().replace("file://", "")).exists())
                            dataBeanList.add(dataBean);
                    } else if (TextUtils.isEmpty(dataBean.getTags()) ? false : dataBean.getTags().contains(word.toLowerCase())) {
                        if (new File(dataBean.getLocalVideo().replace("file://", "")).exists() && new File(dataBean.getLocalThumb().replace("file://", "")).exists())
                            dataBeanList.add(dataBean);
                    }
                }
            }


            myDbHelper.close();
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme);
            Bitmap bmp = null;
            ImageView imageView = null;
            final LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setBackgroundColor(getResources().getColor(R.color.lightest_gray));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            final LinearLayout linearLayoutButton = new LinearLayout(this);
            linearLayoutButton.setOrientation(LinearLayout.HORIZONTAL);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            linearLayout.setLayoutParams(layoutParams);
            layoutParams.gravity = Gravity.CENTER;
            linearLayoutButton.setLayoutParams(layoutParams);
            linearLayoutButton.setGravity(Gravity.CENTER);
            mainlayout.removeView(linearLayout);
            for (int i = 0; i < dataBeanList.size(); i++) {
                final int k = i;
                String ty = dataBeanList.get(i).getType() == 2 ? "image" : dataBeanList.get(i).getType() == 1 ? "audio" : "video";
                if (ty.equals("image")) {
                    imageView = new ImageView(SoftKeyboard.this);
                /*    try {
                        File file = new File(dataBeanList.get(k).getLocalThumb());
                        bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imageView1.setImageBitmap(bmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    imageView.setId(i);
                    imageView.setAdjustViewBounds(true);
                    imageView.setPadding(2, 2, 2, 2);

                    Utils.glide(this, imageView, dataBeanList.get(k).getLocalVideo());
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setLayoutParams(layoutParameter);
                    suggestionlayout.addView(imageView);
                    assert imageView != null;
                    finalBmp1 = bmp;
                    finalImageView = imageView;
                    mainlayout.removeView(linearLayout);
                    mainlayout.addView(linearLayout, 0);
                    mainlayout.invalidate();

                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
//                                mainlayout.removeView(linearLayout);
                            if (dialog.isShowing()) {
                                Log.d("yes", "OPEN");
                                dialog.dismiss();
                            }
                            linearLayout.removeAllViews();
                            linearLayout.getLayoutParams().height = linearLayout.getHeight();
//                                linearLayout.setVisibility(View.VISIBLE);
                            int height = mainlayout.getHeight();
                            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SoftKeyboard.this, R.style.AppTheme);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.popup, null);
                            LinearLayout preview = (LinearLayout) dialogView.findViewById(R.id.preview);
                            dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    alert.dismiss();
                                }
                            });
                            preview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                }
                            });
                            dialogBuilder.setView(dialogView);
                            int id = finalImageView.getId();
//                            final String name1 = Name.get(id).toString();
//                                mainlayout.addView(linearLayout, 0);
//                                AlertDialog.Builder builder = new AlertDialog.Builder(SoftKeyboard.this, R.style.AppTheme1);
//                                builder.setTitle("Select Label");
                            final int inPixels = (int) getApplicationContext().getResources().getDimension(R.dimen.dialogBox_height);
                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, inPixels);
                            String qrPath = dataBeanList.get(k).getLocalVideo();
                            ImageView imageView2 = new ImageView(SoftKeyboard.this);
                            imageView2.setAdjustViewBounds(true);
                            imageView2.setPadding(2, 2, 2, 2);
                            imageView2.setLayoutParams(layoutParams1);
                            imageView2.setImageURI(Uri.parse(qrPath));
//                                linearLayout.setVisibility(View.VISIBLE);
                            preview.addView(imageView2);
//                                linearLayout.setVisibility(View.VISIBLE);
                            imageView2.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View v) {
                                                                  if (isImageViewLongPressed) {
                                                                      // Do something when the button is released.
                                                                      mainlayout.removeView(linearLayout);
                                                                      alert.dismiss();

//                                        linearLayout.setVisibility(View.GONE);
                                                                      linearLayout.removeAllViews();
                                                                      isImageViewLongPressed = false;
                                                                  }
                                                              }
                                                          }

                            );
                            imageView2.setOnTouchListener(new View.OnTouchListener()

                                                          {
                                                              @Override
                                                              public boolean onTouch(View v, MotionEvent event) {
                                                                  if (event.getAction() == MotionEvent.ACTION_UP) {
                                                                      mainlayout.removeView(linearLayout);
                                                                      linearLayout.setVisibility(View.GONE);
                                                                      linearLayout.removeAllViews();
                                                                      isImageViewLongPressed = false;
                                                                  }
                                                                  if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                                                      mainlayout.removeView(linearLayout);
                                                                      linearLayout.setVisibility(View.GONE);
                                                                      linearLayout.removeAllViews();
                                                                      isImageViewLongPressed = false;
                                                                  }
                                                                  if (event.getAction() == MotionEvent.AXIS_HSCROLL) {
                                                                      mainlayout.removeView(linearLayout);
                                                                      linearLayout.setVisibility(View.GONE);
                                                                      linearLayout.removeAllViews();
                                                                      isImageViewLongPressed = false;
                                                                  }
                                                                  alert.dismiss();
                                                                  linearLayout.removeAllViews();
                                                                  return true;
                                                              }
                                                          }

                            );
                            isImageViewLongPressed = true;
                            alert = dialogBuilder.create();
                            Window window;
                            window = alert.getWindow();
                            window.setLayout(getMaxWidth(), inPixels);
                            window.setGravity(Gravity.CENTER);
                            //                                window.setBackgroundDrawable(getDrawable(R.drawable.backgroundcolor));
                            WindowManager.LayoutParams lp = window.getAttributes();
                            lp.token = mInputView.getWindowToken();
                            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
                            window.setAttributes(lp);
                            lp.gravity = Gravity.TOP | Gravity.RIGHT;

//                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            lp.x = 100;
                            lp.y = height - 300;
                            window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                            alert.show();

                            return true;
                        }
                    });

                    final Bitmap finalBmp = bmp;
                    imageView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            } else {


                                int id = finalImageView.getId();
                                String ty = dataBeanList.get(k).getType() == 2 ? "image" : dataBeanList.get(k).getType() == 1 ? "audio" : "video";
                                if (ty.equals("image")) {
                                    updatePic(dataBeanList.get(k).getVideoID());
                                    int codeOfEmoji = 0x1F60A;
//                          ImageSpan span = new ImageSpan(getApplicationContext(), R.drawable.amitabh);
                                    int start = 0;
                                    int end = 5;
                                    String text = String.valueOf(Character.toChars(codeOfEmoji));
//                          mComposing.append(text);
                                    commitTyped(getCurrentInputConnection());
//                                    Drawable mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.amitabh, null);
//                                    Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
//                                    String name = Name.get(id).toString();
                                    String sharePath = dataBeanList.get(id).getLocalVideo().replace("file://", "");
//                                    InputConnection inputConnection = getCurrentInputConnection();
////                                    inputConnection.commitText();
//                                    ClipboardManager mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                                    ContentValues values = new ContentValues(2);
//                                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
//                                    values.put(MediaStore.Images.Media.DATA, name);
//                                    ContentResolver theContent = getContentResolver();
//                                    Uri imageUri = theContent.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                                    Log.d("jh", String.valueOf(imageUri));
//                                    ClipData theClip = ClipData.newUri(getContentResolver(), "Image", imageUri);
//                                    mClipboard.setPrimaryClip(theClip);
                                    /*String path = MediaStore.Images.Media.insertImage(getContentResolver(), finalBmp1, "Emoticon", null);
                                    Uri fileUri = Uri.parse(path);
                                    EditorInfo editorInfo = getCurrentInputEditorInfo();
                                    Log.d("jhj", String.valueOf(editorInfo));
                                    Intent picMessageIntent = new Intent(Intent.ACTION_SEND);
                                    picMessageIntent.setPackage(packageName);
                                    picMessageIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                    picMessageIntent.setType("image*//*");
                                    picMessageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    picMessageIntent.setPackage(getCurrentAppPackage(getApplicationContext(), editorInfo));
                                    startActivity(picMessageIntent);*/

                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setPackage(packageName);
                                    sharingIntent.setType("image/jpeg");
                                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                                    finalBmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                                    try {
                                        f.createNewFile();
                                        FileOutputStream fo = new FileOutputStream(f);
                                        fo.write(bytes.toByteArray());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(sharePath));
                                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(Intent.createChooser(sharingIntent, "Share Image")
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            //.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                            .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY));


                                    /*sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    sharingIntent.setType("image/png");
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    Intent activityToBeStarted = Intent.createChooser(sharingIntent, "Share image using")
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            //.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                            .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                                    //.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(activityToBeStarted);
                                    */
                                } else {
                                    EditorInfo editorInfo = getCurrentInputEditorInfo();
                                    Log.d("jhj", String.valueOf(editorInfo));
//                                    String name = Name.get(id).toString();
                                    String sharePath = dataBeanList.get(k).getLocalVideo().replace("file://", "");
                                    Uri uri = Uri.parse(sharePath);
                                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                                    sendIntent.setPackage(packageName);
                                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    sendIntent.setType("video/*");
                                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    sendIntent.setPackage(getCurrentAppPackage(getApplicationContext(), editorInfo));
                                    startActivity(sendIntent);
                                }
                            }
                        }
                    });

                    imageView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.onTouchEvent(event);
//                                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                                }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (isImageViewLongPressed) {
                                    // Do something when the button is released.
                                    mainlayout.removeView(linearLayout);
                                    alert.dismiss();
//                                        linearLayout.setVisibility(View.GONE);
                                    linearLayout.removeAllViews();
                                    isImageViewLongPressed = false;
                                }
                            }
                            return true;
                        }
                    });

                } else if (ty.equalsIgnoreCase("audio")) {
                    final FrameLayout linearLayoutAudio = new FrameLayout(this);
                    FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(WIDTH, HEIGHT, Gravity.CENTER);
                    linearLayoutAudio.setLayoutParams(layoutParams1);

                    imageView = new ImageView(SoftKeyboard.this);

                    final ImageView imageView1 = new ImageView(SoftKeyboard.this);

                    FrameLayout.LayoutParams layoutParamsPlay = new FrameLayout.LayoutParams(60, 60, Gravity.CENTER);


                    imageView1.setImageDrawable(getResources().getDrawable(R.drawable.play));

                    imageView1.setLayoutParams(layoutParamsPlay);
                    imageView1.setAlpha(0.7f);
                    try {
                    /*    File file = new File(dataBeanList.get(k).getLocalThumb());
                        bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imageView1.setImageBitmap(bmp);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageView.setId(k);

                    imageView.setAdjustViewBounds(true);
                    imageView.setPadding(2, 2, 2, 2);
                    Utils.glide(this, imageView, dataBeanList.get(k).getLocalThumb());
//                    imageView.setImageURI(Uri.parse(dataBeanList.get(k).getLocalThumb()));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setLayoutParams(layoutParameter);
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.audio));

                    linearLayoutAudio.removeAllViews();
                    linearLayoutAudio.addView(imageView, 0);
                    linearLayoutAudio.addView(imageView1, 1);
                    suggestionlayout.addView(linearLayoutAudio);

                    assert imageView != null;
                    finalBmp1 = bmp;
                    finalImageView = imageView;
                    mainlayout.removeView(linearLayout);
                    mainlayout.addView(linearLayout, 0);
                    mainlayout.invalidate();

                    imageView1.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.onTouchEvent(event);
//                                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                                }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (isMediaLongPressed) {
                                    mainlayout.removeView(linearLayout);
                                    dialog.dismiss();
                                    mainlayout.setVisibility(View.VISIBLE);
                                    linearLayout.removeAllViews();
                                    if (isAudioStatus)
                                        sendMediaButton1(SoftKeyboard.this, KeyEvent.KEYCODE_MEDIA_PLAY);
                                    isMediaLongPressed = false;
                                }
                            }
                            return true;
                        }
                    });


                    imageView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isVideoViewLongPressed) {
                                mainlayout.removeView(linearLayout);
                                linearLayout.removeAllViews();
                                isVideoViewLongPressed = false;
                                if (isAudioStatus)
                                    sendMediaButton1(SoftKeyboard.this, KeyEvent.KEYCODE_MEDIA_PLAY);
                            } else {

                                updateVideo(dataBeanList.get(k).getVideoID());

//                                String name = Name.get(id).toString();
                                String sharePath = dataBeanList.get(k).getLocalVideo().replace("file://", "");
                                Uri uri = Uri.parse(sharePath);
                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                sendIntent.setPackage(packageName);
                                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                sendIntent.setType("audio/*");
                                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(sendIntent);
//                                    linearLayout.removeView(linearLayoutButton);
                                mainlayout.removeView(linearLayout);
                                linearLayout.removeAllViews();
                            }

                        }
                    });

                    imageView1.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            if (new File(dataBeanList.get(k).getLocalVideo().replace("file://", "")).exists()) {
                                linearLayout.removeAllViews();
                                int height = mainlayout.getHeight();
//                            int id = imageView1.getId();
                                dialog.setContentView(R.layout.popupvideo);
                                dialog.getWindow().
                                        setType(WindowManager.LayoutParams.TYPE_TOAST);
                                viz = (VideoView) dialog.findViewById(R.id.previewvideo);
                                audView = (ImageView) dialog.findViewById(R.id.previewAudio);
                                Utils.glide(SoftKeyboard.this, audView, dataBeanList.get(k).getLocalThumb());
                                audView.setVisibility(View.VISIBLE);
                                FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(1, 1);
                                viz.setLayoutParams(frameParams);


                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                                lp.copyFrom(dialog.getWindow().

                                        getAttributes()

                                );
                                dialog.getWindow().

                                        setAttributes(lp);

                                Window dialogWindow = dialog.getWindow();
                                WindowManager.LayoutParams lp1 = dialogWindow.getAttributes();
                                dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
                                lp1.dimAmount = 0.0f;
                                lp1.gravity = Gravity.TOP;
                                lp1.x = 0;   //x position
                                lp1.y = height - 400;
                                Uri uri = Uri.parse(dataBeanList.get(k).getLocalVideo());
                                Log.v("Media-URI", uri + "");
                                viz.setVideoURI(uri);
                                viz.start();
                                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                audView.setVisibility(View.VISIBLE);


                                dialog.show();
                                viz.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.setLooping(true);
                                    }
                                });
                                dialog.setCanceledOnTouchOutside(true);
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener()

                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        linearLayout.removeAllViews();
                                        dialog.dismiss();
                                        isMediaLongPressed = false;
                                        mainlayout.removeView(linearLayout);
                                        mainlayout.setVisibility(View.VISIBLE);
                                    }
                                });
                                viz.setOnTouchListener(new View.OnTouchListener()

                                {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_UP) {
//                                                mainlayout.removeView(linearLayout);
                                            linearLayout.removeAllViews();
                                            dialog.dismiss();
                                            isMediaLongPressed = false;
                                            mainlayout.removeView(linearLayout);
                                            mainlayout.setVisibility(View.VISIBLE);
//
                                        }
                                        return true;
                                    }
                                });
                                viz.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        isMediaLongPressed = false;
                                        mainlayout.removeView(linearLayout);
                                        mainlayout.setVisibility(View.VISIBLE);
                                    }
                                });
                                if (mAudioManager.isMusicActive())

                                {
                                    sendMediaButton(SoftKeyboard.this, KeyEvent.KEYCODE_MEDIA_PAUSE);
                                    isAudioStatus = true;
                                }

                                isMediaLongPressed = true;


                            }
                            return true;
                        }

                    });

                } else if (ty.equals("video")) {
//                    final String name1 = Name.get(i).toString();
                    String base = Environment.getExternalStorageDirectory().getAbsolutePath();
//                        String imagePath = "file://" + base + "/saved_images/" + name1 + ".gif";
                    File extStore = Environment.getExternalStorageDirectory();
                    String qrPath = dataBeanList.get(k).getLocalThumb();
//                        File myFile = new File(imagePath);
                    Log.d("file", "exits");
//                            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
//                            metaRetriever.setDataSource(imagePath);
//                            String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
//                            String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//                            Log.d("hw", height + "," + width);
//                            int oldHeight = Integer.valueOf(height);
//                            int oldwidth = Integer.valueOf(width);
//                            int newwidth = (200 * oldwidth) / oldHeight;
//                            Log.d("nhw", "" + newwidth);

                    final FrameLayout linearLayoutvideo = new FrameLayout(this);
                    FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(WIDTH, HEIGHT, Gravity.CENTER);
                    linearLayoutvideo.setLayoutParams(layoutParams1);
                    final ImageView imageView1 = new ImageView(SoftKeyboard.this);
                    imageView1.setLayoutParams(layoutParams1);
                    imageView1.setId(k);

//                    Bitmap bitmap = BitmapFactory.decodeFile(dataBeanList.get(k).getLocalThumb());

//                    Utils.glide(this, imageView1, 0, dataBeanList.get(k).getLocalThumb());

                /*    File file = new File(dataBeanList.get(k).getLocalThumb());
                    Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageView1.setImageBitmap(b);*/

                    ImageView videoImageview = new ImageView(SoftKeyboard.this);
                    videoImageview.setId(i);
                    videoImageview.setAdjustViewBounds(true);
                    videoImageview.setLayoutParams(layoutParameter);
                    videoImageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    linearLayoutvideo.addView(videoImageview);
                    linearLayoutvideo.addView(imageView1);
                    suggestionlayout.addView(linearLayoutvideo);
                    FrameLayout.LayoutParams layoutParamspreview = new FrameLayout.LayoutParams(WIDTH, HEIGHT);
                    FrameLayout.LayoutParams layoutParamsPlay = new FrameLayout.LayoutParams(60, 60, Gravity.CENTER);
                    layoutParamspreview.setMargins(15, 1, 15, 1);
                    videoImageview.setLayoutParams(layoutParamspreview);
                    Utils.glide(this, videoImageview, dataBeanList.get(k).getLocalThumb());
                    imageView1.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    imageView1.setAlpha(0.7f);

                    imageView1.setLayoutParams(layoutParamsPlay);
                    mainlayout.removeView(linearLayout);
                    mainlayout.addView(linearLayout, 0);
                    mainlayout.invalidate();
                    videoImageview.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            if (new File(dataBeanList.get(k).getLocalVideo().replace("file://", "")).exists()) {
                                linearLayout.removeAllViews();
                                int height = mainlayout.getHeight();
                                int id = imageView1.getId();
//                            final String name1 = Name.get(id).toString();
//                                    dialog = new Dialog(getApplicationContext());
//                                            @Override
//                                            public boolean onTouchEvent(MotionEvent event) {
//                                                // Tap anywhere to close dialog.
//                                                this.dismiss();
//                                                return true;
//                                            }
//                                        };
//                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.popupvideo);
                                dialog.getWindow().
                                        setType(WindowManager.LayoutParams.TYPE_TOAST);
                                final VideoView viz = (VideoView) dialog.findViewById(R.id.previewvideo);
                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                                lp.copyFrom(dialog.getWindow().

                                        getAttributes()

                                );
                                dialog.getWindow().

                                        setAttributes(lp);

                                Window dialogWindow = dialog.getWindow();
                                WindowManager.LayoutParams lp1 = dialogWindow.getAttributes();
                                dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
                                lp1.dimAmount = 0.0f;
                                lp1.gravity = Gravity.TOP;
                                lp1.x = 0;   //x position
                                lp1.y = height - 400;
                                Uri uri = Uri.parse(dataBeanList.get(k).getLocalVideo());
                                Log.v("Video-URI", uri + "");
                                viz.setVideoURI(uri);
                                viz.start();
                                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


                                dialog.show();

                                dialog.setCanceledOnTouchOutside(true);
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener()

                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        linearLayout.removeAllViews();
                                        dialog.dismiss();
                                        isMediaLongPressed = false;
                                        mainlayout.removeView(linearLayout);
                                        mainlayout.setVisibility(View.VISIBLE);
                                    }
                                });
                                viz.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.setLooping(true);
                                    }
                                });
                                dialog.setCanceledOnTouchOutside(true);
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener()

                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        linearLayout.removeAllViews();
                                        dialog.dismiss();
                                        isVideoViewLongPressed = false;
                                        mainlayout.removeView(linearLayout);
                                        mainlayout.setVisibility(View.VISIBLE);
                                    }
                                });
                                viz.setOnTouchListener(new View.OnTouchListener()

                                {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_UP) {
//                                                mainlayout.removeView(linearLayout);
                                            linearLayout.removeAllViews();
                                            dialog.dismiss();
                                            isVideoViewLongPressed = false;
                                            mainlayout.removeView(linearLayout);
                                            mainlayout.setVisibility(View.VISIBLE);
//
                                        }
                                        return true;
                                    }
                                });
                                viz.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        isVideoViewLongPressed = false;
                                        mainlayout.removeView(linearLayout);
                                        mainlayout.setVisibility(View.VISIBLE);
                                    }
                                });
                                if (mAudioManager.isMusicActive())

                                {
                                    sendMediaButton(SoftKeyboard.this, KeyEvent.KEYCODE_MEDIA_PAUSE);
                                    isStatus = true;
                                }

                                isVideoViewLongPressed = true;
                            }

                            return true;
                        }

                    });

                    videoImageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isVideoViewLongPressed) {
                                mainlayout.removeView(linearLayout);
                                linearLayout.removeAllViews();
                                isVideoViewLongPressed = false;
                                if (isStatus)
                                    sendMediaButton1(SoftKeyboard.this, KeyEvent.KEYCODE_MEDIA_PLAY);
                            } else {
                                updateVideo(dataBeanList.get(k).getVideoID());
                                int id = imageView1.getId();
//                                String name = Name.get(id).toString();
                                String sharePath = dataBeanList.get(k).getLocalVideo().replace("file://", "");
                                Uri uri = Uri.parse(sharePath);
                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                sendIntent.setPackage(packageName);
                                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                sendIntent.setType("video/*");
                                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(sharePath));
                                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Intent.createChooser(sendIntent, "Share Video")
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        //.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                        .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                        .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)

                                );

//                                    linearLayout.removeView(linearLayoutButton);
                                mainlayout.removeView(linearLayout);
                                linearLayout.removeAllViews();
                            }

                        }
                    });

                    videoImageview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.onTouchEvent(event);
//                                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                                }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (isVideoViewLongPressed) {
                                    mainlayout.removeView(linearLayout);
                                    dialog.dismiss();
                                    mainlayout.setVisibility(View.VISIBLE);
                                    linearLayout.removeAllViews();
                                    if (isStatus)
                                        sendMediaButton1(SoftKeyboard.this, KeyEvent.KEYCODE_MEDIA_PLAY);
                                    isVideoViewLongPressed = false;
                                }
                            }
                            return true;
                        }
                    });

                }
            }
        }


        if (mCapsLock && isAlphaNumCharSent && primaryCode != Keyboard.KEYCODE_SHIFT) {
            mQwertyKeyboard.setShifted(!mCapsLock);
            mInputView.invalidateAllKeys();
            mCapsLock = !mCapsLock;
        }
    }

    private static void sendMediaButton(Context context, int keyCode) {
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);

        keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);
    }

    private static void sendMediaButton1(Context context, int keyCode) {
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);

        keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);
    }

    public void onPress(int primaryCode) {

    }

    public String getCurrentAppPackage(Context context, EditorInfo info) {
        if (info != null && info.packageName != null) {
            return info.packageName;
        }
        final PackageManager pm = context.getPackageManager();
        //Get the Activity Manager Object
        ActivityManager aManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //Get the list of running Applications
        List<ActivityManager.RunningAppProcessInfo> rapInfoList = aManager.getRunningAppProcesses();
        //Iterate all running apps to get their details
        for (ActivityManager.RunningAppProcessInfo rapInfo : rapInfoList) {
            //error getting package name for this process so move on
            if (rapInfo.pkgList.length == 0) {
                Log.i("DISCARDED PACKAGE", rapInfo.processName);
                continue;
            }
            try {
                PackageInfo pkgInfo = pm.getPackageInfo(rapInfo.pkgList[0], PackageManager.GET_ACTIVITIES);
                return pkgInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                // Keep iterating
            }
        }
        return null;
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < results.length; ++i) {
            // Returned suggestions are contained in SuggestionsInfo
            final int len = results[i].getSuggestionsCount();
            sb.append('\n');

            for (int j = 0; j < len; ++j) {
                sb.append("," + results[i].getSuggestionAt(j));
            }

            sb.append(" (" + len + ")");
        }
        Log.d("SoftKeyboard", "SUGGESTIONS1: " + sb.toString());
    }

    private static final int NOT_A_LENGTH = -1;

    private void dumpSuggestionsInfoInternal(
            final List<String> sb, final SuggestionsInfo si, final int length, final int offset) {
        // Returned suggestions are contained in SuggestionsInfo
        final int len = si.getSuggestionsCount();
        for (int j = 0; j < len; ++j) {
            sb.add(si.getSuggestionAt(j));
        }
    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        Log.d("SoftKeyboard", "onGetSentenceSuggestions");
        sb = new ArrayList<>();
        for (int i = 0; i < results.length; ++i) {
            final SentenceSuggestionsInfo ssi = results[i];
            for (int j = 0; j < ssi.getSuggestionsCount(); ++j) {
                dumpSuggestionsInfoInternal(
                        sb, ssi.getSuggestionsInfoAt(j), ssi.getOffsetAt(j), ssi.getLengthAt(j));
            }
        }
        Log.d("SoftKeyboard", "SUGGESTIONS: " + sb.toString());
        setSuggestions(sb, true, true);
    }

    @Override
    public Bitmap imageSuccessfullyLoaded(Bitmap bitmap, int i) {
        return bitmap;
    }


    private void updateVideo(int videoId) {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {

                service = ApiClientConnection.getInstance().createService();

                /*UserId instagramRequest = new UserId();
                instagramRequest.userid = userId;*/
                Call<String> call = service.updateVideo(videoId);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call,
                                           Response<String> response) {
                        Log.d("updateVideo---", response.toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("updateVideo---", t.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePic(int videoId) {

        ApiInterface service;
        try {
            if (Utils.isNetworkAvailable(this)) {

                service = ApiClientConnection.getInstance().createService();

                /*UserId instagramRequest = new UserId();
                instagramRequest.userid = userId;*/
                Call<String> call = service.updatePic(videoId);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call,
                                           Response<String> response) {
                        Log.d("updatePic", response.toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("updatePic", t.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}