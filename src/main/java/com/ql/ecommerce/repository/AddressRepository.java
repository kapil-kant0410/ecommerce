package com.ql.ecommerce.repository;

import com.ql.ecommerce.entity.Address;
import com.ql.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByUser(User user);
}
