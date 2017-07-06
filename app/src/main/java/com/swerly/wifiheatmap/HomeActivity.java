package com.swerly.wifiheatmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fabSetup();
    }

    private void fabSetup(){
        findViewById(R.id.home_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, FunctionalityActivity.class);
                startActivity(i);
            }
        });
    }

}
