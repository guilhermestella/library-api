package com.gs.api.service.impl;

import com.gs.api.api.dto.LoanFilterDTO;
import com.gs.api.exception.BusinessException;
import com.gs.api.model.entity.Loan;
import com.gs.api.repository.LoanRepository;
import com.gs.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository repository;

    @Override
    public Loan save(Loan loan) {
        if ( repository.existsByBookIdAndReturnedIsFalse(loan.getBook()) ) {
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        if ( !repository.existsById(id) ) {
            throw new BusinessException("Loan not found");
        }
        return repository.findById(id);
    }

    @Override
    public Loan returnBook(Loan loan) {
        return repository.save(loan.returnBook());
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
        return repository.findByBookIsbnOrCustumer(filter.getIsbn(), filter.getCustomer(), pageable);
    }
}
