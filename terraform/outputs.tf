output "prod_ip" {
  description = "prod VM 외부 IP → 가비아 A 레코드에 등록"
  value       = google_compute_address.prod.address
}

output "monitoring_ip" {
  description = "monitoring VM 외부 IP → 가비아 A 레코드에 등록"
  value       = google_compute_address.monitoring.address
}

output "cloud_sql_ip" {
  description = "Cloud SQL 공개 IP → 앱 환경변수 및 DataGrip 접속에 사용"
  value       = google_sql_database_instance.main.public_ip_address
}

output "service_account_email" {
  description = "앱 서비스 계정 이메일 → GCP 콘솔에서 키 발급 후 앱에 적용"
  value       = google_service_account.app.email
}
