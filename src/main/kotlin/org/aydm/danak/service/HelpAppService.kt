package org.aydm.danak.service

import org.aydm.danak.domain.HelpApp
import org.aydm.danak.repository.HelpAppRepository
import org.aydm.danak.repository.search.HelpAppSearchRepository
import org.aydm.danak.service.dto.HelpAppDTO
import org.aydm.danak.service.mapper.HelpAppMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID
import java.util.stream.Collectors

/**
 * Service Implementation for managing [HelpApp].
 */
@Service
@Transactional
class HelpAppService(
    private val helpAppRepository: HelpAppRepository,
    private val helpAppMapper: HelpAppMapper,
    private val helpAppSearchRepository: HelpAppSearchRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a helpApp.
     *
     * @param helpAppDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(helpAppDTO: HelpAppDTO): HelpAppDTO {
        log.debug("Request to save HelpApp : $helpAppDTO")
        var helpApp = helpAppMapper.toEntity(helpAppDTO)
        helpApp = helpAppRepository.save(helpApp)
        val result = helpAppMapper.toDto(helpApp)
        helpAppSearchRepository.save(helpApp)
        return result
    }

    /**
     * Update a helpApp.
     *
     * @param helpAppDTO the entity to save.
     * @return the persisted entity.
     */
    fun update(helpAppDTO: HelpAppDTO): HelpAppDTO {
        log.debug("Request to save HelpApp : {}", helpAppDTO)
        var helpApp = helpAppMapper.toEntity(helpAppDTO)
        helpApp = helpAppRepository.save(helpApp)
        val result = helpAppMapper.toDto(helpApp)
        helpAppSearchRepository.save(helpApp)
        return result
    }

    /**
     * Partially updates a helpApp.
     *
     * @param helpAppDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(helpAppDTO: HelpAppDTO): Optional<HelpAppDTO> {
        log.debug("Request to partially update HelpApp : {}", helpAppDTO)

        return helpAppRepository.findById(helpAppDTO.id)
            .map {
                helpAppMapper.partialUpdate(it, helpAppDTO)
                it
            }
            .map { helpAppRepository.save(it) }
            .map {
                helpAppSearchRepository.save(it)

                it
            }
            .map { helpAppMapper.toDto(it) }
    }

    /**
     * Get all the helpApps.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(): MutableList<HelpAppDTO> {
        log.debug("Request to get all HelpApps")
        return helpAppRepository.findAll()
            .mapTo(mutableListOf(), helpAppMapper::toDto)
    }

    /**
     * Get one helpApp by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: UUID): Optional<HelpAppDTO> {
        log.debug("Request to get HelpApp : $id")
        return helpAppRepository.findById(id)
            .map(helpAppMapper::toDto)
    }

    /**
     * Delete the helpApp by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: UUID) {
        log.debug("Request to delete HelpApp : $id")

        helpAppRepository.deleteById(id)
        helpAppSearchRepository.deleteById(id)
    }

    /**
     * Search for the helpApp corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String): MutableList<HelpAppDTO> {
        log.debug("Request to search HelpApps for query $query")
        return helpAppSearchRepository.search(query)
            .map { helpAppMapper.toDto(it) }
            .collect(Collectors.toList())
            .toMutableList()
    }
}
