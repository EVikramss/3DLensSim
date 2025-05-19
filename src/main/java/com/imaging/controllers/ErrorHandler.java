package com.imaging.controllers;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler
	public String handleError(Exception e) {
		e.printStackTrace();
		return e.getMessage();
	}
}
