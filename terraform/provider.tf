terraform {
  required_version = ">= 1.5"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }

  # tfstate를 GCS에 원격 저장 (terraform init 전에 버킷 수동 생성 필요)
  backend "gcs" {
    bucket      = "ilgijjan-499704-tfstate"
    prefix      = "terraform/state"
    credentials = "ilgijjan-499704-70fd533f468b.json"
  }
}

provider "google" {
  project     = var.project_id
  region      = var.region
  zone        = var.zone
  credentials = file(var.credentials_file)
}
