package com.example.cmput301project.itemClasses;

import java.util.UUID;

public class UniqueId {
    private long msb;
    private long lsb;
    public UniqueId(){
        UUID value = UUID.randomUUID();
        this.msb = value.getMostSignificantBits();
        this.lsb = value.getLeastSignificantBits();
    }

    public UniqueId(long msb, long lsb){
        this.msb = msb;
        this.lsb = lsb;
    }

    public String toString(){
        return (new UUID(msb, lsb)).toString();
    }

    public static UniqueId fromString(String value){
        UUID uuid = UUID.fromString(value);
        return new UniqueId(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    public long getMsb() {
        return msb;
    }

    public void setMsb(long msb) {
        this.msb = msb;
    }

    public long getLsb() {
        return lsb;
    }

    public void setLsb(long lsb) {
        this.lsb = lsb;
    }
}
