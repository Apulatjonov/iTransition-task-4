package com.itransition.task_4.repository;

import com.itransition.task_4.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findUserEntityByMail(String mail);
//    Optional<UserEntity> findUserEntityByEMailAndPassword(String eMail, String password);
}
