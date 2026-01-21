package com.maknom.eco.guard.repository;

import com.maknom.eco.guard.model.user.UserBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserBean, Long> {

   @Query("""
           SELECT u
           FROM users u 
           WHERE u.email =:email 
           """)
   Optional<UserBean> findByEmail(@Param("email") String email);


   Optional<UserBean> findById(Long id);
}
