variable "project_id" {
  description = "GCP 프로젝트 ID"
  type        = string
}

variable "region" {
  description = "GCP 리전"
  type        = string
  default     = "asia-northeast3"
}

variable "zone" {
  description = "GCP 존"
  type        = string
  default     = "asia-northeast3-a"
}

variable "bucket_name" {
  description = "GCS 버킷 이름 (전 세계 유일해야 함)"
  type        = string
  default     = "ilgijjan-bucket"
}

variable "db_user" {
  description = "Cloud SQL 사용자 이름"
  type        = string
  default     = "ilgijjan"
}

variable "db_password" {
  description = "Cloud SQL 사용자 비밀번호"
  type        = string
  sensitive   = true
}

variable "ssh_username" {
  description = "VM SSH 접속 유저명 (GCP 계정 이메일 @ 앞부분)"
  type        = string
}

variable "ssh_public_key" {
  description = "VM에 등록할 SSH 공개키 (ssh-keygen으로 생성한 .pub 파일 내용)"
  type        = string
}

variable "credentials_file" {
  description = "Terraform 서비스 계정 JSON 키 파일 경로"
  type        = string
}
