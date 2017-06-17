package com.jwtauth.repository;

import com.jwtauth.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Marius on 6/12/2017.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
