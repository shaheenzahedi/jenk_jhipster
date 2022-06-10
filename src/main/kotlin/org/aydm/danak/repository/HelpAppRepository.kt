package org.aydm.danak.repository

import org.aydm.danak.domain.HelpApp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data SQL repository for the [HelpApp] entity.
 */
@Suppress("unused")
@Repository
interface HelpAppRepository : JpaRepository<HelpApp, UUID>, JpaSpecificationExecutor<HelpApp>
