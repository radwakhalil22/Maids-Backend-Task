package com.libraryManagement.libraryManagement.patron.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.libraryManagement.libraryManagement.borrowingRecord.entites.BorrowingRecord;
import com.libraryManagement.libraryManagement.borrowingRecord.repositories.BorrowingRecordRepository;
import com.libraryManagement.libraryManagement.exceptions.ApiErrorMessageKeyEnum;
import com.libraryManagement.libraryManagement.exceptions.BusinessLogicViolationException;
import com.libraryManagement.libraryManagement.patron.entities.Patron;
import com.libraryManagement.libraryManagement.patron.models.mapinterface.PatronMapper;
import com.libraryManagement.libraryManagement.patron.models.request.PatronReqModel;
import com.libraryManagement.libraryManagement.patron.models.response.PatronResModel;
import com.libraryManagement.libraryManagement.patron.repositories.PatronRepository;
import com.libraryManagement.libraryManagement.patron.services.PatronService;

import jakarta.transaction.Transactional;


@Component
@Transactional
public class PatronServiceImpl implements PatronService {
	
	@Autowired
	private PatronRepository patronRepository;
	
	@Autowired
	private PatronMapper patronMapper;
	
    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;
	
	@Override
	@Transactional
	public PatronResModel createPatron(PatronReqModel patronReqModel) {
	    Patron patron = patronMapper.mapToPatron(patronReqModel);
	    patron = patronRepository.save(patron);
	    PatronResModel patronResModel = patronMapper.mapToPatronResModel(patron);
	    return patronResModel;
	}

	@Override
	public PatronResModel getPatronById(Long patronId) {
		PatronResModel patronResModel = new PatronResModel();
//		clientRepo.findById(clientId).get();
		patronResModel = patronMapper.mapToPatronResModel(patronRepository.findById(patronId).get());
		return patronResModel;
	}
	
	@Override
	public PatronResModel updatePatronById(PatronReqModel patronReqModel , Long patronId) {
		PatronResModel patronResModel = new PatronResModel();
		Patron patron = patronRepository.findById(patronId).get();
		Patron updatedPatron = patronMapper.mapToPatron(patron, patronReqModel);
		patronRepository.save(updatedPatron);
		patronResModel = patronMapper.mapToPatronResModel(updatedPatron);
		return patronResModel;
	}
	
	
	@Override
	public List<PatronResModel> getAllPatrons(Integer pageSize, Integer pageIndex, String sortField, String sortOrder) {
	    Pageable pageable = null;
	    if (sortField != null && !sortField.isBlank() && sortOrder != null && !sortOrder.isBlank()) {
	        pageable = PageRequest.of(pageIndex, pageSize,
					sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
							: Sort.by(sortField).descending());
	    } else {
	        pageable = PageRequest.of(pageIndex, pageSize);
	    }
	    
	    Page<Patron> patronPage = patronRepository.findAll(pageable);
	    List<PatronResModel> patronResModels = patronPage.getContent().stream()
	            .map(patronMapper::mapToPatronResModel)
	            .collect(Collectors.toList());
	    
	    return patronResModels ;
	}
	
	@Override
	public void deletePatronById(Long patronId) {
		List<BorrowingRecord> borrowingRecord = borrowingRecordRepository.findByPatronIdAndReturnDateIsNotNull(patronId);
		if(!borrowingRecord.isEmpty()) {
			borrowingRecordRepository.deleteAll(borrowingRecord);
			patronRepository.deleteById(patronId);
		}
		else {
        	throw new BusinessLogicViolationException(ApiErrorMessageKeyEnum.BCV_PATRON_MUST_RETURN_ALL_BOOKS.name());
		}
	}

}