package com.libraryManagement.libraryManagement.exceptions;

public enum ApiErrorMessageKeyEnum {
	
	BCV_BOOK_HAS_ALREADY_BEEN_RETURNED,
	BCV_BOOK_IS_NOT_AVAILABLE,
	BCV_BOOK_SHOULD_BE_RETURNED,
	BCV_PATRON_MUST_RETURN_ALL_BOOKS,
	BCV_PATRON_NOT_FOUND,
	BCV_BOOK_NOT_FOUND,
	INCORRECT_RESULT_SIZE_DATA_ACCESS,
	REFRENCE_ID_NOT_FOUND,
	RUN_TIME_EXCEPTION,
	NULL_POINTER_EXCEPTION,
	JDBC_CONNECTON_ERROR,
	DATABASE_CONSTRAINT_VIOLATION,
	PATH_PARAMETER_TYPE_MISS_MATCH,
	METHOD_NOT_ALLOWED,
	MEDIA_TYPE_NOT_ACCEPTED,
	MEDIA_TYPE_NOT_SUPPORTED,
	ACCESS_IS_DENIED,
	GENERAL_ERROR,
	MODEL_PARAMETER_TYPE_MISS_MATCH,
	INTERNAL_SERVER_ERROR;
	
	private ApiErrorMessageKeyEnum() {}

}
