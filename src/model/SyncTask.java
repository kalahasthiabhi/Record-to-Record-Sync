package model;

public class SyncTask {
    public final String crm;
    public final String recordId;
    public final String action;
    public final ExternalRecord payload;

    public SyncTask(String crm, String recordId, String action, ExternalRecord payload) {
        this.crm = crm;
        this.recordId = recordId;
        this.action = action;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "model.SyncTask{" +
                "crm='" + crm + '\'' +
                ", recordId='" + recordId + '\'' +
                ", action='" + action + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
