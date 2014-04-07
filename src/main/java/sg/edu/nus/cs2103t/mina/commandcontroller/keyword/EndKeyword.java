package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

public class EndKeyword extends StartKeyword {
    
    static {
        EndKeyword endPrototype = new EndKeyword();
        KeywordFactory.addAliasEntry(SimpleKeyword.END.getFormattedKeyword(), endPrototype);
        KeywordFactory.addAliasEntry("-end", endPrototype);
        KeywordFactory.addAliasEntry("-due", endPrototype);
        KeywordFactory.addAliasEntry("-by", endPrototype);
        KeywordFactory.addAliasEntry("-before", endPrototype);
        KeywordFactory.addAliasEntry("-on", endPrototype);
        KeywordFactory.addAliasEntry("-to", endPrototype);
    }
    
    public EndKeyword(StandardKeyword type) {
        super(type);
    }
    
    public EndKeyword(){
        this(SimpleKeyword.END);
        this.defaultTime = "235959";
    }
    
    @Override
    protected Keyword createKeyword() {
        return new EndKeyword();
    }
    
}
