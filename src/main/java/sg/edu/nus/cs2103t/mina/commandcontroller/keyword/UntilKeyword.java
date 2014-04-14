package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

//@author A0099151B
public class UntilKeyword extends EndKeyword {

    static {
        UntilKeyword untilKeyword = new UntilKeyword();
        KeywordFactory.addAliasEntry("-until", untilKeyword);
    }

    public UntilKeyword(StandardKeyword type) {
        super(type);
    }

    public UntilKeyword() {
        this(SimpleKeyword.UNTIL);
        _defaultTime = "235959";
    }

    @Override
    protected Keyword createKeyword() {
        return new UntilKeyword();
    }
}
