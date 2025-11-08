package com.example.repartir_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private int year;
    private List<MonthlyCountDto> monthlyRegistrations;

    private long centresCount;
    private long blockedAccountsCount;
    private long pendingAccountsCount;
    private long activeAdminsCount;
}


