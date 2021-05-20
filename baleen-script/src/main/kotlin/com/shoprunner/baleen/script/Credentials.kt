package com.shoprunner.baleen.script

data class Credentials(
    val url: String,
    val user: String,
    val password: String? = null,
    val dataSourceProperties: Map<String, String> = emptyMap()
)

class CredentialsBuilder {
    var url: String? = null
    var user: String? = null
    var password: String? = null
    var dataSourceProperties: MutableMap<String, String> = mutableMapOf()

    fun addDataSourceProperty(key: String, value: String) {
        dataSourceProperties[key] = value
    }

    fun build(): Credentials = Credentials(
        url = this.url!!,
        user = this.user!!,
        password = this.password,
        dataSourceProperties = this.dataSourceProperties
    )
}
