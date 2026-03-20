# Monitoring 구성 가이드

## 개요

모니터링은 별도 GCP VM(`ilgijjan-monitoring`)에서 Docker로 운영됩니다.
이 폴더의 파일들은 **기록/복구용**이며, 실제 실행은 monitoring VM에서만 합니다.

## 서버 정보

| 항목 | 값 |
|------|-----|
| VM 이름 | ilgijjan-monitoring |
| 리전 | asia-northeast3-a |
| 머신 타입 | e2-small |
| 도메인 | monitoring.ilgijjan.store |

> IP 정보는 GCP 콘솔 또는 `terraform output`으로 확인하세요.

## 구성 요소

| 서비스 | 역할 | 포트 |
|--------|------|------|
| Grafana | 메트릭/로그 시각화 | 3000 |
| Prometheus | 메트릭 수집 (prod VM actuator 스크랩) | 9090 |
| Loki | 로그 수집 (Promtail에서 push) | 3100 |
| nginx | 리버스 프록시 + HTTPS (Let's Encrypt) | 80, 443 |

## 파일 설명

```
monitoring/
├── CLAUDE.md            # 이 파일
├── docker-compose.yml   # Grafana + Prometheus + Loki 구성
├── prometheus.yml       # Prometheus 스크랩 설정 (prod VM 내부 IP: 10.10.0.2)
├── loki-config.yml      # Loki 설정
└── promtail-config.yml  # prod VM에 배치해야 하는 Promtail 설정
```

> ⚠️ `promtail-config.yml`은 **prod VM 홈 디렉토리**에 복사해야 합니다.
> prod VM의 `docker-compose.yml`이 `./promtail-config.yml`로 참조합니다.

## 초기 설치 순서 (서버 날아갔을 때 복구)

### 1. Docker 설치
```bash
sudo apt update && sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update && sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo usermod -aG docker $USER
```

### 2. 설정 파일 배치
```bash
mkdir -p ~/monitoring
```

아래 3개 파일을 monitoring VM의 `~/monitoring/` 에 내용 그대로 복사 붙여넣기 합니다.
- `monitoring/docker-compose.yml` → `~/monitoring/docker-compose.yml`
- `monitoring/prometheus.yml` → `~/monitoring/prometheus.yml` (`${PROD_VM_INTERNAL_IP}` → prod VM 내부 IP로 변경, GCP 콘솔에서 확인)
- `monitoring/loki-config.yml` → `~/monitoring/loki-config.yml`

그리고 `monitoring/promtail-config.yml`은 **prod VM 홈 디렉토리**에 복사합니다.
- `monitoring/promtail-config.yml` → prod VM `~/promtail-config.yml` (`${MONITORING_VM_INTERNAL_IP}` → monitoring VM 내부 IP로 변경, GCP 콘솔에서 확인)

### 3. 모니터링 스택 실행
```bash
cd ~/monitoring
sudo docker compose up -d
```

### 4. nginx 설치 및 HTTPS 설정
```bash
sudo apt install -y nginx certbot python3-certbot-nginx
sudo tee /etc/nginx/sites-available/monitoring > /dev/null << 'EOF'
server {
    listen 80;
    server_name monitoring.ilgijjan.store;

    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
EOF
sudo ln -s /etc/nginx/sites-available/monitoring /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx
sudo certbot --nginx -d monitoring.ilgijjan.store --email {이메일} --agree-tos --non-interactive
```

## Grafana 초기 설정

- 접속: https://monitoring.ilgijjan.store
- 초기 계정: admin / admin (최초 접속 시 비밀번호 변경 권장)
- Data Source 추가:
  - Loki: `http://loki:3100`
  - Prometheus: `http://prometheus:9090`
