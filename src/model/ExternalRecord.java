package model;

public class ExternalRecord {
    private final String id;
    private final String displayName;
    private final String contactEmail;

    public ExternalRecord(String id, String displayName, String contactEmail) {
        this.id = id;
        this.displayName = displayName;
        this.contactEmail = contactEmail;
    }

    @Override
    public String toString() {
        return "id = " + id +
                ", displayName = " + displayName +
                ", contactEmail = " + contactEmail;
    }
}
