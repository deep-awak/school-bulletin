package api.poja.app.endpoint;

import api.poja.app.config.JwtTokenProvider;
import api.poja.app.jpa.UserEntity;
import api.poja.app.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String password = request.get("password");

    log.info("Login attempt for: {}", email);

    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email, password));

      log.info("Authentication successful for: {}", email);

      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      UserEntity user =
          userRepository
              .findByEmail(email)
              .orElseThrow(() -> new RuntimeException("User not found"));

      String token =
          jwtTokenProvider.generateToken(
              user.getId(), user.getEmail(), user.getRole().getName().name());

      Map<String, String> response = new HashMap<>();
      response.put("token", token);
      response.put("role", user.getRole().getName().name());
      response.put("email", user.getEmail());
      response.put("fullName", user.getFullName());

      return ResponseEntity.ok(response);

    } catch (AuthenticationException e) {
      log.error("Authentication failed for {}: {}", email, e.getMessage());
      return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
  }
}
