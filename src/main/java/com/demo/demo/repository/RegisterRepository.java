package com.demo.demo.repository;

import com.demo.demo.entity.Register;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    // ✅ SỬA: Thêm dấu gạch dưới (_) để map đúng vào event.id
    boolean existsByAccount_IdAndEvent_Id(long accountId, long eventId);

    // ✅ SỬA: Tìm List Register theo Event ID
    List<Register> findByEvent_Id(long eventId);

    // ✅ SỬA: Tìm List Register theo Account ID
    List<Register> findByAccount_Id(long accountId);

    // ✅ SỬA QUAN TRỌNG NHẤT: Lấy danh sách ĐÃ check-in
    // Logic: Tìm trong bảng Register, cột Event có ID là X, và CheckInTime KHÁC NULL
    List<Register> findByEvent_IdAndCheckInTimeIsNotNull(long eventId);
    Optional<Register> findByAccount_IdAndEvent_Id(Long accountId, Long eventId);
}
