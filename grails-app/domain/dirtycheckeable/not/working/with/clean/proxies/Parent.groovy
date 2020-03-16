package dirtycheckeable.not.working.with.clean.proxies

class Parent extends Domain {

    static mapWith = "neo4j"
    Child child

    static constraints = {
        child nullable: false
    }

    static mapping = {
        child lazy: false
    }
}
