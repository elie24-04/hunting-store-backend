package com.fypbackend.spring_boot.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryAlertResponse {
    private int threshold;
    private List<InventoryAlertDto> alerts;
}
