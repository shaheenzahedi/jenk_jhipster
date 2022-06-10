package org.aydm.danak.service

import org.aydm.danak.domain.* // for static metamodels
import org.aydm.danak.domain.HelpApp
import org.aydm.danak.repository.HelpAppRepository
import org.aydm.danak.repository.search.HelpAppSearchRepository
import org.aydm.danak.service.criteria.HelpAppCriteria
import org.aydm.danak.service.dto.HelpAppDTO
import org.aydm.danak.service.mapper.HelpAppMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.jhipster.service.QueryService
import tech.jhipster.service.filter.Filter
import java.util.UUID
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [HelpApp] entities in the database.
 * The main input is a [HelpAppCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [HelpAppDTO] or a [Page] of [HelpAppDTO] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class HelpAppQueryService(
    private val helpAppRepository: HelpAppRepository,
    private val helpAppMapper: HelpAppMapper,
    private val helpAppSearchRepository: HelpAppSearchRepository,
) : QueryService<HelpApp>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [HelpAppDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: HelpAppCriteria?): MutableList<HelpAppDTO> {
        log.debug("find by criteria : $criteria")
        val specification = createSpecification(criteria)
        return helpAppMapper.toDto(helpAppRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [HelpAppDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: HelpAppCriteria?, page: Pageable): Page<HelpAppDTO> {
        log.debug("find by criteria : $criteria, page: $page")
        val specification = createSpecification(criteria)
        return helpAppRepository.findAll(specification, page)
            .map(helpAppMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: HelpAppCriteria?): Long {
        log.debug("count by criteria : $criteria")
        val specification = createSpecification(criteria)
        return helpAppRepository.count(specification)
    }

    /**
     * Function to convert [HelpAppCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: HelpAppCriteria?): Specification<HelpApp?> {
        var specification: Specification<HelpApp?> = Specification.where(null)
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            val distinctCriteria = criteria.distinct
            if (distinctCriteria != null) {
                specification = specification.and(distinct(distinctCriteria))
            }
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, HelpApp_.id))
            }
            if (criteria.staticPageId != null) {
                specification = specification.and(buildSpecification(criteria.staticPageId, HelpApp_.staticPageId))
            }
            if (criteria.staticPageIdId != null) {
                specification = specification.and(
                    buildSpecification(criteria.staticPageIdId as Filter<UUID>) {
                        it.join(HelpApp_.staticPageIds, JoinType.LEFT).get(StaticPage_.id)
                    }
                )
            }
        }
        return specification
    }
}
