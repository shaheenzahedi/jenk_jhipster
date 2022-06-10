package org.aydm.danak.service

import org.aydm.danak.domain.* // for static metamodels
import org.aydm.danak.domain.StaticPage
import org.aydm.danak.repository.StaticPageRepository
import org.aydm.danak.repository.search.StaticPageSearchRepository
import org.aydm.danak.service.criteria.StaticPageCriteria
import org.aydm.danak.service.dto.StaticPageDTO
import org.aydm.danak.service.mapper.StaticPageMapper
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
 * Service for executing complex queries for [StaticPage] entities in the database.
 * The main input is a [StaticPageCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [StaticPageDTO] or a [Page] of [StaticPageDTO] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class StaticPageQueryService(
    private val staticPageRepository: StaticPageRepository,
    private val staticPageMapper: StaticPageMapper,
    private val staticPageSearchRepository: StaticPageSearchRepository,
) : QueryService<StaticPage>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [StaticPageDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: StaticPageCriteria?): MutableList<StaticPageDTO> {
        log.debug("find by criteria : $criteria")
        val specification = createSpecification(criteria)
        return staticPageMapper.toDto(staticPageRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [StaticPageDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: StaticPageCriteria?, page: Pageable): Page<StaticPageDTO> {
        log.debug("find by criteria : $criteria, page: $page")
        val specification = createSpecification(criteria)
        return staticPageRepository.findAll(specification, page)
            .map(staticPageMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: StaticPageCriteria?): Long {
        log.debug("count by criteria : $criteria")
        val specification = createSpecification(criteria)
        return staticPageRepository.count(specification)
    }

    /**
     * Function to convert [StaticPageCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: StaticPageCriteria?): Specification<StaticPage?> {
        var specification: Specification<StaticPage?> = Specification.where(null)
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            val distinctCriteria = criteria.distinct
            if (distinctCriteria != null) {
                specification = specification.and(distinct(distinctCriteria))
            }
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, StaticPage_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, StaticPage_.name))
            }
            if (criteria.status != null) {
                specification = specification.and(buildSpecification(criteria.status, StaticPage_.status))
            }
            if (criteria.fileId != null) {
                specification = specification.and(buildSpecification(criteria.fileId, StaticPage_.fileId))
            }
            if (criteria.helpAppId != null) {
                specification = specification.and(
                    buildSpecification(criteria.helpAppId as Filter<UUID>) {
                        it.join(StaticPage_.helpApp, JoinType.LEFT).get(HelpApp_.id)
                    }
                )
            }
        }
        return specification
    }
}
