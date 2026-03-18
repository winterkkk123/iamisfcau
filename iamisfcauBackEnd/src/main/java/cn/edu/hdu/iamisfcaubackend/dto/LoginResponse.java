package cn.edu.hdu.iamisfcaubackend.dto;

public class LoginResponse {
    private boolean success;
    private String message;

    private String id;
    private String name;
    private String role;
    private String college;
    private String avatar;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message, String id, String name, String role, String college, String avatar) {
        this.success = success;
        this.message = message;
        this.id = id;
        this.name = name;
        this.role = role;
        this.college = college;
        this.avatar = avatar;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}