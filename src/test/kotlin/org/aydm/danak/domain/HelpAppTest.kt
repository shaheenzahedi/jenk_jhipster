package org.aydm.danak.domain

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.web.rest.equalsVerifier
import org.junit.jupiter.api.Test
import java.util.UUID

class HelpAppTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(HelpApp::class)
        val helpApp1 = HelpApp()
        helpApp1.id = UUID.randomUUID()
        val helpApp2 = HelpApp()
        helpApp2.id = helpApp1.id
        assertThat(helpApp1).isEqualTo(helpApp2)
        helpApp2.id = UUID.randomUUID()
        assertThat(helpApp1).isNotEqualTo(helpApp2)
        helpApp1.id = null
        assertThat(helpApp1).isNotEqualTo(helpApp2)
    }
}
