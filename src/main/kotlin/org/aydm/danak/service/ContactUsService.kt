package org.aydm.danak.service

import org.aydm.danak.domain.ContactUs
import org.aydm.danak.repository.ContactUsRepository
import org.aydm.danak.repository.search.ContactUsSearchRepository
import org.aydm.danak.service.dto.ContactUsDTO
import org.aydm.danak.service.mapper.ContactUsMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID
import java.util.stream.Collectors

/**
 * Service Implementation for managing [ContactUs].
 */
@Service
@Transactional
class ContactUsService(
    private val contactUsRepository: ContactUsRepository,
    private val contactUsMapper: ContactUsMapper,
    private val contactUsSearchRepository: ContactUsSearchRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a contactUs.
     *
     * @param contactUsDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(contactUsDTO: ContactUsDTO): ContactUsDTO {
        log.debug("Request to save ContactUs : $contactUsDTO")
        var contactUs = contactUsMapper.toEntity(contactUsDTO)
        contactUs = contactUsRepository.save(contactUs)
        val result = contactUsMapper.toDto(contactUs)
        contactUsSearchRepository.save(contactUs)
        return result
    }

    /**
     * Update a contactUs.
     *
     * @param contactUsDTO the entity to save.
     * @return the persisted entity.
     */
    fun update(contactUsDTO: ContactUsDTO): ContactUsDTO {
        log.debug("Request to save ContactUs : {}", contactUsDTO)
        var contactUs = contactUsMapper.toEntity(contactUsDTO)
        contactUs = contactUsRepository.save(contactUs)
        val result = contactUsMapper.toDto(contactUs)
        contactUsSearchRepository.save(contactUs)
        return result
    }

    /**
     * Partially updates a contactUs.
     *
     * @param contactUsDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(contactUsDTO: ContactUsDTO): Optional<ContactUsDTO> {
        log.debug("Request to partially update ContactUs : {}", contactUsDTO)

        return contactUsRepository.findById(contactUsDTO.id)
            .map {
                contactUsMapper.partialUpdate(it, contactUsDTO)
                it
            }
            .map { contactUsRepository.save(it) }
            .map {
                contactUsSearchRepository.save(it)

                it
            }
            .map { contactUsMapper.toDto(it) }
    }

    /**
     * Get all the contactuses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(): MutableList<ContactUsDTO> {
        log.debug("Request to get all Contactuses")
        return contactUsRepository.findAll()
            .mapTo(mutableListOf(), contactUsMapper::toDto)
    }

    /**
     * Get one contactUs by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: UUID): Optional<ContactUsDTO> {
        log.debug("Request to get ContactUs : $id")
        return contactUsRepository.findById(id)
            .map(contactUsMapper::toDto)
    }

    /**
     * Delete the contactUs by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: UUID) {
        log.debug("Request to delete ContactUs : $id")

        contactUsRepository.deleteById(id)
        contactUsSearchRepository.deleteById(id)
    }

    /**
     * Search for the contactUs corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String): MutableList<ContactUsDTO> {
        log.debug("Request to search Contactuses for query $query")
        return contactUsSearchRepository.search(query)
            .map { contactUsMapper.toDto(it) }
            .collect(Collectors.toList())
            .toMutableList()
    }
}
