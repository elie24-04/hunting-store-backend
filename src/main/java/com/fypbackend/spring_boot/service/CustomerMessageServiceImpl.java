package com.fypbackend.spring_boot.service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.fypbackend.spring_boot.dao.CustomerMessageRepository;
import com.fypbackend.spring_boot.dto.message.AdminCustomerMessageResponse;
import com.fypbackend.spring_boot.dto.message.CustomerMessageCreateRequest;
import com.fypbackend.spring_boot.entity.CustomerMessage;
import com.fypbackend.spring_boot.entity.MessageStatus;
import com.fypbackend.spring_boot.entity.MessageType;

@Service
public class CustomerMessageServiceImpl implements CustomerMessageService {

    private final CustomerMessageRepository customerMessageRepository;

    public CustomerMessageServiceImpl(CustomerMessageRepository customerMessageRepository) {
        this.customerMessageRepository = customerMessageRepository;
    }

    @Override
    public UUID createMessage(CustomerMessageCreateRequest request) {
        CustomerMessage message = new CustomerMessage();
        message.setName(request.getName().trim());
        message.setEmail(request.getEmail().trim());
        message.setPhone(StringUtils.hasText(request.getPhone()) ? request.getPhone().trim() : null);
        message.setSubject(request.getSubject().trim());
        message.setMessage(request.getMessage().trim());
        message.setType(request.getType());
        message.setStatus(MessageStatus.NEW);

        return customerMessageRepository.save(message).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminCustomerMessageResponse> adminListMessages(
            MessageStatus status,
            MessageType type,
            String q,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Pageable pageable) {

        Specification<CustomerMessage> specification = Specification.where(null);

        if (status != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (type != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (StringUtils.hasText(q)) {
            String keyword = "%" + q.trim().toLowerCase(Locale.ROOT) + "%";
            specification = specification.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), keyword),
                    cb.like(cb.lower(root.get("email")), keyword),
                    cb.like(cb.lower(root.get("subject")), keyword),
                    cb.like(cb.lower(root.get("message")), keyword)));
        }
        if (dateFrom != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
        }
        if (dateTo != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), dateTo));
        }

        return customerMessageRepository.findAll(specification, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public AdminCustomerMessageResponse updateStatus(UUID id, MessageStatus status) {
        CustomerMessage customerMessage = getEntity(id);
        if (status == MessageStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status update supports READ or REPLIED only");
        }

        customerMessage.setStatus(status);
        return toResponse(customerMessageRepository.save(customerMessage));
    }

    @Override
    @Transactional
    public AdminCustomerMessageResponse updateAdminNote(UUID id, String note) {
        CustomerMessage customerMessage = getEntity(id);
        customerMessage.setAdminNote(StringUtils.hasText(note) ? note.trim() : null);
        return toResponse(customerMessageRepository.save(customerMessage));
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCustomerMessageResponse getById(UUID id) {
        return toResponse(getEntity(id));
    }

    private CustomerMessage getEntity(UUID id) {
        return customerMessageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer message not found"));
    }

    private AdminCustomerMessageResponse toResponse(CustomerMessage message) {
        return new AdminCustomerMessageResponse(
                message.getId(),
                message.getName(),
                message.getEmail(),
                message.getPhone(),
                message.getSubject(),
                message.getMessage(),
                message.getType(),
                message.getStatus(),
                message.getAdminNote(),
                message.getCreatedAt(),
                message.getUpdatedAt());
    }
}
