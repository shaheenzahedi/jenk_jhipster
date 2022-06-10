package org.aydm.danak.web.rest

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.IntegrationTest
import org.aydm.danak.domain.HelpApp
import org.aydm.danak.domain.StaticPage
import org.aydm.danak.repository.HelpAppRepository
import org.aydm.danak.repository.search.HelpAppSearchRepository
import org.aydm.danak.service.mapper.HelpAppMapper
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.UUID
import java.util.stream.Stream
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [HelpAppResource] REST controller.
 */
@IntegrationTest
@Extensions(
    ExtendWith(MockitoExtension::class)
)
@AutoConfigureMockMvc
@WithMockUser
class HelpAppResourceIT {
    @Autowired
    private lateinit var helpAppRepository: HelpAppRepository

    @Autowired
    private lateinit var helpAppMapper: HelpAppMapper

    /**
     * This repository is mocked in the org.aydm.danak.repository.search test package.
     *
     * @see org.aydm.danak.repository.search.HelpAppSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockHelpAppSearchRepository: HelpAppSearchRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restHelpAppMockMvc: MockMvc

    private lateinit var helpApp: HelpApp

    @BeforeEach
    fun initTest() {
        helpApp = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createHelpApp() {
        val databaseSizeBeforeCreate = helpAppRepository.findAll().size
        // Create the HelpApp
        val helpAppDTO = helpAppMapper.toDto(helpApp)
        restHelpAppMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(helpAppDTO))
        ).andExpect(status().isCreated)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeCreate + 1)
        val testHelpApp = helpAppList[helpAppList.size - 1]

        assertThat(testHelpApp.staticPageId).isEqualTo(DEFAULT_STATIC_PAGE_ID)

        // Validate the HelpApp in Elasticsearch
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createHelpAppWithExistingId() {
        // Create the HelpApp with an existing ID
        helpAppRepository.saveAndFlush(helpApp)
        val helpAppDTO = helpAppMapper.toDto(helpApp)

        val databaseSizeBeforeCreate = helpAppRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restHelpAppMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(helpAppDTO))
        ).andExpect(status().isBadRequest)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeCreate)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository, times(0)).save(helpApp)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllHelpApps() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)

        // Get all the helpAppList
        restHelpAppMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(helpApp.id.toString())))
            .andExpect(jsonPath("$.[*].staticPageId").value(hasItem(DEFAULT_STATIC_PAGE_ID.toString())))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getHelpApp() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)

        val id = helpApp.id
        assertNotNull(id)

        // Get the helpApp
        restHelpAppMockMvc.perform(get(ENTITY_API_URL_ID, helpApp.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(helpApp.id.toString()))
            .andExpect(jsonPath("$.staticPageId").value(DEFAULT_STATIC_PAGE_ID.toString()))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getHelpAppsByIdFiltering() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)
        val id = helpApp.id

        defaultHelpAppShouldBeFound("id.equals=$id")
        defaultHelpAppShouldNotBeFound("id.notEquals=$id")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllHelpAppsByStaticPageIdIsEqualToSomething() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)

        // Get all the helpAppList where staticPageId equals to DEFAULT_STATIC_PAGE_ID
        defaultHelpAppShouldBeFound("staticPageId.equals=$DEFAULT_STATIC_PAGE_ID")

        // Get all the helpAppList where staticPageId equals to UPDATED_STATIC_PAGE_ID
        defaultHelpAppShouldNotBeFound("staticPageId.equals=$UPDATED_STATIC_PAGE_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllHelpAppsByStaticPageIdIsNotEqualToSomething() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)

        // Get all the helpAppList where staticPageId not equals to DEFAULT_STATIC_PAGE_ID
        defaultHelpAppShouldNotBeFound("staticPageId.notEquals=$DEFAULT_STATIC_PAGE_ID")

        // Get all the helpAppList where staticPageId not equals to UPDATED_STATIC_PAGE_ID
        defaultHelpAppShouldBeFound("staticPageId.notEquals=$UPDATED_STATIC_PAGE_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllHelpAppsByStaticPageIdIsInShouldWork() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)

        // Get all the helpAppList where staticPageId in DEFAULT_STATIC_PAGE_ID or UPDATED_STATIC_PAGE_ID
        defaultHelpAppShouldBeFound("staticPageId.in=$DEFAULT_STATIC_PAGE_ID,$UPDATED_STATIC_PAGE_ID")

        // Get all the helpAppList where staticPageId equals to UPDATED_STATIC_PAGE_ID
        defaultHelpAppShouldNotBeFound("staticPageId.in=$UPDATED_STATIC_PAGE_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllHelpAppsByStaticPageIdIsNullOrNotNull() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)

        // Get all the helpAppList where staticPageId is not null
        defaultHelpAppShouldBeFound("staticPageId.specified=true")

        // Get all the helpAppList where staticPageId is null
        defaultHelpAppShouldNotBeFound("staticPageId.specified=false")
    }


    /**
     * Executes the search, and checks that the default entity is returned
     */

    @Throws(Exception::class)
    private fun defaultHelpAppShouldBeFound(filter: String) {
        restHelpAppMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(helpApp.id?.toString())))
            .andExpect(jsonPath("$.[*].staticPageId").value(hasItem(DEFAULT_STATIC_PAGE_ID.toString())))

        // Check, that the count call also returns 1
        restHelpAppMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    @Throws(Exception::class)
    private fun defaultHelpAppShouldNotBeFound(filter: String) {
        restHelpAppMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restHelpAppMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingHelpApp() {
        // Get the helpApp
        restHelpAppMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString()))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewHelpApp() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)

        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size

        // Update the helpApp
        val updatedHelpApp = helpAppRepository.findById(helpApp.id).get()
        // Disconnect from session so that the updates on updatedHelpApp are not directly saved in db
        em.detach(updatedHelpApp)
        updatedHelpApp.staticPageId = UPDATED_STATIC_PAGE_ID
        val helpAppDTO = helpAppMapper.toDto(updatedHelpApp)

        restHelpAppMockMvc.perform(
            put(ENTITY_API_URL_ID, helpAppDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(helpAppDTO))
        ).andExpect(status().isOk)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)
        val testHelpApp = helpAppList[helpAppList.size - 1]
        assertThat(testHelpApp.staticPageId).isEqualTo(UPDATED_STATIC_PAGE_ID)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository).save(testHelpApp)
    }

    @Test
    @Transactional
    fun putNonExistingHelpApp() {
        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size
        helpApp.id = UUID.randomUUID()

        // Create the HelpApp
        val helpAppDTO = helpAppMapper.toDto(helpApp)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHelpAppMockMvc.perform(
            put(ENTITY_API_URL_ID, helpAppDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(helpAppDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository, times(0)).save(helpApp)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchHelpApp() {
        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size
        helpApp.id = UUID.randomUUID()

        // Create the HelpApp
        val helpAppDTO = helpAppMapper.toDto(helpApp)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHelpAppMockMvc.perform(
            put(ENTITY_API_URL_ID, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(helpAppDTO))
        ).andExpect(status().isBadRequest)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository, times(0)).save(helpApp)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamHelpApp() {
        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size
        helpApp.id = UUID.randomUUID()

        // Create the HelpApp
        val helpAppDTO = helpAppMapper.toDto(helpApp)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHelpAppMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(helpAppDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository, times(0)).save(helpApp)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateHelpAppWithPatch() {
        helpAppRepository.saveAndFlush(helpApp)

        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size

// Update the helpApp using partial update
        val partialUpdatedHelpApp = HelpApp().apply {
            id = helpApp.id
        }

        restHelpAppMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedHelpApp.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedHelpApp))
        )
            .andExpect(status().isOk)

// Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)
        val testHelpApp = helpAppList.last()
        assertThat(testHelpApp.staticPageId).isEqualTo(DEFAULT_STATIC_PAGE_ID)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateHelpAppWithPatch() {
        helpAppRepository.saveAndFlush(helpApp)

        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size

// Update the helpApp using partial update
        val partialUpdatedHelpApp = HelpApp().apply {
            id = helpApp.id

            staticPageId = UPDATED_STATIC_PAGE_ID
        }

        restHelpAppMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedHelpApp.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedHelpApp))
        )
            .andExpect(status().isOk)

// Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)
        val testHelpApp = helpAppList.last()
        assertThat(testHelpApp.staticPageId).isEqualTo(UPDATED_STATIC_PAGE_ID)
    }

    @Throws(Exception::class)
    fun patchNonExistingHelpApp() {
        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size
        helpApp.id = UUID.randomUUID()

        // Create the HelpApp
        val helpAppDTO = helpAppMapper.toDto(helpApp)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHelpAppMockMvc.perform(
            patch(ENTITY_API_URL_ID, helpAppDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(helpAppDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository, times(0)).save(helpApp)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchHelpApp() {
        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size
        helpApp.id = UUID.randomUUID()

        // Create the HelpApp
        val helpAppDTO = helpAppMapper.toDto(helpApp)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHelpAppMockMvc.perform(
            patch(ENTITY_API_URL_ID, UUID.randomUUID())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(helpAppDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository, times(0)).save(helpApp)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamHelpApp() {
        val databaseSizeBeforeUpdate = helpAppRepository.findAll().size
        helpApp.id = UUID.randomUUID()

        // Create the HelpApp
        val helpAppDTO = helpAppMapper.toDto(helpApp)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHelpAppMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(helpAppDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the HelpApp in the database
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeUpdate)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository, times(0)).save(helpApp)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteHelpApp() {
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)

        val databaseSizeBeforeDelete = helpAppRepository.findAll().size

        // Delete the helpApp
        restHelpAppMockMvc.perform(
            delete(ENTITY_API_URL_ID, helpApp.id.toString())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val helpAppList = helpAppRepository.findAll()
        assertThat(helpAppList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the HelpApp in Elasticsearch
        verify(mockHelpAppSearchRepository, times(1)).deleteById(helpApp.id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchHelpApp() {
        // Configure the mock search repository
        // Initialize the database
        helpAppRepository.saveAndFlush(helpApp)
        `when`(mockHelpAppSearchRepository.search("id:${helpApp.id}"))
            .thenReturn(Stream.of(helpApp))
        // Search the helpApp
        restHelpAppMockMvc.perform(get("$ENTITY_SEARCH_API_URL?query=id:${helpApp.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(helpApp.id.toString())))
            .andExpect(jsonPath("$.[*].staticPageId").value(hasItem(DEFAULT_STATIC_PAGE_ID.toString())))
    }

    companion object {

        private val DEFAULT_STATIC_PAGE_ID: UUID = UUID.randomUUID()
        private val UPDATED_STATIC_PAGE_ID: UUID = UUID.randomUUID()

        private val ENTITY_API_URL: String = "/api/help-apps"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"
        private val ENTITY_SEARCH_API_URL: String = "/api/_search/help-apps"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): HelpApp {
            val helpApp = HelpApp(
                staticPageId = DEFAULT_STATIC_PAGE_ID

            )

            return helpApp
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): HelpApp {
            val helpApp = HelpApp(
                staticPageId = UPDATED_STATIC_PAGE_ID

            )

            return helpApp
        }
    }
}
