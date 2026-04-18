package config

case class DatabaseConfig(
                           name: String,
                           dbType: String,
                           jdbcUrl: String,
                           user: String,
                           password: String,
                           driver: String,
                           hdfsBasePath: String,
                         )