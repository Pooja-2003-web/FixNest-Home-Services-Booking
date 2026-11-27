package com.fixnest.web;
import com.fixnest.model.*; import com.fixnest.repo.*; import com.fixnest.service.*; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
  private final ServiceRepository services; private final BookingRepository bookingRepo; private final BookingService bookings; private final FaqRepository faqs; private final SupportRepository support;
  public AdminController(ServiceRepository s, BookingRepository br, BookingService b, FaqRepository f, SupportRepository sr){ services=s; bookingRepo=br; bookings=b; faqs=f; support=sr; }

  @GetMapping("/services") public List<ServiceItem> all(){ return services.findAll(); }
  @PostMapping("/services") public ServiceItem add(@RequestBody ServiceItem s){ s.setId(null); return services.save(s); }
  @PutMapping("/services/{id}") public ServiceItem update(@PathVariable Long id, @RequestBody ServiceItem s){ s.setId(id); return services.save(s); }
  @DeleteMapping("/services/{id}") public void delete(@PathVariable Long id){ services.deleteById(id); }

  @GetMapping("/bookings") public List<Booking> bookings(){ return bookingRepo.findAll(); }
  @PatchMapping("/bookings/{id}/status") public Booking status(@PathVariable Long id, @RequestBody Map<String,String> b){ return bookings.setStatus(id, BookingStatus.valueOf(b.get("status"))); }

  @GetMapping("/support") public List<SupportRequest> supportList(){ return support.findAllByOrderByIdDesc(); }
  @PatchMapping("/support/{id}") public SupportRequest supportStatus(@PathVariable Long id, @RequestBody Map<String,String> b){
    SupportRequest s = support.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
    s.setStatus(b.get("status")); return support.save(s); }

  @PostMapping("/faq") public ChatbotFaq addFaq(@RequestBody ChatbotFaq f){ f.setId(null); return faqs.save(f); }
  @DeleteMapping("/faq/{id}") public void delFaq(@PathVariable Long id){ faqs.deleteById(id); }
}
