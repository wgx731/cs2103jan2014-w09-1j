package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;


public enum CompositeKeyword implements StandardKeyword {
    AGENDAOF("AgendaOf"),
    URGENT("Urgent");
    
    private String _type;
    private CompositeKeyword(String type) {
        _type = type;
    }
    @Override
    public String getFilePrefix() {
        return _type + "Composite";
    }
    @Override
    public String getFormattedKeyword() {
        return DELIMITER + getKeyword();
    }
    @Override
    public String getKeyword() {
        return _type.toLowerCase();
    }
}
