package org.aydm.danak.service

import org.aydm.danak.domain.* // for static metamodels
import org.aydm.danak.domain.ContactUs
import org.aydm.danak.repository.ContactUsRepository
import org.aydm.danak.repository.search.ContactUsSearchRepository
import org.aydm.danak.service.criteria.ContactUsCriteria
import org.aydm.danak.service.dto.ContactUsDTO
import org.aydm.danak.service.mapper.ContactUsMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.jhipster.service.QueryService

/**
 * Service for executing complex queries for [ContactUs] entities in the database.
 * The main input is a [ContactUsCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [ContactUsDTO] or a [Page] of [ContactUsDTO] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class ContactUsQueryService(
    private val contactUsRepository: ContactUsRepository,
    private val contactUsMapper: ContactUsMapper,
    private val contactUsSearchRepository: ContactUsSearchRepository,
) : QueryService<ContactUs>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [ContactUsDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ContactUsCriteria?): MutableList<ContactUsDTO> {
        log.debug("find by criteria : $criteria")
        val specification = createSpecification(criteria)
        return contactUsMapper.toDto(contactUsRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [ContactUsDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ContactUsCriteria?, page: Pageable): Page<ContactUsDTO> {
        log.debug("find by criteria : $criteria, page: $page")
        val specification = createSpecification(criteria)
        return contactUsRepository.findAll(specification, page)
            .map(contactUsMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: ContactUsCriteria?): Long {
        log.debug("count by criteria : $criteria")
        val specification = createSpecification(criteria)
        return contactUsRepository.count(specification)
    }

    /**
     * Function to convert [ContactUsCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: ContactUsCriteria?): Specification<ContactUs?> {
        var specification: Specification<ContactUs?> = Specification.where(null)
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            val distinctCriteria = criteria.distinct
            if (distinctCriteria != null) {
                specification = specification.and(distinct(distinctCriteria))
            }
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, ContactUs_.id))
            }
            if (criteria.userId != null) {
                specification = specification.and(buildSpecification(criteria.userId, ContactUs_.userId))
            }
            if (criteria.email != null) {
                specification = specification.and(buildStringSpecification(criteria.email, ContactUs_.email))
            }
            if (criteria.createTime != null) {
                specification = specification.and(buildRangeSpecification(criteria.createTime, ContactUs_.createTime))
            }
        }
        return specification
    }
}
