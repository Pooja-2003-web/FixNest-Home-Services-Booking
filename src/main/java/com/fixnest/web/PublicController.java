package com.fixnest.web;
import com.fixnest.model.*; import com.fixnest.repo.*; import com.fixnest.service.*; import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*; import java.util.*;

@RestController @RequestMapping("/api")
public class PublicController {
  public record SupportBody(@NotBlank(message="Name is required") String name, @Pattern(regexp="\\d{10}", message="Phone must be 10 digits") String phone,
      String type, String preferredTime, String message) {}
  private final ServiceRepository services; private final BookingService bookings; private final ChatService chat; private final FaqRepository faqs;
  private final AuthService auth; private final SupportRepository support;
  public PublicController(ServiceRepository s, BookingService b, ChatService c, FaqRepository f, AuthService a, SupportRepository sr){ services=s; bookings=b; chat=c; faqs=f; auth=a; support=sr; }

  @GetMapping("/services") public List<ServiceItem> services(){ return services.findByActiveTrue(); }

  @PostMapping("/auth/register") public Map<String,Object> register(@Valid @RequestBody AuthService.RegisterRequest r){ return auth.register(r); }
  @PostMapping("/auth/login") public Map<String,Object> login(@Valid @RequestBody AuthService.LoginRequest r){ return auth.login(r); }

  @GetMapping("/me/bookings") public List<Booking> mine(@RequestHeader(value="Authorization", required=false) String h){ return bookings.mine(auth.require(h)); }
  @PostMapping("/bookings") public Booking book(@RequestHeader(value="Authorization", required=false) String h, @Valid @RequestBody BookingService.BookingRequest r){ return bookings.create(auth.require(h), r); }
  @PostMapping("/bookings/{id}/pay") public Booking pay(@RequestHeader(value="Authorization", required=false) String h, @PathVariable Long id, @RequestBody BookingService.PayRequest r){ return bookings.pay(auth.require(h), id, r); }
  @PatchMapping("/bookings/{id}/reschedule") public Booking reschedule(@RequestHeader(value="Authorization", required=false) String h, @PathVariable Long id, @Valid @RequestBody BookingService.RescheduleRequest r){ return bookings.reschedule(auth.require(h), id, r); }
  @PatchMapping("/bookings/{id}/cancel") public Booking cancel(@RequestHeader(value="Authorization", required=false) String h, @PathVariable Long id){ return bookings.cancel(auth.require(h), id); }

  @GetMapping("/chat/faq") public List<ChatbotFaq> faq(){ return faqs.findAll(); }
  @PostMapping("/chat") public ChatService.Reply ask(@RequestBody Map<String,String> body){ return chat.answer(body.getOrDefault("message","")); }

  @PostMapping("/support") public SupportRequest request(@RequestHeader(value="Authorization", required=false) String h, @Valid @RequestBody SupportBody b){
    SupportRequest s = new SupportRequest(); Customer c = auth.optional(h);
    s.setCustomerId(c == null ? null : c.getId()); s.setName(b.name()); s.setPhone(b.phone());
    s.setType("AGENT".equals(b.type()) ? "AGENT" : "CALLBACK"); s.setPreferredTime(b.preferredTime()); s.setMessage(b.message());
    return support.save(s);
  }
}
