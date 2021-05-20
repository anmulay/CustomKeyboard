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

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    static EditText etVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// Instantiate google Text-To-Speech Engine

        etVal = findViewById(R.id.etVal);
    }
}
