package com.fixnest.service;
import com.fixnest.model.Customer; import com.fixnest.repo.CustomerRepository; import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException; import java.util.*; import java.util.concurrent.ConcurrentHashMap;

/** Customer register / login. Passwords are BCrypt-hashed; sessions are simple in-memory tokens. */
@Service
public class AuthService {
  public record RegisterRequest(@NotBlank(message="Name is required") String name, @Pattern(regexp="\\d{10}", message="Phone must be 10 digits") String phone,
      String email, @Size(min=6, message="Password must be at least 6 characters") String password) {}
  public record LoginRequest(@NotBlank(message="Phone is required") String phone, @NotBlank(message="Password is required") String password) {}

  private final CustomerRepository customers;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
  private final Map<String, Long> tokens = new ConcurrentHashMap<>();
  public AuthService(CustomerRepository c){ customers = c; }

  public Map<String, Object> register(RegisterRequest r) {
    Customer c = customers.findByPhone(r.phone()).orElseGet(Customer::new); // reuses a customer created by an older booking
    if (c.getPasswordHash() != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "This phone number is already registered. Please log in.");
    c.setName(r.name().trim()); c.setPhone(r.phone()); c.setEmail(r.email()); c.setPasswordHash(encoder.encode(r.password()));
    return session(customers.save(c));
  }
  public Map<String, Object> login(LoginRequest r) {
    Customer c = customers.findByPhone(r.phone().trim()).filter(x -> x.getPasswordHash() != null)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not registered. Please register first."));
    if (!encoder.matches(r.password(), c.getPasswordHash()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password. Please try again.");
    return session(c);
  }
  private Map<String, Object> session(Customer c) {
    String t = UUID.randomUUID().toString(); tokens.put(t, c.getId());
    Map<String, Object> m = new HashMap<>(); m.put("token", t); m.put("customer", c); return m;
  }
  public Customer require(String header) {
    Long id = (header != null && header.startsWith("Bearer ")) ? tokens.get(header.substring(7)) : null;
    if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in first");
    return customers.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in first"));
  }
  public Customer optional(String header) { try { return require(header); } catch (Exception e) { return null; } }
}
