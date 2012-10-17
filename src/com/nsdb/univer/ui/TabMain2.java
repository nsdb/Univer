package com.nsdb.univer.ui;

import com.nsdb.univer.uisupporter.ActiveFragmentHost2;

import android.os.Bundle;
import android.widget.Button;

public class TabMain2 extends ActiveFragmentHost2 {
	
	Button bookmarkettab,professortab;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmain2);

        addFragment(new BookMarketMain4(this),"BookMarketMain",R.id.screen1);
        addFragment(new ProfessorMain(this),"ProfessorMain",R.id.screen2);
        bookmarkettab=(Button)findViewById(R.id.bookmarkettab);
        professortab=(Button)findViewById(R.id.professortab);
        bookmarkettab.setOnClickListener(new OnClickSwitcher(0));
        professortab.setOnClickListener(new OnClickSwitcher(1));
        
        switchScreen(0);
    }

}