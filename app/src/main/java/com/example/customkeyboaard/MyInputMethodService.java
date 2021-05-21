/*
 * Developer : Anup Atul Mulay
 * Contact Number: +1-317-998-0306
 * email: anup.mulay96@gmail.com / anmulay@iupui.edu
 *
 * Developer : Vidhi Patel
 * Contact Number: +1-463-867-1832
 * email: vidhihpatel02@gmail.com / patelvid@iu.edu
 * */

package com.example.customkeyboaard;

import android.bluetooth.BluetoothAdapter;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;

import com.tapwithus.sdk.TapListener;
import com.tapwithus.sdk.TapSdk;
import com.tapwithus.sdk.TapSdkFactory;
import com.tapwithus.sdk.airmouse.AirMousePacket;
import com.tapwithus.sdk.bluetooth.BluetoothManager;
import com.tapwithus.sdk.bluetooth.TapBluetoothManager;
import com.tapwithus.sdk.mouse.MousePacket;

import java.util.ArrayList;
import java.util.Locale;

public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener, TextToSpeech.OnInitListener {

    TextToSpeech tts;
    TapSdk sdk;
    BluetoothManager bluetoothManager;
    TapBluetoothManager tapBluetoothManager;
    static boolean allowSearchScan = false;
    static boolean isNumberMode = false;
    static int numberModeToggle = 0;
    boolean isDatabaseMode = false;
    static boolean isspecialCharMode = false;
    static int spModeToggle = 0;
    static boolean isAutoSuggestionMode = false;
    static int autoSuggestionModeToggle = 0;
    static int edModeToggle = 0;
    InputConnection inputConnection;

    // Auto suggestion mode variables
    String str1 = "";
    String[] splited;
    ArrayList<String> SuggestionsResult = new ArrayList<String>();


    @Override
    public View onCreateInputView() {
        KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        Keyboard keyboard = new Keyboard(this, R.xml.number_pad);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        inputConnection = getCurrentInputConnection();

        prepare();

        return keyboardView;
    }

    public void prepare() {
        bluetoothManager = new BluetoothManager(this, BluetoothAdapter.getDefaultAdapter());
        tapBluetoothManager = new TapBluetoothManager(bluetoothManager);
        sdk = new TapSdk(tapBluetoothManager);     // Connect Bluetooth Manager with Tap Strap SDK
        tts = new TextToSpeech(this, MyInputMethodService.this);  // Instantiate google Text-To-Speech Engine
        TapSdkFactory.getDefault(getApplicationContext());
        sdk.registerTapListener(mTap);
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();

        if (inputConnection != null) {
            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE:
                    CharSequence selectedText = inputConnection.getSelectedText(0);

                    if (TextUtils.isEmpty(selectedText)) {
                        inputConnection.deleteSurroundingText(1, 0);
                    } else {
                        inputConnection.commitText("", 1);
                    }
                    break;

                default:
                    char code = (char) primaryCode;
                    inputConnection.commitText(String.valueOf(code), 1);

            }
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public TapListener mTap = new TapListener() {

        AlphabetMode alMode = new AlphabetMode(MyInputMethodService.this);                 // Instantiate Alphabet Mode
        TypedString tyString = new TypedString();
        NumbersMode nmMode = new NumbersMode(MyInputMethodService.this);
        SpecialCharactersMode specialCharMode = new SpecialCharactersMode(MyInputMethodService.this);
        EditMode edMode = new EditMode(MyInputMethodService.this);
        AutoSuggestionMode autoSuggestionsMode = new AutoSuggestionMode();           // Instantiate AutoSuggestion Mode

        @Override
        public void onBluetoothTurnedOn() {

        }

        @Override
        public void onBluetoothTurnedOff() {

        }

        @Override
        public void onTapStartConnecting(@NonNull String tapIdentifier) {

        }

        @Override
        public void onTapConnected(@NonNull String tapIdentifier) {
            tyString.typedStringInitialise();   // Initialise Syntagm
            alMode.speakOut(tts, "Tap Strap connected to the phone. You can start keyflow");    // SpeakOut once tapStrap connected to phone
            alMode.alModeInitialise();      // Initialise Alphabet Mode
            nmMode.nmModeInitialise();
            specialCharMode.spModeInitialise();
        }

        @Override
        public void onTapDisconnected(@NonNull String tapIdentifier) {

        }

        @Override
        public void onTapResumed(@NonNull String tapIdentifier) {

        }

        @Override
        public void onTapChanged(@NonNull String tapIdentifier) {

        }

        @Override
        public void onControllerModeStarted(@NonNull String tapIdentifier) {

        }

        @Override
        public void onTextModeStarted(@NonNull String tapIdentifier) {

        }

        @Override
        public void onTapInputReceived(@NonNull String tapIdentifier, int data) {

            /*////////////////////////////////////////
            /////////// ALPHABET MODE ////////////////
            ////////////////////////////////////////*/

            if (!allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 2) {
                alMode.alModeForward(tts);
                //countTotalTaps.performCounting("alModeForward");
            } else if (!allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 4) {
                alMode.alModeBackward(tts);
                //countTotalTaps.performCounting("alModeBackward");
            } else if (!allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 6) {
                alMode.alModeHopping(tts);
                //countTotalTaps.performCounting("alModeHopping");
            } else if (!allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 1) {
                String results;
                results = alMode.alModeSelect(tts, tyString.alreadyTyped/*,tyString.word*/);
                tyString.alreadyTyped = results;

                inputConnection.commitText(String.valueOf(tyString.alreadyTyped.charAt(tyString.alreadyTyped.length() - 1)), 1);
                Log.d("TypedString", tyString.alreadyTyped);
                //countTotalTaps.performCounting("alModeSelection");
            } else if (!allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 30) {
                alMode.alModeSpeakOut(tts, tyString.alreadyTyped/*+" "+tyString.word*/);
                //countTotalTaps.performCounting("alModeSpeakOut");
            } else if (!allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 16) {
                //***************** alMode -> Deletion ****************
                String results;
                results = alMode.alModeDelete(tts, tyString.alreadyTyped/*,tyString.word*/);
                tyString.alreadyTyped = results;

                inputConnection.deleteSurroundingText(1, 0);
                Log.d("TypedWord", tyString.word);
                Log.d("TypedString", tyString.alreadyTyped);
                //countTotalTaps.performCounting("alMOdeDeletion");
            } else if (!allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 8) {
                //************** alMode -> Reset ***************
                alMode.alModeReset(tts);
                //countTotalTaps.performCounting("alModeReset");
            }


            /*////////////////////////////////////////
            /////////// NUMBER MODE ////////////////
            ////////////////////////////////////////*/


            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 3) {
                tts.speak("Entered Number Mode", TextToSpeech.QUEUE_FLUSH, null, null);
                isNumberMode = true;
                numberModeToggle = 1;
                nmMode.nmModeInitialise();
                //countTotalTaps.performCounting("nmModeEnter");
            } else if (!allowSearchScan & isNumberMode == true & numberModeToggle == 1 & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 3) // nmMode -> Exit
            {
                tts.speak("Exit Number Mode", TextToSpeech.QUEUE_FLUSH, null, null);
                isNumberMode = false;
                numberModeToggle = 0;
                nmMode.numberHeadPoint = 0;
                //countTotalTaps.performCounting("nmModeExit");
            } else if (!allowSearchScan & isNumberMode == true & numberModeToggle == 1 & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 2) {
                nmMode.nmModeForward(tts);
                //countTotalTaps.performCounting("nmModeForward");
            } else if (!allowSearchScan & isNumberMode == true & numberModeToggle == 1 & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 4) {
                nmMode.nmModeBackward(tts);
                //countTotalTaps.performCounting("nmModeBackward");
            } else if (!allowSearchScan & isNumberMode == true & numberModeToggle == 1 & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 1) {
                //tyString.alreadyTyped = nmMode.nmModeSelection(tts,tyString.alreadyTyped);
                String results;
                results = nmMode.nmModeSelection(tts, tyString.alreadyTyped/*,tyString.word*/);
                tyString.alreadyTyped = results;

                Log.d("TypedString", tyString.alreadyTyped);
                inputConnection.commitText(String.valueOf(tyString.alreadyTyped.charAt(tyString.alreadyTyped.length() - 1)), 1);
                //countTotalTaps.performCounting("nmModeSelection");
            } else if (!allowSearchScan & isNumberMode == true & numberModeToggle == 1 & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 16) {
                String results;
                results = nmMode.nmModeDeletion(tts, tyString.alreadyTyped/*,tyString.word*/);
                tyString.alreadyTyped = results;

                Log.d("TypedString", tyString.alreadyTyped);
                inputConnection.deleteSurroundingText(1, 0);
                //countTotalTaps.performCounting("nmModeDeletion");
            } else if (!allowSearchScan & isNumberMode == true & numberModeToggle == 1 & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 30) {
                nmMode.nmModeSpeakOut(tts, tyString.alreadyTyped);
            }


            /*////////////////////////////////////////
            /////////// SPECIAL CHARACTER MODE ////////////////
            ////////////////////////////////////////*/

            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 5) {
                tts.speak("Entered Special Characters Mode", TextToSpeech.QUEUE_FLUSH, null, null);
                isspecialCharMode = true;
                spModeToggle = 1;
                specialCharMode.spModeInitialise();
                //countTotalTaps.performCounting("specialCharModeEnter");
            }
            // spMode -> Exit
            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isDatabaseMode == false & isspecialCharMode == true & spModeToggle == 1 & isAutoSuggestionMode == false & data == 5) {
                tts.speak("Exit Special Characters Mode", TextToSpeech.QUEUE_FLUSH, null, null);
                isspecialCharMode = false;
                spModeToggle = 0;
                specialCharMode.spHeadPoint = 0;
                //countTotalTaps.performCounting("specialCharModeExit");
            }
            // spMode -> Forward
            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isDatabaseMode == false & isspecialCharMode == true & spModeToggle == 1 & isAutoSuggestionMode == false & data == 2) {
                specialCharMode.spModeForward(tts);
                //countTotalTaps.performCounting("specialCharModeForward");
            }
            // spMode -> Backward
            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isDatabaseMode == false & isspecialCharMode == true & spModeToggle == 1 & isAutoSuggestionMode == false & data == 4) {
                specialCharMode.spModeBackward(tts);
                //countTotalTaps.performCounting("specialCharModeBackward");
            }
            // spMode -> Selection
            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isDatabaseMode == false & isspecialCharMode == true & spModeToggle == 1 & isAutoSuggestionMode == false & data == 1) {
                //tyString.alreadyTyped = specialCharMode.spModeSelection(tts,tyString.alreadyTyped,tyString.word);
                String results = specialCharMode.spModeSelection(tts, tyString.alreadyTyped/*,tyString.word*/);
                tyString.alreadyTyped = results;
                Log.d("TypedString", tyString.alreadyTyped);
                inputConnection.commitText(String.valueOf(tyString.alreadyTyped.charAt(tyString.alreadyTyped.length() - 1)), 1);
                //countTotalTaps.performCounting("specialCharModeSelection");
            }
            // spMode -> Deletion
            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isDatabaseMode == false & isspecialCharMode == true & spModeToggle == 1 & isAutoSuggestionMode == false & data == 16) {
                String results;
                results = specialCharMode.spModeDeletion(tts, tyString.alreadyTyped/*,tyString.word*/);
                tyString.alreadyTyped = results;
                Log.d("TypedString", tyString.alreadyTyped);
                inputConnection.deleteSurroundingText(1, 0);
                //countTotalTaps.performCounting("specialCharModeDeletion");
            }
            //spMode -> Speakout
            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isDatabaseMode == false & isspecialCharMode == true & spModeToggle == 1 & isAutoSuggestionMode == false & data == 30) {
                specialCharMode.spModeSpeakOut(tts, tyString.alreadyTyped);
            }


            /*////////////////////////////////////////
            /////////// EDIT MODE ////////////////
            ////////////////////////////////////////*/

            //Index + Middle + Ring Finger ==> Enter In Edit Mode
            else if (!allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 14 & edModeToggle == 0) {
                Log.d("TypedString", tyString.alreadyTyped);
                edModeToggle = 1;
                edMode.edModeInitialise(tts, tyString.alreadyTyped, getApplicationContext());
                //countTotalTaps.performCounting("editModeEnter");
            }
            //Index + Middle + Ring ==> Exit Edit Mode
            else if (/*allowSearchScan==true &*/ isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 14 & edModeToggle == 1 & EditMode.editMode) {
                tts.speak("Exit Edit Mode ", TextToSpeech.QUEUE_FLUSH, null, null);
                edModeToggle = 0;
                allowSearchScan = false;
                EditMode.editMode = false;
                //countTotalTaps.performCounting("editModeExit");
            }
            //Index Finger to Navigate in Selected Word
            else if (allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 2) {
                edMode.edModeForwardNav(tts, tyString.alreadyTyped);
                //countTotalTaps.performCounting("editModeForwardNav");
            } else if (allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 4) {
                edMode.edModeBackwardNav(tts, tyString.alreadyTyped);
            }
            //Ring Finger for Decision Making
            else if (allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 8) {
                edMode.edModeDecisionNav(tts);
                //countTotalTaps.performCounting("editModeDecisionNav");
            }
            // Decision Selection in Edit Mode
            else if (allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 1) {
                edMode.edModeDecisionSelection(tts, tyString.alreadyTyped);
                //countTotalTaps.performCounting("editModeDecisionSelection");
            }
            //Deletion in Edit Mode allowSearch Scan
            else if (allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 16) {
                tyString.alreadyTyped = edMode.edModeDeletion(tts, tyString.alreadyTyped);
                //countTotalTaps.performCounting("editModeDeletion");
            }
            //SpeakOut in EditMode
            else if (allowSearchScan & isNumberMode == false & isDatabaseMode == false & isspecialCharMode == false & isAutoSuggestionMode == false & data == 30) {
                edMode.edModeSpeakOut(tts, tyString.alreadyTyped/*+" "+tyString.word*/);
            }

            /*////////////////////////////////////////
            /////////// Auto Suggestion MODE ////////////////
            ////////////////////////////////////////*/

            else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isspecialCharMode == false & isAutoSuggestionMode == false & autoSuggestionModeToggle == 0 & data == 9) {
                if (tyString.alreadyTyped.length() == 0) {
                    tts.speak("Nothing typed No Autosuggestions ", TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    String str = tyString.alreadyTyped;
                    splited = str.split("\\s+");
                    str1 = splited[splited.length - 1];
                    SuggestionsResult = autoSuggestionsMode.fetchAutoSuggestions(getApplicationContext(), tts, str1);
                    if (SuggestionsResult != null) {
                        isAutoSuggestionMode = true;
                    }
                }

                //countTotalTaps.performCounting("autoSuggestionModeFetch");
            } else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isspecialCharMode == false & isAutoSuggestionMode == true & autoSuggestionModeToggle == 1 & data == 9) {
                tts.speak("Exit AutoSuggestions Mode ", TextToSpeech.QUEUE_FLUSH, null, null);
                isAutoSuggestionMode = false;
                autoSuggestionModeToggle = 0;

                //countTotalTaps.performCounting("autoSuggestionModeExit");
            } else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isspecialCharMode == false & isAutoSuggestionMode == true & autoSuggestionModeToggle == 1 & data == 2) // Forward Navigation in AutoSuggestion Mode
            {
                autoSuggestionsMode.forwardNavigateSuggestions(tts, SuggestionsResult);

                //countTotalTaps.performCounting("autoSuggestionModeForwardNav");
            } else if (!allowSearchScan & isNumberMode == false & numberModeToggle == 0 & isspecialCharMode == false & isAutoSuggestionMode == true & autoSuggestionModeToggle == 1 & data == 1)//Selection in AutoSuggestion Mode
            {
                String word = autoSuggestionsMode.selectAutoSuggestion(tts);

                if (tyString.alreadyTyped.contains(" ")) {
                    tyString.alreadyTyped = "";

                    String temp = "";
                    for (int i = 0; i < splited.length - 1; i++) {
                        if (temp.equals("")) {
                            temp = temp + splited[i];
                            continue;
                        }
                        temp = temp + " " + splited[i];
                    }
                    tyString.alreadyTyped = temp + " " + word;
                } else {
                    tyString.alreadyTyped = word;
                }

                Log.d("TypedString", tyString.alreadyTyped);

                //countTotalTaps.performCounting("autoSuggestionModeSelection");
            }
        }

        @Override
        public void onMouseInputReceived(@NonNull String tapIdentifier, @NonNull MousePacket data) {

        }

        @Override
        public void onAirMouseInputReceived(@NonNull String tapIdentifier, @NonNull AirMousePacket data) {

        }

        @Override
        public void onError(@NonNull String tapIdentifier, int code, @NonNull String description) {

        }
    };

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            tts.setSpeechRate(1.5f);
            tts.setPitch(1.0f);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not Supported");
            }
        } else {
            Log.e("TTS", "Initialization Failed! Activity");
        }
    }
}
