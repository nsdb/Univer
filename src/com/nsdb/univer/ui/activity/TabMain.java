package com.nsdb.univer.ui.activity;

import com.nsdb.univer.R;
import com.nsdb.univer.ui.fragment.BoardMain;
import com.nsdb.univer.ui.fragment.BookMarketMain;
import com.nsdb.univer.ui.fragment.ChatRoomMain;
import com.nsdb.univer.ui.fragment.ProfessorMain;

import android.os.Bundle;
import android.widget.Button;

public class TabMain extends ActiveFragmentHost {
	
	Button bookmarketbtn,professorbtn,chatroombtn,boardbtn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmain);

        addFragment(new BookMarketMain(this),"BookMarketMain",R.id.bookmarketscreen);
        addFragment(new ProfessorMain(this),"ProfessorMain",R.id.professorscreen);
        addFragment(new ChatRoomMain(this),"ChatRoomMain",R.id.chatroomscreen);
        addFragment(new BoardMain(this),"BoardMain",R.id.boardscreen);
        bookmarketbtn=(Button)findViewById(R.id.bookmarketbtn);
        professorbtn=(Button)findViewById(R.id.professorbtn);
        chatroombtn=(Button)findViewById(R.id.chatroombtn);
        boardbtn=(Button)findViewById(R.id.boardbtn);
        bookmarketbtn.setOnClickListener(new OnClickSwitcher(0));
        professorbtn.setOnClickListener(new OnClickSwitcher(1));
        chatroombtn.setOnClickListener(new OnClickSwitcher(2));
        boardbtn.setOnClickListener(new OnClickSwitcher(3));
        
    	switchScreen(0);
    }

}