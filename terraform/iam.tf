# ============================================================
# 앱용 서비스 계정
# ============================================================
resource "google_service_account" "app" {
  account_id   = "ilgijjan-app"
  display_name = "Ilgijjan App Service Account"
}

# GCS 읽기/쓰기
resource "google_project_iam_member" "app_storage" {
  project = var.project_id
  role    = "roles/storage.objectAdmin"
  member  = "serviceAccount:${google_service_account.app.email}"
}

# Vision, Gemini 등 나머지 API는 VM에 cloud-platform scope가 부여되어 있어
# API 활성화만으로 충분히 호출 가능 (별도 IAM 불필요)
