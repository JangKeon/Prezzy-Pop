package gachon.mpclass.prezzy_pop;

class User {        //상속용
    private String key;
    private String nick;
    private String domain;

    User(){}

    User(String key, String nick, String domain){
        this.key = key;
        this.nick = nick;
        this.domain = domain;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick){
        this.nick = nick;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

}