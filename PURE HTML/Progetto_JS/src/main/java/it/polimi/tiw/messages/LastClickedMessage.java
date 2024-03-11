package it.polimi.tiw.messages;

import java.util.Date;

public final class LastClickedMessage {

    private final String username;
    private final int id;
    private final Date date;

    public LastClickedMessage(String username, int id, Date date) {
        this.username = username;
        this.id = id;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }
}
