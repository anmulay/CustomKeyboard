package com.example.customkeyboaard;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.customkeyboaard.EarconManager.deleteChar;

public class SpecialCharactersMode {

    Context context;

    ArrayList<String> spSuggestions;
    ArrayAdapter<String> spSuggestionsAdapter;

    boolean spFront,spBack,spPrev;
    int spHeadPoint;
    String searchValue;

    String del_char;

    public SpecialCharactersMode(Context context)
    {

        this.context = context;
    }

    public void spModeInitialise()
    {
        //Adding Special Characters into Different Plane
        String spCharacters = "&,.,@,!,*,#,$,%,?";
        spSuggestions = new ArrayList<>(Arrays.asList(spCharacters.split(",")));
        spSuggestionsAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,spSuggestions);
        spSuggestionsAdapter.notifyDataSetChanged();

        spFront=false;
        spBack=false;
        spHeadPoint=0;
        spPrev=false; //#############
    }

    public void spModeForward(TextToSpeech tts)
    {
        if(spFront)
        {
            spHeadPoint = spHeadPoint + 1;
            spFront=false;
        }

        if(spHeadPoint==spSuggestions.size())
        {
            spHeadPoint = 0;
        }

        if(spSuggestions.get(spHeadPoint).equals("#"))
        {
            tts.speak("Hash", TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else if(spSuggestions.get(spHeadPoint).equals("$"))
        {
            tts.speak("Dollar",TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else
        {
            tts.speak(spSuggestions.get(spHeadPoint),TextToSpeech.QUEUE_FLUSH,null,null);
        }

        //tts.speak(spSuggestions.get(spHeadPoint),TextToSpeech.QUEUE_FLUSH,null,null);
        spHeadPoint++;
        spBack=true;
    }

    public void spModeBackward(TextToSpeech tts)
    {
        if (spBack) {
            spHeadPoint = spHeadPoint - 2;
            spBack = false;
        }
        else
        {
            spHeadPoint--;
        }

        if(spHeadPoint < 0 )
        {
            spHeadPoint =spSuggestions.size()-1;
        }


        if(spSuggestions.get(spHeadPoint).equals("#"))
        {
            tts.speak("Hash",TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else if(spSuggestions.get(spHeadPoint).equals("$"))
        {
            tts.speak("Dollar",TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else
        {
            tts.speak(spSuggestions.get(spHeadPoint),TextToSpeech.QUEUE_FLUSH,null,null);
        }
        //tts.speak(spSuggestions.get(spHeadPoint),TextToSpeech.QUEUE_FLUSH,null,null);
        spFront=true;
        spPrev=true;
    }

    public String spModeSelection(TextToSpeech tts, String alreadyTyped/*, String word*/)
    {
        if(!spPrev)
        {
            searchValue = spSuggestions.get(spHeadPoint-1);

            if(EditMode.editMode & EditMode.decision.equals("Insert"))
            {
                if(alreadyTyped.charAt(EditMode.insertion_index) == ' ')
                {
                    EditMode.speakInsertedAtSpace(tts,searchValue);
                }
                else
                {
                    EditMode.speakReplacedAtCharacter(tts,searchValue,alreadyTyped.charAt(EditMode.insertion_index));
                }
                alreadyTyped = EditMode.insertInEdit(tts,alreadyTyped,searchValue,EditMode.insertion_index);

                if(MyInputMethodService.isNumberMode == true | MyInputMethodService.numberModeToggle == 1 | MyInputMethodService.isspecialCharMode == true | MyInputMethodService.spModeToggle==1)
                {
                    MyInputMethodService.isNumberMode = false;
                    MyInputMethodService.numberModeToggle = 0;

                    MyInputMethodService.isspecialCharMode = false;
                    MyInputMethodService.spModeToggle = 0;

                }
                MyInputMethodService.allowSearchScan = true;

                //word = "";
            }
            else if(EditMode.editMode & EditMode.decision.equals("Replace"))
            {
                if(alreadyTyped.charAt(EditMode.insertion_index) == ' ')
                {
                    EditMode.speakReplacedAtSpace(tts,searchValue);
                }
                else
                {
                    EditMode.speakReplacedAtCharacter(tts,searchValue,alreadyTyped.charAt(EditMode.insertion_index));
                }
                alreadyTyped = EditMode.replaceInEdit(tts,alreadyTyped,searchValue,EditMode.insertion_index);

                if(MyInputMethodService.isNumberMode == true | MyInputMethodService.numberModeToggle == 1 | MyInputMethodService.isspecialCharMode == true | MyInputMethodService.spModeToggle==1)
                {
                    MyInputMethodService.isNumberMode = false;
                    MyInputMethodService.numberModeToggle = 0;

                    MyInputMethodService.isspecialCharMode = false;
                    MyInputMethodService.spModeToggle = 0;

                }
                MyInputMethodService.allowSearchScan = true;

                //word = "";
            }
            else
            {
                //count("Selection");
                if(searchValue.equals(" "))
                {
                    alreadyTyped = addSpaceAtEnd(tts, alreadyTyped/*, word*/, searchValue);
                    //word = "";
                    //alreadyTyped = addSpaceAtEnd(tts,alreadyTyped,searchValue);
                }
                else
                {
                    /*word*/ alreadyTyped = addAtEnd(tts/*, word*/,alreadyTyped, searchValue);
                    //alreadyTyped = alreadyTyped;
                    //alreadyTyped = addAtEnd(tts,alreadyTyped,searchValue);
                }
            }
            spPrev=false;
        }
        else
        {
            if(!spFront)
            {
                searchValue = spSuggestions.get(spHeadPoint-1);

                if(EditMode.editMode & EditMode.decision.equals("Insert"))
                {
                    if(alreadyTyped.charAt(EditMode.insertion_index) == ' ')
                    {
                        EditMode.speakInsertedAtSpace(tts,searchValue);
                    }
                    else
                    {
                        EditMode.speakInsertedAtCharacter(tts,searchValue,alreadyTyped.charAt(EditMode.insertion_index));
                    }
                    alreadyTyped = EditMode.insertInEdit(tts,alreadyTyped,searchValue,EditMode.insertion_index);

                    if(MyInputMethodService.isNumberMode == true | MyInputMethodService.numberModeToggle == 1 | MyInputMethodService.isspecialCharMode == true | MyInputMethodService.spModeToggle==1)
                    {
                        MyInputMethodService.isNumberMode = false;
                        MyInputMethodService.numberModeToggle = 0;

                        MyInputMethodService.isspecialCharMode = false;
                        MyInputMethodService.spModeToggle = 0;

                    }
                    MyInputMethodService.allowSearchScan = true;

                    //word = "";
                }
                else if(EditMode.editMode & EditMode.decision.equals("Replace"))
                {
                    if(alreadyTyped.charAt(EditMode.insertion_index) == ' ')
                    {
                        EditMode.speakReplacedAtSpace(tts,searchValue);
                    }
                    else
                    {
                        EditMode.speakReplacedAtCharacter(tts,searchValue,alreadyTyped.charAt(EditMode.insertion_index));
                    }
                    alreadyTyped = EditMode.replaceInEdit(tts,alreadyTyped,searchValue,EditMode.insertion_index);

                    if(MyInputMethodService.isNumberMode == true | MyInputMethodService.numberModeToggle == 1 | MyInputMethodService.isspecialCharMode == true | MyInputMethodService.spModeToggle==1)
                    {
                        MyInputMethodService.isNumberMode = false;
                        MyInputMethodService.numberModeToggle = 0;

                        MyInputMethodService.isspecialCharMode = false;
                        MyInputMethodService.spModeToggle = 0;

                    }
                    MyInputMethodService.allowSearchScan = true;

                    //word = "";
                }
                else
                {
                    //count("Selection");
                    if(searchValue.equals(" "))
                    {
                        alreadyTyped = addSpaceAtEnd(tts, alreadyTyped/*, word*/, searchValue);
                        //word = "";
                        //alreadyTyped = addSpaceAtEnd(tts,alreadyTyped,searchValue);
                    }
                    else
                    {
                        /*word*/ alreadyTyped = addAtEnd(tts/*, word*/,alreadyTyped, searchValue);
                        //alreadyTyped = alreadyTyped;
                        //alreadyTyped = addAtEnd(tts,alreadyTyped,searchValue);
                    }
                }
            }
            else
            {
                searchValue=spSuggestions.get(spHeadPoint);

                if(EditMode.editMode & EditMode.decision.equals("Insert"))
                {
                    if(alreadyTyped.charAt(EditMode.insertion_index) == ' ')
                    {
                        EditMode.speakInsertedAtSpace(tts,searchValue);
                    }
                    else
                    {
                        EditMode.speakInsertedAtCharacter(tts,searchValue,alreadyTyped.charAt(EditMode.insertion_index));
                    }
                    alreadyTyped = EditMode.insertInEdit(tts,alreadyTyped,searchValue,EditMode.insertion_index);

                    if(MyInputMethodService.isNumberMode == true | MyInputMethodService.numberModeToggle == 1 | MyInputMethodService.isspecialCharMode == true | MyInputMethodService.spModeToggle==1)
                    {
                        MyInputMethodService.isNumberMode = false;
                        MyInputMethodService.numberModeToggle = 0;

                        MyInputMethodService.isspecialCharMode = false;
                        MyInputMethodService.spModeToggle = 0;

                    }
                    MyInputMethodService.allowSearchScan = true;

                    //word = "";
                }
                else if(EditMode.editMode & EditMode.decision.equals("Replace"))
                {
                    if(alreadyTyped.charAt(EditMode.insertion_index) == ' ')
                    {
                        EditMode.speakReplacedAtSpace(tts,searchValue);
                    }
                    else
                    {
                        EditMode.speakReplacedAtCharacter(tts,searchValue,alreadyTyped.charAt(EditMode.insertion_index));
                    }
                    alreadyTyped = EditMode.replaceInEdit(tts,alreadyTyped,searchValue,EditMode.insertion_index);

                    if(MyInputMethodService.isNumberMode == true | MyInputMethodService.numberModeToggle == 1 | MyInputMethodService.isspecialCharMode == true | MyInputMethodService.spModeToggle==1)
                    {
                        MyInputMethodService.isNumberMode = false;
                        MyInputMethodService.numberModeToggle = 0;

                        MyInputMethodService.isspecialCharMode = false;
                        MyInputMethodService.spModeToggle = 0;

                    }
                    MyInputMethodService.allowSearchScan = true;

                    //word = "";
                }
                else
                {
                    //count("Selection");
                    if(searchValue.equals(" "))
                    {
                        alreadyTyped = addSpaceAtEnd(tts, alreadyTyped/*, word*/, searchValue);
                        //word = "";
                        //alreadyTyped = addSpaceAtEnd(tts,alreadyTyped,searchValue);
                    }
                    else
                    {
                        /*word*/ alreadyTyped = addAtEnd(tts, /*word*/alreadyTyped, searchValue);
                        //alreadyTyped = alreadyTyped;
                        //alreadyTyped = addAtEnd(tts,alreadyTyped,searchValue);
                    }
                }
            }
        }
        return alreadyTyped;
    }

    public String spModeDeletion(TextToSpeech tts,String alreadyTyped/*,String word*/)
    {
        if(alreadyTyped.isEmpty() /*& word.isEmpty()*/)
        {
            tts.speak("No Character to Delete", TextToSpeech.QUEUE_FLUSH, null, null);
            //nav_index=0;//After Deleting Entirely a word if you select a new word then it should start from first character
        }
        else
        {
            //Normal Deletion from End of the String in Number Mode
            del_char = alreadyTyped.substring(alreadyTyped.length()-1);

            if(del_char.equals(" "))
            {
                tts.speak("Space", TextToSpeech.QUEUE_ADD, null, null);
                tts.playEarcon(deleteChar,TextToSpeech.QUEUE_ADD,null,null);
            }
            else {
                tts.speak(del_char+"", TextToSpeech.QUEUE_ADD, null, null);
                tts.playEarcon(deleteChar,TextToSpeech.QUEUE_ADD,null,null);
            }
            alreadyTyped = alreadyTyped.substring(0,alreadyTyped.length()-1);
        }
        return alreadyTyped;
    }

    public void spModeSpeakOut(TextToSpeech tts, String alredyTyped)
    {

        speakOutSelection(tts,alredyTyped);
    }

    public void speakOutSelection(TextToSpeech tts,String text)
    {
        if(text.length() == 1 & text.charAt(0) == ' ')
        {
            tts.setPitch(1.5f);
            speakOut(tts,"No Character Selected to Speak Out");
            tts.setPitch(1.0f);
        }
        else
        {
            tts.setPitch(1.5f);
            speakOut(tts,text);
            tts.setPitch(1.0f);
        }
    }

    public void speakOut(TextToSpeech tts,String text)
    {
        if(!text.equals("STOP"))
        {
            tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
        }
    }

    public String addSpaceAtEnd(TextToSpeech tts,String alredyTyped/*,String word*/, String searchValue)
    {
        //alredyTyped = alredyTyped +" ";
        alredyTyped = alredyTyped + searchValue;
        tts.setPitch(2.0f);
        tts.speak("space", TextToSpeech.QUEUE_ADD, null, null);
        tts.setPitch(1.0f);
        return alredyTyped;
    }

    public String addAtEnd(TextToSpeech tts,String alreadyTyped,String searchValue)
    {
        tts.setPitch(2.0f);
        //tts.playEarcon(selectEarcon,TextToSpeech.QUEUE_FLUSH,null,null);
        if(searchValue.equals("#"))
        {
            //tts.playEarcon(selectEarcon,TextToSpeech.QUEUE_FLUSH,null,null);
            tts.speak("Hash",TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else if(searchValue.equals("$"))
        {
            //tts.playEarcon(selectEarcon,TextToSpeech.QUEUE_FLUSH,null,null);
            tts.speak("Dollar",TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else
        {
            //tts.playEarcon(selectEarcon,TextToSpeech.QUEUE_FLUSH,null,null);
            tts.speak(searchValue, TextToSpeech.QUEUE_ADD, null, null);
        }
        //tts.speak(searchValue, TextToSpeech.QUEUE_ADD, null, null);
        tts.setPitch(1.0f);
        alreadyTyped = alreadyTyped + searchValue;
        return alreadyTyped;
    }

}
