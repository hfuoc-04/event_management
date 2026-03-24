package com.demo.demo.model.reponse;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RegisterResponse {
    private long id;
    private long accountId;
    private long eventId;
    private Date checkInTime;
    private List<String> images;

    // Biến bạn yêu cầu
    private boolean isCheckIn;

    // Thông tin Account (để hiện danh sách người tham gia)
    private String fullName;
    private String email;
    private String userImage;

    // 👇 THÊM CÁC BIẾN NÀY ĐỂ FE SHOW LIST SỰ KIỆN ĐÃ ĐĂNG KÝ
    private String eventName;
    private String eventImage;
    private Date startTime;
}
