package org.aydm.danak.repository

import org.aydm.danak.domain.StaticPage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data SQL repository for the [StaticPage] entity.
 */
@Suppress("unused")
@Repository
interface StaticPageRepository : JpaRepository<StaticPage, UUID>, JpaSpecificationExecutor<StaticPage>
