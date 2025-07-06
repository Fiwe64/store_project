package storeproject.model;


public class User {
    private int id;
    private String name;
    private String cpf;
    private String password;
    private UserRole role;
    private String address;
    private String city;
    private String state;
    private String zip;

    // Construtor padrão
    public User() {}




    public User(int id, String name, String cpf, String password, UserRole role,
                String address, String city, String state, String zip) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.password = password;
        this.role = role;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }
    public User( String name, String cpf, String password, UserRole role,
                String address, String city, String state, String zip) {
        this.name = name;
        this.cpf = cpf;
        this.password = password;
        this.role = role;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
