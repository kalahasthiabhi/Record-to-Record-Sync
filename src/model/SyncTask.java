package model;

public class SyncTask {
    public final CRM crm;
    public final Action action;
    public final ExternalRecord payload;

    public SyncTask(CRM crm, Action action, ExternalRecord payload) {
        this.crm = crm;
        this.action = action;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "model.SyncTask{" +
                "crm='" + crm.name()+ '\'' +
                ", action='" + action.name() + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
