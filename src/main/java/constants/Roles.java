package constants;

public enum Roles {
    USER("user"),
    ADMIN("admin"),
    SUPERVISOR("supervisor");

    private final String role;

    Roles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
