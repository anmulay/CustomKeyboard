package com.example.customkeyboaard;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import static com.example.customkeyboaard.EarconManager.selectEarcon;

import java.util.ArrayList;

public class AutoSuggestionMode {
    private ArrayList SuggestionsResult = new ArrayList();
    int autoSuggestionHeadPoint = 0;

    /*
     * Fetch AutoSuggestions Result Set (Most 5 auto suggestions)
     * */
    public ArrayList<String> fetchAutoSuggestions(Context context, TextToSpeech tts, String alreadyTyped)
    {
        if (alreadyTyped.length() < 3)
        {
            tts.speak("Text is too small for Autosuggestion", TextToSpeech.QUEUE_ADD, null, null);
            MyInputMethodService.isAutoSuggestionMode = false;
            MyInputMethodService.autoSuggestionModeToggle=0;
            return null;
        }
        else
        {
            tts.speak("Fetching Auto Suggestions for "+alreadyTyped,TextToSpeech.QUEUE_FLUSH,null,null);
            MyInputMethodService.isAutoSuggestionMode=true;
            MyInputMethodService.autoSuggestionModeToggle=1;
            autoSuggestionHeadPoint=0;
            AutoSuggestionsParser SGP = new AutoSuggestionsParser();
            SuggestionsResult = (ArrayList) SGP.autoSuggestions(context,alreadyTyped);
            Log.d("Auto Suggestions",SuggestionsResult.toString());

        }
        if(SuggestionsResult.size() == 0)
        {
            tts.speak("No Suggestions Available",TextToSpeech.QUEUE_ADD,null,null);
            MyInputMethodService.isAutoSuggestionMode = false;
            MyInputMethodService.autoSuggestionModeToggle=0;
            return null;
        }
        return SuggestionsResult;
    }

    /*
     * Forward Navigation through Auto Suggestions
     * eg: auto suggestions for 'Ind' are 'Indiana' | 'Indianapolis' | 'India'
     * */
    public void forwardNavigateSuggestions(TextToSpeech tts,ArrayList<String> suggestionsResult)
    {
        if(autoSuggestionHeadPoint==SuggestionsResult.size())
        {
            autoSuggestionHeadPoint=0;
        }

        tts.speak(suggestionsResult.get(autoSuggestionHeadPoint),TextToSpeech.QUEUE_ADD,null,null);
        autoSuggestionHeadPoint++;
    }

    /*
     * Select the Auto Suggestion
     * eg: 'Indiana' is Selected
     * */
    public String selectAutoSuggestion(TextToSpeech tts)
    {
        String searchValue="";
        if(SuggestionsResult.size() == 0)
        {
            tts.speak("Navigate to Listen to Autosuggestions",TextToSpeech.QUEUE_ADD,null,null);
        }
        else
        {
            tts.setPitch(1.5f);
            searchValue = (String) SuggestionsResult.get(autoSuggestionHeadPoint - 1);
            tts.playEarcon(selectEarcon,TextToSpeech.QUEUE_FLUSH,null,null);
            tts.speak(searchValue, TextToSpeech.QUEUE_ADD, null, null);
            tts.setPitch(1.0f);
        }
        autoSuggestionHeadPoint=0;
        MyInputMethodService.isAutoSuggestionMode=false;
        MyInputMethodService.autoSuggestionModeToggle=0;

        return searchValue;
    }
}
