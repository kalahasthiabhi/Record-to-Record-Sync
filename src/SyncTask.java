public class SyncTask {
    public final String crm;
    public final String recordId;
    public final String action;
    public final String payload;

    public SyncTask(String crm, String recordId, String action, String payload) {
        this.crm = crm;
        this.recordId = recordId;
        this.action = action;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "SyncTask{" +
                "crm='" + crm + '\'' +
                ", recordId='" + recordId + '\'' +
                ", action='" + action + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
