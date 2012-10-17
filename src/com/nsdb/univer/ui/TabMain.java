package com.nsdb.univer.ui;

import com.nsdb.univer.uisupporter.ActiveFragmentHost;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Button;

public class TabMain extends ActiveFragmentHost {
	
	int screenId;
	Fragment bookmarketmain,professormain;
	Button bookmarkettab,professortab;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmain);

        screenId=R.id.screen;
        bookmarketmain=new BookMarketMain4(this);
        professormain=new ProfessorMain(this);
        bookmarkettab=(Button)findViewById(R.id.bookmarkettab);
        professortab=(Button)findViewById(R.id.professortab);
        bookmarkettab.setOnClickListener(new OnClickSwitcher(screenId,bookmarketmain,"BookMarketMain"));
        professortab.setOnClickListener(new OnClickSwitcher(screenId,professormain,"ProfessorMain"));
        
        switchScreen(screenId,bookmarketmain,"BookMarketMain");
    }

}