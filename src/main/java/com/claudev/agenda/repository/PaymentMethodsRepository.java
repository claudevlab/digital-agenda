package com.claudev.agenda.repository;

import com.claudev.agenda.entity.PaymentMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodsRepository  extends JpaRepository<PaymentMethods,Long> {
}
