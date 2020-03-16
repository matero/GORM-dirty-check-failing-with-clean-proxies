package dirtycheckeable.not.working.with.clean.proxies

abstract class Domain {

    static mapWith = "neo4j"
    String name

    static mapping = {
        id generator: 'snowflake'
    }
}
