# ============================================================
# GCP API 활성화
# ============================================================
resource "google_project_service" "apis" {
  for_each = toset([
    "compute.googleapis.com",
    "sqladmin.googleapis.com",
    "storage.googleapis.com",
    "vision.googleapis.com",
    "generativelanguage.googleapis.com",
    "iam.googleapis.com",
    "firebase.googleapis.com",
  ])
  service            = each.key
  disable_on_destroy = false
}

# ============================================================
# VPC & 서브넷
# ============================================================
resource "google_compute_network" "main" {
  name                    = "ilgijjan-vpc"
  auto_create_subnetworks = false
  depends_on              = [google_project_service.apis]
}

resource "google_compute_subnetwork" "main" {
  name          = "ilgijjan-subnet"
  ip_cidr_range = "10.10.0.0/24"
  network       = google_compute_network.main.id
  region        = var.region
}

# ============================================================
# 방화벽
# ============================================================

# SSH
resource "google_compute_firewall" "allow_ssh" {
  name    = "ilgijjan-allow-ssh"
  network = google_compute_network.main.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["ilgijjan-vm"]
}

# HTTP / HTTPS
resource "google_compute_firewall" "allow_http_https" {
  name    = "ilgijjan-allow-http-https"
  network = google_compute_network.main.name

  allow {
    protocol = "tcp"
    ports    = ["80", "443"]
  }
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["ilgijjan-vm"]
}

# Grafana(3000), Prometheus(9090), Loki(3100)
resource "google_compute_firewall" "allow_monitoring" {
  name    = "ilgijjan-allow-monitoring"
  network = google_compute_network.main.name

  allow {
    protocol = "tcp"
    ports    = ["3000", "9090", "3100"]
  }
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["ilgijjan-monitoring"]
}

# 내부 통신 (prod → monitoring Loki 전송 등)
resource "google_compute_firewall" "allow_internal" {
  name    = "ilgijjan-allow-internal"
  network = google_compute_network.main.name

  allow { protocol = "tcp" }
  allow { protocol = "udp" }
  allow { protocol = "icmp" }

  source_ranges = ["10.10.0.0/24"]
}
