package com.demo.demo.service;

import com.demo.demo.entity.Account;
import com.demo.demo.entity.Event;
import com.demo.demo.entity.EventStatus;
import com.demo.demo.entity.Register;
import com.demo.demo.model.reponse.RegisterResponse;
import com.demo.demo.model.request.RegisterRequest;
import com.demo.demo.repository.AuthenticationRepository;
import com.demo.demo.repository.EventRepository;
import com.demo.demo.repository.RegisterRepository;
import jakarta.transaction.Transactional;
import com.demo.demo.exception.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;

@Service

public class RegisterEventService {
    private final RegisterRepository registerRepository;
    private final AuthenticationRepository accountRepository;
    private final EventRepository eventRepository;

    @Autowired
    public RegisterEventService(RegisterRepository registerRepository,AuthenticationRepository accountRepository, EventRepository eventRepository){
        this.registerRepository = registerRepository;
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
    }



    @Transactional
    public RegisterResponse registerForEvent(RegisterRequest requestModel)  {
        // 1. Check for duplicate registration
        if(registerRepository.existsByAccountIdAndEventId(requestModel.getAccountId(), requestModel.getEventId())) {
            throw new BadRequestException("Account is already registered for this event.");
        }

        // 2. Fetch related entities
        Account account = accountRepository.findById(requestModel.getAccountId())
                .orElseThrow(() -> new BadRequestException("Account not found with id: " + requestModel.getAccountId()));

        Event event = eventRepository.findById(requestModel.getEventId())
                .orElseThrow(() -> new BadRequestException("Event not found with id: " + requestModel.getEventId()));

        // 3. Business logic validation
        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.COMPLETED) {
            throw new BadRequestException("Cannot register for an event that is cancelled or completed.");
        }

        // 4. Create and save the new registration
        Register newRegister = new Register();
        newRegister.setAccount(account);
        newRegister.setEvent(event);
        // checkInTime and images are null/empty by default

        Register savedRegister = registerRepository.save(newRegister);

        return toModel(savedRegister);

    }
    private RegisterResponse toModel(Register register) {
        RegisterResponse Model = new RegisterResponse();
        Model.setId(register.getId());
        Model.setCheckInTime(register.getCheckInTime());
        Model.setImages(register.getImages());
        // Map parent IDs to avoid sending full objects
        if (register.getAccount() != null) {
            Model.setAccountId(register.getAccount().getId());
        }
        if (register.getEvent() != null) {
            Model.setEventId(register.getEvent().getId());
        }
        return Model;
    }

}
