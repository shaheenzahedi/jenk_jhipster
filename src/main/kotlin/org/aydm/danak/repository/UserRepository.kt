package org.aydm.danak.repository

import org.aydm.danak.domain.User

import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.Optional
import java.time.Instant

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    
    fun findOneByActivationKey(activationKey: String): Optional<User>

    fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime: Instant): List<User>

    fun findOneByResetKey(resetKey: String): Optional<User>

    fun findOneByEmailIgnoreCase(email: String?): Optional<User>

    fun findOneByLogin(login: String): Optional<User>


    @EntityGraph(attributePaths = ["authorities"])
    fun findOneWithAuthoritiesByLogin(login: String): Optional<User>

    @EntityGraph(attributePaths = ["authorities"])
    fun findOneWithAuthoritiesByEmailIgnoreCase(email: String): Optional<User>

    

    fun findAllByIdNotNullAndActivatedIsTrue(pageable: Pageable): Page<User>
    

}
