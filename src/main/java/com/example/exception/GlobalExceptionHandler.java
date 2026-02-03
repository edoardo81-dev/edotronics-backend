package com.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/* ---------- helpers ---------- */
	private ApiError build(HttpStatus status, String message, HttpServletRequest req) {
		return new ApiError(OffsetDateTime.now().toString(), status.value(), status.getReasonPhrase(), message,
				req.getRequestURI());
	}

	/* ---------- 404 ---------- */
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		return ResponseEntity.status(status).body(build(status, ex.getMessage(), req));
	}

	/* ---------- 404 (endpoint/static resource non trovato) ---------- */
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		return ResponseEntity.status(status).body(build(status, "Endpoint non trovato", req));
	}

	/* ---------- 400 ---------- */
	@ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class })
	public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(build(status, ex.getMessage(), req));
	}

	/* JSON malformato / body non leggibile */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(build(status, "Request body non valido o JSON malformato", req));
	}

	/* Validazione @Valid su DTO */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": "
						+ (fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "valore non valido"))
				.collect(Collectors.joining("; "));

		if (msg.isBlank())
			msg = "Validazione fallita";
		return ResponseEntity.status(status).body(build(status, msg, req));
	}

	/* Validazione su @PathVariable/@RequestParam */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(build(status, ex.getMessage(), req));
	}

	/* ---------- 405 ---------- */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest req) {
		HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
		return ResponseEntity.status(status).body(build(status, "Metodo HTTP non supportato per questo endpoint", req));
	}

	/* ---------- 409 (business conflict) ---------- */
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.CONFLICT;
		return ResponseEntity.status(status).body(build(status, ex.getMessage(), req));
	}

	/* ---------- 409 (vincoli DB) ---------- */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.CONFLICT;
		return ResponseEntity.status(status)
				.body(build(status, "Violazione vincoli database (dato duplicato o relazione non valida)", req));
	}

	/* ---------- 500 ---------- */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return ResponseEntity.status(status).body(build(status, "Errore interno del server", req));
	}

	/* ---------- 400 (path variable / request param type mismatch) ---------- */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		String param = ex.getName(); // es: "category"
		Object value = ex.getValue(); // es: "accessori"
		String required = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "tipo atteso";

		String msg;
		// messaggio "pro" se è il tuo enum
		if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
			Object[] allowed = ex.getRequiredType().getEnumConstants();
			String allowedStr = java.util.Arrays.stream(allowed).map(Object::toString)
					.collect(java.util.stream.Collectors.joining(", "));
			msg = "Valore non valido per '" + param + "': " + value + ". Valori ammessi: " + allowedStr;
		} else {
			msg = "Valore non valido per '" + param + "': " + value + " (atteso: " + required + ")";
		}

		return ResponseEntity.status(status).body(build(status, msg, req));
	}

}
