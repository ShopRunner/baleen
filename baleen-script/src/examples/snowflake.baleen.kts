//val credentials = Credentials(
//    url = "jdbc:snowflake://xp71857.us-east-1.snowflakecomputing.com/?warehouse=SHOPRUNNER&db=SHOPRUNNER",
//    user = System.getenv("DATABASE_USER"),
//    dataSourceProperties = mapOf(
//        "authenticator" to "externalBrowser"
//    )
//)
//
//database(credentials) {

database {
    // Does validation on all rows of a table.
    table("engineer.example", "table example", tags = mapOf("ID" to withAttributeValue("ID"))) {
        tag("rowId", withAttributeValue("ID"))

        "ID".type(IntegerType())
        "FIRST_NAME".type(StringType(0, 32))
        "LAST_NAME".type(StringType(0, 32))
    }

    // Does validation on a sample of rows of a table.
    sample("engineer.example", 10, "sampling example", tags = mapOf("ID" to withAttributeValue("ID"))) {
        tag("rowId", withAttributeValue("ID"))

        "ID".type(IntegerType())
        "FIRST_NAME".type(StringType(0, 32))
        "LAST_NAME".type(StringType(0, 32))
    }

    // Does validation on the results of a query
    query("query example", "SELECT id, first_name, last_name FROM engineer.example WHERE last_name = 'Smith'",
        tags = mapOf("ID" to withAttributeValue("ID"))) {
        "ID".type(IntegerType())
        "FIRST_NAME".type(StringType(0, 32))
        "LAST_NAME".type(StringConstantType("Smith"))
    }

    // Example of just writing tests without defining columns
    table("engineer.example", "row test only", tags = mapOf("ID" to withAttributeValue("ID"))) {
        test("first name should not be same as last name") { data ->
            assertNotEquals("first name != last name", data.getAsString("FIRST_NAME"), data.getAsString("LAST_NAME"))
        }

        test("ID should always be odd") { data ->
            assertTrue("ID is Odd", data.getAsLong("ID")!! % 2 != 0L, data.getAsLong("ID"))
        }

        test("names should not be all caps") { data ->
            assertNotEquals("first name is not all caps", data.getAsString("FIRST_NAME")!!.toUpperCase(), data.getAsString("FIRST_NAME"))
            assertNotEquals("last name is not all caps", data.getAsString("LAST_NAME")!!.toUpperCase(), data.getAsString("LAST_NAME"))
        }
    }
}

