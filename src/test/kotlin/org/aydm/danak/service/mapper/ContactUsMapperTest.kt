package org.aydm.danak.service.mapper

import org.junit.jupiter.api.BeforeEach

class ContactUsMapperTest {

    private lateinit var contactUsMapper: ContactUsMapper

    @BeforeEach
    fun setUp() {
        contactUsMapper = ContactUsMapperImpl()
    }
}
