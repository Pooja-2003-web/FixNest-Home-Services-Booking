package com.fixnest.web;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; import java.util.Map;
/** Sends clean {"message": "..."} errors that the website shows to the user. */
@RestControllerAdvice
public class ApiErrors {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String,String>> status(ResponseStatusException e){ return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason() == null ? "Error" : e.getReason())); }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,String>> invalid(MethodArgumentNotValidException e){
    String m = e.getBindingResult().getFieldErrors().stream().map(f -> f.getDefaultMessage()).findFirst().orElse("Invalid input");
    return ResponseEntity.badRequest().body(Map.of("message", m)); }
}
