# ============================================================
# 고정 외부 IP
# ============================================================
resource "google_compute_address" "prod" {
  name   = "ilgijjan-prod-ip"
  region = var.region
}

resource "google_compute_address" "monitoring" {
  name   = "ilgijjan-monitoring-ip"
  region = var.region
}

# ============================================================
# prod VM — e2-standard-2 (2 vCPU, 8GB)
# Spring Boot + Redis + RabbitMQ + Promtail + nginx
# ============================================================
resource "google_compute_instance" "prod" {
  name         = "ilgijjan-prod"
  machine_type = "e2-standard-2"
  zone         = var.zone
  tags         = ["ilgijjan-vm"]

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2204-lts"
      size  = 20
      type  = "pd-ssd"
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.main.id
    access_config {
      nat_ip = google_compute_address.prod.address
    }
  }

  service_account {
    email  = google_service_account.app.email
    scopes = ["cloud-platform"]
  }

  metadata = {
    ssh-keys = "ubuntu:${var.ssh_public_key}"
  }

  depends_on = [google_project_service.apis]
}

# ============================================================
# monitoring VM — e2-small (2 vCPU 공유, 2GB)
# Grafana + Loki + Prometheus + nginx
# ============================================================
resource "google_compute_instance" "monitoring" {
  name         = "ilgijjan-monitoring"
  machine_type = "e2-small"
  zone         = var.zone
  tags         = ["ilgijjan-vm", "ilgijjan-monitoring"]

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2204-lts"
      size  = 20
      type  = "pd-ssd"
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.main.id
    access_config {
      nat_ip = google_compute_address.monitoring.address
    }
  }

  metadata = {
    ssh-keys = "ubuntu:${var.ssh_public_key}"
  }

  depends_on = [google_project_service.apis]
}
