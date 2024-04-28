package com.libraryManagement.libraryManagement.patronTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.libraryManagement.libraryManagement.borrowingRecord.entites.BorrowingRecord;
import com.libraryManagement.libraryManagement.borrowingRecord.repositories.BorrowingRecordRepository;
import com.libraryManagement.libraryManagement.exceptions.BusinessLogicViolationException;
import com.libraryManagement.libraryManagement.patron.entities.Patron;
import com.libraryManagement.libraryManagement.patron.models.mapinterface.PatronMapper;
import com.libraryManagement.libraryManagement.patron.models.request.PatronReqModel;
import com.libraryManagement.libraryManagement.patron.models.response.PatronResModel;
import com.libraryManagement.libraryManagement.patron.repositories.PatronRepository;
import com.libraryManagement.libraryManagement.patron.services.impl.PatronServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PatronServiceImplTest {
	
	@Mock
    private PatronRepository patronRepository;

    @Mock
    private PatronMapper patronMapper;

    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;

    @InjectMocks
    private PatronServiceImpl patronService;

    @Test
    void testCreatePatron() {
        // Arrange
        PatronReqModel patronReqModel = new PatronReqModel();
        Patron patron = new Patron();
        PatronResModel expected = new PatronResModel();
        when(patronMapper.mapToPatron(patronReqModel)).thenReturn(patron);
        when(patronRepository.save(patron)).thenReturn(patron);
        when(patronMapper.mapToPatronResModel(patron)).thenReturn(expected);

        // Act
        PatronResModel actual = patronService.createPatron(patronReqModel);

        // Assert
        assertEquals(expected, actual);
        verify(patronMapper).mapToPatron(patronReqModel);
        verify(patronRepository).save(patron);
        verify(patronMapper).mapToPatronResModel(patron);
    }

    @Test
    void testGetPatronById() {
        // Arrange
        long patronId = 1L;
        Patron patron = new Patron();
        PatronResModel expected = new PatronResModel();
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(patron));
        when(patronMapper.mapToPatronResModel(patron)).thenReturn(expected);

        // Act
        PatronResModel actual = patronService.getPatronById(patronId);

        // Assert
        assertEquals(expected, actual);
        verify(patronRepository).findById(patronId);
        verify(patronMapper).mapToPatronResModel(patron);
    }

    @Test
    void testUpdatePatronById() {
        // Arrange
        long patronId = 1L;
        PatronReqModel patronReqModel = new PatronReqModel();
        Patron patron = new Patron();
        PatronResModel expected = new PatronResModel();
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(patron));
        when(patronRepository.save(patron)).thenReturn(patron);
        when(patronMapper.mapToPatron(patron, patronReqModel)).thenReturn(patron);
        when(patronMapper.mapToPatronResModel(patron)).thenReturn(expected);

        // Act
        PatronResModel actual = patronService.updatePatronById(patronReqModel, patronId);

        // Assert
        assertEquals(expected, actual);
        verify(patronRepository).findById(patronId);
        verify(patronRepository).save(patron);
        verify(patronMapper).mapToPatron(patron, patronReqModel);
        verify(patronMapper).mapToPatronResModel(patron);
    }

    @Test
    void testGetAllPatrons() {
        // Arrange
        int pageSize = 10;
        int pageIndex = 0;
        String sortField = "name";
        String sortOrder = "asc";
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.ASC, sortField));
        List<Patron> patronList = new ArrayList<>();
        Patron patron1 = new Patron();
        patron1.setName("Patron 1");
        patronList.add(patron1);
        Patron patron2 = new Patron();
        patron2.setName("Patron 2");
        patronList.add(patron2);
        Page<Patron> page = new PageImpl<>(patronList, pageable, patronList.size());
        List<PatronResModel> expected = new ArrayList<>();
        for (Patron patron : patronList) {
            expected.add(new PatronResModel());
        }
        when(patronRepository.findAll(pageable)).thenReturn(page);
        when(patronMapper.mapToPatronResModel(patron1)).thenReturn(new PatronResModel());
        when(patronMapper.mapToPatronResModel(patron2)).thenReturn(new PatronResModel());

        // Act
        List<PatronResModel> actual = patronService.getAllPatrons(pageSize, pageIndex, sortField, sortOrder);

        // Assert
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
        verify(patronRepository).findAll(pageable);
        verify(patronMapper).mapToPatronResModel(patron1);
        verify(patronMapper).mapToPatronResModel(patron2);
    }

    @Test
    void testDeletePatronById_PatronFound() {
        // Arrange
        long patronId = 1L;
        List<BorrowingRecord> borrowingRecord = new ArrayList<>();
        when(borrowingRecordRepository.findByPatronIdAndReturnDateIsNotNull(patronId)).thenReturn(borrowingRecord);
        when(patronRepository.existsById(patronId)).thenReturn(true);

        // Act
        patronService.deletePatronById(patronId);

        // Assert
        verify(borrowingRecordRepository).findByPatronIdAndReturnDateIsNotNull(patronId);
        verify(borrowingRecordRepository).deleteAll(borrowingRecord);
        verify(patronRepository).deleteById(patronId);
    }

    @Test
    void testDeletePatronById_PatronNotFound() {
        // Arrange
        long patronId = 1L;
        when(patronRepository.existsById(patronId)).thenReturn(false);

        // Act & Assert
        assertThrows(BusinessLogicViolationException.class, () -> patronService.deletePatronById(patronId));
        verify(borrowingRecordRepository).findByPatronIdAndReturnDateIsNotNull(patronId);
        verify(patronRepository).existsById(patronId);
    }

}
