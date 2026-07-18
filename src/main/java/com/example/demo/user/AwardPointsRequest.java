package com.example.demo.user;
import java.util.UUID;
public class AwardPointsRequest {
    private UUID token;
    private int amount;
    private String eventId;
    public UUID getToken(){
        return token;
    }
    public void setToken(UUID token){
        this.token = token;
    }
    public int getAmount(){
        return amount;
    }
    public void setAmount(int amount){
        this.amount = amount;
    }
    public String getEventId(){
        return eventId;
    }
    public void setEventId(String eventId){
        this.eventId = eventId;
    }


}
