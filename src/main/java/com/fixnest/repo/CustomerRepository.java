package com.fixnest.repo;
import com.fixnest.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CustomerRepository extends JpaRepository<Customer, Long> { Optional<Customer> findByPhone(String phone); }
