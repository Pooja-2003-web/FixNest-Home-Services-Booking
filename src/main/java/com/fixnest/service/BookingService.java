package com.fixnest.service;
import com.fixnest.model.*; import com.fixnest.repo.*; import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException; import java.time.LocalDate; import java.util.List;

@Service
public class BookingService {
  public record BookingRequest(@NotNull(message="Choose a service") Long serviceId,
      @NotNull(message="Choose a date") @FutureOrPresent(message="Date cannot be in the past") LocalDate date,
      @NotBlank(message="Choose a time") String time, @NotBlank(message="Address is required") String address) {}
  public record RescheduleRequest(@NotNull(message="Choose a date") @FutureOrPresent(message="Date cannot be in the past") LocalDate date,
      @NotBlank(message="Choose a time") String time) {}
  public record PayRequest(String method, boolean simulateFailure) {}

  private final ServiceRepository services; private final BookingRepository bookings;
  public BookingService(ServiceRepository s, BookingRepository b){ services=s; bookings=b; }

  @Transactional
  public Booking create(Customer c, BookingRequest r) {
    ServiceItem s = services.findById(r.serviceId()).filter(ServiceItem::isActive)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not available"));
    Booking b = new Booking(); b.setCustomer(c); b.setService(s); b.setBookingDate(r.date()); b.setTimeSlot(r.time()); b.setAddress(r.address()); b.setAmount(s.getPrice());
    Payment p = new Payment(); p.setBooking(b); p.setAmount(s.getPrice()); b.setPayment(p);
    return bookings.save(b);
  }
  /** Simulated payment: no gateway, just updates statuses. */
  @Transactional
  public Booking pay(Customer c, Long id, PayRequest r) {
    Booking b = owned(c, id); Payment p = b.getPayment();
    if (p.getStatus() == PaymentStatus.PAID) throw new ResponseStatusException(HttpStatus.CONFLICT, "Already paid");
    p.setMethod(r.method() == null ? "UPI" : r.method());
    boolean ok = !r.simulateFailure();
    p.setStatus(ok ? PaymentStatus.PAID : PaymentStatus.FAILED);
    if (ok) b.setStatus(BookingStatus.CONFIRMED);
    return b;
  }
  @Transactional
  public Booking reschedule(Customer c, Long id, RescheduleRequest r) {
    Booking b = owned(c, id); active(b); b.setBookingDate(r.date()); b.setTimeSlot(r.time()); return b;
  }
  @Transactional
  public Booking cancel(Customer c, Long id) { Booking b = owned(c, id); active(b); b.setStatus(BookingStatus.CANCELLED); return b; }

  private Booking owned(Customer c, Long id) {
    Booking b = get(id);
    if (!b.getCustomer().getId().equals(c.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This is not your booking");
    return b;
  }
  private void active(Booking b) {
    if (b.getStatus() != BookingStatus.PENDING && b.getStatus() != BookingStatus.CONFIRMED)
      throw new ResponseStatusException(HttpStatus.CONFLICT, "This booking can no longer be changed");
  }
  public Booking get(Long id){ return bookings.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found")); }
  public List<Booking> mine(Customer c){ return bookings.findByCustomerIdOrderByCreatedAtDesc(c.getId()); }
  @Transactional public Booking setStatus(Long id, BookingStatus st){ Booking b = get(id); b.setStatus(st); return b; }
}
