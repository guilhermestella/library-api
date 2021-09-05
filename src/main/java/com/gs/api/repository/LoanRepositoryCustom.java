package com.gs.api.repository;

import com.gs.api.model.entity.Loan;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepositoryCustom {

    List<Loan> findNotReturnedLoansAfterDay(LocalDate verificationDate, int daysConsideretAsLate);
}
