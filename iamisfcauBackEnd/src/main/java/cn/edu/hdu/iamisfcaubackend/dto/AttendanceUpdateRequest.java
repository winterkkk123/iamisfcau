package cn.edu.hdu.iamisfcaubackend.dto;

public class AttendanceUpdateRequest {

    private String attendStatus; // 已出勤 / 未出勤 / 请假
    private Boolean isLate;

    public String getAttendStatus() {
        return attendStatus;
    }

    public void setAttendStatus(String attendStatus) {
        this.attendStatus = attendStatus;
    }

    public Boolean getIsLate() {
        return isLate;
    }

    public void setIsLate(Boolean isLate) {
        this.isLate = isLate;
    }
}