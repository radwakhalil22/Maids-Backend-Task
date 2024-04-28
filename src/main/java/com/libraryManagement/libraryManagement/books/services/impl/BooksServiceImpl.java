package com.libraryManagement.libraryManagement.books.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.libraryManagement.libraryManagement.books.entities.Books;
import com.libraryManagement.libraryManagement.books.models.mapinterface.BooksMapper;
import com.libraryManagement.libraryManagement.books.models.request.BookReqModel;
import com.libraryManagement.libraryManagement.books.models.response.BookResModel;
import com.libraryManagement.libraryManagement.books.repositories.BooksRepository;
import com.libraryManagement.libraryManagement.books.services.BooksService;
import com.libraryManagement.libraryManagement.borrowingRecord.entites.BorrowingRecord;
import com.libraryManagement.libraryManagement.borrowingRecord.repositories.BorrowingRecordRepository;
import com.libraryManagement.libraryManagement.exceptions.ApiErrorMessageKeyEnum;
import com.libraryManagement.libraryManagement.exceptions.BusinessLogicViolationException;

import jakarta.transaction.Transactional;


@Component
@Transactional
public class BooksServiceImpl implements BooksService {
	
	@Autowired
	private BooksRepository booksRepository;
	
	@Autowired
	private BooksMapper booksMapper;
	
    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;
	
	@Override
	@Transactional
	public BookResModel createBook(BookReqModel bookReqModel) {
	    Books book = booksMapper.mapToBooks(bookReqModel);
	    book = booksRepository.save(book);
	    BookResModel bookResModel = booksMapper.mapToBookResModel(book);
	    return bookResModel;
	}

	@Override
	public BookResModel getBookById(Long bookId) {
		BookResModel bookResModel = new BookResModel();
		bookResModel = booksMapper.mapToBookResModel(booksRepository.findById(bookId).get());
		return bookResModel;
	}
	
	@Override
	public BookResModel updateBookById(BookReqModel bookReqModel , Long bookId) {
		BookResModel bookResModel = new BookResModel();
		Books book = booksRepository.findById(bookId).get();
		Books updatedBook = booksMapper.mapToBooks(book, bookReqModel);
		booksRepository.save(updatedBook);
		bookResModel = booksMapper.mapToBookResModel(updatedBook);
		return bookResModel;
	}
	
	
	@Override
	public List<BookResModel> getAllBooks(Integer pageSize, Integer pageIndex, String sortField, String sortOrder) {
	    Pageable pageable = null;
	    if (sortField != null && !sortField.isBlank() && sortOrder != null && !sortOrder.isBlank()) {
	        pageable = PageRequest.of(pageIndex, pageSize,
					sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
							: Sort.by(sortField).descending());
	    } else {
	        pageable = PageRequest.of(pageIndex, pageSize);
	    }
	    
	    Page<Books> booksPage = booksRepository.findAll(pageable);
	    List<BookResModel> booksResModels = booksPage.getContent().stream()
	            .map(booksMapper::mapToBookResModel)
	            .collect(Collectors.toList());
	    
	    return booksResModels ;
	}
	
	@Override
	public void deleteBookById(Long bookId) {
		Optional<Books> optionalBook = booksRepository.findById(bookId);
		if (optionalBook.isPresent()) {
			Books book = optionalBook.get();
			List<BorrowingRecord> borrowingRecords = borrowingRecordRepository.findByBookIdAndReturnDateIsNotNull(bookId);
			if (!borrowingRecords.isEmpty()) {
				borrowingRecordRepository.deleteAll(borrowingRecords);
				booksRepository.deleteById(bookId);
			} else {
				List<BorrowingRecord> unreturnedRecords = borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId);
				if (!unreturnedRecords.isEmpty()) {
					throw new BusinessLogicViolationException(ApiErrorMessageKeyEnum.BCV_BOOK_SHOULD_BE_RETURNED.name());
				} else {
					booksRepository.deleteById(bookId);
				}
			}
		} else {
			booksRepository.deleteById(bookId);
			}
		}
//		Books book = booksRepository.findById(bookId).get();
//		List<BorrowingRecord> borrowingRecord = borrowingRecordRepository.findByBookIdAndReturnDateIsNotNull(bookId);
//		if(book != null && !borrowingRecord.isEmpty()) {
//			borrowingRecordRepository.deleteAll(borrowingRecord);
//			booksRepository.deleteById(bookId);
//		}
//		else if(book != null && !borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId).isEmpty()) {
//        	throw new BusinessLogicViolationException(ApiErrorMessageKeyEnum.BCV_BOOK_SHOULD_BE_RETURNED.name());
//		}else {
//			
//			booksRepository.deleteById(bookId);
//		}
//	}

	
}
