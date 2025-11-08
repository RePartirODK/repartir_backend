package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.DashboardStatsDto;
import com.example.repartir_backend.dto.MonthlyCountDto;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.CentreFormationRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UtilisateurRepository utilisateurRepository;
    private final CentreFormationRepository centreFormationRepository;
    private final AdminRepository adminRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(Integer yearOpt) {
        int year = yearOpt != null ? yearOpt : Year.now().getValue();

        List<Object[]> rows = utilisateurRepository.countMonthlyRegistrations(year);
        Map<Integer, Long> byMonth = new HashMap<>();
        for (Object[] row : rows) {
            Integer month = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            byMonth.put(month, count);
        }

        List<MonthlyCountDto> monthly = new ArrayList<>(12);
        for (int m = 1; m <= 12; m++) {
            monthly.add(MonthlyCountDto.builder()
                    .month(m)
                    .count(byMonth.getOrDefault(m, 0L))
                    .build());
        }

        long centres = centreFormationRepository.count();
        long blocked = utilisateurRepository.countByEstActiveFalse();
        long pending = utilisateurRepository.countByEtat(Etat.EN_ATTENTE);
        long adminsActive = adminRepository.count();

        return DashboardStatsDto.builder()
                .year(year)
                .monthlyRegistrations(monthly)
                .centresCount(centres)
                .blockedAccountsCount(blocked)
                .pendingAccountsCount(pending)
                .activeAdminsCount(adminsActive)
                .build();
    }
}


