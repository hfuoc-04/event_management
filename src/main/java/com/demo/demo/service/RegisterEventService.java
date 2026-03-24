package com.demo.demo.service;

import com.demo.demo.entity.Account;
import com.demo.demo.entity.Event;
import com.demo.demo.entity.EventStatus;
import com.demo.demo.entity.Register;
import com.demo.demo.model.reponse.RegisterResponse;
import com.demo.demo.model.request.PostImageRequest;
import com.demo.demo.model.request.RegisterRequest;
import com.demo.demo.repository.AuthenticationRepository;
import com.demo.demo.repository.EventRepository;
import com.demo.demo.repository.RegisterRepository;
import com.demo.demo.exception.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class RegisterEventService {
    private final RegisterRepository registerRepository;
    private final AuthenticationRepository accountRepository;
    private final EventRepository eventRepository;

    @Autowired
    public RegisterEventService(RegisterRepository registerRepository,
                                AuthenticationRepository accountRepository,
                                EventRepository eventRepository) {
        this.registerRepository = registerRepository;
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
    }

    // =========================================================================
    // 1. ĐĂNG KÝ SỰ KIỆN
    // =========================================================================
    @Transactional
    public RegisterResponse registerForEvent(RegisterRequest requestModel) {
        // Kiểm tra duplicate dùng Account_Id và Event_Id
        if (registerRepository.existsByAccount_IdAndEvent_Id(requestModel.getAccountId(), requestModel.getEventId())) {
            throw new BadRequestException("Account is already registered for this event.");
        }

        Account account = accountRepository.findById(requestModel.getAccountId())
                .orElseThrow(() -> new BadRequestException("Account not found with id: " + requestModel.getAccountId()));

        Event event = eventRepository.findById(requestModel.getEventId())
                .orElseThrow(() -> new BadRequestException("Event not found with id: " + requestModel.getEventId()));

        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.COMPLETED) {
            throw new BadRequestException("Cannot register for an event that is cancelled or completed.");
        }

        Register newRegister = new Register();
        newRegister.setAccount(account);
        newRegister.setEvent(event);
        newRegister.setCheckInTime(null); // Mặc định là chưa check-in (NULL)

        Register savedRegister = registerRepository.save(newRegister);
        return toModel(savedRegister);
    }

    // =========================================================================
    // 2. LẤY DANH SÁCH ĐĂNG KÝ (GET)
    // =========================================================================
    public List<RegisterResponse> getRegistersByEventId(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new BadRequestException("Event not found with id: " + eventId);
        }
        // Gọi repo tìm theo Event_Id
        List<Register> registers = registerRepository.findByEvent_Id(eventId);
        return registers.stream().map(this::toModel).collect(Collectors.toList());
    }

    public List<RegisterResponse> getRegistersByAccountId(long accountId) {
        // Gọi repo tìm theo Account_Id
        List<Register> registers = registerRepository.findByAccount_Id(accountId);
        return registers.stream().map(this::toModel).collect(Collectors.toList());
    }

    // =========================================================================
    // 3. XỬ LÝ CHECK-IN (PUT) - Python gọi cái này
    // =========================================================================
    @Transactional
    public RegisterResponse checkIn(Long registerId) {
        System.out.println(">>> START CHECK-IN for Register ID: " + registerId);

        Register register = registerRepository.findById(registerId)
                .orElseThrow(() -> new BadRequestException("Registration not found with id: " + registerId));

        // Kiểm tra xem đã check-in chưa (checkInTime có null không?)
        if (register.getCheckInTime() != null) {
            System.out.println(">>> FAILED: User already checked in at " + register.getCheckInTime());
            throw new BadRequestException("User has already checked in.");
        }

        // Cập nhật thời gian thực
        register.setCheckInTime(new Date());

        // Lưu xuống Database
        Register updatedRegister = registerRepository.save(register);

        System.out.println(">>> SUCCESS: Checked in at " + updatedRegister.getCheckInTime());
        return toModel(updatedRegister);
    }

    // =========================================================================
    // 4. KIỂM TRA TRẠNG THÁI (GET) - Web gọi cái này
    // =========================================================================
    public boolean checkRegistrationStatus(long accountId, long eventId) {
        return registerRepository.existsByAccount_IdAndEvent_Id(accountId, eventId);
    }

    public List<RegisterResponse> getCheckedInUsersByEventId(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new BadRequestException("Event not found with id: " + eventId);
        }
        // Lấy danh sách những người có checkInTime KHÁC NULL
        List<Register> registers = registerRepository.findByEvent_IdAndCheckInTimeIsNotNull(eventId);

        return registers.stream().map(this::toModel).collect(Collectors.toList());
    }

    // =========================================================================
    // 5. UPLOAD ẢNH (PUT)
    // =========================================================================
    @Transactional
    public RegisterResponse addImages(Long registerId, PostImageRequest requestModel) {
        Register register = registerRepository.findById(registerId)
                .orElseThrow(() -> new BadRequestException("Registration not found with id: " + registerId));

        if (register.getCheckInTime() == null) {
            throw new BadRequestException("Must check in before posting images.");
        }
        if (requestModel.getImageUrls() == null || requestModel.getImageUrls().isEmpty()) {
            throw new BadRequestException("Image URL list cannot be empty.");
        }

        List<String> images = register.getImages();
        images.addAll(requestModel.getImageUrls());

        Register updatedRegister = registerRepository.save(register);
        return toModel(updatedRegister);
    }
    // =========================================================================
    // 7. HỦY ĐĂNG KÝ (DELETE) - [MỚI]
    // =========================================================================
    @Transactional
    public void cancelRegistration(long accountId, long eventId) {
        // 1. Tìm bản ghi đăng ký
        Register register = registerRepository.findByAccount_IdAndEvent_Id(accountId, eventId)
                .orElseThrow(() -> new BadRequestException("Registration not found for this account and event."));

        // 2. Nếu đã Check-in rồi thì không cho hủy
        if (register.getCheckInTime() != null) {
            throw new BadRequestException("Cannot cancel registration because you have already checked in.");
        }

        // 3. Nếu sự kiện đã kết thúc/hủy thì chặn luôn (Logic tùy chọn)
        if (register.getEvent().getStatus() == EventStatus.COMPLETED || register.getEvent().getStatus() == EventStatus.CANCELLED) {
            throw new BadRequestException("Cannot cancel registration for a completed or cancelled event.");
        }

        // 4. Xóa
        registerRepository.delete(register);
    }

    // =========================================================================
    // 6. MAPPER (Convert Entity -> DTO)
    // =========================================================================
    private RegisterResponse toModel(Register register) {
        RegisterResponse model = new RegisterResponse();
        model.setId(register.getId());
        model.setCheckInTime(register.getCheckInTime());
        model.setImages(register.getImages());

        // --- LOGIC QUAN TRỌNG NHẤT ---
        // Nếu checkInTime != null thì Frontend sẽ hiểu là checkIn = true
        model.setCheckIn(register.getCheckInTime() != null);
        // -----------------------------

        if (register.getEvent() != null) {
            model.setEventId(register.getEvent().getId());
            model.setEventName(register.getEvent().getName());
            model.setEventImage(register.getEvent().getImage());
            model.setStartTime(register.getEvent().getStartTime());

        }

        if (register.getAccount() != null) {
            model.setAccountId(register.getAccount().getId());
            model.setFullName(register.getAccount().getFullName());
            model.setEmail(register.getAccount().getEmail());
            model.setUserImage(register.getAccount().getImage());
        }

        return model;
    }


}
