package com.example.pomelipo;

public class WorkSession {
    private String sessionName;
    private int workTime;
    private int chillTime;
    private int repetitions;

    public WorkSession(String sessionName, int workTime, int chillTime, int repetitions) {
        this.sessionName = sessionName;
        this.workTime = workTime;
        this.chillTime = chillTime;
        this.repetitions = repetitions;
    }

    public WorkSession() {
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public int getWorkTime() {
        return workTime;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    public int getChillTime() {
        return chillTime;
    }

    public void setChillTime(int chillTime) {
        this.chillTime = chillTime;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }
}
