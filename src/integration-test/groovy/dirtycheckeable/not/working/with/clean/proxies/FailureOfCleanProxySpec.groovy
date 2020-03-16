package dirtycheckeable.not.working.with.clean.proxies

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.*
import org.grails.datastore.mapping.proxy.EntityProxy
import spock.lang.Specification

@Integration
@Rollback
class FailureOfCleanProxySpec extends Specification {

    private static final String CHILD_NAME = "child"
    private static final String PARENT_NAME = "parent"

    Long childId
    Long parentId

    def setup() {
        deleteAllNodes()
        Child.withNewSession {
            Child.withNewTransaction {
                final Child child = new Child(name: CHILD_NAME).save(flush: true, failOnErrors: true)
                new Parent(name: PARENT_NAME, child: child).save(flush: true, failOnErrors: true)
            }
        }
        waitUntilObjectsAreAccessible()
        assert childId
        assert parentId
    }

    def cleanup() {
        deleteAllNodes()
    }

    private void deleteAllNodes() {
        Child.withNewSession {
            Child.withNewTransaction {
                Child.findByName(CHILD_NAME)?.delete(flush: true)
                Parent.findByName(PARENT_NAME)?.delete(flush: true)
            }
        }
        waitUntilObjectsArentAccessible()
    }

    def "modified domains object works as expected"() {
        given: "I have a Domain OBJECT"
        final Child object = Child.findByName(CHILD_NAME)
        assert !(object instanceof EntityProxy)

        when:
        object.name = "changed"

        then: "it should be considered dirty"
        object.isDirty()
        and: "its property should be considered dirty"
        object.isDirty("name")
    }

    def "unmodified domains object works as expected"() {
        given: "I have a Domain OBJECT"
        final Child object = Child.findByName(CHILD_NAME)
        assert !(object instanceof EntityProxy)

        expect: "it should not be considered dirty"
        !object.isDirty()
        and: "its property should not be considered dirty"
        !object.isDirty("name")
    }

    def "modified proxy object works as expected"() {
        given: "I have a Domain PROXY"
        final Child proxy = Child.load(childId)
        assert proxy instanceof EntityProxy

        when:
        proxy.name = "changed"

        then: "it should be considered dirty"
        proxy.isDirty()
        and: "its property should be considered dirty"
        proxy.isDirty("name")
    }

    def "UNEXPECTED BEHAVIOR -> unmodified proxy object is treated as dirty when it doesnt have any change"() {
        given: "I have a Domain OBJECT"
        Parent parent = Parent.list()[0]
        assert !(parent instanceof EntityProxy)
        assert parent.child instanceof EntityProxy

        when: "the child is initialized by some action"
        parent.child.name != null

        then: "child is considered dirty"
        parent.child.isDirty()
        and: "inherited properties are considered dirty"
        parent.child.isDirty("name")
        and: "defined  properties are considered dirty"
        parent.child.isDirty("anotherProperty")
    }

    private void waitUntilObjectsAreAccessible() {
        int remainingTries = 20
        boolean domainsAreAccessible = false
        while (!domainsAreAccessible && remainingTries) {
            Child.withNewSession {
                Child.withNewTransaction {
                    childId = Child.findByName(CHILD_NAME)?.id
                    parentId = Parent.findByName(PARENT_NAME)?.id
                    domainsAreAccessible = childId && parentId
                }
            }
            sleep(50)
            remainingTries--
        }
    }

    private void waitUntilObjectsArentAccessible() {
        int remainingTries = 20
        boolean domainsAreAccessible = true
        while (domainsAreAccessible && remainingTries) {
            Child.withNewSession {
                Child.withNewTransaction {
                    def child = Child.findByName(CHILD_NAME)
                    def parent = Parent.findByName(PARENT_NAME)
                    domainsAreAccessible = child && parent
                }
            }
            sleep(50)
            remainingTries--
        }
    }
}
