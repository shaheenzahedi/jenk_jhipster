package org.aydm.danak

import org.aydm.danak.WDanakApp

import org.springframework.boot.test.context.SpringBootTest

/**
 * Base composite annotation for integration tests.
 */
@kotlin.annotation.Target(AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(classes = [WDanakApp::class])
annotation class IntegrationTest {
}
