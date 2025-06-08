package model;

public class SyncTask {
    private final CRM crm;
    private final Action action;
    private final ExternalRecord payload;


    public SyncTask(CRM crm, Action action, ExternalRecord payload) {
        this.crm = crm;
        this.action = action;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "model.SyncTask{" +
                "crm= " + crm.name()+ ',' +
                ", action= " + action.name() + ',' +
                ", payload= " + payload +
                '}';
    }
}
