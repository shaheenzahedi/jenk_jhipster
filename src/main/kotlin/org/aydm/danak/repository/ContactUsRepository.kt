package org.aydm.danak.repository

import org.aydm.danak.domain.ContactUs
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data SQL repository for the [ContactUs] entity.
 */
@Suppress("unused")
@Repository
interface ContactUsRepository : JpaRepository<ContactUs, UUID>, JpaSpecificationExecutor<ContactUs>
