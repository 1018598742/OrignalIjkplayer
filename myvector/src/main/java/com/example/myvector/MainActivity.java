package com.example.myvector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView mImaeView;
    private boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImaeView = ((ImageView) findViewById(R.id.imageView));
        mImaeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = !isChecked;
                final int [] stateSet = {android.R.attr.state_checked * (isChecked ?1:-1)};
                mImaeView.setImageState(stateSet,true);
             }
        });
    }
}
