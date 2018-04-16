package pl.rafalpieniazek.graphsocketserver.server;

public enum Events {
    HI_NAME("HI %s"),
    HI_UUID("HI, I'M %s"),
    UNKNOW_COMMAND("SORRY, I DIDN'T UNDERSTAND THAT");

    private String message;

    Events(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
