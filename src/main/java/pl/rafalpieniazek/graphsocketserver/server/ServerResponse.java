package pl.rafalpieniazek.graphsocketserver.server;

public enum ServerResponse {
    HI_NAME("HI %s"),
    HI_UUID("HI, I'M %s"),
    BYE("BYE %s, WE SPOKE FOR %d MS"),
    EDGE_ADDED("EDGE ADDED"),
    NODE_ADDED("NODE ADDED"),
    NODE_REMOVED("NODE REMOVED"),
    EDGE_REMOVED("EDGE REMOVED"),
    NODE_NOT_FOUND("ERROR: NODE NOT FOUND"),
    NODE_ALREADY_EXISTS("ERROR: NODE ALREADY EXISTS"),
    UNKNOWN_COMMAND("SORRY, I DIDN'T UNDERSTAND THAT");

    private String message;

    ServerResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
