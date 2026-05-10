package edu.zut.awir.awir8.service;

public class DuplicateEmailException extends RuntimeException {
	public DuplicateEmailException(String email) {
		super("Email already exists: " + email);
	}
}

