package dirtycheckeable.not.working.with.clean.proxies

class Child extends Domain {

    static mapWith = "neo4j"
    String anotherProperty

    static constraints = {
        anotherProperty nullable: true
    }
}
