package model;

public class InternalRecord {
    private final String id;
    private final String name;
    private final String email;
    private final long updatedAt;

    public InternalRecord(String id, String name, String email, long updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public long getUpdatedAt() { return updatedAt; }
}
