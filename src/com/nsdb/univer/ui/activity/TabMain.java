package com.nsdb.univer.ui.activity;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.ui.FontSetter;
import com.nsdb.univer.ui.fragment.BoardMain;
import com.nsdb.univer.ui.fragment.BookMarketMain;
import com.nsdb.univer.ui.fragment.ChatRoomMain;
import com.nsdb.univer.ui.fragment.OptionMain;
import com.nsdb.univer.ui.fragment.ProfessorMain;

import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;

public class TabMain extends ActiveFragmentHost {
	
	ImageButton bookmarketbtn,professorbtn,chatroombtn,boardbtn,optionbtn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmain);
        FontSetter.setDefault(this);

        // Tab Activate
        addFragment(new BookMarketMain(this),"BookMarketMain",R.id.bookmarketscreen);
        addFragment(new ProfessorMain(this),"ProfessorMain",R.id.professorscreen);
        addFragment(new ChatRoomMain(this),"ChatRoomMain",R.id.chatroomscreen);
        addFragment(new BoardMain(this),"BoardMain",R.id.boardscreen);
        addFragment(new OptionMain(this),"OptionMain",R.id.optionscreen);
        bookmarketbtn=(ImageButton)findViewById(R.id.bookmarketbtn);
        professorbtn=(ImageButton)findViewById(R.id.professorbtn);
        chatroombtn=(ImageButton)findViewById(R.id.chatroombtn);
        boardbtn=(ImageButton)findViewById(R.id.boardbtn);
        optionbtn=(ImageButton)findViewById(R.id.optionbtn);
        bookmarketbtn.setOnClickListener(new OnClickSwitcher(0));
        professorbtn.setOnClickListener(new OnClickSwitcher(1));
        chatroombtn.setOnClickListener(new OnClickSwitcher(2));
        boardbtn.setOnClickListener(new OnClickSwitcher(3));
        optionbtn.setOnClickListener(new OnClickSwitcher(4));
        if(getIntent().getBooleanExtra("noti",false)==true)
        	switchScreen(2);
        else
        	switchScreen(0);
    	
    	// Tab Resize
    	Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
    	int convertX=display.getWidth()/5;
    	int convertY=Math.round((float)(convertX*98)/128);
    	bookmarketbtn.setLayoutParams(new LayoutParams(convertX,convertY));
    	professorbtn.setLayoutParams(new LayoutParams(convertX,convertY));
    	chatroombtn.setLayoutParams(new LayoutParams(convertX,convertY));
    	boardbtn.setLayoutParams(new LayoutParams(convertX,convertY));
    	optionbtn.setLayoutParams(new LayoutParams(convertX,convertY));
    }
    

    @Override
    protected void switchScreen(int position) {
    	super.switchScreen(position);
    	bookmarketbtn.setImageResource(R.drawable.cm_tab1);
    	professorbtn.setImageResource(R.drawable.cm_tab2);
    	chatroombtn.setImageResource(R.drawable.cm_tab3);
    	boardbtn.setImageResource(R.drawable.cm_tab4);
    	optionbtn.setImageResource(R.drawable.cm_tab5);
    	switch(getCurrentPosition()) {
    	case 0: bookmarketbtn.setImageResource(R.drawable.cm_tab1_selected); break;
    	case 1: professorbtn.setImageResource(R.drawable.cm_tab2_selected); break;
    	case 2: chatroombtn.setImageResource(R.drawable.cm_tab3_selected); break;
    	case 3: boardbtn.setImageResource(R.drawable.cm_tab4_selected); break;
    	case 4: optionbtn.setImageResource(R.drawable.cm_tab5_selected); break;
    	}
    }
}