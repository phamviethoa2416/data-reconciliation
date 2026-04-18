package config

case class DatabaseConfig(
                           name: String,
                           jdbcUrl: String,
                           user: String,
                           password: String,
                           driver: String,
                           hdfsBasePath: String,
                         )