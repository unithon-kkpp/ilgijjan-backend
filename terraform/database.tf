 # ============================================================
# Cloud SQL — MySQL 8.0, db-f1-micro, public IP
# ============================================================
resource "google_sql_database_instance" "main" {
  name             = "ilgijjan-mysql"
  database_version = "MYSQL_8_0"
  region           = var.region

  settings {
    tier = "db-f1-micro"

    ip_configuration {
      ipv4_enabled = true
      authorized_networks {
        name  = "prod-vm"
        value = google_compute_address.prod.address
      }
      # DataGrip 등 외부 접속 시 SSH 터널 사용 (prod VM 경유)
      # authorized_networks {
      #   name  = "my-ip"
      #   value = "YOUR_IP/32"
      # }
    }

    backup_configuration {
      enabled            = true
      binary_log_enabled = true
    }
  }

  # 실수로 terraform destroy 해도 DB는 삭제 안 됨
  deletion_protection = true

  depends_on = [google_project_service.apis]
}

resource "google_sql_database" "app" {
  name     = "ilgijjan"
  instance = google_sql_database_instance.main.name
}

resource "google_sql_user" "app" {
  name     = var.db_user
  instance = google_sql_database_instance.main.name
  password = var.db_password
}
