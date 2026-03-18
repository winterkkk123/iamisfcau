package cn.edu.hdu.iamisfcaubackend.dto;

public class UserDto {
    private String id;
    private String name;
    private String role;
    private String password;
    private String gender;
    private String avatar;
    private String department;
    private String college;
    private String phone;

    public UserDto() {}

    public UserDto(String id, String name, String role, String password, String gender,
                   String avatar, String department, String college, String phone) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
        this.gender = gender;
        this.avatar = avatar;
        this.department = department;
        this.college = college;
        this.phone = phone;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}