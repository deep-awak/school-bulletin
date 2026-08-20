package api.poja.app.config;

import api.poja.app.service.AdminBootstrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapInitializer implements CommandLineRunner {

  private final AdminBootstrapService adminBootstrapService;

  @Override
  public void run(String... args) {
    try {
      adminBootstrapService.bootstrapAdminIfNeeded();
    } catch (Exception e) {
      log.error("Error during admin bootstrap: {}", e.getMessage(), e);
    }
  }
}
