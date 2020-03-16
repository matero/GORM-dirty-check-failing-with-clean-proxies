package dirtycheckeable.not.working.with.clean.proxies

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class DomainSpec extends Specification implements DomainUnitTest<Domain> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
