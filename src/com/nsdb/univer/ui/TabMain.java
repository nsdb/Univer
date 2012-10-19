package com.nsdb.univer.ui;

import com.nsdb.univer.uiparent.ActiveFragmentHost;

import android.os.Bundle;
import android.widget.Button;

public class TabMain extends ActiveFragmentHost {
	
	Button bookmarkettab,professortab;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmain);

        addFragment(new BookMarketMain(this),"BookMarketMain",R.id.screen1);
        addFragment(new ProfessorMain(this),"ProfessorMain",R.id.screen2);
        bookmarkettab=(Button)findViewById(R.id.bookmarkettab);
        professortab=(Button)findViewById(R.id.professortab);
        bookmarkettab.setOnClickListener(new OnClickSwitcher(0));
        professortab.setOnClickListener(new OnClickSwitcher(1));
        
        switchScreen(0);
    }

}