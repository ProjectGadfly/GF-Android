package com.forvm.gadfly.projectgadfly;

public class TicketScript {
    private String ticketNumber;
    private String scriptTitle;

    public TicketScript(String ticketNumber, String scriptTitle) {
        this.ticketNumber = ticketNumber;
        this.scriptTitle = scriptTitle;
    }

    public String getTicket() {
        return ticketNumber;
    }

    public String getTitle() {
        return scriptTitle;
    }

}
