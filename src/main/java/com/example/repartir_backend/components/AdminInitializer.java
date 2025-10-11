package com.example.repartir_backend.components;

import com.example.repartir_backend.services.AdminServices;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {
    private final AdminServices adminServices;

    public AdminInitializer(AdminServices adminServices) {
        this.adminServices = adminServices;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        adminServices.Admin();
    }
}
