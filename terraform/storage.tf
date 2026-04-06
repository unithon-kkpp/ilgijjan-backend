# ============================================================
# GCS 버킷 — 이미지/음악 파일 저장
# ============================================================
resource "google_storage_bucket" "main" {
  name          = var.bucket_name
  location      = var.region
  force_destroy = false

  uniform_bucket_level_access = true

  cors {
    origin          = ["*"]
    method          = ["GET", "HEAD"]
    response_header = ["*"]
    max_age_seconds = 3600
  }
}

# 버킷 내 파일 전체 공개 읽기 (이미지/음악 URL 직접 접근용)
resource "google_storage_bucket_iam_member" "public_read" {
  bucket = google_storage_bucket.main.name
  role   = "roles/storage.objectViewer"
  member = "allUsers"
}

# ============================================================
# GCS 버킷 — Loki 로그 저장
# ============================================================
resource "google_storage_bucket" "logs" {
  name          = "ilgijjan-490801-logs"
  location      = var.region
  force_destroy = false

  uniform_bucket_level_access = true
}
