package com.fixnest.repo;
import com.fixnest.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface BookingRepository extends JpaRepository<Booking, Long> {
  List<Booking> findByCustomerPhoneOrderByCreatedAtDesc(String phone);
  List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
