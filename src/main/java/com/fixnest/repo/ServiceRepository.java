package com.fixnest.repo;
import com.fixnest.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ServiceRepository extends JpaRepository<ServiceItem, Long> { List<ServiceItem> findByActiveTrue(); }
