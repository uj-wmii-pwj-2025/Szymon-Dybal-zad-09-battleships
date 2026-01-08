package uj.wmii.pwj.collections.gameHelpers;

public enum ShotResult {
    PUDLO("pudlo"),
    TRAFIONY("trafiony"),
    TRAFIONY_ZATOPIONY("Trafiony zatopiony"),
    WSZYSTKIE_ZATOPIONE("ostatni zatopiony");

    private String message;
    ShotResult(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }

}
