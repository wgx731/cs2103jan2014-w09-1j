package sg.edu.nus.cs2103t.mina.test.commandcontroller.keyword;

import java.text.ParseException;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.KeywordFactory;

public class KeywordFactoryDriver {
    public static void main(String[] args) throws ClassNotFoundException, ParseException{
        KeywordFactory keywordFactory = KeywordFactory.getInstance();
        keywordFactory.createKeyword("-priority");
        keywordFactory.createKeyword("-start");
        keywordFactory.createKeyword("-starting");
        keywordFactory.createKeyword("-from");
    }
}
