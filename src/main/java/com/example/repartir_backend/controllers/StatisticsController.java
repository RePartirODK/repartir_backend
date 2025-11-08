package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.DashboardStatsDto;
import com.example.repartir_backend.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/administrateurs/statistiques")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Retourne les statistiques du tableau de bord admin, incluant:
     * - Comptes par mois (année en cours par défaut)
     * - KPIs: centres, comptes bloqués, en attente, admins actifs
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDto> getDashboard(@RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statisticsService.getDashboardStats(year));
    }
}


