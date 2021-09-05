package com.gs.api.repository.impl;

import com.gs.api.model.entity.Loan;
import com.gs.api.repository.LoanRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;

public class LoanRepositoryCustomImpl implements LoanRepositoryCustom {

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Loan> findNotReturnedLoansAfterDay(LocalDate verificationDate, int daysConsideretAsLate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Loan> query = cb.createQuery(Loan.class);
        Root<Loan> from = query.from(Loan.class);
        Predicate isNotReturned = cb.isFalse(from.get("returned"));
        Expression<LocalDate> maxDateExpression = cb.sum(from.get("loanDate"), daysConsideretAsLate).as(LocalDate.class);
        Predicate isLate = cb.greaterThan(maxDateExpression, verificationDate);

        query.where(isNotReturned, isLate);
        return entityManager.createQuery(query).getResultList();
    }
}
